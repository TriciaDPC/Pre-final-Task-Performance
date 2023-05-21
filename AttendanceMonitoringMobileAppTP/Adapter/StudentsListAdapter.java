package com.example.AttendanceMonitoringMobileAppTP.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.AttendanceMonitoringMobileAppTP.R;
import com.example.AttendanceMonitoringMobileAppTP.realm.Class_Names;
import com.example.AttendanceMonitoringMobileAppTP.realm.Students_List;
import com.example.AttendanceMonitoringMobileAppTP.viewholders.ViewHolder_students;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class StudentsListAdapter extends RealmRecyclerViewAdapter<Students_List, ViewHolder_students> {

    // Activity instance and list of students
    private final Activity mActivity;
    RealmResults<Students_List> mList;

    // student ID and room ID for this adapter instance
    String stuID, mroomID;

    // Realm instance for database operations
    Realm realm = Realm.getDefaultInstance();

    // Constructor for the adapter
    public StudentsListAdapter(RealmResults<Students_List> list, Activity context, String roomID, String extraClick) {

        // Call parent constructor with the provided list and set other class variables
        super(context, list, true);
        mActivity = context;
        mList = list;
        mroomID = roomID;
    }

    // Create a new ViewHolder for the item views
    @NonNull
    @Override
    public ViewHolder_students onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_attendance_adapter, parent, false);
        return new ViewHolder_students(itemView, mActivity, mList, mroomID);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_students holder, final int position) {
        holder.radioButton_present.setChecked(false);
        holder.radioButton_absent.setChecked(false);
        // Get the student at the given position
        Students_List temp = getItem(position);

        // Set the student name and registration number to the ViewHolder views
        holder.student_name.setText(temp.getName_student());
        holder.student_regNo.setText(temp.getRegNo_student());

        // Get the value from SharedPreferences for the student ID
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        stuID = temp.getRegNo_student();
        String value = preferences.getString(stuID, null);

        // Set the appropriate radio button based on the value retrieved from SharedPreferences
        if (value==null){
            // Do nothing if no value was found in SharedPreferences
        } else {
            if (value.equals("Present")) {
                holder.radioButton_present.setChecked(true);
            } else {
                holder.radioButton_absent.setChecked(true);
            }
        }
    }
    public void filterList(RealmResults<Students_List> filteredList) {
        updateData(filteredList);
       notifyDataSetChanged();

   }
}
