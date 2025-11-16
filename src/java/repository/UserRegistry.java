package repository;

import entity.User;
import util.FileManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserRegistry {
    private static final UserRegistry INSTANCE = new UserRegistry();

    private final Map<String, User> users = new HashMap<>();
    private boolean initialized = false;

    private UserRegistry() {
    }

    public static UserRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        users.clear();
        users.putAll(FileManager.loadUsers());
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise user registry", e);
            }
        }
    }

    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveUsers(users);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save users", e);
        }
    }

    public User getUserById(String userId) {
        ensureInitialized();
        return users.get(userId);
    }

    public User authenticateUser(String userId, String password) {
        ensureInitialized();
        User user = users.get(userId);
        if (user == null || !user.verifyPassword(password)) {
            return null;
        }
        return user;
    }

    public void addUser(User user) {
        ensureInitialized();
        users.put(user.getUserId(), user);
    }

    public void removeUser(String userId) {
        ensureInitialized();
        users.remove(userId);
    }

    public void removeUser(String userId) {
        ensureInitialized();
        users.remove(userId);
    }

    public Collection<User> getAllUsers() {
        ensureInitialized();
        return Collections.unmodifiableCollection(users.values());
    }
}

