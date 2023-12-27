package com.androidcourese.coursetable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewPageActivity extends AppCompatActivity {

    private static final String TAG = "HZNU课表小助手";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page);

        // 初始化 WebView
        webView = findViewById(R.id.webView);

        // 判空处理
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            // 设置支持 JavaScript
            webSettings.setJavaScriptEnabled(true);
            // 允许Dom缓存数据
            webSettings.setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            Toast.makeText(NewPageActivity.this, "请登录教务系统，再进行操作", Toast.LENGTH_SHORT).show();
            // 加载网页
            webView.loadUrl("https://jwxt.hznu.edu.cn");

            // 设置 WebView
            setupWebView();
        } else {
            // 处理 webView 为 null 的情况
            // 可以添加一些日志输出或者其他处理
        }
    }

    public void onBackButtonClick(View view) {
        super.onBackPressed();
    }

    public void onGOKebiao(View view) {
        Toast.makeText(NewPageActivity.this, "抓取流程启动………………", Toast.LENGTH_SHORT).show();
        webView.loadUrl("https://jwxt.hznu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default");
    }

    public void onCaptureKebiao(View view) {
        Toast.makeText(NewPageActivity.this, "抓取流程启动………………", Toast.LENGTH_SHORT).show();
        // 跳转https://jwxt.hznu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default
        webView.loadUrl("https://jwxt.hznu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default");
    }

    private void setupWebView() {
        if (webView != null) {
            // 添加一个js交互对象
            webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

            // 使webview支持javascript
            WebSettings mSetting = webView.getSettings();
            mSetting.setJavaScriptEnabled(true);

            // 添加一个WebViewClient监听状态
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //取出网址
                    // 在结束加载网页时会回调
                    Log.i(TAG, "onPageFinished: " + url);
                    if (!url.equals("https://jwxt.hznu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default")) {
                        Toast.makeText(NewPageActivity.this, "请切换到课表页面", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //消息通知
                    Toast.makeText(NewPageActivity.this, "开始分析页面 请稍后……", Toast.LENGTH_SHORT).show();

                    // 延迟一段时间再执行 JavaScript，例如延迟 5 秒否则拿不到数据
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewPageActivity.this, "页面分析完毕", Toast.LENGTH_SHORT).show();
                            // 获取页面内容
                            view.loadUrl("javascript:window.java_obj.getSource(document.getElementsByTagName('html')[0].innerHTML);");
                        }
                    }, 6000); // 延迟 6000 毫秒（6 秒）

                    super.onPageFinished(view, url);
                }
            });
        } else {
            Log.e(TAG, "webView is null");
        }
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getSource(String html) {
            String regFormat = "\\s*|\t|\r|\n";
            String regTag = "<[^>]*>";
            String speechText = html.replaceAll(regFormat, "").replaceAll(regTag, "");
//            Log.e(TAG, "HTML----------" + html);

            // 从 HTML 中提取课表
            Document document = Jsoup.parse(html);
            // 获取表格元素
            Element kebiaoElement = document.getElementById("kblist_table");

            // 检查是否找到表格
            if (kebiaoElement != null) {
                String kebiao = kebiaoElement.toString();
//                Log.e(TAG, "课表----------" + kebiao);

                // 从课表中提取课程信息
                Elements trs = kebiaoElement.getElementsByTag("tr");
                //名字 2023-2024学年第1学期吴银杰的课表 　学号：2021212205196 @-实训●-实践○-实验★-理论注：红色斜体为待筛选，蓝色为已选上
                Element Nametr = trs.get(0);
                Elements Nametds = Nametr.getElementsByTag("td");
                String schoolYear = "";
                String studentName = "";
                String studentID = "";

                for (int j = 0; j < Nametds.size(); j++) {
                    Element td = Nametds.get(j);

                    String INFOtext = td.text();
                    Log.e(TAG, "子信息----------" + INFOtext);
                    // 定义正则表达式，匹配学年部分
                    String regex = "(\\d{4}-\\d{4}学年第\\d学期).*";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(INFOtext);
                    if (matcher.matches()) {
                        // 获取匹配到的学年部分
                        schoolYear = matcher.group(1);
                    }
                    String regex2 = "期(.*)的课表";
                    Pattern pattern2 = Pattern.compile(regex2);
                    Matcher matcher2 = pattern2.matcher(INFOtext);
                    while (matcher2.find()) {
                        studentName = matcher2.group();
                        int index = studentName.indexOf("的课表");
                        studentName = studentName.substring(1, index);
                    }
                    String regex3 = "：(\\S+).*";
                    Pattern pattern3 = Pattern.compile(regex3);
                    Matcher matcher3 = pattern3.matcher(INFOtext);
                    while (matcher3.find()) {
                        studentID = matcher3.group();
                        int index = studentID.indexOf(" @");
                        studentID = studentID.substring(1, index);
                    }

                }

                Log.e(TAG, "【学年】" + schoolYear);
                Log.e(TAG, "【姓名】" + studentName);
                Log.e(TAG, "【学号】" + studentID);
                for (int i = 1; i < trs.size(); i++) {
                    Log.e(TAG, "----------");
                    Element tr = trs.get(i);
                    Elements tds = tr.getElementsByTag("td");
                    for (int j = 0; j < tds.size(); j++) {
                        String className = "";
                        String classArea = "";
                        String classTeacher = "";
                        Element td = tds.get(j);
                        String text = td.text();
                        Log.e(TAG, "子信息----------" + text);
                        String regex1 = "(.*) 周数";
                        Pattern pattern1 = Pattern.compile(regex1);
                        Matcher matcher1 = pattern1.matcher(text);
                        while (matcher1.find()) {
                            className = matcher1.group();

                            int index = className.indexOf(" 周数");

                            className = className.substring(0, index);
                            Log.d("classinfo", className);

                        }

                        String regex2 = "上课地点：(.*)教师 ：";
                        Pattern pattern2 = Pattern.compile(regex2);
                        Matcher matcher2 = pattern2.matcher(text);
                        while (matcher2.find()) {
                            classArea = matcher2.group();

                            int index = classArea.indexOf("教师 ：");
                            classArea = classArea.substring(5, index);
                            Log.d("classinfo", classArea);
                        }
                        String regex3 = "教师 ：(.*) 教学班";
                        Pattern pattern3 = Pattern.compile(regex3);
                        Matcher matcher3 = pattern3.matcher(text);
                        while (matcher3.find()) {
                            classTeacher = matcher3.group();

                            int index = classTeacher.indexOf(" 教学班");
                            classTeacher = classTeacher.substring(4, index);
                            Log.d("classinfo", classTeacher);
                        }

                    }
                }
            } else {
                Toast.makeText(NewPageActivity.this, "当前页面未找到您的课程", Toast.LENGTH_SHORT).show();
                // 未找到表格
                Log.e(TAG, "Table Not Found");
            }
        }
    }
}