package com.androidcourese.coursetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SeeCourseActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = new DatabaseHelper(this, "database.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_course);

        // Retrieve the Course object from the intent
        Intent intent = getIntent();
        final Course seeCourse = (Course) intent.getSerializableExtra("seeCourse");

        // Initialize EditTexts to display and edit course information
        final EditText seeCourseName = findViewById(R.id.see_course_name);
        final EditText seeDay = findViewById(R.id.see_week);
        final EditText seeStart = findViewById(R.id.see_classes_begin);
        final EditText seeEnd = findViewById(R.id.see_classes_ends);
        final EditText seeTeacher = findViewById(R.id.see_teacher_name);
        final EditText seeClassRoom = findViewById(R.id.see_class_room);

        // Set initial text in EditTexts with course information
        seeCourseName.setText(seeCourse.getCourseName());
        seeDay.setText(String.valueOf(seeCourse.getDay()));
        seeStart.setText(String.valueOf(seeCourse.getStart()));
        seeEnd.setText(String.valueOf(seeCourse.getEnd()));
        seeTeacher.setText(seeCourse.getTeacher());
        seeClassRoom.setText(seeCourse.getClassRoom());

        // Button for deleting the course
        Button delBtn = findViewById(R.id.btn_del);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepare intent to send back to MainActivity with information about deletion
                Intent intent = new Intent();
                intent.putExtra("preCourse", seeCourse);
                intent.putExtra("isDelete",true);
                setResult(Activity.RESULT_OK, intent);


                // Print seeCourse for debugging
                Log.d("SeeCourseActivity", "Deleting seeCourse: " + seeCourse.toString());

                finish(); // Finish this activity
            }
        });

// Button for revising the course
        Button ReviseBtn = findViewById(R.id.btn_revise);
        ReviseBtn.setOnClickListener(view -> {
            // Prepare intent to send back to MainActivity with information about revision
            Intent Intent = new Intent();
            Intent.putExtra("preCourse", seeCourse);
            Intent.putExtra("isDelete",false);
            setResult(Activity.RESULT_OK, Intent);


            // Print seeCourse for debugging
            Log.d("SeeCourseActivity", "Revising seeCourse: " + seeCourse.toString());

            finish(); // Finish this activity
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Log a message when onActivityResult is called
        Log.d("SeeCourseActivity", "返回修改值");
    }
}
