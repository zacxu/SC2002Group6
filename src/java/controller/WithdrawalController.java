package controller;

import entity.*;

import util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages withdrawal requests and persistence.
 * 
 * 
 * handles  student submissions, staff approval flow, lookups, 
 * 
 * 
 */





public class WithdrawalController {

    private static final WithdrawalController INSTANCE = new WithdrawalController();

    private final Map<String, WithdrawalRequest> withdrawalRequests = new HashMap<>();
    private final SessionController sessionController = SessionController.getInstance();
    private final AuthController authController = AuthController.getInstance();
    private final ApplicationController applicationController = ApplicationController.getInstance();
    private final InternshipController internshipController = InternshipController.getInstance();


    private boolean initialized = false;
    private int nextWithdrawalRequestId = 1;



    private WithdrawalController() {
    }

    public static WithdrawalController getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        loadWithdrawalRequests();
        initialized = true;
    }


    private void loadWithdrawalRequests() throws IOException {
        withdrawalRequests.clear();
        withdrawalRequests.putAll(FileManager.loadWithdrawalRequests());
        updateNextWithdrawalId();
    }



    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise withdrawal registry", e);
            }
        }
    }




    private void updateNextWithdrawalId() {
        nextWithdrawalRequestId = 1;
        for (String key : withdrawalRequests.keySet()) {
            if (key.startsWith("WR")) {
                try {
                    int value = Integer.parseInt(key.substring(2));
                    if (value >= nextWithdrawalRequestId) {
                        nextWithdrawalRequestId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                    // ignore malformed id
                }
            }
        }
    }



    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveWithdrawalRequests(withdrawalRequests);
        } catch (IOException e) {
            throw new RuntimeException("Error saving withdrawal requests: " + e.getMessage(), e);
        }
    }






    public WithdrawalRequest requestWithdrawal(String applicationId) {
        ensureInitialized();

        Application application = applicationController.getApplication(applicationId);

        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }


        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can request withdrawal");
        }


        Student student = (Student) currentUser;
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only withdraw your own applications");
        }

        for (WithdrawalRequest request : withdrawalRequests.values()) {

            if (request.getApplicationId().equals(applicationId) && request.getStatus() == WithdrawalRequest.WithdrawalStatus.Pending) {
                throw new IllegalStateException("You already have a pending withdrawal request for this application");
            }
        }



        boolean isAfterPlacement = student.hasAcceptedPlacement() && student.getAcceptedInternshipId().equals(application.getInternshipId());



        String requestId = generateWithdrawalRequestId();
        WithdrawalRequest request = new WithdrawalRequest(requestId, applicationId, student.getUserId(), application.getInternshipId(), isAfterPlacement);

        withdrawalRequests.put(requestId, request);
        save();
        return request;


    }





    public void approveWithdrawal(String requestId) {
        ensureInitialized();

        WithdrawalRequest request = requireRequest(requestId);
        request.setStatus(WithdrawalRequest.WithdrawalStatus.Approved);

        Application application = applicationController.getApplication(request.getApplicationId());
        if (application != null) {
            User user = authController.getUser(request.getStudentId());


            if (user instanceof Student) {
                Student student = (Student) user;
                student.removeAppliedInternship(request.getInternshipId());

                if (student.hasAcceptedPlacement() && request.getInternshipId().equals(student.getAcceptedInternshipId())) {
                    student.setAcceptedInternshipId(null);

                }
            }

            if (application.getStatus() == Application.ApplicationStatus.Successful) {

                InternshipOpportunity internship = internshipController.getInternship(request.getInternshipId());

                if (internship != null && internship.getFilledSlots() > 0) {
                    internship.setFilledSlots(internship.getFilledSlots() - 1);

                    if (internship.getStatus() == InternshipOpportunity.InternshipStatus.Filled
                        && internship.getAvailableSlots() > 0) {
                        internship.setStatus(InternshipOpportunity.InternshipStatus.Approved);
                    }
                    internshipController.save();
                }
            }

            applicationController.removeApplication(request.getApplicationId());
        }

        save();
    }



    

    public void rejectWithdrawal(String requestId) {
        ensureInitialized();
        WithdrawalRequest request = requireRequest(requestId);
        request.setStatus(WithdrawalRequest.WithdrawalStatus.Rejected);

        save();
    }



    public List<WithdrawalRequest> getPendingWithdrawalRequests() {
        ensureInitialized();
        List<WithdrawalRequest> pending = new ArrayList<>();

        for (WithdrawalRequest request : withdrawalRequests.values()) {
            if (request.getStatus() == WithdrawalRequest.WithdrawalStatus.Pending) {
                pending.add(request);
            }
        }
        return pending;
    }

    public List<WithdrawalRequest> getWithdrawalsByStudent(String studentId) {
        ensureInitialized();

        List<WithdrawalRequest> result = new ArrayList<>();

        for (WithdrawalRequest request : withdrawalRequests.values()) {
            if (request.getStudentId().equals(studentId)) {
                result.add(request);
            }
        }
        return result;
    }



    public List<WithdrawalRequest> getMyWithdrawalRequests() {
        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof Student)) {
            return Collections.emptyList();
        }
        return getWithdrawalsByStudent(currentUser.getUserId());
    }


    public Collection<WithdrawalRequest> getAllWithdrawalRequests() {
        ensureInitialized();
        return Collections.unmodifiableCollection(withdrawalRequests.values());
    }


    public Map<String, WithdrawalRequest> getWithdrawalRequests() {
        ensureInitialized();
        return Collections.unmodifiableMap(withdrawalRequests);
    }

    public WithdrawalRequest getWithdrawalRequest(String requestId) {
        ensureInitialized();
        return withdrawalRequests.get(requestId);
    }


    public String generateWithdrawalRequestId() {
        ensureInitialized();
        return "WR" + String.format("%05d", nextWithdrawalRequestId++);
    }



    private WithdrawalRequest requireRequest(String requestId) {
        WithdrawalRequest request = withdrawalRequests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Withdrawal request not found");
        }
        return request;
    }
}

