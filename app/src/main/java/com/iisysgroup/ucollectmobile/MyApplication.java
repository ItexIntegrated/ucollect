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
        requestManager = RequestManager.initialize(this,testMerchantId, testMerchantKey);
        requestManager.workingMode = RequestManager.MODE.DEBUG;
    }
}
