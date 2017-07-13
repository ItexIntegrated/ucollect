package com.iisysgroup.ucollectmobile;

import android.app.Application;

import com.iisysgroup.ucollect.RequestManager;

/**
 * Created by Bamitale@Itex on 07/11/2016.
 */

public class MyApplication extends Application {
    static String testMerchantId = "cipgubatest", testMerchantKey = "b4a4808d-9f36-4404-8ffb-f4fb4952dcbc".trim();
    public RequestManager requestManager;

    @Override
    public void onCreate() {
        super.onCreate();
        testMerchantId = "CMNGN10381";
        testMerchantKey = "e17fde60-b24f-4fc5-b8ad-c8ba261f2fda".trim();

        requestManager = RequestManager.initialize(this,testMerchantId, testMerchantKey);
//        requestManager.workingMode = RequestManager.MODE.DEBUG;
    }
}
