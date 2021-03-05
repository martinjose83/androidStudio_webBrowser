package com.marts.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickInMyAdapterListener {

    private static final String MY_PREFS_NAME = "MyPrefsFile";
    ProgressBar progressBar;
    ListViewCustomAdaptor listAdapter;
    ListViewCustomAdaptorBM listAdapterBM;
    WebView myWebView;
    EditText txturl;
    List<History> bkmkset = new ArrayList<>();
    List<History> histList1 = new ArrayList<>();
    private List<History> histList;
    private List<History> bookmkSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        histList = loadSharedPreferencesHistList();
        bookmkSet = loadSharedPreferencesbookmkSet();
        ;
        pagesetup();
        showPage("https://www.google.com");
    }

    private void pagesetup() {
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        myWebView = (WebView) findViewById(R.id.webview1);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.setBackgroundColor(Color.WHITE);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress((newProgress));
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
            }


        });
        ImageButton gobtn = (ImageButton) findViewById(R.id.go_btn1);
        gobtn.setOnClickListener((View v) -> {
            txturl = findViewById(R.id.edtxturl1);
            String usrentry = txturl.getText().toString();
            String[] str = usrentry.split("www.");
            String url = str[str.length - 1];

            showPage("https://www." + url);
        });
        ImageButton bckbtn = (ImageButton) findViewById(R.id.back_btn1);
        bckbtn.setOnClickListener((View v) -> {
            if (myWebView.canGoBack()) {
                myWebView.goBack();
            }

        });
        ImageButton fwdbtn = (ImageButton) findViewById(R.id.fwd_btn1);
        fwdbtn.setOnClickListener((View v) -> {
            if (myWebView.canGoForward()) {
                myWebView.goForward();
            }

        });
        ImageButton homebtn = (ImageButton) findViewById(R.id.home_btn1);
        homebtn.setOnClickListener((View v) -> {

            showPage("https://google.com");
        });
        ImageButton refreshbtn = (ImageButton) findViewById(R.id.refresh_btn1);
        refreshbtn.setOnClickListener((View v) -> {

            myWebView.reload();
        });
        ImageButton addbkmk = (ImageButton) findViewById(R.id.addbkmk);
        addbkmk.setOnClickListener((View v) -> {
            Log.d("bk","book mark");
            // SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            String title = myWebView.getTitle();
            String current_url = myWebView.getUrl();
            boolean exists = false;
            for(History bkmks : bookmkSet){
                Log.d("bk","book mark 2 "+title +" , "+bkmks.getTitle());
                if(bkmks.getTitle().equalsIgnoreCase(title)){
                    Log.d("bk","book mark 2 "+title +" , "+bkmks.getTitle());
                    Toast.makeText(this,title+" already exists in the List",Toast.LENGTH_LONG);
                    exists = true;
                    return;
                }
            }
            if(!exists) {
                Log.d("bk","book mark 3 "+ title);
                bookmkSet.add(new History(current_url, new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis())), title));
            } });
        ImageButton histbtn = (ImageButton) findViewById(R.id.hist1);
        histbtn.setOnClickListener((View v) -> {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            String current_url;
            if (histList.size() > 0) {
                current_url = (histList.get(histList.size() - 1)).getUrl();
            } else {
                current_url = "https://google.com";
            }
            editor.putString("current_url", current_url);
            editor.apply();

            setContentView(R.layout.listhistory_list);
            Log.d("his", "here1");
            setupScreen();

            Button clrhist = findViewById(R.id.hisclr);
            clrhist.setOnClickListener((View v1) -> {
                MainActivity.this.clearHistory();

            });
            Button backbtn = findViewById(R.id.backbtn);
            backbtn.setOnClickListener((View v2) -> MainActivity.this.hisBack());

            Log.d("his", "step2");
        });

        ImageButton bokmkbtn = findViewById(R.id.bookmk1);
        Log.d("his", "step31");
        bokmkbtn.setOnClickListener((View v) -> {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            String current_url = (histList.get(histList.size() - 1)).getUrl();
            editor.putString("current_url", current_url);
            editor.apply();

            setContentView(R.layout.bookmark_list);
            Log.d("his", "here32");
            setupScreen2();

            Button hmbtn = findViewById(R.id.homebtn);
            hmbtn.setOnClickListener((View v2) -> {
                MainActivity.this.showhome();

            });




            Button backbtn = findViewById(R.id.backbtn);
            backbtn.setOnClickListener((View v2) -> MainActivity.this.hisBack());

            Log.d("his", "step2");
        });
    }

    private void hisBack() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String url = prefs.getString("current_url", "https://google.com");
        pagesetup();
        showPage(url);
    }
    private void showhome() {
              pagesetup();
        showPage("https://google.com");
    }

    private List<History> loadSharedPreferencesbookmkSet() {
        SharedPreferences histPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
//        String json = null;
        if (histPrefs.contains("MyBookMarks")) {
            String json = histPrefs.getString("MyBookMarks", "default");
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<History>>() {
                }.getType();
                bkmkset = gson.fromJson(json, type);
            }
        }
      /*  SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyBookMarks", null);

        if (json==null) {
*/
      //  bkmkset = new ArrayList<History>();
       /* } else {
            Type type =  new TypeToken<ArrayList<History>>() {

            }.getType();
            bkmkset = gson.fromJson(json, type);
        }*/
        return bkmkset;
    }

    public List<History> loadSharedPreferencesHistList() {

        SharedPreferences histPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
//        String json = null;
        if (histPrefs.contains("MyHistList")) {
            String json = histPrefs.getString("MyHistList", "default");
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<History>>() {
                }.getType();
                histList1 = gson.fromJson(json, type);
            }
        }
        // Type type = (Type) new TypeToken<List<History>>(){}.getType();
        //  List<History> histList2 = gson.fromJson(json, (java.lang.reflect.Type) type);
