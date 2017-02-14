package com.iisysgroup.ucollect;

/**
 * Created by Bamitale@Itex on 06/02/2017.
 */

public interface ResultCallback {
    /**
     * Implement to notify users of breakdown in the transaction process
     *
     * @param exception Exception
     */
    void onTransactionError(Exception exception);

    /**
     * Implement to notify users of the status of transaction processing
     *
     * @param transactionResult TransactionResult
     */
    void onTransactionComplete(TransactionResult transactionResult);
}
