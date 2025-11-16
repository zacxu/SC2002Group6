package service;

import entity.WithdrawalRequest;

import java.util.List;

/**
 * WithdrawalReportingService defines methods for reporting withdrawal requests.
 * 
 */



public interface WithdrawalReportingService {

    List<WithdrawalRequest> getAllWithdrawalRequests();

    List<WithdrawalRequest> getPendingWithdrawalRequests();

    List<WithdrawalRequest> getWithdrawalsByStudent(String studentId);
}


