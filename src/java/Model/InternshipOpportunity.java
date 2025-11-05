package model;

import java.time.LocalDate;

public class InternshipOpportunity {
    private String internshipId;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private String companyName;
    private String companyRepId;
    private int totalSlots;
    private int filledSlots;
    private boolean visible;
    
    public enum InternshipLevel {
        Basic, Intermediate, Advanced
    }
    
    public enum InternshipStatus {
        Pending, Approved, Rejected, Filled
    }
    
    public InternshipOpportunity(String internshipId, String title, String description,
                                  InternshipLevel level, String preferredMajor,
                                  LocalDate openingDate, LocalDate closingDate,
                                  String companyName, String companyRepId, int totalSlots) {
        this.internshipId = internshipId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = InternshipStatus.Pending;
        this.companyName = companyName;
        this.companyRepId = companyRepId;
        this.totalSlots = totalSlots;
        this.filledSlots = 0;
        this.visible = true;
    }
    
    public String getInternshipId() {
        return internshipId;
    }
    
    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
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
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getCompanyRepId() {
        return companyRepId;
    }
    
    public void setCompanyRepId(String companyRepId) {
        this.companyRepId = companyRepId;
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
    
    public void setFilledSlots(int filledSlots) {
        this.filledSlots = filledSlots;
        if (this.filledSlots >= this.totalSlots) {
            this.status = InternshipStatus.Filled;
        }
    }
    
    public int getAvailableSlots() {
        return totalSlots - filledSlots;
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
    
    public boolean isOpenForApplication() {
        LocalDate today = LocalDate.now();
        return status == InternshipStatus.Approved 
            && visible 
            && today.isAfter(openingDate.minusDays(1))
            && today.isBefore(closingDate.plusDays(1))
            && filledSlots < totalSlots;
    }
    
    public boolean isAfterClosingDate() {
        return LocalDate.now().isAfter(closingDate);
    }
}

