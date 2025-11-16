package controller;


import entity.*;
import util.*;

import repository.ApplicationRegistry;
import repository.InternshipRegistry;
import service.SessionService;
import service.StudentApplicationService;
import validator.ApplicationValidator;





import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


/**
 * Manages internship applications and registry for internship applications.
 * 
 * 
 * handles initialisation/persistence, student applications, company/staff approvals and rejections,
 * 
 * 
 */





 public class ApplicationController implements StudentApplicationService {

    private static final ApplicationController INSTANCE = new ApplicationController();


    private final SessionService sessionService = SessionController.getInstance();
    private final AuthController authController = AuthController.getInstance();
    private final InternshipRegistry internshipRegistry = InternshipRegistry.getInstance();
    private final ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private final ApplicationValidator applicationValidator = new ApplicationValidator();

    private boolean initialized = false;

    private ApplicationController() {
    }

    public static ApplicationController getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        applicationRegistry.initialize();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise applications", e);
            }
        }
    }






    @Override
    public Application applyForInternship(String internshipId) {
        ensureInitialized();
        internshipController().ensureInitialized();

        Student student = requireStudent();
        Internship internship = requireInternship(internshipId);

        if (student.getAppliedInternshipIds().contains(internshipId)) {
            throw new IllegalStateException("You have already applied for this internship");
        }

        if (!applicationValidator.validateApplicationEligibility(student, internship).isValid()) {
            throw new IllegalStateException(
                applicationValidator.validateApplicationEligibility(student, internship).getErrorMessage()
            );
        }

        Application application = new Application(
            applicationRegistry.nextId(),
            student.getUserId(),
            internshipId,
            LocalDate.now()
        );

        applicationRegistry.addApplication(application);
        student.addAppliedInternship(internshipId);
        applicationRegistry.save();
        return application;
    }







    public void approveApplication(String applicationId, boolean isStaffAction) {
        ensureInitialized();
        Application application = requireApplication(applicationId);
        Internship internship = requireInternship(application.getInternshipId());

        if (!isStaffAction) {
            requireCompanyRepresentativeOwnership(internship);
        }

        application.setStatus(ApplicationStatus.Successful);
        applicationRegistry.save();
    }

    public void rejectApplication(String applicationId, boolean isStaffAction) {
        ensureInitialized();
        Application application = requireApplication(applicationId);
        Internship internship = requireInternship(application.getInternshipId());

        if (!isStaffAction) {
            requireCompanyRepresentativeOwnership(internship);
        }

        application.setStatus(ApplicationStatus.Unsuccessful);
        applicationRegistry.save();
    }







    @Override
    public void acceptPlacement(String applicationId) {
        ensureInitialized();
        internshipController().ensureInitialized();

        Application application = requireApplication(applicationId);
        Student student = requireStudent();

        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only accept your own placements");
        }

        if (application.getStatus() != ApplicationStatus.Successful) {
            throw new IllegalStateException("Can only accept successful applications");
        }

        if (student.hasAcceptedPlacement()) {
            throw new IllegalStateException("You have already accepted a placement");
        }

        student.setAcceptedInternshipId(application.getInternshipId());

        List<Application> otherApplications = applicationRegistry.getApplicationsByStudent(student.getUserId());
        for (Application other : new ArrayList<>(otherApplications)) {
            if (!other.getApplicationId().equals(applicationId)) {
                removeApplication(other.getApplicationId());
            }
        }

        Internship internship = requireInternship(application.getInternshipId());
        internship.incrementFilledSlots();
        internshipRegistry.save();
        applicationRegistry.save();
    }






    public List<Application> getApplicationsByStudent(String studentId) {
        ensureInitialized();
        return applicationRegistry.getApplicationsByStudent(studentId);
    }

    public List<Application> getApplicationsByInternship(String internshipId) {
        ensureInitialized();
        return applicationRegistry.getApplicationsByInternship(internshipId);
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        ensureInitialized();
        List<Application> result = new ArrayList<>();
        for (Application application : applicationRegistry.getAllApplications()) {
            if (application.getStatus() == status) {
                result.add(application);
            }
        }
        return result;
    }




    public Collection<Application> getAllApplications() {
        ensureInitialized();
        return applicationRegistry.getAllApplications();
    }

    public Application getApplication(String applicationId) {
        ensureInitialized();
        return applicationRegistry.getApplicationById(applicationId);
    }



    public void removeApplication(String applicationId) {
        ensureInitialized();
        Application removed = applicationRegistry.getApplicationById(applicationId);

        if (removed != null) {
            Student student = (Student) authController.getUser(removed.getStudentId());
            if (student != null) {
                student.removeAppliedInternship(removed.getInternshipId());
            }
            applicationRegistry.removeApplication(applicationId);
            applicationRegistry.save();
        }
    }




    public void save() {
        applicationRegistry.save();
    }



    private Application requireApplication(String applicationId) {
        Application application = applicationRegistry.getApplicationById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        return application;
    }

    private Internship requireInternship(String internshipId) {
        Internship internship = internshipRegistry.getInternshipById(internshipId);
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        return internship;
    }

    private Student requireStudent() {
        if (!(sessionService.getCurrentUser() instanceof Student)) {
            throw new IllegalStateException("Only students can perform this action");
        }
        return (Student) sessionService.getCurrentUser();
    }

    private InternshipController internshipController() {
        return InternshipController.getInstance();
    }

    private void requireCompanyRepresentativeOwnership(Internship internship) {

        if (!(sessionService.getCurrentUser() instanceof CompanyRepresentative)) {
            throw new IllegalStateException("Only company representatives can perform this action");
        }
        if (!internship.getCompanyRepId().equals(sessionService.getCurrentUser().getUserId())) {
            throw new IllegalStateException("You can only manage applications for your own internships");
        }


    }
}


