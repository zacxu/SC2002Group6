package boundary;

import controller.*;
import entity.*;

import util.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


/**
 * class handles the Company Representative menu for the internship placement management system.
 * 
 * Provides a menu-driven interface for Company Representatives to interact with the system.
 * 
 * It handles the creation, editing, deletion, and approval of internship opportunities.
 * 
 * 
 * 
 */





public class CompanyRepMenu {
    private final Scanner scanner;
    private final AuthController authController;
    private final InternshipController internshipController;
    private final ApplicationController applicationController;
    private final ApplicationApprovalControllerCompanyRep applicationApprovalControllerCompanyRep;

    
    public CompanyRepMenu(Scanner scanner,
                          AuthController authController,
                          InternshipController internshipController,
                          ApplicationController applicationController,
                          ApplicationApprovalControllerCompanyRep applicationApprovalControllerCompanyRep) {


        this.scanner = scanner;
        this.authController = authController;
        this.internshipController = internshipController;
        this.applicationController = applicationController;
        this.applicationApprovalControllerCompanyRep = applicationApprovalControllerCompanyRep;


    }
    




    public void showMenu(CompanyRepresentative companyRep) {

        while (true) {


            System.out.println("\n=== Company Representative Menu ===");
            System.out.println("1. Create Internship Opportunity");
            System.out.println("2. View My Internships");
            System.out.println("3. Edit Internship Opportunity");
            System.out.println("4. Delete Internship Opportunity");
            System.out.println("5. View Applications for Internship");
            System.out.println("6. Approve/Reject Application");
            System.out.println("7. Toggle Internship Visibility");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();
            


            switch (choice) {
                case "1":
                    createInternship();
                    break;

                case "2":
                    viewMyInternships(companyRep);
                    break;

                case "3":
                    editInternship();
                    break;

                case "4":
                    deleteInternship();
                    break;

                case "5":
                    viewApplications();
                    break;

                case "6":
                    approveRejectApplication();
                    break;

                case "7":
                    toggleVisibility();
                    break;

                case "8":
                    changePassword();
                    break;

                case "9":
                    authController.logout();
                    return;



                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    




    private void createInternship() {
        System.out.println("\n=== Create Internship Opportunity ===");
        
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Level (Basic/Intermediate/Advanced): ");
        String levelStr = scanner.nextLine().trim();

        InternshipOpportunity.InternshipLevel level;


        try {
            level = InternshipOpportunity.InternshipLevel.valueOf(levelStr);
        } 
        catch (IllegalArgumentException e) {
            
            System.out.println("Invalid level.");
            return;

        }
        
        System.out.print("Preferred Major: ");
        String preferredMajor = scanner.nextLine().trim();


        
        System.out.print("Opening Date (yyyy-MM-dd): ");
        LocalDate openingDate = DateUtils.parseDate(scanner.nextLine().trim());
        
        System.out.print("Closing Date (yyyy-MM-dd): ");
        LocalDate closingDate = DateUtils.parseDate(scanner.nextLine().trim());



        
        System.out.print("Number of Slots (1-10): ");
        int slots = Integer.parseInt(scanner.nextLine().trim());
        


        try {

            InternshipOpportunity internship = internshipController.createInternship( title, description, level, preferredMajor, openingDate, closingDate, slots );

            System.out.println("Internship created successfully! ID: " + internship.getInternshipId());
            System.out.println("Status: Pending approval from Career Center Staff");


        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    






    private void viewMyInternships(CompanyRepresentative companyRep) {
        System.out.println("\n=== My Internships ===");
        
        List<InternshipOpportunity> internships = internshipController.getInternshipsByCompanyRep(companyRep.getUserId());
        
        if (internships.isEmpty()) {
            System.out.println("You have not created any internships.");
            return;
        }
        
        for (InternshipOpportunity internship : internships) {
            System.out.println("\nID: " + internship.getInternshipId());
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Status: " + internship.getStatus());
            System.out.println("Level: " + internship.getLevel());
            System.out.println("Preferred Major: " + internship.getPreferredMajor());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Slots: " + internship.getFilledSlots() + "/" + internship.getTotalSlots());
            System.out.println("Visible: " + internship.isVisible());
        }
    }
    





    private void editInternship() {
        System.out.println("\n=== Edit Internship Opportunity ===");
        System.out.print("Internship ID: ");
        String internshipId = scanner.nextLine().trim();
        
        InternshipOpportunity internship = internshipController.getInternship(internshipId);
        if (internship == null) {
            System.out.println("Internship not found.");
            return;
        }
        


        System.out.print("Title [" + internship.getTitle() + "]: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) title = internship.getTitle();

        
        System.out.print("Description [" + internship.getDescription() + "]: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) description = internship.getDescription();
        
        
        System.out.print("Level (Basic/Intermediate/Advanced) [" + internship.getLevel() + "]: ");
        String levelStr = scanner.nextLine().trim();
        InternshipOpportunity.InternshipLevel level = internship.getLevel();


        if (!levelStr.isEmpty()) {
            try {
                level = InternshipOpportunity.InternshipLevel.valueOf(levelStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level, keeping current value.");
            }
        }
        


        System.out.print("Preferred Major [" + internship.getPreferredMajor() + "]: ");
        String preferredMajor = scanner.nextLine().trim();
        if (preferredMajor.isEmpty()) preferredMajor = internship.getPreferredMajor();


        
        System.out.print("Opening Date (yyyy-MM-dd) [" + DateUtils.formatDate(internship.getOpeningDate()) + "]: ");
        String openingDateStr = scanner.nextLine().trim();
        LocalDate openingDate = internship.getOpeningDate();
        if (!openingDateStr.isEmpty()) {
            openingDate = DateUtils.parseDate(openingDateStr);
        }
        


        System.out.print("Closing Date (yyyy-MM-dd) [" + DateUtils.formatDate(internship.getClosingDate()) + "]: ");
        String closingDateStr = scanner.nextLine().trim();
        LocalDate closingDate = internship.getClosingDate();
        if (!closingDateStr.isEmpty()) {
            closingDate = DateUtils.parseDate(closingDateStr);
        }

        
        System.out.print("Number of Slots [" + internship.getTotalSlots() + "]: ");
        String slotsStr = scanner.nextLine().trim();
        int slots = internship.getTotalSlots();
        if (!slotsStr.isEmpty()) {
            slots = Integer.parseInt(slotsStr);
        }
        

        try {
            
            internshipController.updateInternship(internshipId, title, description, level, preferredMajor, openingDate, closingDate, slots);
            
            System.out.println("Internship updated successfully!");

        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }






    
    private void deleteInternship() {
        System.out.println("\n=== Delete Internship Opportunity ===");
        System.out.print("Internship ID: ");
        String internshipId = scanner.nextLine().trim();
        
        System.out.print("Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim();


        if (!"yes".equalsIgnoreCase(confirm)) {
            return;
        }
        
        try {
            List<Application> existingApplications = applicationController.getApplicationsByInternship(internshipId);
            internshipController.deleteInternship(internshipId, existingApplications);

            System.out.println("Internship deleted successfully!");
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




    
    private void viewApplications() {
        System.out.println("\n=== View Applications ===");
        System.out.print("Internship ID: ");
        String internshipId = scanner.nextLine().trim();
        
        List<Application> applications = applicationController.getApplicationsByInternship(internshipId);
        
        if (applications.isEmpty()) {
            System.out.println("No applications for this internship.");
            return;
        }

        
        InternshipOpportunity internship = internshipController.getInternship(internshipId);
        if (internship != null) {
            System.out.println("Internship: " + internship.getTitle());
        }

        
        for (Application app : applications) {

            User student = authController.getUser(app.getStudentId());
            
            System.out.println("\nApplication ID: " + app.getApplicationId());

            if (student instanceof Student) {
                Student s = (Student) student;
                System.out.println("Student: " + s.getName() + " (" + s.getUserId() + ")");
                System.out.println("Year: " + s.getYearOfStudy() + ", Major: " + s.getMajor());
            }
            System.out.println("Status: " + app.getStatus());
        }
    }
    






    private void approveRejectApplication() {

        System.out.println("\n=== Approve/Reject Application ===");
        System.out.print("Application ID: ");
        String applicationId = scanner.nextLine().trim();
        
        System.out.print("Action (approve/reject): ");
        String action = scanner.nextLine().trim().toLowerCase();
        
        try {

            if ("approve".equals(action)) {

                applicationApprovalControllerCompanyRep.approveApplication(applicationId);
                System.out.println("Application approved!");
            } 
            
            else if ("reject".equals(action)) {
                applicationApprovalControllerCompanyRep.rejectApplication(applicationId);
                System.out.println("Application rejected!");
            } 
            
            else {
                System.out.println("Invalid action.");
            }
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    






    private void toggleVisibility() {
        System.out.println("\n=== Toggle Visibility ===");
        System.out.print("Internship ID: ");
        String internshipId = scanner.nextLine().trim();
        
        try {
            internshipController.toggleVisibility(internshipId);
            InternshipOpportunity updatedInternship = internshipController.getInternship(internshipId);

            System.out.println("Visibility toggled to: " + (updatedInternship.isVisible() ? "ON" : "OFF"));
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    


    
    

    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Current Password: ");
        String oldPassword = scanner.nextLine().trim();

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine().trim();
        
        try {

            authController.changePassword(oldPassword, newPassword);
            System.out.println("Password changed successfully!");
        } 

        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

