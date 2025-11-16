package validator;

import entity.Application;
import entity.Internship;
import entity.Student;
import entity.enums.ApplicationStatus;

import util.SystemConstants;

import java.time.LocalDate;


/**
 * ApplicationValidator class contains methods to validate application eligibility and acceptance.
 * 
 *  methods to validate the year restriction, the application limit, 
 * the internship eligibility,
 * and  application acceptance.
 * 
 *
 * 
 */


public class ApplicationValidator {

    public boolean canStudentApplyToInternship(Student student, Internship internship) {
        return validateApplicationEligibility(student, internship).isValid();
    }

    public boolean validateYearRestriction(Student student, Internship internship) {
        if (student == null || internship == null) {
            return false;
        }
        int year = student.getYearOfStudy();

        switch (internship.getLevel()) {
            case Basic:
                return true;

            case Intermediate:

            case Advanced:
                return year > SystemConstants.BASIC_LEVEL_MAX_YEAR;


            default:
                return false;
        }
    }



    public boolean hasReachedApplicationLimit(Student student) {
        return student != null && student.getApplicationCount() >= SystemConstants.MAX_APPLICATIONS_PER_STUDENT;
    }




    public boolean isInternshipEligibleForApplication(Internship internship) {
        if (internship == null) {
            
            return false;
        }


        return internship.isVisible()
            && internship.getStatus() == entity.enums.InternshipStatus.Approved
            && internship.getAvailableSlots() > 0
            && !LocalDate.now().isAfter(internship.getClosingDate());
    }




    public ValidationResult validateApplicationEligibility(Student student, Internship internship) {
        if (student == null || internship == null) {
            return ValidationResult.failure("Invalid student or internship");
        }

        if (hasReachedApplicationLimit(student)) {
            return ValidationResult.failure("Maximum number of applications reached");
        }

        if (!validateYearRestriction(student, internship)) {
            return ValidationResult.failure("Internship level not permitted for current year of study");
        }

        if (!student.getMajor().equalsIgnoreCase(internship.getPreferredMajor())) {
            return ValidationResult.failure("Preferred major does not match");
        }

        if (!isInternshipEligibleForApplication(internship)) {
            return ValidationResult.failure("Internship is not open for application");
        }

        return ValidationResult.success();
    }




    public boolean canAccept(Application application) {
        return application != null && application.getStatus() == ApplicationStatus.Successful;
    }
}


