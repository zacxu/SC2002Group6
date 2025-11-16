package controller;

import entity.*;

import repository.*;
import service.*;
import validator.InternshipValidator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;


import java.util.List;



/**
 * Manages internship opportunities and persistence.
 * 
 *  loads/saves opportunities, handles rules for creation/update/deletion/visibility
 * 
 * Approves/rejects offerings for staff
 * 
 */



 public class InternshipController implements CompanyInternshipManagementService, StaffInternshipApprovalService {
    private static final InternshipController INSTANCE = new InternshipController();

    private final SessionService sessionService = SessionController.getInstance();
    private final InternshipRegistry internshipRegistry = InternshipRegistry.getInstance();
    private final ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private final InternshipValidator internshipValidator = new InternshipValidator();



    private InternshipController() {
    }

    public static InternshipController getInstance() {
        return INSTANCE;
    }

    public void initialize() throws IOException {
        internshipRegistry.initialize();
    }

    public void ensureInitialized() {
        try {
            internshipRegistry.initialize();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialise internships", e);
        }
    }






    @Override
    public Internship createInternship(String title,
                                       String description,
                                       InternshipLevel level,
                                       String preferredMajor,
                                       LocalDate openingDate,
                                       LocalDate closingDate,
                                       int totalSlots) {
        ensureInitialized();
        CompanyRepresentative representative = requireCompanyRep();

        if (!internshipValidator.canRepCreateMoreInternships(representative)) {
            throw new IllegalStateException("Maximum number of internships (" + representative.getCreatedInternshipIds().size() + ") reached");
        }

        if (!internshipValidator.isWithinSlotLimit(totalSlots)) {
            throw new IllegalArgumentException("Slots must be between 1 and 10");
        }

        if (!internshipValidator.isDateRangeValid(openingDate, closingDate)) {
            throw new IllegalArgumentException("Invalid date range: closing date must be after opening date");
        }

        String internshipId = internshipRegistry.nextId();
        Internship internship = new Internship(
            internshipId,
            title,
            description,
            level,
            preferredMajor,
            openingDate,
            closingDate,
            representative.getCompanyName(),
            representative.getUserId(),
            totalSlots

        );



        internshipRegistry.addInternship(internship);
        representative.addCreatedInternship(internshipId);
        internshipRegistry.save();
        return internship;
    }







    @Override
    public void updateInternship(String internshipId,
                                 String title,
                                 String description,
                                 InternshipLevel level,
                                 String preferredMajor,
                                 LocalDate openingDate,
                                 LocalDate closingDate,
                                 int totalSlots) {
        ensureInitialized();
        Internship internship = requireInternship(internshipId);
        CompanyRepresentative representative = requireCompanyRep();

        if (!internship.getCompanyRepId().equals(representative.getUserId())) {
            throw new IllegalStateException("You can only edit your own internships");
        }

        if (internship.getStatus() == InternshipStatus.Approved
            || internship.getStatus() == InternshipStatus.Filled) {
            throw new IllegalStateException("Cannot edit internship that is already approved or filled");
        }

        if (!internshipValidator.isDateRangeValid(openingDate, closingDate)) {
            throw new IllegalArgumentException("Invalid date range: closing date must be after opening date");
        }

        if (totalSlots < internship.getFilledSlots()) {
            throw new IllegalArgumentException("Total slots cannot be less than filled slots");
        }

        internship.setTitle(title);
        internship.setDescription(description);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setOpeningDate(openingDate);
        internship.setClosingDate(closingDate);
        internship.setTotalSlots(totalSlots);

        internshipRegistry.save();
    }








    @Override
    public void deleteInternship(String internshipId) {
        ensureInitialized();
        Internship internship = requireInternship(internshipId);
        CompanyRepresentative representative = requireCompanyRep();

        if (!internship.getCompanyRepId().equals(representative.getUserId())) {
            throw new IllegalStateException("You can only delete your own internships");
        }

        List<Application> existingApps = applicationRegistry.getApplicationsByInternship(internshipId);
        if (!existingApps.isEmpty()
            && internship.getStatus() == InternshipStatus.Approved) {
            throw new IllegalStateException("Cannot delete internship with existing applications");
        }

        internshipRegistry.removeInternship(internshipId);
        representative.removeCreatedInternship(internshipId);
        internshipRegistry.save();
    }







    @Override
    public void toggleVisibility(String internshipId) {
        ensureInitialized();
        Internship internship = requireInternship(internshipId);

        CompanyRepresentative representative = requireCompanyRep();
        if (!internship.getCompanyRepId().equals(representative.getUserId())) {
            throw new IllegalStateException("You can only toggle visibility for your own internships");
        }

        internship.toggleVisibility();
        internshipRegistry.save();
    }









    @Override
    public List<Internship> getInternshipsByCompanyRep(String companyRepId) {
        ensureInitialized();
        return internshipRegistry.getInternshipsByCompanyRep(companyRepId);
    }

    @Override
    public List<Internship> getPendingInternships() {
        ensureInitialized();
        ensureStaff();
        return internshipRegistry.getPendingInternships();
    }


    @Override
    public void approveInternship(String internshipId) {
        ensureInitialized();
        ensureStaff();
        Internship internship = requireInternship(internshipId);
        if (internship.getStatus() != InternshipStatus.Pending) {
            throw new IllegalStateException("Only pending internships can be approved");
        }
        internship.setStatus(InternshipStatus.Approved);
        internshipRegistry.save();
    }


    @Override
    public void rejectInternship(String internshipId) {
        ensureInitialized();
        ensureStaff();
        Internship internship = requireInternship(internshipId);
        if (internship.getStatus() != InternshipStatus.Pending) {
            throw new IllegalStateException("Only pending internships can be rejected");
        }
        internship.setStatus(InternshipStatus.Rejected);
        internshipRegistry.save();
    }


    public Internship getInternship(String internshipId) {
        ensureInitialized();
        return internshipRegistry.getInternshipById(internshipId);
    }

    public Collection<Internship> getAllInternships() {
        ensureInitialized();
        return internshipRegistry.getAllInternships();
    }

    public List<Internship> getVisibleInternships() {
        ensureInitialized();
        return internshipRegistry.getVisibleInternships();
    }

    public void save() {
        internshipRegistry.save();
    }



    
    private Internship requireInternship(String internshipId) {
        Internship internship = internshipRegistry.getInternshipById(internshipId);
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        return internship;
    }

    private CompanyRepresentative requireCompanyRep() {
        if (!(sessionService.getCurrentUser() instanceof CompanyRepresentative)) {
            throw new IllegalStateException("Only Company Representatives can perform this action");
        }
        return (CompanyRepresentative) sessionService.getCurrentUser();
    }

    private void ensureStaff() {
        if (!(sessionService.getCurrentUser() instanceof CareerCenterStaff)) {
            throw new IllegalStateException("Only Career Center Staff can perform this action");
        }
    }
}


