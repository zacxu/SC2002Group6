package repository;

import entity.Application;
import util.FileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Manages application persistence and retrieval for applicaitons 
 * 
 * handles application submissions, lookups, and persistence
 * 
 * 
 */



 public class ApplicationRegistry {
    private static final ApplicationRegistry INSTANCE = new ApplicationRegistry();

    private final Map<String, Application> applications = new HashMap<>();
    private boolean initialized = false;
    private int nextApplicationId = 1;

    private ApplicationRegistry() {
    }

    public static ApplicationRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        applications.clear();
        applications.putAll(FileManager.loadApplications());
        updateNextApplicationId();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise application registry", e);
            }
        }
    }

    private void updateNextApplicationId() {
        nextApplicationId = 1;
        for (String key : applications.keySet()) {
            if (key.startsWith("APP")) {
                try {
                    int value = Integer.parseInt(key.substring(3));
                    if (value >= nextApplicationId) {
                        nextApplicationId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveApplications(applications);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save applications", e);
        }
    }

    public String nextId() {
        ensureInitialized();
        return "APP" + String.format("%05d", nextApplicationId++);
    }

    public Application newApplication(String studentId, String internshipId) {
        return new Application(nextId(), studentId, internshipId, LocalDate.now());
    }

    public void addApplication(Application application) {
        ensureInitialized();
        applications.put(application.getApplicationId(), application);
    }

    public void removeApplication(String applicationId) {
        ensureInitialized();
        applications.remove(applicationId);
    }

    public Application getApplicationById(String applicationId) {
        ensureInitialized();
        return applications.get(applicationId);
    }

    public Collection<Application> getAllApplications() {
        ensureInitialized();
        return Collections.unmodifiableCollection(applications.values());
    }

    public List<Application> getApplicationsByStudent(String studentId) {
        ensureInitialized();
        List<Application> list = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getStudentId().equals(studentId)) {
                list.add(application);
            }
        }
        return list;
    }

    public List<Application> getApplicationsByInternship(String internshipId) {
        ensureInitialized();
        List<Application> list = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getInternshipId().equals(internshipId)) {
                list.add(application);
            }
        }
        return list;
    }
}


