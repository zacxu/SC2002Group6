package util;

import entity.Student;
import entity.InternshipOpportunity;



/**
 * This class focues on rules for internship eligibility checks
 * 
 * It also handles , year of study validation, major validation, and slot count validation
 */


public class Validator {

    private static final String DEFAULT_PASSWORD = "password";
    

    public static boolean isDefaultPassword(String password) {
        return DEFAULT_PASSWORD.equals(password);
    }


    
    public static boolean isEligibleForInternship(Student student, InternshipOpportunity internship) {
        
        if (!student.getMajor().equalsIgnoreCase(internship.getPreferredMajor())) {
            return false;
        }
        

        int year = student.getYearOfStudy();
        InternshipOpportunity.InternshipLevel level = internship.getLevel();
        
        if (year == 1 || year == 2) {
            return level == InternshipOpportunity.InternshipLevel.Basic;
        } else if (year == 3 || year == 4) {
            return true; 
        }
        
        return false;
    }
    
    public static boolean isValidYearOfStudy(int year) {
        return year >= 1 && year <= 4;
    }
    
    public static boolean isValidMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            return false;
        }
        String[] validMajors = {"CSC", "EEE", "MAE", "CE", "ME", "CS", "EE"};
        for (String validMajor : validMajors) {
            if (validMajor.equalsIgnoreCase(major.trim())) {
                return true;
            }
        }
        return true; 
    }
    
    public static boolean isValidSlotCount(int slots) {
        return slots > 0 && slots <= 10;
    }
}

