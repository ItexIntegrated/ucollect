package com.iisysgroup.ucollect;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
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

    static String testMerchantId = "cipgubatest", testMerchantKey = "b4a4808d-9f36-4404-8ffb-f4fb4952dcbc".trim();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.iisysgroup.ucollect_android", appContext.getPackageName());
    }


    @Test
    public void testNetworkCall(){
        RequestManager requestManager =  RequestManager.initialize(null, testMerchantId, testMerchantKey);

        requestManager.workingMode = RequestManager.MODE.DEBUG;

        String date =  new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new Date());
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

        String requestString = null;
        try {
            requestString =  requestManager.buildTransactionRequest();
        } catch (Exception e) {
            e.printStackTrace();

        }
        assertTrue( requestString != null);
        System.out.println(requestString);
        String url = "https://databaseendsrv.cloudapp.net/cipg-payportal/regdtran";

        String response = null;
        try {
            response = CipgProcessor.processTransaction(url, requestString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response);
        assertTrue(response != null);
    }
}
