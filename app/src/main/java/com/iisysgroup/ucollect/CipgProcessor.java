package com.iisysgroup.ucollect;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import bolts.Task;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Bamitale@Itex on 11/11/2016.
 */

final class CipgProcessor {

    static Task<RequestManager.Status> initiateTransaction() {
        final RequestManager requestManager = RequestManager.getInstance();
        return Task.callInBackground(new Callable<RequestManager.Status>() {
            @Override
            public RequestManager.Status call() throws Exception {
                String url = requestManager.context.getString(requestManager.workingMode == RequestManager.MODE.LIVE ? R.string.cipg_live_url : R.string.cipg_test_url);

                String content = "";
                try {
                    content = requestManager.buildTransactionRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                String responseString = processTransaction(url, content);
//                Log.d("Ucollect", responseString);
                String response = new JSONObject(responseString).getJSONObject("result").toString();


                Gson gson = new Gson();
                RequestManager.Status status = gson.fromJson(response, RequestManager.Status.class);

                return status;
            }
        });

    }

    static Task<TransactionResult> verifyTransactionStatus() {
        final RequestManager requestManager = RequestManager.getInstance();
        return Task.callInBackground(new Callable<TransactionResult>() {
            @Override
            public TransactionResult call() throws Exception {
                String url = requestManager.context.getString(requestManager.workingMode == RequestManager.MODE.LIVE ? R.string.cipg_live_status_url : R.string.cipg_test_status_url);

                String content = "";
                try {
                    content = requestManager.buildVerificationRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                String responseString = processTransaction(url, content);
//                Log.d("Ucollect", responseString);
                String response = new JSONObject(responseString).getJSONObject("result").toString();

                Gson gson = new Gson();
                TransactionResult result = gson.fromJson(response, TransactionResult.class);


                return result;
            }
        });
    }

    static Task<RequestManager.Status> authorizeTransaction() {
        final RequestManager requestManager = RequestManager.getInstance();

        return Task.callInBackground(new Callable<RequestManager.Status>() {
            @Override
            public RequestManager.Status call() throws Exception {
                String url = requestManager.context.getString(requestManager.workingMode == RequestManager.MODE.LIVE ? R.string.cipg_live_otp_url : R.string.cipg_test_otp_url);

                String content = "";
                try {
                    content = requestManager.buildAuthorizationRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                String responseString = processTransaction(url, content);
//                Log.d("Ucollect", responseString);
                String response = new JSONObject(responseString).getJSONObject("result").toString();

                Gson gson = new Gson();
                RequestManager.Status status = gson.fromJson(response, RequestManager.Status.class);



                return status;
            }
        });
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static String processTransaction(final String urlString, final String requestString) throws MalformedURLException {

        OkHttpClient.Builder builder =  new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

         if( RequestManager.getInstance().workingMode == RequestManager.MODE.DEBUG) {
             builder.hostnameVerifier(new HostnameVerifier() {
                 @Override
                 public boolean verify(String hostname, SSLSession session) {
                   //  System.out.println("Hostname: " + hostname);
                     return true;
                 }
             });
         }

       OkHttpClient okHttpClient =   builder.build();

        RequestBody  requestBody = RequestBody.create(JSON,  requestString);

        Request request =  new Request.Builder()
                .url(urlString)
                .addHeader("Content-Type", "text/json")
                .post(requestBody)
                .build();


        try {
            Response response =  okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Network Connection Error");
        }
    }

}
