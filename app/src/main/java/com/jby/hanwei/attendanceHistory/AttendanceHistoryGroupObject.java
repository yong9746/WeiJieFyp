package com.jby.hanwei.attendanceHistory;

import java.util.ArrayList;

public class AttendanceHistoryGroupObject {
    private String id, date, status, holiday;
    private ArrayList<AttendanceHistoryChildObject> attendanceHistoryChildObjectArrayList;

    public AttendanceHistoryGroupObject(String id, String date, String status, String holiday, ArrayList<AttendanceHistoryChildObject> attendanceHistoryChildObjectArrayList) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.holiday = holiday;
        this.attendanceHistoryChildObjectArrayList = attendanceHistoryChildObjectArrayList;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getHoliday() {
        return holiday;
    }

    public ArrayList<AttendanceHistoryChildObject> getAttendanceHistoryChildObjectArrayList() {
        return attendanceHistoryChildObjectArrayList;
    }
}
