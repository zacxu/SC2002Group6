package repository;

import entity.Internship;
import entity.enums.InternshipStatus;
import util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternshipRegistry {
    private static final InternshipRegistry INSTANCE = new InternshipRegistry();

    private final Map<String, Internship> internships = new HashMap<>();
    private boolean initialized = false;
    private int nextInternshipId = 1;

    private InternshipRegistry() {
    }

    public static InternshipRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        internships.clear();
        internships.putAll(FileManager.loadInternships());
        updateNextInternshipId();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise internship registry", e);
            }
        }
    }

    private void updateNextInternshipId() {
        nextInternshipId = 1;
        for (String key : internships.keySet()) {
            if (key.startsWith("INT")) {
                try {
                    int value = Integer.parseInt(key.substring(3));
                    if (value >= nextInternshipId) {
                        nextInternshipId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveInternships(internships);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save internships", e);
        }
    }

    public String nextId() {
        ensureInitialized();
        return "INT" + String.format("%05d", nextInternshipId++);
    }

    public Internship getInternshipById(String internshipId) {
        ensureInitialized();
        return internships.get(internshipId);
    }

    public Collection<Internship> getAllInternships() {
        ensureInitialized();
        return Collections.unmodifiableCollection(internships.values());
    }

    public List<Internship> getVisibleInternships() {
        ensureInitialized();
        List<Internship> result = new ArrayList<>();
        for (Internship internship : internships.values()) {
            if (internship.isVisible() && internship.getStatus() == InternshipStatus.Approved) {
                result.add(internship);
            }
        }
        return result;
    }

    public List<Internship> getPendingInternships() {
        ensureInitialized();
        List<Internship> pending = new ArrayList<>();
        for (Internship internship : internships.values()) {
            if (internship.getStatus() == InternshipStatus.Pending) {
                pending.add(internship);
            }
        }
        return pending;
    }

    public List<Internship> getInternshipsByCompanyRep(String repId) {
        ensureInitialized();
        List<Internship> owned = new ArrayList<>();
        for (Internship internship : internships.values()) {
            if (internship.getCompanyRepId().equals(repId)) {
                owned.add(internship);
            }
        }
        return owned;
    }

    public void addInternship(Internship internship) {
        ensureInitialized();
        internships.put(internship.getInternshipId(), internship);
    }

    public void removeInternship(String internshipId) {
        ensureInitialized();
        internships.remove(internshipId);
    }
}