//        Log.d("his", json);
//        if (json.isEmpty()) {
//
//            histList1 = new ArrayList<History>();
//        } else {
//            Type type = new TypeToken<ArrayList<History>>() {
//
//            }.getType();
//            histList1 = gson.fromJson(json, type);
//        }
        return histList1;
    }

    private void clearHistory() {
        histList.clear();
        setupScreen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(histList);
        prefsEditor.putString("MyHistList", json);
        prefsEditor.apply();
        String json1 = gson.toJson(bookmkSet);
        prefsEditor.putString("MyBookMarks", json1);
        prefsEditor.commit();

        Log.d("his", "onstop");

    }

    @Override
    protected void onDestroy() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(histList);
        prefsEditor.putString("MyHistList", json);
        prefsEditor.apply();
        String json1 = gson.toJson(bookmkSet);
        prefsEditor.putString("MyBookMarks", json1);
        prefsEditor.commit();
        Log.d("his", "ondestroy");
        super.onDestroy();
    }


    private void showPage(String s) {
        Log.d("his", "step21");

        WebViewClient wvc = new WebViewClient();
        myWebView.setWebViewClient(wvc);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(s);
        int time = (int) (System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        Log.d("his", "step21 " + formatter.format(date));
        History his = new History(myWebView.getUrl(), formatter.format(date));
        histList.add(his);
        txturl = findViewById(R.id.edtxturl1);
        txturl.setText(myWebView.getUrl());
    }


    public void setupScreen() {

        ListView list = (ListView) findViewById(R.id.Listview);
//        setTitle(R.string.test_url);

        listAdapter = new ListViewCustomAdaptor(MainActivity.this, histList);


        list.setAdapter(listAdapter);
        Log.d("his", "step3");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pagesetup();
                showPage((histList.get(position)).getUrl());
                Log.d("his", "step5");
                Log.d("his", getString(R.string.item_clicked) + " " + position + " " + (histList.get(position)).getUrl());

            }


        });


    }

    public void setupScreen2() {

        ListView list = (ListView) findViewById(R.id.Listview);
//        setTitle(R.string.test_url);

        listAdapterBM = new ListViewCustomAdaptorBM(MainActivity.this, bookmkSet, (OnClickInMyAdapterListener) this);


        list.setAdapter(listAdapterBM);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pagesetup();
                showPage((bookmkSet.get(position)).getUrl());
                Log.d("his", "step5");
                Log.d("his", getString(R.string.item_clicked) + " " + position + " " + (bookmkSet.get(position)).getUrl());

            }


        });


    }

    @Override
    public void onremovebookmarkclicked(int i) {
        //  Log.d("his", "inMA to remove "+i);
        bookmkSet.remove(i);
        setupScreen2();
    }

}
