package util;

import entity.InternshipOpportunity;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * class handles the filter for the internship placement management system.
 * 
 * Provides a filter for the internship placement management system.
 * 
 * It handles the filter for the internship placement management system.
 * 
 * 
 * 
 */




public class Filter {
    private InternshipOpportunity.InternshipStatus statusFilter;
    private String majorFilter;
    private InternshipOpportunity.InternshipLevel levelFilter;
    private LocalDate closingDateFilter;
    private String sortBy; 


    
    public Filter() {
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.closingDateFilter = null;
        this.sortBy = "alphabetical";
    }


    
    public Filter(Filter other) {
        this.statusFilter = other.statusFilter;
        this.majorFilter = other.majorFilter;
        this.levelFilter = other.levelFilter;
        this.closingDateFilter = other.closingDateFilter;
        this.sortBy = other.sortBy;
    }


    
    public List<InternshipOpportunity> apply(List<InternshipOpportunity> internships) {
        List<InternshipOpportunity> filtered = new ArrayList<>(internships);
        
        if (statusFilter != null) {
            filtered = filtered.stream()
                .filter(i -> i.getStatus() == statusFilter)
                .collect(Collectors.toList());
        }
        
        if (majorFilter != null && !majorFilter.isEmpty()) {
            filtered = filtered.stream()
                .filter(i -> i.getPreferredMajor().equalsIgnoreCase(majorFilter))
                .collect(Collectors.toList());
        }
        
        if (levelFilter != null) {
            filtered = filtered.stream()
                .filter(i -> i.getLevel() == levelFilter)
                .collect(Collectors.toList());
        }
        
        if (closingDateFilter != null) {
            filtered = filtered.stream()
                .filter(i -> !i.getClosingDate().isAfter(closingDateFilter))
                .collect(Collectors.toList());
        }
        
        


        if ("alphabetical".equals(sortBy)) {
            filtered.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        } else if ("closingDate".equals(sortBy)) {
            filtered.sort((a, b) -> a.getClosingDate().compareTo(b.getClosingDate()));
        } else if ("level".equals(sortBy)) {
            filtered.sort((a, b) -> a.getLevel().compareTo(b.getLevel()));
        }
        
        return filtered;
    }



    
    public InternshipOpportunity.InternshipStatus getStatusFilter() {
        return statusFilter;
    }
    
    public void setStatusFilter(InternshipOpportunity.InternshipStatus statusFilter) {
        this.statusFilter = statusFilter;
    }
    
    public String getMajorFilter() {
        return majorFilter;
    }
    
    public void setMajorFilter(String majorFilter) {
        this.majorFilter = majorFilter;
    }
    
    public InternshipOpportunity.InternshipLevel getLevelFilter() {
        return levelFilter;
    }
    
    public void setLevelFilter(InternshipOpportunity.InternshipLevel levelFilter) {
        this.levelFilter = levelFilter;
    }
    
    public LocalDate getClosingDateFilter() {
        return closingDateFilter;
    }
    
    public void setClosingDateFilter(LocalDate closingDateFilter) {
        this.closingDateFilter = closingDateFilter;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public void reset() {
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.closingDateFilter = null;
        this.sortBy = "alphabetical";

        
    }
}

