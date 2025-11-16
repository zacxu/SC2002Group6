package controller;

import entity.Internship;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
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

    public List<Internship> generateReport(Filter filter) {
        List<Internship> all = new ArrayList<>(internshipController.getAllInternships());
        if (filter == null) {
            filter = new Filter();
        }
        return filter.apply(all);
    }

    public List<Internship> generateReportByStatus(InternshipStatus status) {
        Filter filter = new Filter();
        filter.setStatusFilter(status);
        return generateReport(filter);
    }

    public List<Internship> generateReportByMajor(String major) {
        Filter filter = new Filter();
        filter.setMajorFilter(major);
        return generateReport(filter);
    }

    public List<Internship> generateReportByLevel(InternshipLevel level) {
        Filter filter = new Filter();
        filter.setLevelFilter(level);
        return generateReport(filter);
    }
}


