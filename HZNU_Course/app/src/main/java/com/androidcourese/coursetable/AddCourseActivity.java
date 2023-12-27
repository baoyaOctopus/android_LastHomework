package com.androidcourese.coursetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCourseActivity extends AppCompatActivity {

    EditText inputCourseName;
    EditText inputTeacher;
    EditText inputClassRoom;

    Spinner inputDay;
    Spinner inputStart;
    Spinner inputEnd;

    boolean isRevise = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        setFinishOnTouchOutside(false);

        inputCourseName = findViewById(R.id.course_name);
        inputTeacher = findViewById(R.id.teacher_name);
        inputClassRoom = findViewById(R.id.class_room);

        inputDay = findViewById(R.id.week);
        inputStart = findViewById(R.id.classes_begin);
        inputEnd = findViewById(R.id.classes_ends);

        Intent intent = getIntent();
        final Course ReviseCourse = (Course) intent.getSerializableExtra("ReviseCourse");
        isRevise = intent.getBooleanExtra("isRevise", false);

        Button okButton = findViewById(R.id.button);

        if (isRevise) {
            // 如果是修改课程信息
            // 设置EditText和Spinner的初始值
            inputCourseName.setText(ReviseCourse.getCourseName());
            inputClassRoom.setText(ReviseCourse.getClassRoom());
            inputTeacher.setText(ReviseCourse.getTeacher());
            setSpinnerDefaultValue(inputDay, String.valueOf(ReviseCourse.getDay()));
            setSpinnerDefaultValue(inputStart, String.valueOf(ReviseCourse.getStart()));
            setSpinnerDefaultValue(inputEnd, String.valueOf(ReviseCourse.getEnd()));

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String courseName = inputCourseName.getText().toString();
                    String teacher = inputTeacher.getText().toString();
                    String classRoom = inputClassRoom.getText().toString();
                    String day = inputDay.getSelectedItem().toString();
                    String start = inputStart.getSelectedItem().toString();
                    String end = inputEnd.getSelectedItem().toString();

                    if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(day) ||
                            TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
                        Toast.makeText(AddCourseActivity.this, "基本课程信息未填写", Toast.LENGTH_SHORT).show();
                    } else {
                        Course newCourse = new Course(courseName, teacher, classRoom,
                                Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));

                        Intent intent = new Intent();
                        intent.putExtra("PreCourse", ReviseCourse);
                        intent.putExtra("newCourse", newCourse);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });

        } else {
            // 如果是添加新课程
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseName = inputCourseName.getText().toString();
                    String teacher = inputTeacher.getText().toString();
                    String classRoom = inputClassRoom.getText().toString();
                    String day = inputDay.getSelectedItem().toString();
                    String start = inputStart.getSelectedItem().toString();
                    String end = inputEnd.getSelectedItem().toString();

                    if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(day) ||
                            TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
                        Toast.makeText(AddCourseActivity.this, "基本课程信息未填写", Toast.LENGTH_SHORT).show();
                    } else {
                        Course course = new Course(courseName, teacher, classRoom,
                                Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));
                        Intent intent = new Intent();
                        intent.putExtra("course", course);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }
    }

    private void setSpinnerDefaultValue(Spinner spinner, String value) {
        SpinnerAdapter spinnerAdapter = spinner.getAdapter();
        int size = spinnerAdapter.getCount();

        for (int i = 0; i < size; i++) {
            if (value.equals(spinnerAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);
                break;
            }
        }
    }
}
