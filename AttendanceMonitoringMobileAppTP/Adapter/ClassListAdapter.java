package com.example.AttendanceMonitoringMobileAppTP.Adapter;


import android.app.Activity;

import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.AttendanceMonitoringMobileAppTP.R;
import com.example.AttendanceMonitoringMobileAppTP.realm.Class_Names;
import com.example.AttendanceMonitoringMobileAppTP.realm.Students_List;
import com.example.AttendanceMonitoringMobileAppTP.viewholders.ViewHolder;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ClassListAdapter extends RealmRecyclerViewAdapter<Class_Names, ViewHolder> {

    private final Activity mActivity;
    private static RealmResults<Class_Names> mList;
    private final Realm realm;

    public ClassListAdapter(RealmResults<Class_Names> list, Activity context) {
        super(context, list, true);
        mActivity = context;
        mList = list;
        realm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_adapter, parent, false);
        return new ViewHolder(itemView, mActivity, mList);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Class_Names temp = getItem(position);

        holder.class_name.setText(temp.getName_class());
        holder.subject_name.setText(temp.getName_subject());
        holder.total_students.setText("Students: " + realm.where(Students_List.class)
                .equalTo("class_id", temp.getId())
                .count());

        switch (temp.getPosition_bg()) {
            case "0":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_paleblue);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_1);
                break;
            case "1":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_green);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_2);
                break;
            case "2":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_yellow);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_3);
                break;
            case "3":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_palegreen);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_4);
                break;
            case "4":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_paleorange);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_5);
                break;
            case "5":
                holder.imageView_bg.setImageResource(R.drawable.asset_bg_white);
                holder.frameLayout.setBackgroundResource(R.drawable.gradient_color_6);
                holder.subject_name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                holder.class_name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                holder.total_students.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color_secondary));
                break;
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity, R.style.AlertDialogStyle)
                        .setCancelable(true)
                        .setTitle("Delete class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the class from the database
                                final String id = temp.getId();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Class_Names classToDelete = realm.where(Class_Names.class).equalTo("id", id).findFirst();
                                        classToDelete.deleteFromRealm();
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(mActivity, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                                        RealmResults<Class_Names> results = realm.where(Class_Names.class)
                                                .findAll();
                                        if (results.isEmpty()) {
                                            // Show empty message
                                            mActivity.findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);
                                            mActivity.findViewById(R.id.imageEmpty).setVisibility(View.VISIBLE);
                                        } else {
                                            // Hide empty message
                                            mActivity.findViewById(R.id.emptyMessage).setVisibility(View.GONE);
                                            mActivity.findViewById(R.id.imageEmpty).setVisibility(View.GONE);
                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(mActivity, R.color.theme_light));

                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(mActivity, R.color.theme_light));

                return true;
            }

        });
    }
    public void filterList(RealmResults<Class_Names> filteredList) {
        updateData(filteredList);
        notifyDataSetChanged();
    }
}





