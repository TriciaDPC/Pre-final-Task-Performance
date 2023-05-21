package com.example.AttendanceMonitoringMobileAppTP.realm.BottomSheet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AttendanceMonitoringMobileAppTP.Adapter.StudentsListAdapter;
import com.example.AttendanceMonitoringMobileAppTP.R;
import com.example.AttendanceMonitoringMobileAppTP.realm.Students_List;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.realm.Realm;

public class Student_Edit_Sheet extends BottomSheetDialogFragment {

    public String _name, _regNo, _mobNo;
    public EditText name_student, regNo_student, mobNo_student;
    public CardView call;

    public Student_Edit_Sheet(String stuName, String regNo, String mobileNo) {
        _name = stuName;
        _regNo = regNo;
        _mobNo = mobileNo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.bottomsheet_student_edit, container, false);

        name_student = v.findViewById(R.id.stu_name_edit);
        regNo_student = v.findViewById(R.id.stu_regNo_edit);
        mobNo_student = v.findViewById(R.id.stu_mobNo_edit);
        call = v.findViewById(R.id.call_edit);

        name_student.setText(_name);
        regNo_student.setText(_regNo);
        mobNo_student.setText(_mobNo);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:" + _mobNo.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        TextView deleteButton = v.findViewById(R.id.delete_students);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = name_student.getText().toString();
                final String regNo = regNo_student.getText().toString();

                // Check if name and regNo are not empty
                if (name.isEmpty() || regNo.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter student name and registration number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the entered name and regNo match with the current student's name and regNo
                if (!name.equals(_name) || !regNo.equals(_regNo)) {
                    Toast.makeText(getContext(), "Invalid student name or registration number", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this student?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Students_List studentToDelete = realm.where(Students_List.class).equalTo("regNo_student", _regNo).findFirst();
                                        if (studentToDelete != null) {
                                            studentToDelete.deleteFromRealm();
                                        }
                                    }
                                });
                                realm.close();

                                // Refresh the RecyclerView adapter
                                RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView_detail);
                                StudentsListAdapter adapter = (StudentsListAdapter) recyclerView.getAdapter();
                                adapter.notifyDataSetChanged();

                                // Close the bottom sheet dialog
                                dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        TextView saveButton = v.findViewById(R.id.SaveEditButton);
        saveButton.setEnabled(false); // initially disable the save button

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = name_student.getText().toString().trim();
                final String regNo = regNo_student.getText().toString().trim();
                final String mobileNo = mobNo_student.getText().toString().trim();
                final boolean[] isChanged = {false}; // flag to indicate if any changes have been made

                // Validate the input format
                if (!name.matches("[a-zA-Z ]+")) {
                    Toast.makeText(getContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!regNo.matches("\\d{12}")) {
                    Toast.makeText(getContext(), "Please enter a valid registration number (12 digits)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mobileNo.matches("09\\d{9}")) {
                    Toast.makeText(getContext(), "Please enter a valid mobile number starting with 09 (11 digits)", Toast.LENGTH_SHORT).show();
                    return;
                }

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Students_List studentToEdit = realm.where(Students_List.class).equalTo("regNo_student", _regNo).findFirst();
                        if (studentToEdit != null) {
                            // Compare the values of the fields with the original values
                            isChanged[0] = !studentToEdit.getName_student().equals(name) ||
                                    !studentToEdit.getRegNo_student().equals(regNo) ||
                                    !studentToEdit.getMobileNo_student().equals(mobileNo);

                            if (isChanged[0]) {
                                studentToEdit.setName_student(name);
                                studentToEdit.setRegNo_student(regNo);
                                studentToEdit.setMobileNo_student(mobileNo);
                            }
                        }
                    }
                });
                realm.close();

                if (!isChanged[0]) {
                    // if no changes have been made
                    Toast.makeText(getContext(), "No changes made", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Refresh the RecyclerView adapter
                RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView_detail);
                StudentsListAdapter adapter = (StudentsListAdapter) recyclerView.getAdapter();
                adapter.notifyDataSetChanged();

                // Show the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Student Information Updated");
                builder.setMessage("The student information has been successfully updated.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //this will close the sheet
                        dismiss();
                    }
                });
                builder.show();
            }
        });


// add a TextWatcher to track changes in the EditText fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(true); // enable the save button when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        name_student.addTextChangedListener(textWatcher);
        regNo_student.addTextChangedListener(textWatcher);
        mobNo_student.addTextChangedListener(textWatcher);
    return v;
    }}