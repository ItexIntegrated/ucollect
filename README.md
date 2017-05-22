# ucollect-android
U-collect in-app payment SDK for android.

## Installation
Add the bintray repository to your build file in your root build.gradle at the end of repositories:

```gradle
 allprojects {
        repositories {
            ...
            maven { url 'https://dl.bintray.com/itex/U-collect/' }
        }
    } 
    
```

Add the dependencies
```gradle
 dependencies {
 compile 'com.iisysgroup.ucollectmobilelib:ucollect-android:1.1.0@aar'
 compile 'com.android.support:cardview-v7:25.1.0'
 }
 
 ```
## Usage
### Initialization
```java
 Context context  = getApplicationContext();
 RequestManager requestManager = RequestManager.initialize(context,testMerchantId, testMerchantKey);
 
 ```
### Test Mode
To activate testing mode
 ```java
 requestManager.workingMode = RequestManager.MODE.DEBUG; // For  Test
 
```

### Building Transaction Request
To start a transaction, Implement TransactionCallback
```java
 TransactionCallback transactionCallback = new TransactionCallback() {
     @Override
     public void onTransactionError(Exception e) {

     }

     @Override
     public void onTransactionComplete(TransactionResult transactionResult) {

     }

     @Override
     public void onRequestOtpAuthorization() {

     }
 };
 
 String date =  new SimpleDateFormat(RequestManager.UCOLLECT_DATE_FORMAT).format(new Date());
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
 requestManager.cardPan =  getCardNumber().getText().toString().trim();
 requestManager.cardCVV = getCardCvv().getText().toString().trim();
 try {
     requestManager.cardExpiryMonth = Integer.parseInt(getCardMonth().getText().toString().trim());//        requestManager.cardExpiryMonth = 01;
     requestManager.cardExpiryYear = Integer.parseInt(getCardYear().getText().toString().trim());//        requestManager.cardExpiryYear = 2018;
 } catch (NumberFormatException e) {
     e.printStackTrace();
 }
 requestManager.cardHolderName = getCardHolderName().getText().toString().trim();

 //Pin is optional - dependent on the card scheme
 requestManager.cardPin = getCardPin().getText().toString().trim();
 

 requestManager.startPaymentTransaction(transactionCallback);
 ```


### Authorizing Transactions
When a transaction needs to be authorized using OTP, implement the onRequestAuthorization, and call requestManager.authorizeTransaction(otp);

```java
 …
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
 ```

### Querying Transaction Status
To query the status of an on-going or already complete transaction
```java
String merchantGeneratedReferenceNumber = "14811308291201"; // Previous Transaction's Merchant Generated Reference Number
requestManager.queryTransactionStatus("14811308291201", resultCallback);
```




