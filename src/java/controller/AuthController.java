package controller;

import entity.*;
import util.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This controller handles user registry plus authentication flows
 * 
 * It handles loads/saves users, logs in/out, password changes, company rep registration/approval
 * 
 */





public class AuthController {

    private static final AuthController INSTANCE = new AuthController();

    private final Map<String, User> users = new HashMap<>();
    private final SessionController sessionController = SessionController.getInstance();

    private boolean initialized = false;



    private AuthController() {
    }

    public static AuthController getInstance() {
        return INSTANCE;
    }


    public synchronized void initialize()  {
        if (initialized) {
            return;
        }
        loadUsers();
        initialized = true;
    }



    private void loadUsers()  {

        users.clear();
        try {
            users.putAll(FileManager.loadUsers());

       
            List<Student> students = FileManager.loadStudents();
            List<CareerCenterStaff> staff = FileManager.loadStaff();


            for (Student student : students) {
                users.put(student.getUserId(), student);
            }


            for (CareerCenterStaff staffMember : staff) {
                users.put(staffMember.getUserId(), staffMember);
            }


            saveUsers();


        } catch (IOException e) {
            throw new RuntimeException("Failed to load users", e);
        }
        
            
    }



    
    private void ensureInitialized() {
        if (!initialized) {
            
            initialize();
            
            
        }
    }




    public synchronized void saveUsers() {
        
        try {
            FileManager.saveUsers(users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users", e);
        }
        
    }



    

    public User login(String userId, String password) {
        ensureInitialized();

        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User ID not found");
        }

        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative companyRepresentative = (CompanyRepresentative) user;
            if (!companyRepresentative.isApproved()) {
                throw new IllegalStateException("Your account is pending approval from Career Center Staff");
            }
        }

        if (!user.verifyPassword(password)) {
            throw new IllegalArgumentException("Incorrect password");
        }

        sessionController.setCurrentUser(user);
        return user;
    }



    public void logout() {
        persistAll();
        sessionController.logout();
    }



    public void changePassword(String oldPassword, String newPassword) {
        ensureInitialized();

        User currentUser = sessionController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }

        currentUser.changePassword(oldPassword, newPassword);
        saveUsers();
    }



    public CompanyRepresentative registerCompanyRep(String email,
                                                    String name,
                                                    String password,
                                                    String companyName,
                                                    String department,
                                                    String position) {

        ensureInitialized();

        

        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User ID already exists");
        }

        CompanyRepresentative companyRepresentative = new CompanyRepresentative(email, name, password, companyName, department, position);
        
        users.put(companyRepresentative.getUserId(), companyRepresentative);

        saveUsers();
        
        return companyRepresentative;

    }





    public User getUser(String userId) {
        ensureInitialized();
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        ensureInitialized();
        return Collections.unmodifiableCollection(users.values());
    }

    public Map<String, User> getUsers() {
        ensureInitialized();
        return Collections.unmodifiableMap(users);
    }

    public void addUser(User user) {
        ensureInitialized();
        users.put(user.getUserId(), user);
    }




    public List<CompanyRepresentative> getPendingCompanyReps() {
        ensureInitialized();
        List<CompanyRepresentative> pending = new ArrayList<>();

        for (User user : users.values()) {

            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative companyRepresentative = (CompanyRepresentative) user;
                if (!companyRepresentative.isApproved()) {
                    pending.add(companyRepresentative);
                }
            }
        }
        return pending;
    }




    public void approveCompanyRepresentative(String userId) {
        ensureInitialized();
        User user = users.get(userId);

        if (!(user instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company Representative not found");
        }

        CompanyRepresentative representative = (CompanyRepresentative) user;
        representative.setApproved(true);
        saveUsers();
    }



    public void rejectCompanyRepresentative(String userId) {
        ensureInitialized();
        User removed = users.remove(userId);

        if (removed == null || !(removed instanceof CompanyRepresentative)) {
            throw new IllegalArgumentException("Company Representative not found");
        }
        saveUsers();
    }



    private void persistAll() {

        try {
            saveUsers();

        } catch (RuntimeException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }


        try {
            InternshipController.getInstance().save();

        } catch (RuntimeException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }


        try {
            ApplicationController.getInstance().save();

        } catch (RuntimeException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
        

        try {
            WithdrawalController.getInstance().save();

        } catch (RuntimeException e) {
            System.err.println("Error saving withdrawal requests: " + e.getMessage());
        }
    }
}


