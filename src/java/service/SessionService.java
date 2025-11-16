package service;

import entity.User;

/**
 * SessionService defines methods for session management
 * 
 * 
 */


public interface SessionService {
    User getCurrentUser();

    void setCurrentUser(User user);

    boolean isLoggedIn();

    void clearSession();
}


