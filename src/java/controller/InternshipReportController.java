package controller;

import entity.InternshipOpportunity;
import util.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides reporting capabilities over internship data.
 * 
 * reporting utilities over internships, apply filters for status, major, or level
 */




public class InternshipReportController {
    
    private final InternshipController internshipController = InternshipController.getInstance();

    public List<InternshipOpportunity> generateReport(Filter filter) {
        List<InternshipOpportunity> all = new ArrayList<>(internshipController.getAllInternships());
        
        if (filter == null) {
            filter = new Filter();
        }

        return filter.apply(all);
    }



    public List<InternshipOpportunity> generateReportByStatus(InternshipOpportunity.InternshipStatus status) {
        Filter filter = new Filter();
        filter.setStatusFilter(status);
        return generateReport(filter);
    }



    public List<InternshipOpportunity> generateReportByMajor(String major) {
        Filter filter = new Filter();
        filter.setMajorFilter(major);
        return generateReport(filter);
    }



    public List<InternshipOpportunity> generateReportByLevel(InternshipOpportunity.InternshipLevel level) {
        Filter filter = new Filter();
        filter.setLevelFilter(level);
        return generateReport(filter);
    }


}


