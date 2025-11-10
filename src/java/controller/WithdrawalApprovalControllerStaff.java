package controller;

import entity.CareerCenterStaff;
import entity.User;
import entity.WithdrawalRequest;

import java.util.List;

/**
 * Staff only class for handling  withdrawal approvals 
 */




public class WithdrawalApprovalControllerStaff {

    private final SessionController sessionController = SessionController.getInstance();
    private final WithdrawalController withdrawalController = WithdrawalController.getInstance();



    private void ensureStaff() {
        User currentUser = sessionController.getCurrentUser();
        if (!(currentUser instanceof CareerCenterStaff)) {
            throw new IllegalStateException("Only Career Center Staff can perform this action");
        }
    }



    
    public void approveWithdrawal(String withdrawalId) {
        ensureStaff();
        withdrawalController.approveWithdrawal(withdrawalId);
    }

    public void rejectWithdrawal(String withdrawalId) {
        ensureStaff();
        withdrawalController.rejectWithdrawal(withdrawalId);
    }




    public List<WithdrawalRequest> getPendingWithdrawals() {
        ensureStaff();
        return withdrawalController.getPendingWithdrawalRequests();
    }



    
}


