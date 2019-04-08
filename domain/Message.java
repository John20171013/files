package com.example.myfiles.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private Integer id;
    private String userid;
    private String message;
    private String time;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{\"type\":\"chatting\",\"name\":\""+userid.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2")+"\",\"msg\":\""+message+"\",\"time\":\""+time+"\"}";
    }

    public Message() {
    }

    public Message(String userid, String message) {
        this.userid = userid;
        this.message = message;
    }
}
