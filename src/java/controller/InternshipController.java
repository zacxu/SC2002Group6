package controller;

import entity.*;
import util.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages internship opportunities and persistence.
 * 
 *  loads/saves opportunities, handles rules for creation/update/deletion/visibility
 * 
 * Approves/rejects offerings for staff
 * 
 */



public class InternshipController {

    private static final InternshipController INSTANCE = new InternshipController();

    private final Map<String, InternshipOpportunity> internships = new HashMap<>();

    private final SessionController sessionController = SessionController.getInstance();
    private final AuthController authController = AuthController.getInstance();

    private boolean initialized = false;
    private int nextInternshipId = 1;



    private InternshipController() {
    }


    public static InternshipController getInstance() {
        return INSTANCE;
    }



    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        loadInternships();
        initialized = true;
    }



    private void loadInternships() throws IOException {
        internships.clear();
        internships.putAll(FileManager.loadInternships());
        updateNextInternshipId();
        rebuildCompanyRepresentativeInternships();
    }



    private void ensureLoaded() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise internship registry", e);
            }
        }
    }



    public void ensureInitialized() {
        ensureLoaded();
    }




    private void updateNextInternshipId() {
        nextInternshipId = 1;
        for (String key : internships.keySet()) {
            if (key.startsWith("INT")) {
                try {
                    int value = Integer.parseInt(key.substring(3));
                    if (value >= nextInternshipId) {
                        nextInternshipId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                   
                }
            }
        }
    }



    private void rebuildCompanyRepresentativeInternships() {
        for (User user : authController.getAllUsers()) {
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative representative = (CompanyRepresentative) user;
                representative.getCreatedInternshipIds().clear();
            }
        }


        for (InternshipOpportunity internship : internships.values()) {
            User owner = authController.getUser(internship.getCompanyRepId());

            if (owner instanceof CompanyRepresentative) {
                CompanyRepresentative representative = (CompanyRepresentative) owner;
                representative.addCreatedInternship(internship.getInternshipId());

            }

        }
    }



    public synchronized void save() {
        ensureLoaded();
        try {
            FileManager.saveInternships(internships);
        } catch (IOException e) {
            throw new RuntimeException("Error saving internships: " + e.getMessage(), e);
        }
    }






    public InternshipOpportunity createInternship(String title,
                                                  String description,
                                                  InternshipOpportunity.InternshipLevel level,
                                                  String preferredMajor,
                                                  LocalDate openingDate,
                                                  LocalDate closingDate,
                                                  int totalSlots) {


        ensureLoaded();


        User currentUser = sessionController.getCurrentUser();

        if (!(currentUser instanceof CompanyRepresentative)) {

            throw new IllegalStateException("Only Company Representatives can create internships");
        }


        
        CompanyRepresentative representative = (CompanyRepresentative) currentUser;

        if (!representative.canCreateMoreInternships()) {

            throw new IllegalStateException("Maximum number of internships (5) reached");
        }


        if (!Validator.isValidSlotCount(totalSlots)) {
            throw new IllegalArgumentException("Slots must be between 1 and 10");
        }


        if (!DateUtils.isValidDateRange(openingDate, closingDate)) {
            throw new IllegalArgumentException("Invalid date range: closing date must be after opening date");
        }


        String internshipId = generateInternshipId();
        InternshipOpportunity opportunity = new InternshipOpportunity( internshipId, title, description, level, preferredMajor, openingDate, closingDate, representative.getCompanyName(), representative.getUserId(), totalSlots);


        internships.put(internshipId, opportunity);
        representative.addCreatedInternship(internshipId);
        save();

        return opportunity;



    }





    public void updateInternship(String internshipId,
                                 String title,
                                 String description,
                                 InternshipOpportunity.InternshipLevel level,
                                 String preferredMajor,
                                 LocalDate openingDate,
                                 LocalDate closingDate,
                                 int totalSlots) {

        ensureLoaded();

        InternshipOpportunity internship = requireInternship(internshipId);
        User currentUser = sessionController.getCurrentUser();
        if (currentUser == null || !internship.getCompanyRepId().equals(currentUser.getUserId())) {
            throw new IllegalStateException("You can only edit your own internships");
        }

        if (internship.getStatus() == InternshipOpportunity.InternshipStatus.Approved
            || internship.getStatus() == InternshipOpportunity.InternshipStatus.Filled) {
            throw new IllegalStateException("Cannot edit internship that is already approved or filled");
        }

        if (!DateUtils.isValidDateRange(openingDate, closingDate)) {
            throw new IllegalArgumentException("Invalid date range: closing date must be after opening date");
        }

        if (totalSlots < internship.getFilledSlots()) {
            throw new IllegalArgumentException(
                "Total slots cannot be less than filled slots (" + internship.getFilledSlots() + ")"
            );
        }

        internship.setTitle(title);
        internship.setDescription(description);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setOpeningDate(openingDate);
        internship.setClosingDate(closingDate);
        internship.setTotalSlots(totalSlots);

        save();

    }





    public void deleteInternship(String internshipId, List<Application> existingApplications) {

        ensureLoaded();

        InternshipOpportunity internship = requireInternship(internshipId);
        User currentUser = sessionController.getCurrentUser();
        if (currentUser == null || !internship.getCompanyRepId().equals(currentUser.getUserId())) {
            throw new IllegalStateException("You can only delete your own internships");
        }

        if (internship.getStatus() == InternshipOpportunity.InternshipStatus.Approved
            && existingApplications != null
            && !existingApplications.isEmpty()) {
            throw new IllegalStateException("Cannot delete internship with existing applications");
        }

        internships.remove(internshipId);

        if (currentUser instanceof CompanyRepresentative) {
            ((CompanyRepresentative) currentUser).removeCreatedInternship(internshipId);
        }

        save();
    }





    public void toggleVisibility(String internshipId) {
        ensureLoaded();
        InternshipOpportunity internship = requireInternship(internshipId);
        User currentUser = sessionController.getCurrentUser();

        if (currentUser instanceof CompanyRepresentative && !internship.getCompanyRepId().equals(currentUser.getUserId())) {
            
            throw new IllegalStateException("You can only toggle visibility for your own internships");
        }

        internship.toggleVisibility();
        save();
    }





    public void approveInternship(String internshipId) {
        ensureLoaded();
        InternshipOpportunity internship = requireInternship(internshipId);

        if (internship.getStatus() != InternshipOpportunity.InternshipStatus.Pending) {
            throw new IllegalStateException("Only pending internships can be approved");
        }


        internship.setStatus(InternshipOpportunity.InternshipStatus.Approved);
        save();
    }




    public void rejectInternship(String internshipId) {
        ensureLoaded();
        InternshipOpportunity internship = requireInternship(internshipId);

        if (internship.getStatus() != InternshipOpportunity.InternshipStatus.Pending) {
            throw new IllegalStateException("Only pending internships can be rejected");
        }
        internship.setStatus(InternshipOpportunity.InternshipStatus.Rejected);
        save();
    }





    public InternshipOpportunity getInternship(String internshipId) {
        ensureLoaded();
        return internships.get(internshipId);
    }



    
    public Collection<InternshipOpportunity> getAllInternships() {
        ensureLoaded();
        return Collections.unmodifiableCollection(internships.values());
    }




    public List<InternshipOpportunity> getInternshipsByCompanyRep(String companyRepId) {
        ensureLoaded();
        List<InternshipOpportunity> list = new ArrayList<>();

        for (InternshipOpportunity opportunity : internships.values()) {
            if (opportunity.getCompanyRepId().equals(companyRepId)) {
                list.add(opportunity);
            }
        }
        return list;
    }




    public List<InternshipOpportunity> getPendingInternships() {
        ensureLoaded();
        List<InternshipOpportunity> pending = new ArrayList<>();

        for (InternshipOpportunity opportunity : internships.values()) {

            if (opportunity.getStatus() == InternshipOpportunity.InternshipStatus.Pending) {
                pending.add(opportunity);
            }
        }
        return pending;
    }




    public List<InternshipOpportunity> getVisibleInternships() {
        ensureLoaded();
        List<InternshipOpportunity> visible = new ArrayList<>();
        for (InternshipOpportunity opportunity : internships.values()) {
            if (opportunity.isVisible()
                && opportunity.getStatus() == InternshipOpportunity.InternshipStatus.Approved) {
                visible.add(opportunity);
            }
        }
        return visible;
    }




    public Map<String, InternshipOpportunity> getInternships() {
        ensureLoaded();
        return Collections.unmodifiableMap(internships);
    }




    public String generateInternshipId() {
        ensureLoaded();
        return "INT" + String.format("%05d", nextInternshipId++);
    }



    private InternshipOpportunity requireInternship(String internshipId) {
        InternshipOpportunity internship = internships.get(internshipId);
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        return internship;
        
    }
}


