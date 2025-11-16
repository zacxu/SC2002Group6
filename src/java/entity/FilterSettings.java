package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilterSettings {
    private final List<String> statusIn = new ArrayList<>();
    private final List<String> preferredMajorsIn = new ArrayList<>();
    private final List<String> levelsIn = new ArrayList<>();
    private LocalDate closingDateBefore;
    private boolean alphabeticalOrder = true;

    public List<String> getStatusIn() {
        return new ArrayList<>(statusIn);
    }

    public List<String> getPreferredMajorsIn() {
        return new ArrayList<>(preferredMajorsIn);
    }

    public List<String> getLevelsIn() {
        return new ArrayList<>(levelsIn);
    }

    public LocalDate getClosingDateBefore() {
        return closingDateBefore;
    }

    public boolean isAlphabeticalOrder() {
        return alphabeticalOrder;
    }

    public void setStatusFilters(List<String> statuses) {
        statusIn.clear();
        if (statuses != null) {
            statusIn.addAll(statuses);
        }
    }

    public void setMajorFilters(List<String> majors) {
        preferredMajorsIn.clear();
        if (majors != null) {
            preferredMajorsIn.addAll(majors);
        }
    }

    public void setLevelFilters(List<String> levels) {
        levelsIn.clear();
        if (levels != null) {
            levelsIn.addAll(levels);
        }
    }

    public void setClosingDateBefore(LocalDate date) {
        this.closingDateBefore = date;
    }

    public void setAlphabeticalOrder(boolean alphabeticalOrder) {
        this.alphabeticalOrder = alphabeticalOrder;
    }

    public void clearFilters() {
        statusIn.clear();
        preferredMajorsIn.clear();
        levelsIn.clear();
        closingDateBefore = null;
        alphabeticalOrder = true;
    }
}


