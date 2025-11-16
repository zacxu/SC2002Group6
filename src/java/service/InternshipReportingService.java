package service;

import entity.FilterSettings;
import entity.Internship;
import entity.enums.InternshipStatus;

import java.util.List;

/**
 * InternshipReportingService defines methods for various reports
 * 
 * 
 */



public interface InternshipReportingService {

    List<Internship> generateReport(FilterSettings filterSettings);

    List<Internship> generateReportByStatus(InternshipStatus status);

    List<Internship> generateReportByMajor(String major);
}


