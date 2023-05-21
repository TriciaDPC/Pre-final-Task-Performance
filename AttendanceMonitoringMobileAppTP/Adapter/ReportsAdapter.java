package com.example.AttendanceMonitoringMobileAppTP.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.AttendanceMonitoringMobileAppTP.R;
import com.example.AttendanceMonitoringMobileAppTP.realm.Attendance_Reports;
import com.example.AttendanceMonitoringMobileAppTP.viewholders.ViewHolder_reports;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ReportsAdapter extends RealmRecyclerViewAdapter<Attendance_Reports, ViewHolder_reports> {

    private final Activity mActivity;
    RealmResults<Attendance_Reports> mList;
    String mroomID;

    public ReportsAdapter(RealmResults<Attendance_Reports> list, Activity context, String roomID) {

        super(context, list, true);

        mActivity = context;
        mList = list;
        mroomID =roomID;
    }
    @NonNull
    @Override
    public ViewHolder_reports onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_adapter_item, parent, false);
        return new ViewHolder_reports(itemView, mActivity, mList);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_reports holder, int position) {
        Attendance_Reports temp = getItem(position);
        holder.month.setText(temp.getMonthOnly());
        holder.date.setText(temp.getDateOnly());
    }
}
