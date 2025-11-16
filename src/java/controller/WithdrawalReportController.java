package controller;

import entity.WithdrawalRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides reporting utilities for withdrawal requests.
 * 
 * helper that surfaces withdrawal request lists (all, pending, or by student)
 */



public class WithdrawalReportController {

    private final WithdrawalController withdrawalController = WithdrawalController.getInstance();



    public List<WithdrawalRequest> getAllWithdrawalRequests() {
        return new ArrayList<>(withdrawalController.getAllWithdrawalRequests());
    }



    public List<WithdrawalRequest> getPendingWithdrawalRequests() {
        return withdrawalController.getPendingWithdrawalRequests();
    }



    public List<WithdrawalRequest> getWithdrawalsByStudent(String studentId) {
        return withdrawalController.getRequestsByStudent(studentId);
    }
    
}


