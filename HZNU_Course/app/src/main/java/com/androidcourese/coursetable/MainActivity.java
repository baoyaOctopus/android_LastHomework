package com.androidcourese.coursetable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout day;
    private DatabaseHelper databaseHelper = new DatabaseHelper(this, "database.db", null, 1);
    View clickedView;
    int currentCoursesNumber = 0;
    int maxCoursesNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        // 删除所有数据库内容
//        deleteAllData();

        // 加载数据
        loadData();
    }

    private void deleteAllData() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        // 删除所有数据
        sqLiteDatabase.execSQL("DELETE FROM courses");
        // 重置一些变量（如果需要的话）
        currentCoursesNumber = 0;
        maxCoursesNumber = 0;
        // 清除左侧视图
        LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
        leftViewLayout.removeAllViews();
    }

    @SuppressLint("Range")
    private boolean isCourseDataValid(Course course) {
        return (course.getDay() >= 1 && course.getDay() <= 7) &&
                (course.getStart() <= course.getEnd());
    }

    private void deleteInvalidCourse(Course course) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from courses where course_name = ? and day =? and class_start=? and class_end=?",
                new String[]{course.getCourseName(),
                        String.valueOf(course.getDay()),
                        String.valueOf(course.getStart()),
                        String.valueOf(course.getEnd())});
    }

    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end")));

                if (isCourseDataValid(course)) {
                    coursesList.add(course);
                } else {
                    deleteInvalidCourse(course);
                }
            } while(cursor.moveToNext());
        }
        cursor.close();

        for (Course course : coursesList) {
            createLeftView(course);
            createItemCourseView(course);
        }
    }

    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " +
                        "values(?, ?, ?, ?, ?, ?)",
                new String[] {course.getCourseName(),
                        course.getTeacher(),
                        course.getClassRoom(),
                        String.valueOf(course.getDay()),
                        String.valueOf(course.getStart()),
                        String.valueOf(course.getEnd())});
    }

    private void createLeftView(Course course) {
        int endNumber = course.getEnd();
        if (endNumber > maxCoursesNumber) {
            for (int i = 0; i < endNumber - maxCoursesNumber; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110, 180);
                view.setLayoutParams(params);
                TextView text = view.findViewById(R.id.class_number_text);
                text.setText(String.valueOf(++currentCoursesNumber));
                LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
                leftViewLayout.addView(view);
            }
            maxCoursesNumber = endNumber;
        }
    }

    private RelativeLayout getViewDay(int day) {
        int dayId = 0;
        switch (day) {
            case 1: dayId = R.id.monday; break;
            case 2: dayId = R.id.tuesday; break;
            case 3: dayId = R.id.wednesday; break;
            case 4: dayId = R.id.thursday; break;
            case 5: dayId = R.id.friday; break;
            case 6: dayId = R.id.saturday; break;
            case 7: dayId = R.id.weekday; break;
        }
        return findViewById(dayId);
    }

    private void createItemCourseView(final Course course) {
        int getDay = course.getDay();
        if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd())
            Toast.makeText(this, "Invalid day or time range", Toast.LENGTH_LONG).show();
        else {
            day = getViewDay(getDay);

            int height = 200;
            final View v = LayoutInflater.from(this).inflate(R.layout.course_card, null);
            v.setY(height * (course.getStart() - 1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (course.getEnd() - course.getStart() + 1) * height - 8);

            v.setLayoutParams(params);
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" + course.getClassRoom());
            day.addView(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedView = view;
                    Intent intent = new Intent(MainActivity.this, SeeCourseActivity.class);
                    intent.putExtra("seeCourse", course);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_courses:
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Course course = (Course) data.getSerializableExtra("course");
                    createLeftView(course);
                    createItemCourseView(course);
                    saveData(course);
                }
                break;

            case 1:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    final Course preCourse = (Course) data.getSerializableExtra("preCourse");
                    final boolean isDelete = data.getBooleanExtra("isDelete", true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (preCourse != null) {
                                if (isDelete) {
                                    if (clickedView != null && day != null) {
                                        clickedView.setVisibility(View.GONE);
                                        day.removeView(clickedView);
                                        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                                        String deleteSql = "delete from courses where course_name = ? and day =? and class_start=? and class_end=?";
                                        sqLiteDatabase.execSQL(deleteSql, new String[]{preCourse.getCourseName(),
                                                String.valueOf(preCourse.getDay()),
                                                String.valueOf(preCourse.getStart()),
                                                String.valueOf(preCourse.getEnd())});
                                        Log.d("Debug", "Delete SQL: " + deleteSql);
                                        Toast.makeText(MainActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("Debug", "Error: clickedView or day is null during delete operation");
                                        // Handle the case where clickedView or day is null, perhaps show an error message or log.
                                    }
                                } else {
                                    Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                                    intent.putExtra("ReviseCourse", preCourse);
                                    intent.putExtra("isRevise",true);
                                    startActivityForResult(intent,2);
                                }
                            } else {
                                Log.e("Debug", "Error: preCourse is null");
                                // Handle the case where preCourse is null, perhaps show an error message or log.
                            }
                        }
                    });
                }
                break;

            case 2:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Course preCourse = (Course) data.getSerializableExtra("PreCourse");
                    Course newCourse = (Course) data.getSerializableExtra("newCourse");

                    if (preCourse != null && newCourse != null) {
                        clickedView.setVisibility(View.GONE);
                        day = getViewDay(preCourse.getDay());

                        if (day != null) {
                            day.removeView(clickedView);
                            createLeftView(newCourse);
                            createItemCourseView(newCourse);

                            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                            String updateSql = "update courses set " +
                                    "course_name = ?,teacher = ?,class_room=? ,day=? ,class_start=? ,class_end =?" +
                                    "where course_name = ? and day =? and class_start=? and class_end=?";
                            sqLiteDatabase.execSQL(updateSql, new String[]{newCourse.getCourseName(),
                                    newCourse.getTeacher(),
                                    newCourse.getClassRoom(),
                                    String.valueOf(newCourse.getDay()),
                                    String.valueOf(newCourse.getStart()),
                                    String.valueOf(newCourse.getEnd()),
                                    preCourse.getCourseName(),
                                    String.valueOf(preCourse.getDay()),
                                    String.valueOf(preCourse.getStart()),
                                    String.valueOf(preCourse.getEnd())});
                            Log.d("Debug", "Update SQL: " + updateSql);

                            Toast.makeText(MainActivity.this, "Revised successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

        }
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(this, NewPageActivity.class);
        startActivity(intent);
    }
}
