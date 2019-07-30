package com.progmatic.recordislandbackend.dto;

import java.util.Map;

public class MailDTO {

    private String from;
    private String to;
    private String name;
    private String subject;
    private String verificationUrl;
    private String verificationToken;
    private Map<String, String> model;

    public MailDTO() {
    }

    public MailDTO(String from, String to, String name, String subject, String token) {
        this.from = from;
        this.to = to;
        this.name = name;
        this.subject = subject;
        this.verificationToken = token;
    }

    public MailDTO(String from, String to, String name, String subject) {
        this.from = from;
        this.to = to;
        this.name = name;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getModel() {
        return model;
    }

    public void setModel(Map<String, String> model) {
        this.model = model;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
