package controller;

import entity.*;


import util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides read-only access to internship information for  the current user
 * 
 * returns details, all internships, or student-eligible visible opportunities
 * 
 * 
 */


 


public class InternshipViewController {

    private final InternshipController internshipController = InternshipController.getInstance();
    private final SessionController sessionController = SessionController.getInstance();


    public InternshipOpportunity getInternshipDetails(String internshipId) {
        return internshipController.getInternship(internshipId);
    }




    public List<InternshipOpportunity> getVisibleInternshipsForStudent() {
        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can view student-specific internships");
        }


        Student student = (Student) currentUser;


        List<InternshipOpportunity> visible = internshipController.getVisibleInternships();
        List<InternshipOpportunity> eligible = new ArrayList<>();



        for (InternshipOpportunity internship : visible) {
            if (Validator.isEligibleForInternship(student, internship) && internship.isOpenForApplication()) {
                eligible.add(internship);
            }
        }

        return eligible;
    }




    public List<InternshipOpportunity> getAllInternships() {
        return new ArrayList<>(internshipController.getAllInternships());
    }
    
}


