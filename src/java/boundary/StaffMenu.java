package boundary;

import controller.*;
import entity.*;

import util.Filter;

import java.util.List;
import java.util.Scanner;


/**
 * class handles the Staff menu for the internship placement management system.
 * 
 * Provides a menu-driven interface for Staff to interact with the system.
 * 
 * It handles the approval of company representatives, internship opportunities, and withdrawal requests.
 * 
 * 
 * 
 */




public class StaffMenu {

    private final Scanner scanner;
    private final Filter filter;
    private final AuthController authController;
    private final InternshipApprovalControllerStaff internshipApprovalControllerStaff;
    private final WithdrawalApprovalControllerStaff withdrawalApprovalControllerStaff;
    private final InternshipReportController internshipReportController;
    private final WithdrawalReportController withdrawalReportController;
    private final InternshipController internshipController;


    
    public StaffMenu(Scanner scanner,
                     Filter filter,
                     AuthController authController,
                     InternshipApprovalControllerStaff internshipApprovalControllerStaff,
                     WithdrawalApprovalControllerStaff withdrawalApprovalControllerStaff,
                     InternshipReportController internshipReportController,
                     WithdrawalReportController withdrawalReportController,
                     InternshipController internshipController) {



        this.scanner = scanner;
        this.filter = filter;
        this.authController = authController;
        this.internshipApprovalControllerStaff = internshipApprovalControllerStaff;
        this.withdrawalApprovalControllerStaff = withdrawalApprovalControllerStaff;
        this.internshipReportController = internshipReportController;
        this.withdrawalReportController = withdrawalReportController;
        this.internshipController = internshipController;

    }
    





