package entity;

import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;

import java.time.LocalDate;

public class Internship {
    private final String internshipId;
    private final String companyRepId;
    private final String companyName;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private int totalSlots;
    private int filledSlots;
    private boolean visible;

    public Internship(String internshipId,
                      String title,
                      String description,
                      InternshipLevel level,
                      String preferredMajor,
                      LocalDate openingDate,
                      LocalDate closingDate,
                      String companyName,
                      String companyRepId,
                      int totalSlots) {
        this.internshipId = internshipId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.companyName = companyName;
        this.companyRepId = companyRepId;
        this.totalSlots = totalSlots;
        this.filledSlots = 0;
        this.visible = true;
        this.status = InternshipStatus.Pending;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipLevel getLevel() {
        return level;
    }

    public void setLevel(InternshipLevel level) {
        this.level = level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyRepId() {
        return companyRepId;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getFilledSlots() {
        return filledSlots;
    }

    public int getAvailableSlots() {
        return Math.max(0, totalSlots - filledSlots);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void toggleVisibility() {
        this.visible = !this.visible;
    }

    public void incrementFilledSlots() {
        if (filledSlots < totalSlots) {
            filledSlots++;
        }
        if (filledSlots >= totalSlots) {
            status = InternshipStatus.Filled;
        }
    }

    public void decrementFilledSlots() {
        if (filledSlots > 0) {
            filledSlots--;
        }
        if (status == InternshipStatus.Filled && filledSlots < totalSlots) {
            status = InternshipStatus.Approved;
        }
    }

    public boolean isOpenForApplication() {
        LocalDate today = LocalDate.now();
        return status == InternshipStatus.Approved
            && visible
            && !today.isBefore(openingDate)
            && !today.isAfter(closingDate)
            && filledSlots < totalSlots;
    }

    public boolean isAfterClosingDate() {
        return LocalDate.now().isAfter(closingDate);
    }
}
