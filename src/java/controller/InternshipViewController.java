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



    public Internship getInternshipDetails(String internshipId) {
        return internshipController.getInternship(internshipId);
    }


    public List<Internship> getVisibleInternshipsForStudent() {

        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can view student-specific internships");
        }
        Student student = (Student) currentUser;

        List<Internship> visible = internshipController.getVisibleInternships();
        List<Internship> eligible = new ArrayList<>();

        for (Internship internship : visible) {
            if (Validator.isEligibleForInternship(student, internship) && internship.isOpenForApplication()) {
                eligible.add(internship);
            }
        }


        return eligible;
    }


    
    public List<Internship> getAllInternships() {
        return new ArrayList<>(internshipController.getAllInternships());
    }
}

