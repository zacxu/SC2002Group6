package controller;

import entity.*;
import entity.enums.ApplicationStatus;
import entity.enums.WithdrawalStatus;

import repository.*;
import service.SessionService;
import service.StudentWithdrawalService;
import validator.WithdrawalValidator;



import java.io.IOException;
import java.util.Collection;
import java.util.List;


/**
 * Manages withdrawal requests and persistence.
 * 
 * 
 * handles  student submissions, staff approval flow, lookups, 
 * 
 * 
 */




 public class WithdrawalController implements StudentWithdrawalService {

    private static final WithdrawalController INSTANCE = new WithdrawalController();

    private final SessionService sessionService = SessionController.getInstance();
    private final AuthController authController = AuthController.getInstance();
    private final ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private final InternshipRegistry internshipRegistry = InternshipRegistry.getInstance();
    private final WithdrawalRegistry withdrawalRegistry = WithdrawalRegistry.getInstance();
    private final WithdrawalValidator withdrawalValidator = new WithdrawalValidator();


    private boolean initialized = false;


    private WithdrawalController() {
    }

    public static WithdrawalController getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        withdrawalRegistry.initialize();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise withdrawal requests", e);
            }
        }
    }





    @Override
    public WithdrawalRequest requestWithdrawal(String applicationId, String reason) {
        ensureInitialized();
        Application application = applicationRegistry.getApplicationById(applicationId);

        if (!withdrawalValidator.canWithdrawApplication(application)) {
            throw new IllegalStateException("You cannot withdraw this application");
        }

        Student student = requireStudent();
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only withdraw your own applications");
        }

        boolean existsPending = withdrawalRegistry.getAllRequests().stream()
            .anyMatch(req -> req.getApplicationId().equals(applicationId)
                && req.getStatus() == WithdrawalStatus.Pending);

        if (existsPending) {
            throw new IllegalStateException("You already have a pending withdrawal request for this application");
        }

        boolean afterPlacement =
            student.hasAcceptedPlacement() && application.getInternshipId().equals(student.getAcceptedInternshipId());

        WithdrawalRequest request = withdrawalRegistry.newRequest(
            applicationId,
            student.getUserId(),
            application.getInternshipId(),
            afterPlacement,
            reason
        );

        withdrawalRegistry.addRequest(request);
        withdrawalRegistry.save();
        return request;
    }




    public void approveWithdrawal(String requestId) {
        ensureInitialized();
        ensureStaff();
        WithdrawalRequest request = requireRequest(requestId);
        request.setStatus(WithdrawalStatus.Approved);

        Application application = applicationRegistry.getApplicationById(request.getApplicationId());
        if (application != null) {
            Student student = (Student) authController.getUser(request.getStudentId());
            if (student != null) {
                student.removeAppliedInternship(request.getInternshipId());
                if (student.hasAcceptedPlacement()
                    && request.getInternshipId().equals(student.getAcceptedInternshipId())) {
                    student.setAcceptedInternshipId(null);
                }
            }

            if (application.getStatus() == ApplicationStatus.Successful) {
                Internship internship = internshipRegistry.getInternshipById(request.getInternshipId());
                if (internship != null) {
                    internship.decrementFilledSlots();
                    internshipRegistry.save();
                }
            }

            applicationRegistry.removeApplication(request.getApplicationId());
        }

        withdrawalRegistry.save();
    }





    public void rejectWithdrawal(String requestId) {
        ensureInitialized();
        ensureStaff();
        WithdrawalRequest request = requireRequest(requestId);
        request.setStatus(WithdrawalStatus.Rejected);
        withdrawalRegistry.save();
    }



    @Override
    public List<WithdrawalRequest> getRequestsByStudent(String studentId) {
        ensureInitialized();
        return withdrawalRegistry.getRequestsByStudent(studentId);
    }

    public List<WithdrawalRequest> getPendingWithdrawalRequests() {
        ensureInitialized();
        ensureStaff();
        return withdrawalRegistry.getPendingRequests();
    }

    public Collection<WithdrawalRequest> getAllWithdrawalRequests() {
        ensureInitialized();
        ensureStaff();
        return withdrawalRegistry.getAllRequests();
    }


    public WithdrawalRequest getWithdrawalRequest(String requestId) {
        ensureInitialized();
        return withdrawalRegistry.getRequestById(requestId);
    }

    public void save() {
        withdrawalRegistry.save();
    }

    private WithdrawalRequest requireRequest(String requestId) {
        WithdrawalRequest request = withdrawalRegistry.getRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Withdrawal request not found");
        }
        return request;
    }


    
    private Student requireStudent() {
        if (!(sessionService.getCurrentUser() instanceof Student)) {
            throw new IllegalStateException("Only students can perform this action");
        }
        return (Student) sessionService.getCurrentUser();
    }

    private void ensureStaff() {
        if (!(sessionService.getCurrentUser() instanceof CareerCenterStaff)) {
            throw new IllegalStateException("Only Career Center Staff can perform this action");
        }
    }
}

