package service;

import entity.FilterSettings;
import entity.Internship;

import java.util.List;

/**
 * StudentInternshipQueryService defines read only methods for students to browse internships,
 * view details
 * 
 */


public interface StudentInternshipQueryService {

    List<Internship> getVisibleInternshipsForStudent();

    Internship getInternshipDetails(String internshipId);

    List<Internship> applyFilters(FilterSettings filterSettings);
}


