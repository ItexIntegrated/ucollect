package com.iisysgroup.ucollectmobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.iisysgroup.ucollect.TransactionCallback;
import com.iisysgroup.ucollect.RequestManager;
import com.iisysgroup.ucollect.TransactionResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements TransactionCallback, View.OnClickListener {

    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing transaction");
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null).create();

        requestManager =  ((MyApplication)getApplication()).requestManager;

    }

    private EditText getCardNumber(){
        return (EditText) findViewById(R.id.cardNumber);
    }

    private EditText getCardMonth(){
        return (EditText) findViewById(R.id.cardMonth);
    }

    private EditText getCardYear(){
        return (EditText) findViewById(R.id.cardYear);
    }

    private EditText getCardCvv(){
        return (EditText) findViewById(R.id.cardCvv);
    }

    private EditText getCardPin(){
        return (EditText) findViewById(R.id.cardPin);
    }


    private EditText getCardHolderName(){
        return (EditText) findViewById(R.id.cardHolderName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                progressDialog.setMessage("Processing transaction");
                progressDialog.show();
                testTransaction();
                break;
        }
    }




    void testTransaction(){
//        RequestManager requestManager = ((MyApplication)getApplication()).requestManager;

        String date =  new SimpleDateFormat(RequestManager.UCOLLECT_DATE_FORMAT).format(new Date());
        Log.i("Test","Transaction Date" +  date);
        requestManager.transactionDateTime = date;

        //Customer Info
        requestManager.customerLastName = "Test";
        requestManager.customerFirstName = "Tester";
        requestManager.customerEmail = "test@test.com";
        requestManager.customerPhoneNumber = "08085555643";

        //Payment Info
        requestManager.countryCurrencyCode = "566";
        requestManager.totalPurchaseAmount = 50000.0;
        requestManager.numberOfItems = 5;
        requestManager.purchaseDescription = "Buns Purchase";
        requestManager.merchantGeneratedReferenceNumber = System.currentTimeMillis()+"";

        //Card Info
        requestManager.cardPan =  getCardNumber().getText().toString().trim(); //"4999082100029373", "5061020000000002298";
        requestManager.cardCVV = getCardCvv().getText().toString().trim();//        requestManager.cardCVV = "123";
        try {
            requestManager.cardExpiryMonth = Integer.parseInt(getCardMonth().getText().toString().trim());//        requestManager.cardExpiryMonth = 01;
            requestManager.cardExpiryYear = Integer.parseInt(getCardYear().getText().toString().trim());//        requestManager.cardExpiryYear = 2018;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        requestManager.cardHolderName = getCardHolderName().getText().toString().trim();//        requestManager.cardHolderName = "Account Name";

        //Pin is optional - dependent on the card scheme
        requestManager.cardPin = getCardPin().getText().toString().trim();//        requestManager.cardPin = "1111";



//        requestManager.cardPan = ("4999082100029373");
//        requestManager.cardCVV = ("518");
//        requestManager.cardExpiryMonth = (11);
//        requestManager.cardExpiryYear = (2019);
//        requestManager.cardHolderName = ("Test Tester");
//        requestManager.cardPin = ("1234");
//
//        requestManager.cardPan = ("5061020000000002298");
//        requestManager.cardCVV = ("123");
//        requestManager.cardExpiryMonth = (01);
//        requestManager.cardExpiryYear = (2018);
//        requestManager.cardHolderName = ("Test Tester");
//        requestManager.cardPin = ("1111");
//


        //Start Transaction
        requestManager.startPaymentTransaction(this);

    }


    void queryExistingTransaction(){
        //((MyApplication)getApplication()).requestManager.queryTransactionStatus("14811308291201", this);
        requestManager.queryTransactionStatus("14811308291201", this);


    }



    @Override
    public void onTransactionError(Exception e) {
        e.printStackTrace();
        progressDialog.dismiss();
       showMessage("Transaction Failed", e.getMessage());
    }


    @Override
    public void onTransactionComplete(TransactionResult transactionResult ) {
        progressDialog.dismiss();
        String title =  Integer.parseInt(transactionResult.Status) == 0 ? "Transaction Successful": "Transaction Declined";
        String message =  transactionResult.Message;
        showMessage(title, message);
    }


    @Override
    public void onRequestOtpAuthorization() {
        progressDialog.dismiss();

        final EditText editText =  new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        editText.setPadding(16,16,16,16);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP");
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  otp =  editText.getText().toString();

                progressDialog.setMessage("Authorizing transaction");
                progressDialog.show();

                requestManager.authorizeTransaction(otp);
            }
        });

        builder.show();
    }


    void showMessage(String title, String message){
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.show();
    }


}


