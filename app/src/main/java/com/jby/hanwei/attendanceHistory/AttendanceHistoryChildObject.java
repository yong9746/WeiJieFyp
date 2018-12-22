package com.jby.hanwei.attendanceHistory;

public class AttendanceHistoryChildObject {
    private String date, timeIn, timeOut, totalHour;

    public AttendanceHistoryChildObject(String date, String timeIn, String timeOut, String totalHour) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.totalHour = totalHour;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getTotalHour() {
        return totalHour;
    }
}
