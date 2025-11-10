package boundary;

import controller.*;
import entity.*;

import util.Filter;

import java.io.IOException;
import java.util.Scanner;




/**
 * class handles the CLI interface for the internship placement management system.
 * 
 * Provides a menu-driven interface for users to interact with the system.
 * 
 * It also handles the login/registration flow, role-based menus
 * 
 * 
 * 
 */




public class CLI {


    private final Scanner scanner;


    private final SessionController sessionController;
    private final AuthController authController;
    private final InternshipController internshipController;
    private final ApplicationController applicationController;
    private final WithdrawalController withdrawalController;

    private final InternshipViewController internshipViewController;
    private final InternshipApprovalControllerStaff internshipApprovalControllerStaff;
    private final ApplicationApprovalControllerCompanyRep applicationApprovalControllerCompanyRep;
    private final WithdrawalApprovalControllerStaff withdrawalApprovalControllerStaff;
    private final InternshipReportController internshipReportController;
    private final WithdrawalReportController withdrawalReportController;



    private final Filter currentFilter;



    public CLI() {
        this.scanner = new Scanner(System.in);
        this.sessionController = SessionController.getInstance();
        this.authController = AuthController.getInstance();
        this.internshipController = InternshipController.getInstance();
        this.applicationController = ApplicationController.getInstance();
        this.withdrawalController = WithdrawalController.getInstance();

        this.internshipViewController = new InternshipViewController();
        this.internshipApprovalControllerStaff = new InternshipApprovalControllerStaff();
        this.applicationApprovalControllerCompanyRep = new ApplicationApprovalControllerCompanyRep();
        this.withdrawalApprovalControllerStaff = new WithdrawalApprovalControllerStaff();
        this.internshipReportController = new InternshipReportController();
        this.withdrawalReportController = new WithdrawalReportController();

        this.currentFilter = new Filter();
    }






    public void start() {
        System.out.println("\n\n=== Welcome to Internship Placement Management System ===");
        System.out.println();



        try {
            authController.initialize();
            internshipController.initialize();
            applicationController.initialize();
            withdrawalController.initialize();


        } catch (IOException e) {
            System.err.println("Error initializing system: " + e.getMessage());
            System.exit(1);
        }


        while (true) {
            if (sessionController.getCurrentUser() == null) {
                showLoginMenu();
            } else {
                showRoleMenu();
            }
        }

        
    }






    private void showLoginMenu() {
        System.out.println("=== Login ===");
        System.out.println("1. Login");
        System.out.println("2. Register as Company Representative");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {

            case "1":
                System.out.print("User ID: ");
                String userId = scanner.nextLine().trim();


                System.out.print("Password: ");
                String password = scanner.nextLine().trim();


                try {
                    User user = authController.login(userId, password);

                    System.out.println("\nLogin successful! Welcome, " + user.getName() + " (" + user.getUserRole() + ")");

                } 


                catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("\nError: " + e.getMessage());
                }

                break;


            case "2":
                registerCompanyRep();
                break;


            default:
                System.out.println("\nInvalid choice. Please try again.");


        }
    }


    




    private void registerCompanyRep() {

        System.out.println("\n=== Register as Company Representative ===");
        System.out.print("Email (User ID): ");

        String email = scanner.nextLine().trim();


        System.out.print("Name: ");
        String name = scanner.nextLine().trim();


        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        

        System.out.print("Company Name: ");
        String companyName = scanner.nextLine().trim();


        System.out.print("Department: ");
        String department = scanner.nextLine().trim();


        System.out.print("Position: ");
        String position = scanner.nextLine().trim();


        try {
            authController.registerCompanyRep(email, name, password, companyName, department, position);

            System.out.println("\nRegistration successful! Your account is pending approval from Career Center Staff.");
            System.out.println("You will be able to login once your account is approved.");


        } 
        catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }









    private void showRoleMenu() {
        User currentUser = sessionController.getCurrentUser();

        if (currentUser instanceof Student) {
            StudentMenu studentMenu = new StudentMenu(
                scanner,
                currentFilter,
                authController,
                applicationController,
                internshipController,
                internshipViewController,
                withdrawalController
            );

            studentMenu.showMenu((Student) currentUser);



        } else if (currentUser instanceof CompanyRepresentative) {
            CompanyRepMenu companyRepMenu = new CompanyRepMenu(
                scanner,
                authController,
                internshipController,
                applicationController,
                applicationApprovalControllerCompanyRep
            );

            companyRepMenu.showMenu((CompanyRepresentative) currentUser);



        } else if (currentUser instanceof CareerCenterStaff) {
            StaffMenu staffMenu = new StaffMenu(
                scanner,
                currentFilter,
                authController,
                internshipApprovalControllerStaff,
                withdrawalApprovalControllerStaff,
                internshipReportController,
                withdrawalReportController,
                internshipController
            );

            staffMenu.showMenu((CareerCenterStaff) currentUser);



        }
    }




    
    public Filter getCurrentFilter() {
        return currentFilter;
    }

    
}

