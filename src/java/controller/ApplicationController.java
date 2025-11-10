package controller;


import entity.*;
import util.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages internship applications and registry for internship applications.
 * 
 * 
 * handles initialisation/persistence, student applications, company/staff approvals and rejections,
 * 
 * 
 */





public class ApplicationController {
    private static final ApplicationController INSTANCE = new ApplicationController();

    private final Map<String, Application> applications = new HashMap<>();
    private final SessionController sessionController = SessionController.getInstance();
    private final AuthController authController = AuthController.getInstance();
    private final InternshipController internshipController = InternshipController.getInstance();


    private boolean initialized = false;
    private int nextApplicationId = 1;



    private ApplicationController() {
    }



    public static ApplicationController getInstance() {
        return INSTANCE;
    }



    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        loadApplications();
        initialized = true;
    }



    private void loadApplications() throws IOException {
        applications.clear();
        applications.putAll(FileManager.loadApplications());
        updateNextApplicationId();
        syncStudentApplications();
    }



    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise application registry", e);
            }
        }
    }
    



    private void updateNextApplicationId() {
        nextApplicationId = 1;
        for (String key : applications.keySet()) {
            if (key.startsWith("APP")) {
                try {
                    int value = Integer.parseInt(key.substring(3));
                    if (value >= nextApplicationId) {
                        nextApplicationId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                    // ignore malformed id
                }
            }
        }
    }




    private void syncStudentApplications() {
        for (Application application : applications.values()) {
            User user = authController.getUser(application.getStudentId());
            if (user instanceof Student) {
                Student student = (Student) user;
                if (!student.getAppliedInternshipIds().contains(application.getInternshipId())) {
                    student.addAppliedInternship(application.getInternshipId());
                }
            }
        }
    }


    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveApplications(applications);
        } catch (IOException e) {
            throw new RuntimeException("Error saving applications: " + e.getMessage(), e);
        }
    }






    public Application applyForInternship(String internshipId) {
        ensureInitialized();
        internshipController.ensureInitialized();

        User currentUser = sessionController.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can apply for internships");
        }

        Student student = (Student) currentUser;
        InternshipOpportunity internship = internshipController.getInternship(internshipId);


        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }

        if (student.getAppliedInternshipIds().contains(internshipId)) {
            throw new IllegalStateException("You have already applied for this internship");
        }

        if (!student.canApplyForMore()) {
            throw new IllegalStateException("Maximum number of applications (3) reached");
        }

        if (!Validator.isEligibleForInternship(student, internship)) {
            throw new IllegalStateException("You are not eligible for this internship based on your year of study and/or major");
        }

        if (!internship.isOpenForApplication()) {
            throw new IllegalStateException("This internship is not open for applications");
        }




        String applicationId = generateApplicationId();
        Application application = new Application(applicationId, student.getUserId(), internshipId);

        applications.put(applicationId, application);
        student.addAppliedInternship(internshipId);

        save();



        return application;

    }






    public void approveApplication(String applicationId, boolean isStaffAction) {
        ensureInitialized();

        Application application = requireApplication(applicationId);

        InternshipOpportunity internship = internshipController.getInternship(application.getInternshipId());


        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }


        User currentUser = sessionController.getCurrentUser();
        if (!isStaffAction) {

            if (!(currentUser instanceof CompanyRepresentative)) {
                throw new IllegalStateException("Only company representatives can approve applications");
            }
            if (!internship.getCompanyRepId().equals(currentUser.getUserId())) {
                throw new IllegalStateException("You can only approve applications for your own internships");
            }
        }


        application.setStatus(Application.ApplicationStatus.Successful);
        save();

    }





    public void rejectApplication(String applicationId, boolean isStaffAction) {
        ensureInitialized();
        Application application = requireApplication(applicationId);
        InternshipOpportunity internship = internshipController.getInternship(application.getInternshipId());


        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }


        User currentUser = sessionController.getCurrentUser();

        if (!isStaffAction) {
            if (!(currentUser instanceof CompanyRepresentative)) {
                throw new IllegalStateException("Only company representatives can reject applications");
            }
            if (!internship.getCompanyRepId().equals(currentUser.getUserId())) {
                throw new IllegalStateException("You can only reject applications for your own internships");
            }
        }

        application.setStatus(Application.ApplicationStatus.Unsuccessful);
        save();

    }






    public void acceptPlacement(String applicationId) {

        ensureInitialized();
        internshipController.ensureInitialized();

        Application application = requireApplication(applicationId);
        User currentUser = sessionController.getCurrentUser();


        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can accept placements");
        }

        Student student = (Student) currentUser;
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only accept your own placements");
        }

        if (application.getStatus() != Application.ApplicationStatus.Successful) {
            throw new IllegalStateException("Can only accept successful applications");
        }

        if (student.hasAcceptedPlacement()) {
            throw new IllegalStateException("You have already accepted a placement");
        }


        student.setAcceptedInternshipId(application.getInternshipId());

        List<Application> otherApplications = getApplicationsByStudent(student.getUserId());


        for (Application other : otherApplications) {
            if (!other.getApplicationId().equals(applicationId)) {
                removeApplication(other.getApplicationId());
            }


        }

        InternshipOpportunity internship = internshipController.getInternship(application.getInternshipId());

        if (internship != null) {
            internship.setFilledSlots(internship.getFilledSlots() + 1);
            internshipController.save();
        }

        save();
    }





    

    public List<Application> getApplicationsByStudent(String studentId) {
        ensureInitialized();
        List<Application> result = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getStudentId().equals(studentId)) {
                result.add(application);
            }
        }
        return result;
    }




    public List<Application> getApplicationsForCurrentStudent() {
        User currentUser = sessionController.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            return Collections.emptyList();
        }
        return getApplicationsByStudent(currentUser.getUserId());
    }




    public List<Application> getApplicationsByInternship(String internshipId) {
        ensureInitialized();
        List<Application> result = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getInternshipId().equals(internshipId)) {
                result.add(application);
            }
        }
        return result;
    }




    public List<Application> getApplicationsByStatus(Application.ApplicationStatus status) {
        ensureInitialized();
        List<Application> result = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getStatus() == status) {
                result.add(application);
            }
        }
        return result;
    }



    public Application getApplication(String applicationId) {
        ensureInitialized();
        return applications.get(applicationId);
    }



    public Map<String, Application> getApplications() {
        ensureInitialized();
        return Collections.unmodifiableMap(applications);
    }



    public void removeApplication(String applicationId) {
        ensureInitialized();
        Application removed = applications.remove(applicationId);
        if (removed != null) {
            User user = authController.getUser(removed.getStudentId());
            if (user instanceof Student) {
                ((Student) user).removeAppliedInternship(removed.getInternshipId());
            }
        }
        save();
    }



    public String generateApplicationId() {
        ensureInitialized();
        return "APP" + String.format("%05d", nextApplicationId++);
    }



    private Application requireApplication(String applicationId) {
        Application application = applications.get(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        return application;
    }
}


