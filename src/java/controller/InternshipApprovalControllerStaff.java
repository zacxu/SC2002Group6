package controller;

import entity.*;

import java.util.List;

/**
 * stadff only class for handling  internship approvals performed by career center staff.
 */



public class InternshipApprovalControllerStaff {

    private final SessionController sessionController = SessionController.getInstance();
    private final InternshipController internshipController = InternshipController.getInstance();




    private void ensureStaff() {
        User currentUser = sessionController.getCurrentUser();
        if (!(currentUser instanceof CareerCenterStaff)) {
            throw new IllegalStateException("Only Career Center Staff can perform this action");
        }
    }

    public void approveInternship(String internshipId) {
        ensureStaff();
        internshipController.approveInternship(internshipId);
    }


    
    public void rejectInternship(String internshipId) {
        ensureStaff();
        internshipController.rejectInternship(internshipId);
    }


    public List<Internship> getPendingInternships() {
        ensureStaff();
        return internshipController.getPendingInternships();
    }

    
    


}


