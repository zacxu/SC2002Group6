package controller;

import entity.*;
import util.*;
import repository.*;
import service.AuthService;
import service.SessionService;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;

import java.util.List;

import java.util.stream.Collectors;


/**
 * This controller handles user registry plus authentication flows
 * 
 * It handles loads/saves users, logs in/out, password changes, company rep registration/approval
 * 
 */






 public class AuthController implements AuthService {

    private static final AuthController INSTANCE = new AuthController();

    private final SessionService sessionService = SessionController.getInstance();
    private final UserRegistry userRegistry = UserRegistry.getInstance();
    private final InternshipRegistry internshipRegistry = InternshipRegistry.getInstance();
    private final ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private final WithdrawalRegistry withdrawalRegistry = WithdrawalRegistry.getInstance();

    private boolean initialized = false;



    private AuthController() {
    }


    public static AuthController getInstance() {
        return INSTANCE;
    }


    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        userRegistry.initialize();
        internshipRegistry.initialize();
        applicationRegistry.initialize();
        withdrawalRegistry.initialize();
        initialized = true;
    }




    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise system registries", e);
            }
        }
    }






    @Override
    public User login(String userId, String password) {
        ensureInitialized();
        User user = userRegistry.authenticateUser(userId, password);
        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        if (user instanceof CompanyRepresentative && !((CompanyRepresentative) user).isApproved()) {
            throw new IllegalStateException("Your account is pending approval from Career Center Staff");
        }
        sessionService.setCurrentUser(user);
        return user;
    }





    @Override
    public void logout() {
        persistAll();
        sessionService.clearSession();
    }


    @Override
    public void changePassword(String oldPassword, String newPassword) {
        ensureInitialized();
        User currentUser = sessionService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }
        currentUser.changePassword(oldPassword, newPassword);
        userRegistry.save();
    }

   
    public CompanyRepresentative registerCompanyRep(String email,
                                                    String name,
                                                    String password,
                                                    String companyName,
                                                    String department,
                                                    String position) {




        ensureInitialized();
        if (userRegistry.getUserById(email) != null) {
            throw new IllegalArgumentException("User ID already exists");
        }


        CompanyRepresentative representative = new CompanyRepresentative(email, name, password, companyName, department, position);
        userRegistry.addUser(representative);
        userRegistry.save();

        return representative;
    }

    public List<CompanyRepresentative> getPendingCompanyReps() {

        ensureInitialized();

        return userRegistry.getAllUsers().stream()
            .filter(user -> user instanceof CompanyRepresentative)
            .map(user -> (CompanyRepresentative) user)
            .filter(rep -> !rep.isApproved())
            .collect(Collectors.toList());
    }




    public void approveCompanyRepresentative(String userId) {
        ensureInitialized();
        User user = userRegistry.getUserById(userId);

        if (!(user instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company Representative not found");
        }

        ((CompanyRepresentative) user).setApproved(true);
        userRegistry.save();
    }




    public void rejectCompanyRepresentative(String userId) {
        ensureInitialized();

        User user = userRegistry.getUserById(userId);
        if (!(user instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company Representative not found");
        }
        userRegistry.removeUser(userId);
        userRegistry.save();
    }




    public User getUser(String userId) {
        ensureInitialized();
        return userRegistry.getUserById(userId);
    }


    public Collection<User> getAllUsers() {
        ensureInitialized();
        return Collections.unmodifiableCollection(userRegistry.getAllUsers());
    }


    public void addUser(User user) {
        ensureInitialized();
        userRegistry.addUser(user);
        userRegistry.save();
    }
    

    private void persistAll() {
        try {
            userRegistry.save();
        } catch (RuntimeException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
        try {
            internshipRegistry.save();
        } catch (RuntimeException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
        try {
            applicationRegistry.save();
        } catch (RuntimeException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
        try {
            withdrawalRegistry.save();
        } catch (RuntimeException e) {
            System.err.println("Error saving withdrawals: " + e.getMessage());
        }
    }
}


