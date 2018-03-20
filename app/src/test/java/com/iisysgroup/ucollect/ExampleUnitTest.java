package com.iisysgroup.ucollect;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    static String testMerchantId = "cipgubatest", testMerchantKey = "b4a4808d-9f36-4404-8ffb-f4fb4952dcbc".trim();

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testNetworkCall() {
        RequestManager requestManager = RequestManager.initialize(null, testMerchantId, testMerchantKey);

        requestManager.workingMode = RequestManager.MODE.DEBUG;

        String date = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new Date());
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
        requestManager.merchantGeneratedReferenceNumber = System.currentTimeMillis() + "";

        //Card Info
        requestManager.cardPan = ("4999082100029373");
        requestManager.cardCVV = ("518");
        requestManager.cardExpiryMonth = (11);
        requestManager.cardExpiryYear = (2019);
        requestManager.cardHolderName = ("Test Tester");
        requestManager.cardPin = ("1234");

        String requestString = null;
        try {
            requestString = requestManager.buildTransactionRequest();
        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println(requestString);
        assertTrue(requestString != null);

//        String url = "https://databaseendsrv.cloudapp.net/cipg-payportal/regdtran";
//
//        String response = null;
//        try {
//            response = CipgProcessor.processTransaction(url, requestString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(response);
//        assertTrue(response != null);
    }
}