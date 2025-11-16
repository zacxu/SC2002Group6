package validator;

import entity.Application;
import entity.WithdrawalRequest;
import entity.enums.ApplicationStatus;
import entity.enums.WithdrawalStatus;



/**
 * WithdrawalValidator class contains methods to validate withdrawal operations.
 * 
 *  methods to validate the application withdrawal,
 * the application state, and the withdrawal request.
 * 
 * 
 * 
 */


public class WithdrawalValidator {

    public boolean canWithdrawApplication(Application application) {
        return validateWithdrawalRequest(application).isValid();
    }

    public boolean isApplicationInWithdrawableState(Application application) {
        if (application == null) {
            return false;
        }
        ApplicationStatus status = application.getStatus();

        return status == ApplicationStatus.Pending || status == ApplicationStatus.Successful;
    }

    public ValidationResult validateWithdrawalRequest(Application application) {
        if (application == null) {
            return ValidationResult.failure("Application not found");
        }

        if (!isApplicationInWithdrawableState(application)) {
            return ValidationResult.failure("Application cannot be withdrawn");
        }

        return ValidationResult.success();
    }

    public boolean canStaffActOn(WithdrawalRequest request) {
        
        return request != null && request.getStatus() == WithdrawalStatus.Pending;
    }
}