    public void showMenu(CareerCenterStaff staff) {
        while (true) {
            System.out.println("\n=== Career Center Staff Menu ===");
            System.out.println("1. Approve/Reject Company Representative");
            System.out.println("2. Approve/Reject Internship Opportunity");
            System.out.println("3. Handle Withdrawal Requests");
            System.out.println("4. Generate Reports");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");

            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    approveRejectCompanyRep();
                    break;

                case "2":
                    approveRejectInternship();
                    break;

                case "3":
                    handleWithdrawals();
                    break;

                case "4":
                    generateReports();
                    break;

                case "5":
                    changePassword();
                    break;

                case "6":
                    authController.logout();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");


            }
        }
    }
    




    


    private void approveRejectCompanyRep() {

        System.out.println("\n=== Approve/Reject Company Representative ===");
        
        List<CompanyRepresentative> pending = authController.getPendingCompanyReps();
        
        if (pending.isEmpty()) {
            System.out.println("No pending company representative registrations.");
            return;
        }
        


        System.out.println("Pending Registrations:");
        for (int i = 0; i < pending.size(); i++) {
            CompanyRepresentative cr = pending.get(i);
            System.out.println((i + 1) + ". " + cr.getName() + " (" + cr.getUserId() + ")");
            System.out.println("   Company: " + cr.getCompanyName());
            System.out.println("   Department: " + cr.getDepartment());
            System.out.println("   Position: " + cr.getPosition());
        }


        
        System.out.print("Company Rep User ID: ");
        String userId = scanner.nextLine().trim();
        
        User user = authController.getUser(userId);

        if (!(user instanceof CompanyRepresentative)) {
            
            System.out.println("Company Representative not found.");
            return;


        }
        


        System.out.print("Action (approve/reject): ");
        String action = scanner.nextLine().trim().toLowerCase();

        
        if ("approve".equals(action)) {
            authController.approveCompanyRepresentative(userId);
            System.out.println("Company Representative approved!");
        } 
        
        else if ("reject".equals(action)) {
            authController.rejectCompanyRepresentative(userId);
            System.out.println("Company Representative registration rejected and removed.");
        } 
        
        else {
            System.out.println("Invalid action.");
            return;
        }
    }
    






    private void approveRejectInternship() {
        
        System.out.println("\n=== Approve/Reject Internship Opportunity ===");
        
        List<InternshipOpportunity> pending = internshipApprovalControllerStaff.getPendingInternships();
        
        if (pending.isEmpty()) {
            System.out.println("No pending internship opportunities.");
            return;
        }
        

        System.out.println("Pending Internships:");
        for (int i = 0; i < pending.size(); i++) {
            InternshipOpportunity internship = pending.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Company: " + internship.getCompanyName());
            System.out.println("   Level: " + internship.getLevel());
            System.out.println("   ID: " + internship.getInternshipId());
        }
        

        System.out.print("Internship ID: ");
        String internshipId = scanner.nextLine().trim();
        
        System.out.print("Action (approve/reject): ");
        String action = scanner.nextLine().trim().toLowerCase();
        

        try {
            if ("approve".equals(action)) {
                internshipApprovalControllerStaff.approveInternship(internshipId);
                System.out.println("Internship approved!");
            } 
            
            else if ("reject".equals(action)) {
                internshipApprovalControllerStaff.rejectInternship(internshipId);
                System.out.println("Internship rejected!");
            } 
            
            else {
                System.out.println("Invalid action.");
            }
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    






    private void handleWithdrawals() {
        System.out.println("\n=== Handle Withdrawal Requests ===");
        
        List<WithdrawalRequest> pending = withdrawalReportController.getPendingWithdrawalRequests();
        
        if (pending.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }
        

        System.out.println("Pending Withdrawal Requests:");

        for (int i = 0; i < pending.size(); i++) {
            WithdrawalRequest request = pending.get(i);
            InternshipOpportunity internship = internshipController.getInternship(request.getInternshipId());
            User student = authController.getUser(request.getStudentId());
            

            System.out.println((i + 1) + ". Request ID: " + request.getRequestId());

            if (student != null) {
                System.out.println("   Student: " + student.getName() + " (" + student.getUserId() + ")");
            }

            if (internship != null) {
                System.out.println("   Internship: " + internship.getTitle());
            }


            System.out.println("   After Placement: " + request.isAfterPlacement());
        }
        
        System.out.print("Request ID: ");
        String requestId = scanner.nextLine().trim();
        
        System.out.print("Action (approve/reject): ");
        String action = scanner.nextLine().trim().toLowerCase();
        


        try {
            if ("approve".equals(action)) {
                withdrawalApprovalControllerStaff.approveWithdrawal(requestId);
                System.out.println("Withdrawal approved!");
            } 
            
            else if ("reject".equals(action)) {
                withdrawalApprovalControllerStaff.rejectWithdrawal(requestId);
                System.out.println("Withdrawal rejected!");
            } 
            
            else {
                System.out.println("Invalid action.");
            }
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    


    



    private void generateReports() {
        System.out.println("\n=== Generate Reports ===");
        System.out.println("1. All Internships");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Major");
        System.out.println("4. Filter by Level");
        System.out.println("5. Custom Filter");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        List<InternshipOpportunity> results;


        
        switch (choice) {
            case "1":
                results = internshipReportController.generateReport(filter);
                break;


            case "2":
                System.out.print("Status (Pending/Approved/Rejected/Filled): ");
                String statusStr = scanner.nextLine().trim();
                try {
                    InternshipOpportunity.InternshipStatus status = InternshipOpportunity.InternshipStatus.valueOf(statusStr);
                    results = internshipReportController.generateReportByStatus(status);
                } 
                
                catch (IllegalArgumentException e) {
                    System.out.println("Invalid status.");
                    return;
                }
                break;


            case "3":
                System.out.print("Major: ");
                String major = scanner.nextLine().trim();
                results = internshipReportController.generateReportByMajor(major);
                break;


            case "4":
                System.out.print("Level (Basic/Intermediate/Advanced): ");
                String levelStr = scanner.nextLine().trim();

                try {
                    InternshipOpportunity.InternshipLevel level = 
                        InternshipOpportunity.InternshipLevel.valueOf(levelStr);
                    results = internshipReportController.generateReportByLevel(level);
                } 
                
                catch (IllegalArgumentException e) {
                    System.out.println("Invalid level.");
                    return;
                }

                break;


            case "5":
                configureFilter();
                results = internshipReportController.generateReport(filter);
                break;


            default:
                System.out.println("Invalid choice.");
                return;


        }
        
        System.out.println("\n=== Report Results ===");
        if (results.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }
        

        for (InternshipOpportunity internship : results) {

            System.out.println("\nTitle: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Level: " + internship.getLevel());
            System.out.println("Status: " + internship.getStatus());
            System.out.println("Preferred Major: " + internship.getPreferredMajor());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Slots: " + internship.getFilledSlots() + "/" + internship.getTotalSlots());


        }
    }
    






    private void configureFilter() {
        System.out.println("\n=== Configure Filter ===");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Major");
        System.out.println("3. Filter by Level");
        System.out.println("4. Sort Options");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        

        switch (choice) {
            case "1":
                System.out.print("Status (Pending/Approved/Rejected/Filled or 'clear'): ");
                String statusStr = scanner.nextLine().trim();

                if ("clear".equalsIgnoreCase(statusStr)) {
                    filter.setStatusFilter(null);
                } 
                
                else {
                    try {
                        filter.setStatusFilter(InternshipOpportunity.InternshipStatus.valueOf(statusStr));
                    } 
                    
                    catch (IllegalArgumentException e) {
                        System.out.println("Invalid status.");
                    }
                }
                break;


            case "2":
                System.out.print("Major (or 'clear' to remove): ");
                String major = scanner.nextLine().trim();

                if ("clear".equalsIgnoreCase(major)) {
                    filter.setMajorFilter(null);
                } 
                
                else {
                    filter.setMajorFilter(major);
                }

                break;


            case "3":
                System.out.print("Level (Basic/Intermediate/Advanced or 'clear'): ");
                String levelStr = scanner.nextLine().trim();

                if ("clear".equalsIgnoreCase(levelStr)) {
                    filter.setLevelFilter(null);
                } 
                
                else {
                    try {
                        filter.setLevelFilter(InternshipOpportunity.InternshipLevel.valueOf(levelStr));
                    } 
                    
                    catch (IllegalArgumentException e) {
                        System.out.println("Invalid level.");
                    }
                }

                break;


            case "4":
                System.out.print("Sort by (alphabetical/closingDate/level): ");
                String sortBy = scanner.nextLine().trim();
                filter.setSortBy(sortBy);
                break;


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

