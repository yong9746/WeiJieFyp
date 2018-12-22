package com.jby.hanwei.attendanceHistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jby.hanwei.R;

import java.util.ArrayList;

public class AttendanceHistoryExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<AttendanceHistoryGroupObject> attendanceHistoryGroupObjectArrayList;

    public AttendanceHistoryExpandableAdapter(Context context, ArrayList<AttendanceHistoryGroupObject> attendanceHistoryGroupObjectArrayList){
        this.context = context;
        this.attendanceHistoryGroupObjectArrayList = attendanceHistoryGroupObjectArrayList;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    /*-----------------------------------------------------------------------------PARENT VIEW-------------------------------------------------------------*/
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.activity_attendance_history_list_view_group_item, null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);

        }
        else
            groupViewHolder = (GroupViewHolder)convertView.getTag();

        AttendanceHistoryGroupObject object = getGroup(groupPosition);
        groupViewHolder.date.setText(object.getDate());
        groupViewHolder.status.setText(object.getStatus());
        return convertView;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public AttendanceHistoryGroupObject getGroup(int i) {
        return attendanceHistoryGroupObjectArrayList.get(i);
    }

    private static class GroupViewHolder{
        TextView date, status;
        LinearLayout layout;
        GroupViewHolder (View view){
            date = view.findViewById(R.id.date);
            status = view.findViewById(R.id.status);
            layout = view.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public int getGroupCount() {
        return attendanceHistoryGroupObjectArrayList.size();
    }


    /*-----------------------------------------------------------------------END OF PARENT VIEW-------------------------------------------------------------*/
    /*---------------------------------------------------------------------------CHILD VIEW-------------------------------------------------------------------*/
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.activity_attendance_history_list_view_item, null);
            viewHolder = new ChildViewHolder(view);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (ChildViewHolder) view.getTag();

        final AttendanceHistoryChildObject object = getChild(groupPosition, childPosition);

        viewHolder.checkIn.setText(object.getTimeIn());
        viewHolder.checkOut.setText(object.getTimeOut());
        viewHolder.day.setText(object.getDate());
        viewHolder.totalHour.setText(object.getTotalHour());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public int getChildrenCount(int i) {
        return attendanceHistoryGroupObjectArrayList.get(i).getAttendanceHistoryChildObjectArrayList().size();
    }

    @Override
    public AttendanceHistoryChildObject getChild(int groupPosition, int childPosition) {
        return attendanceHistoryGroupObjectArrayList.get(groupPosition).getAttendanceHistoryChildObjectArrayList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    private static class ChildViewHolder{
        final TextView checkIn, checkOut, day, totalHour;

        ChildViewHolder (View view){
            checkIn = view.findViewById(R.id.check_in);
            checkOut = view.findViewById(R.id.check_out);
            day = view.findViewById(R.id.week_day);
            totalHour = view.findViewById(R.id.total_hour);
        }
    }
    /*-----------------------------------------------------------------------------------END OF CHILD VIEW---------------------------------------------------------*/
}
