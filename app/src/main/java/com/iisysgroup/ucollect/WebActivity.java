package com.iisysgroup.ucollect;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import static com.iisysgroup.ucollect.RequestManager.MODE.DEBUG;


public class WebActivity extends AppCompatActivity {


    String host, path;
    WebView webView;
    RequestManager requestManager;
    FrameLayout progressLayout;
    AlertDialog alertDialog;

    WebChromeClient webChromeClient = new WebChromeClient();
    private boolean upsl_process_complete;
    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (upsl_process_complete) {
                exitAndContinueInBackground(view);
                return true;
            }

            showProgressIndicator(true);


            view.loadUrl(url);
            return true;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (upsl_process_complete) {
                exitAndContinueInBackground(view);
                return true;
            }

            String url = request.getUrl().toString();

            showProgressIndicator(true);

            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!upsl_process_complete) {
                if (progressLayout != null)
                    showProgressIndicator(false);
                webView.setVisibility(View.VISIBLE);
            } else {
                exitAndContinueInBackground(webView);

            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            if (requestManager.workingMode == DEBUG)
                handler.proceed();
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            Uri uri = Uri.parse(url);
            if (uri.getLastPathSegment() != null) {
                switch (uri.getLastPathSegment()) {
                    case "uapprove.gsp":
                    case "udecline.gsp":
                    case "ucancel.gsp":
                        upsl_process_complete = true;
                        break;
                }
            }

            showProgressIndicator(true);
            super.onPageStarted(view, url, favicon);
        }


        void showProgressIndicator(boolean show) {
            if (show) {
                progressLayout.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
            } else {
                progressLayout.setVisibility(View.INVISIBLE);
            }
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            exitAndContinueInBackground(webView);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            exitAndContinueInBackground(webView);
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (upsl_process_complete) {
                exitAndContinueInBackground(webView);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(true)
                .create();


        String url = getIntent().getStringExtra(Intent.ACTION_VIEW);
        webView.loadUrl(url);
        requestManager = RequestManager.getInstance();
    }

    void breakDownUrl(String url) {
        Uri uri = Uri.parse(url);
        host = uri.getHost();
        path = uri.getPath();
    }

    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Abort Transaction")
                .setMessage("Do you want to abort this transaction?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitAndContinueInBackground(webView);
                    }
                }).show();

    }

    void exitAndContinueInBackground(WebView view) {
        upsl_process_complete = false;
        view.stopLoading();
        requestManager.queryTransactionStatus();
        finish();
    }


}
