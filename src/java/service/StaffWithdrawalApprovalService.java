package service;

import entity.WithdrawalRequest;

import java.util.List;

/**
 * StaffWithdrawalApprovalService defines methods for staff to moderate withdrawal requests.
 * 
 * 
 */



public interface StaffWithdrawalApprovalService {

    List<WithdrawalRequest> getPendingRequests();

    void approveWithdrawal(String requestId);

    void rejectWithdrawal(String requestId);
}


