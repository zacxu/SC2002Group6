package boundary;

import controller.*;
import entity.*;
import entity.enums.InternshipStatus;
import entity.enums.InternshipLevel;

import util.Filter;

import java.util.List;
import java.util.Scanner;


/**
 * class handles the Student menu for the internship placement management system.
 * 
 * Provides a menu-driven interface for Students to interact with the system.
 * 
 * It handles the application for internships, view applications, accept placement, request withdrawal, and change password.
 * 
 * 
 * 
 */




 public class StudentMenu {
    private final Scanner scanner;
    private final Filter filter;
    private final AuthController authController;
    private final ApplicationController applicationController;
    private final InternshipController internshipController;
    private final InternshipViewController internshipViewController;
    private final WithdrawalController withdrawalController;


    public StudentMenu(Scanner scanner,
                       Filter filter,
                       AuthController authController,
                       ApplicationController applicationController,
                       InternshipController internshipController,
                       InternshipViewController internshipViewController,
                       WithdrawalController withdrawalController) {

        this.scanner = scanner;
        this.filter = filter;
        this.authController = authController;
        this.applicationController = applicationController;
        this.internshipController = internshipController;
        this.internshipViewController = internshipViewController;
        this.withdrawalController = withdrawalController;

    }

    public void showMenu(Student student) {

        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Accept Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. Filter/Sort Internships");
            System.out.println("8. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAvailableInternships(student);
                    break;
                case "2":
                    applyForInternship(student);
                    break;
                case "3":
                    viewMyApplications(student);
                    break;
                case "4":
                    acceptPlacement(student);
                    break;
                case "5":
                    requestWithdrawal(student);
                    break;
                case "6":
                    changePassword();
                    break;
                case "7":
                    configureFilter();
                    break;
                case "8":
                    authController.logout();
                    return;
                default:

                    System.out.println("Invalid choice. Please try again.");

            }
        }
    }





    private void viewAvailableInternships(Student student) {

        System.out.println("\n=== Available Internships ===");

        List<Internship> eligible = internshipViewController.getVisibleInternshipsForStudent();
        List<Internship> filtered = filter.apply(eligible);

        if (filtered.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }
        for (int i = 0; i < filtered.size(); i++) {
            Internship internship = filtered.get(i);
            System.out.println("\n" + (i + 1) + ". " + internship.getTitle());
            System.out.println("   Company: " + internship.getCompanyName());
            System.out.println("   Level: " + internship.getLevel());
            System.out.println("   Preferred Major: " + internship.getPreferredMajor());
            System.out.println("   Closing Date: " + internship.getClosingDate());
            System.out.println("   Available Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
            System.out.println("   ID: " + internship.getInternshipId());
        }
    }





    private void applyForInternship(Student student) {

        System.out.println("\n=== Apply for Internship ===");
        System.out.print("Internship ID: ");

        String internshipId = scanner.nextLine().trim();

        try {
            Application app = applicationController.applyForInternship(internshipId);
            System.out.println("Application submitted successfully! Application ID: " + app.getApplicationId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




    private void viewMyApplications(Student student) {

        System.out.println("\n=== My Applications ===");

        List<Application> applications = applicationController.getApplicationsByStudent(student.getUserId());

        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }
        for (Application app : applications) {
            Internship internship = internshipController.getInternship(app.getInternshipId());
            if (internship != null) {
                System.out.println("\nApplication ID: " + app.getApplicationId());
                System.out.println("Internship: " + internship.getTitle());
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Status: " + app.getStatus());
            }
        }
        if (student.hasAcceptedPlacement()) {

            Internship accepted = internshipController.getInternship(student.getAcceptedInternshipId());
            if (accepted != null) {
                System.out.println("\n=== Accepted Placement ===");
                System.out.println("Internship: " + accepted.getTitle());
                System.out.println("Company: " + accepted.getCompanyName());
            }
        }
    }




    private void acceptPlacement(Student student) {

        System.out.println("\n=== Accept Placement ===");

        List<Application> successfulApps = applicationController.getApplicationsByStudent(student.getUserId())
            .stream()
            .filter(app -> app.getStatus() == entity.enums.ApplicationStatus.Successful)
            .collect(java.util.stream.Collectors.toList());

        if (successfulApps.isEmpty()) {
            System.out.println("You have no successful applications.");
            return;
        }
        System.out.println("Successful Applications:");

        for (int i = 0; i < successfulApps.size(); i++) {
            Application app = successfulApps.get(i);
            Internship internship = internshipController.getInternship(app.getInternshipId());
            if (internship != null) {
                System.out.println((i + 1) + ". " + internship.getTitle() +
                                 " (Application ID: " + app.getApplicationId() + ")");
            }
        }
        System.out.print("Application ID to accept: ");

        String applicationId = scanner.nextLine().trim();

        try {
            applicationController.acceptPlacement(applicationId);
            System.out.println("Placement accepted successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




    private void requestWithdrawal(Student student) {

        System.out.println("\n=== Request Withdrawal ===");

        List<Application> applications = applicationController.getApplicationsByStudent(student.getUserId());
        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }
        System.out.println("Your Applications:");
        for (Application app : applications) {
            Internship internship = internshipController.getInternship(app.getInternshipId());
            if (internship != null) {
                System.out.println("Application ID: " + app.getApplicationId());
                System.out.println("  Internship: " + internship.getTitle());
                System.out.println("  Status: " + app.getStatus());
            }
        }
        System.out.print("Application ID to withdraw: ");
        String applicationId = scanner.nextLine().trim();

        System.out.print("Reason (optional): ");

        String reason = scanner.nextLine().trim();
        try {
            WithdrawalRequest request = withdrawalController.requestWithdrawal(applicationId, reason);
            System.out.println("Withdrawal request submitted. Request ID: " + request.getRequestId());
        } catch (Exception e) {
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void configureFilter() {
        System.out.println("\n=== Filter/Sort Internships ===");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Major");
        System.out.println("3. Filter by Level");
        System.out.println("4. Sort Options");
        System.out.println("5. Reset Filters");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                filterByStatus();
                break;
            case "2":
                filterByMajor();
                break;
            case "3":
                filterByLevel();
                break;
            case "4":
                setSortOption();
                break;
            case "5":
                filter.reset();
                System.out.println("Filters reset.");
                break;

        }

    }

    private void filterByStatus() {
        System.out.println("Status: 1. Approved  2. Pending  3. Rejected  4. Filled  5. Clear");
        String choice = scanner.nextLine().trim();
        switch (choice) {

            case "1":
                filter.setStatusFilter(InternshipStatus.Approved);
                break;
            case "2":
                filter.setStatusFilter(InternshipStatus.Pending);
                break;
            case "3":
                filter.setStatusFilter(InternshipStatus.Rejected);
                break;
            case "4":
                filter.setStatusFilter(InternshipStatus.Filled);
                break;
            case "5":
                filter.setStatusFilter(null);
                break;
        }
    }



    private void filterByMajor() {
        System.out.print("Enter major (or 'clear' to remove): ");
        String major = scanner.nextLine().trim();
        if ("clear".equalsIgnoreCase(major)) {
            filter.setMajorFilter(null);
        } else {
            filter.setMajorFilter(major);
        }
    }



    private void filterByLevel() {
        System.out.println("Level: 1. Basic  2. Intermediate  3. Advanced  4. Clear");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                filter.setLevelFilter(InternshipLevel.Basic);
                break;
            case "2":
                filter.setLevelFilter(InternshipLevel.Intermediate);
                break;
            case "3":
                filter.setLevelFilter(InternshipLevel.Advanced);
                break;
            case "4":
                filter.setLevelFilter(null);
                break;
        }
    }



    
    private void setSortOption() {
        System.out.println("Sort by: 1. Alphabetical  2. Closing Date  3. Level");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                filter.setSortBy("alphabetical");
                break;
            case "2":
                filter.setSortBy("closingDate");
                break;
            case "3":
                filter.setSortBy("level");
                break;
        }
    }
}

