package controller;

import entity.User;
import service.SessionService;

/**
 * stores and clears the currently authenticated user for the CLI session.
 */



 public class SessionController implements SessionService {
    private static final SessionController INSTANCE = new SessionController();

    private User currentUser;


    private SessionController() {
    }
    

    public static SessionController getInstance() { return INSTANCE; }

    @Override
    public User getCurrentUser() { return currentUser; }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    @Override
    public void clearSession() {
        currentUser = null;
    }

    public void logout() {
        clearSession();
    }
}


