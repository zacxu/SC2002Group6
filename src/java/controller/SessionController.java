package controller;

import entity.User;

/**
 * stores and clears the currently authenticated user for the CLI session.
 */



public class SessionController {


    private static final SessionController INSTANCE = new SessionController();

    private User currentUser;



    private SessionController() {
    }

    public static SessionController getInstance() {
        return INSTANCE;
    }



    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }




    public void logout() {
        this.currentUser = null;
    }

}


