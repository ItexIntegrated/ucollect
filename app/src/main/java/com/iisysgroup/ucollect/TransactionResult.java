package com.iisysgroup.ucollect;

/**
 * Created by Bamitale@Itex on 08/12/2016.
 */
public final class TransactionResult {

    private String Transref;
    private String Amount;
    private String Status;
    private String Message;
    private String Pan;
    private String Details;


    @Override
    public String toString() {
        return String.format("%s %s %s %s %s", Transref, Amount, Status, Message, Pan);
    }


    public TransactionStatus getStatus(){
        return Status.equals("000")? TransactionStatus.APPROVED : TransactionStatus.DECLINED;
    }


    public String getTransactionReference() {
        return Transref;
    }

    public String getTransactionAmount() {
        return Amount;
    }

    public String getResponseMessage() {
        return Message;
    }

    public String getPan() {
        return Pan;
    }

    public String getDetails() {
        return Details;
    }
}
