package com.iisysgroup.ucollect;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;


public final class RequestManager {
    public static final String UCOLLECT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static RequestManager ourInstance;
    public MODE workingMode = MODE.LIVE;
    public String purchaseDescription,
            transactionDateTime, // format dd/MM/yyyy hh:mm:ss
            countryCurrencyCode, merchantGeneratedReferenceNumber;
    public double totalPurchaseAmount;
    public int numberOfItems;
    public String customerFirstName = "", customerLastName = "",
            customerEmail = "", customerPhoneNumber = "";
    public String cardPan = "", cardCVV = "", cardHolderName = "", cardPin = "";
    public int cardExpiryMonth, cardExpiryYear;
    Context context;
    KeyCrypto keyCrypto;
    private String merchantID, serviceKey, encKey;
    private String otpString = "";
    private String orderID = "";
    private ResultCallback transactionCallback;


    private RequestManager() {
    }

    static synchronized RequestManager getInstance() {

        if (ourInstance == null)
            ourInstance = new RequestManager();

        return ourInstance;
    }

    /**
     * Call to initialize the instance of the RequestManager class
     *
     * @param context
     * @param merchantID
     * @param serviceKey
     * @return
     */
    public static RequestManager initialize(final Context context, final String merchantID, final String serviceKey) {
        RequestManager requestManager = getInstance();

        if (merchantID == null) throw new RuntimeException("Merchant ID is NULL!");
        requestManager.merchantID = merchantID;

        requestManager.serviceKey = serviceKey;
        String temp = requestManager.normalizeServiceKey(serviceKey);
        temp = TripleDES.encrypt(temp, temp);


        requestManager.encKey = temp;
        requestManager.context = context;

        try {
            requestManager.keyCrypto = new KeyCrypto(KeyCrypto.hexStringToBytes(requestManager.encKey), "DESede", "DESede");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid Key");
        }

        requestManager.workingMode = MODE.LIVE;

        return requestManager;
    }

    private String normalizeServiceKey(String key) {
        if (key == null) throw new RuntimeException("Service Key is NULL!");
        key = key.replaceAll("_", "").replaceAll("-", "").toUpperCase().trim();

        if (key.length() != 32)
            throw new RuntimeException("Invalid Key!");


        return key;
    }


    String buildTransactionRequest() throws Exception {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("merchantId", merchantID);
        jsonObject.put("description", Utility.urlEncode(purchaseDescription));
        jsonObject.put("total", totalPurchaseAmount);
        jsonObject.put("date", transactionDateTime);
        jsonObject.put("countryCurrencyCode", countryCurrencyCode);
        jsonObject.put("noOfItems", numberOfItems);
        jsonObject.put("customerFirstName", customerFirstName);
        jsonObject.put("customerLastname", customerLastName);
        jsonObject.put("customerEmail", customerEmail);
        jsonObject.put("customerPhoneNumber", customerPhoneNumber);
        jsonObject.put("referenceNumber", merchantGeneratedReferenceNumber);
        jsonObject.put("serviceKey", serviceKey);

        String cardDetails = String.format("%s|%s|%s|%s|%s|%s", cardPan, cardCVV, cardExpiryYear, cardExpiryMonth, cardPin, cardHolderName);

        jsonObject.put("detail", encryptData(cardDetails));


        return jsonObject.toString();
    }

    String buildVerificationRequest() throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mid", merchantID);
        jsonObject.put("rid", merchantGeneratedReferenceNumber);

        return jsonObject.toString();
    }

    String buildAuthorizationRequest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oID", orderID);
        jsonObject.put("otp", otpString);

        return jsonObject.toString();
    }


    /**
     * Initiate a card payment transaction
     *
     * @param transactionCallback : TransactionCallback
     */
    public void startPaymentTransaction(final TransactionCallback transactionCallback) {
        this.transactionCallback = transactionCallback;

        CipgProcessor.initiateTransaction().onSuccess(new Continuation<Status, Void>() {
            @Override
            public Void then(Task<Status> task) throws Exception {
                Status status = task.getResult();
               // Log.d("Ucollect", status.toString());

                if (status.getStatus().equals("000")) {
                    queryTransactionStatus();
                } else if (status.getStatus().equals("001")) {

                    if (status.getType().equals("UPSL")) {
                        String url = status.getPinpadURL();


                        Intent intent = new Intent(context, WebActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.ACTION_VIEW, url);
                        context.startActivity(intent);

                    } else if (status.getType().equals("VERVE")) {

                        orderID = status.getOrderID();
                        transactionCallback.onRequestOtpAuthorization();

                    } else {
                        throw new Exception("Error: No process defined for this flow");
                    }

                } else {
                    throw new Exception(status.getMessage());
                }


                return null;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {

                if (task.isFaulted()) {
                    Exception e = task.getError();
                    e.printStackTrace();
                    transactionCallback.onTransactionError(e);
                }
                return null;
            }
        });
    }


    /**
     * @param otpString String
     */
    public void authorizeTransaction(String otpString) {
        this.otpString = otpString;

        CipgProcessor.authorizeTransaction().onSuccess(new Continuation<Status, Void>() {
            @Override
            public Void then(Task<Status> task) throws Exception {

                Status status = task.getResult();

                if (status.getStatus().equals("000")) {
                    queryTransactionStatus();
                } else {
                    throw new Exception(status.getMessage());
                }

                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {

                if (task.isFaulted()) {
                    Exception e = task.getError();
                    e.printStackTrace();
                    transactionCallback.onTransactionError(e);
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    void queryTransactionStatus() {
        CipgProcessor.verifyTransactionStatus().onSuccess(new Continuation<TransactionResult, Void>() {
            @Override
            public Void then(Task<TransactionResult> task) throws Exception {
                transactionCallback.onTransactionComplete(task.getResult());
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    Exception e = task.getError();
                    e.printStackTrace();
                    transactionCallback.onTransactionError(e);
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    /**
     * Query the status of a previous transaction
     *
     * @param merchantGeneratedReferenceNumber - Previous Transaction's Reference Number generated on the Merchant's System
     * @param transactionCallback
     */
    public void queryTransactionStatus(String merchantGeneratedReferenceNumber, ResultCallback transactionCallback) {
        this.merchantGeneratedReferenceNumber = merchantGeneratedReferenceNumber;
        this.transactionCallback = transactionCallback;
        queryTransactionStatus();
    }


    private String encryptData(String data) throws Exception {

        return KeyCrypto.hex(keyCrypto.encryptData(data));
    }


    public enum MODE {DEBUG, LIVE}



    static class Status {

        private String Status;
        private String Message;
        private String Type;
        private String PinpadURL;
        private String OTP;
        private String OrderID;

        Status(String status, String message) {
            this.Status = status;
            this.Message = message;
        }


        String getStatus() {
            return Status;
        }

        String getMessage() {
            return Message;
        }

        String getType() {
            return Type;
        }

        String getPinpadURL() {
            return PinpadURL;
        }

        String getOTP() {
            return OTP;
        }

        String getOrderID() {
            return OrderID;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s %s", Status, Message, Type, OrderID);
        }
    }
}

