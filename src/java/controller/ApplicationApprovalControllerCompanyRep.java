package controller;

import entity.*;

import java.util.ArrayList;
import java.util.List;



/**
 * Company rep only class for handling  application approvals and rejections apllied by students
 * 
 * 
 */






public class ApplicationApprovalControllerCompanyRep {

    private final SessionController sessionController = SessionController.getInstance();
    private final ApplicationController applicationController = ApplicationController.getInstance();
    private final InternshipController internshipController = InternshipController.getInstance();






    private void ensureCompanyRepresentative() {
        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof CompanyRepresentative)) {
            throw new IllegalStateException("Only Company Representatives can perform this action");
        }
    }


    

    public void approveApplication(String applicationId) {
        ensureCompanyRepresentative();

        applicationController.approveApplication(applicationId, false);
    }

    public void rejectApplication(String applicationId) {
        ensureCompanyRepresentative();

        applicationController.rejectApplication(applicationId, false);
    }





    public List<Application> getPendingApplications() {
        ensureCompanyRepresentative();
        User currentUser = sessionController.getCurrentUser();
        String companyRepId = currentUser.getUserId();

        List<Application> pending = new ArrayList<>();


        for (Application application : applicationController.getApplications().values()) {

            if (application.getStatus() == Application.ApplicationStatus.Pending) {
                InternshipOpportunity internship = internshipController.getInternship(application.getInternshipId());

                if (internship != null && internship.getCompanyRepId().equals(companyRepId)) {
                    pending.add(application);
                }
            }
        }
        return pending;
    }



    


    
}




