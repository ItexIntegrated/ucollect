package com.iisysgroup.ucollectmobile;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.iisysgroup.ucollect.RequestManager;
import com.iisysgroup.ucollect.TransactionCallback;
import com.iisysgroup.ucollect.TransactionResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.issysgroup.ucollectmobile", appContext.getPackageName());
    }



    @Test
    public void runUCollectTest(){
        log("Starting runUCollectTest");
        String testMerchantId = "cipgubatest", testMerchantKey = "b4a4808d-9f36-4404-8ffb-f4fb4952dcbc".trim();
        Context context =  InstrumentationRegistry.getContext();
        final RequestManager requestManager = RequestManager.initialize(context,testMerchantId, testMerchantKey);


        requestManager.workingMode = RequestManager.MODE.DEBUG;

        String date =  new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new Date());
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
        requestManager.cardPan = ("4999082100029373");
        requestManager.cardCVV = ("518");
        requestManager.cardExpiryMonth = (11);
        requestManager.cardExpiryYear = (2019);
        requestManager.cardHolderName = ("Test Tester");
        requestManager.cardPin = ("1234");


        TransactionCallback callback =  new TransactionCallback() {
            @Override
            public void onTransactionError(Exception exception) {
                log("----------onTransactionError--------------");
                exception.printStackTrace();

                log("\n-------------------------------------------");
            }

            @Override
            public void onTransactionComplete(TransactionResult transactionResult) {
                log("----------onTransactionComplete------------");

                String result = String.format("%s %s %s %s %s", transactionResult.Status, transactionResult.Message,
                        transactionResult.Transref, transactionResult.Amount, transactionResult.Pan);

                log("\n-------------------------------------------");
            }

            @Override
            public void onRequestOtpAuthorization() {
                log("-------onRequestOtpAuthorization-----------");

                requestManager.authorizeTransaction("123456");

                log("\n-------------------------------------------");
            }
        };

        requestManager.startPaymentTransaction(callback);

    }


    static void log(String value){
        Log.d("ExampleInstrumentedTest", value);
    }

}
