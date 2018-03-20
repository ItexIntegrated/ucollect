package com.iisysgroup.ucollect;


public interface TransactionCallback extends ResultCallback {


    /**
     * Implement to get notified when the user is required to authorize a transaction by OTP
     * call requestManager.authorizeTransaction(otp) to authorize the transaction with the OTP provided by user
     */
    void onRequestOtpAuthorization();
}
