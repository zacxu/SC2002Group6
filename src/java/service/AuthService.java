package service;

import entity.User;

/**
 *  Class  defines authentication methoeds for the CLI
 * - login/logout session handling
 * - change password for the currently logged-in user
 */


public interface AuthService {

    User login(String userId, String password);

    void logout();

    void changePassword(String oldPassword, String newPassword);
}


