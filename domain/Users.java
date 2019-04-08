package com.example.myfiles.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Users {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false,length = 11)
    private String phoneNumber;

    @Column(length = 20)
    private String password;

    @Column(length = 8)
    private String code;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date codeTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCodeTime() {
        return codeTime;
    }

    public void setCodeTime(Date codeTime) {
        this.codeTime = codeTime;
    }
    public Users() {
    }

    public Users(String phoneNumber, String code, Date codeTime) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.codeTime = codeTime;
    }
}
