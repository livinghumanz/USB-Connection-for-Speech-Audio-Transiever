package com.example.autochat;

public class Message {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_IVI = "ivi";


    String message;
    String sentBy;

    public String getMessage(){
        return message;
    }
    public void setMessage(String msg){
        this.message = msg;
    }


    public String getSentBy(){
        return sentBy;
    }
    public void setSentBy(String sMsg){
        this.sentBy = sMsg;
    }

    public Message(String message,String sentBy){
        this.message = message;
        this.sentBy = sentBy;
    }
}
