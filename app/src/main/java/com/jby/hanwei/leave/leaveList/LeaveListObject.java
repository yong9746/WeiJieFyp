package com.jby.hanwei.leave.leaveList;

public class LeaveListObject {
    private String status, fromDate, toDate, type, reason, days, id;

    public LeaveListObject(String id, String status, String fromDate, String toDate, String type, String reason, String days) {
        this.id = id;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.type = type;
        this.reason = reason;
        this.days = days;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getDays() {
        return days;
    }
}
