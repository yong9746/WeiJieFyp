package com.jby.hanwei.leave.leaveList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.jby.hanwei.R;

import java.util.ArrayList;

public class LeaveListActivityAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LeaveListObject> leaveListObjectArrayList;

    public LeaveListActivityAdapter(Context context, ArrayList<LeaveListObject> leaveListObjectArrayList){

        this.context = context;
        this.leaveListObjectArrayList = leaveListObjectArrayList;

    }
    @Override
    public int getCount() {
        return leaveListObjectArrayList.size();
    }

    @Override
    public LeaveListObject getItem(int i) {
        return leaveListObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.activity_leave_list_list_view_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        LeaveListObject object = getItem(i);
        String status = object.getStatus();
        if(status.equals("Pending")){
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.blue));
        }
        else if(status.equals("Approved")){
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.green));
        }
        else {
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.red));
        }
        viewHolder.fromDate.setText(object.getFromDate());
        viewHolder.toDate.setText(object.getToDate());
        viewHolder.status.setText(status);
        return view;
    }

    private static class ViewHolder{
        private TextView fromDate, toDate, status;

        ViewHolder (View view){
            fromDate = (TextView)view.findViewById(R.id.activity_leave_list_list_item_from_date);
            toDate = (TextView)view.findViewById(R.id.activity_leave_list_list_item_to_date);
            status = (TextView)view.findViewById(R.id.activity_leave_list_list_item_to_status);
        }
    }
}
