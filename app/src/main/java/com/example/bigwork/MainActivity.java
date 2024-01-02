package com.example.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String input = "2022-2023学年第1学期";
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{4}学年第\\d学期).*");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Log.d("TA1G", matcher.group());

        }
    }
}