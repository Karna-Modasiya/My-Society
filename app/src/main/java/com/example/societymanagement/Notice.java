package com.example.societymanagement;

public class Notice {
    String notice_text;
    String name;
    String time;
    String notice_desc;
    String documentid;

    public Notice(){ }

    public Notice(String notice_text, String name, String time, String notice_desc) {
        this.notice_text = notice_text;
        this.name = name;
        this.time = time;
        this.notice_desc = notice_desc;
    }

    public String getNotice_text() {
        return notice_text;
    }

    public void setNotice_text(String notice_text) {
        this.notice_text = notice_text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotice_desc() {
        return notice_desc;
    }

    public void setNotice_desc(String notice_desc) {
        this.notice_desc = notice_desc;
    }
    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

}
