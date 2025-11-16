package service;

import entity.Internship;
import entity.enums.InternshipLevel;

import java.time.LocalDate;
import java.util.List;

/**
 * CompanyInternshipManagementService defines CRUD operatiosn for company rep as well as visibility toggle.
 * 
 */


public interface CompanyInternshipManagementService {

    Internship createInternship(String title,
                                String description,
                                InternshipLevel level,
                                String preferredMajor,
                                LocalDate openingDate,
                                LocalDate closingDate,
                                int totalSlots);

    void updateInternship(String internshipId,
                          String title,
                          String description,
                          InternshipLevel level,
                          String preferredMajor,
                          LocalDate openingDate,
                          LocalDate closingDate,
                          int totalSlots);

    void deleteInternship(String internshipId);

    void toggleVisibility(String internshipId);

    List<Internship> getInternshipsByCompanyRep(String companyRepId);
}


