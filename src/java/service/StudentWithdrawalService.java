package service;

import entity.WithdrawalRequest;

import java.util.List;

/**
 * StudentWithdrawalService defines methods for students to request withdrawals and list their own requests.
 * 
 */


 
public interface StudentWithdrawalService {

    WithdrawalRequest requestWithdrawal(String applicationId, String reason);

    List<WithdrawalRequest> getRequestsByStudent(String studentId);
}


