package com.ritchennai.pinnacle;

public class HRStatusModel {
    String count;
    String name;
    String type;
    String fromDate;
    String toDate;
    String status;

    public HRStatusModel(String count, String name, String type, String fromDate, String toDate, String status) {
        this.count = count;
        this.name = name;
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
