package validator;

import entity.CompanyRepresentative;
import entity.Internship;
import util.SystemConstants;
import util.DateUtils;

import java.time.LocalDate;



/**
 * InternshipValidator class contains methods to validate internship creation.
 * 
 *  methods to validate the maximum number of internships per representative,
 * the slot limit, the date range, and the internship creation.
 * 
 * 
 * 
 */




public class InternshipValidator {

    public boolean canRepCreateMoreInternships(CompanyRepresentative representative) {
        return representative != null  && representative.getCreatedInternshipIds().size() < SystemConstants.MAX_INTERNSHIPS_PER_REP;
    }

    public boolean isWithinSlotLimit(int slots) {

        return slots > 0 && slots <= SystemConstants.MAX_SLOTS_PER_INTERNSHIP;

    }

    public boolean isWithinSlotLimit(Internship internship) {

        return internship != null && isWithinSlotLimit(internship.getTotalSlots());
    }

    public boolean isDateRangeValid(LocalDate opening, LocalDate closing) {


        return DateUtils.isValidDateRange(opening, closing);
    }

    public ValidationResult validateInternshipCreation(CompanyRepresentative representative, Internship internship) {
        
        
        if (representative == null || internship == null) {
            return ValidationResult.failure("Invalid data");
        }

        if (!canRepCreateMoreInternships(representative)) {

            return ValidationResult.failure("Maximum number of internships reached");
        }

        if (!isWithinSlotLimit(internship)) {

            return ValidationResult.failure("Slot count must be between 1 and " + SystemConstants.MAX_SLOTS_PER_INTERNSHIP);
        }

        if (!isDateRangeValid(internship.getOpeningDate(), internship.getClosingDate())) {
            
            return ValidationResult.failure("Invalid date range");
        }

        return ValidationResult.success();
    }
}


