package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

public class ApiRegistration2 extends AppCompatActivity {

    private EditText userNameET;
    private EditText userPassword1ET;
    private EditText userPassword2ET;

    private String temporayEmailId;
    private String userName;
    private String userPassword;
    private String userPassword1;
    private String userPassword2;

    private WebView mailView;
    private TextView finalApiKeyTV;
    private TextView loginTV;
    private LinearLayout layout;
    private Button formSubmitButton;
    private Button continueButton;
    private TextView title;

    private String refreshUrl;
    String Testing;
    private String mAPIKey = "";
    private int loginFlag = 0;
    private int flag = 0;
    private static final String FILE_NAME = "api_key.txt";
    private int apiFlag = 0;
    private String ConfTest;

    private static final String tempMailUrl = "https://email-fake.com/";
    private static final String loginUrl = "https://newsdata.io/login";
    private static final String registrationUrl = "https://newsdata.io/register";
    private static final String newsdataLogout = "https://newsdata.io/logout";
    private static String confUrl = "";
    private String EmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_registration2);

        userNameET = findViewById(R.id.name_edittext);
        userPassword1ET = findViewById(R.id.password1_edittext);
        userPassword2ET = findViewById(R.id.password2_edittext);

        mailView = findViewById(R.id.temp_mail_webview);
        finalApiKeyTV = findViewById(R.id.final_api_key);
        loginTV = findViewById(R.id.loginButton);
        layout = findViewById(R.id.form_layout);
        formSubmitButton = findViewById(R.id.submit_apiform);
        continueButton = findViewById(R.id.continue_button);
        title = findViewById(R.id.title);

        mailView.getSettings().setLoadsImagesAutomatically(true);
        mailView.getSettings().setLoadWithOverviewMode(true);
        mailView.getSettings().setDomStorageEnabled(true);
        mailView.getSettings().setJavaScriptEnabled(true);
        mailView.getSettings().setLoadWithOverviewMode(true);
        mailView.getSettings().setUseWideViewPort(true);
        mailView.setWebViewClient(new WebViewClient());
        mailView.loadUrl(tempMailUrl);

        mailView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);
    }


    @SuppressLint("ResourceAsColor")
    public void LoginButton(View v) {
        if (flag == 0) {
            flag = 1;
            loginFlag = 1;
            userName = "";
            userPassword2 = userPassword1;
            userPassword2ET.setEnabled(false);
            userPassword2ET.setBackgroundColor(R.color.gray);
            userNameET.setBackgroundColor(R.color.gray);
            userNameET.setEnabled(false);
            loginTV.setText("Register your self!!");
            title.setText("API Login");
        } else if (flag == 1) {
            finish();
            startActivity(getIntent());
        }
    }

    public void SubmitRegForm(View v) {
        Toast.makeText(ApiRegistration2.this, "Please wait!!", Toast.LENGTH_LONG).show();
        if (loginFlag == 0) {
            userName = userNameET.getText().toString();
        }
        userPassword1 = userPassword1ET.getText().toString();
        if (loginFlag == 0) {
            userPassword2 = userPassword2ET.getText().toString();
        } else {
            userPassword2 = userPassword1;
        }

        if (flag == 1) {
            Toast.makeText(ApiRegistration2.this, "Please wait!!\nLogging you in", Toast.LENGTH_LONG).show();
        }

        if (!userPassword1.equals(userPassword2)) {
            Toast.makeText(this, "Password Mismatch \n", Toast.LENGTH_SHORT).show();
            return;
        }
        userPassword = userPassword1;

        if (userPassword.length() < 7) {
            Toast.makeText(this, "Password too short \nMinumum 7 characters required", Toast.LENGTH_SHORT).show();
            return;
        }
        formSubmitButton.setText("Please wait!!");
        formSubmitButton.setEnabled(false);

        new WebRegistration(ApiRegistration2.this).execute();

        new WebRegistration(ApiRegistration2.this) {
            @Override
            public void doInBackground() {

                try {
                    HttpConnection.Response tempMailId = (HttpConnection.Response) Jsoup.connect(tempMailUrl).method(Connection.Method.GET).timeout(10000).execute();
                    Document mail = tempMailId.parse();
                    EmailId = mail.select("span[id=email_ch_text]").text();
                    temporayEmailId = EmailId;
                    HttpConnection.Response registrationForm = (HttpConnection.Response) Jsoup.connect(registrationUrl).method(Connection.Method.GET).timeout(10000).execute();
                    Document doc = registrationForm.parse();
                    String tokenValue = doc.select("input[name=_token]").attr("value");
                    doc = Jsoup.connect(registrationUrl).data("name", userName).data("email", temporayEmailId).data("_token", tokenValue).data("password", userPassword).data("password_confirmation", userPassword).cookies(registrationForm.cookies()).post();
                    Testing = doc.select("input[id=remember]").attr("name");
                } catch (Exception e) {
                    Log.e("App", "APIRegistration " + e);
                }

            }

            @Override
            public void onPostExecute() {
                if (Testing.equals("remember") && flag==0) {
                    register();
                }
                else if(flag==0 && !Testing.equals("remember")){
                        finalApiKeyTV.setText("Registration Failed\nCheck credentials");
                        return;
                }

                if(flag==1){
                    login();
                }
                super.onPostExecute();
            }
        }.execute();
    }

    public class WebRegistration {
        private Activity activity;

        public WebRegistration(Activity activity) {
            this.activity = activity;
        }

        public void startBackground() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doInBackground();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        public void execute() {
            startBackground();
        }

        public void doInBackground() {
        }

        public void onPostExecute() {
        }

    }

    public void save_api_key() {

        continueButton.setEnabled(false);
        String text = mAPIKey;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            Intent intent = new Intent(ApiRegistration2.this, MainActivity.class);
            startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void register() {
        Toast.makeText(ApiRegistration2.this, "Registration Successful\nPlease wait for Verification", Toast.LENGTH_LONG).show();
        continueButton.setText("Press to get API-Key");
        continueButton.setVisibility(View.VISIBLE);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueButton.setEnabled(false);
                continueButton.setText("Please Wait!!");
                refresh();
            }
        });
    }

            private void login() {
            new WebRegistration(ApiRegistration2.this){
            @Override
            public void doInBackground() {
                try {
                    HttpConnection.Response loginForm = (HttpConnection.Response) Jsoup.connect(loginUrl).method(Connection.Method.GET).timeout(10000).execute();
                    Document Ldoc = loginForm.parse();
                    String LtokenValue = Ldoc.select("input[name=_token]").attr("value");
                    String redirectToValue = Ldoc.select("input[name=redirectTo]").attr("value");

                    Ldoc = Jsoup.connect(loginUrl).data("email", temporayEmailId).data("password", userPassword).data("_token", LtokenValue).data("redirectTo", redirectToValue).data("submit", "Log in").cookies(loginForm.cookies()).post();
                    mAPIKey = Ldoc.select("input[id=nwd-api-key]").attr("value");
                }catch (Exception e) {
                    Log.e("App", "APIRegistration " + e);
                }
            }

            @Override
            public void onPostExecute() {
                formSubmitButton.setText("API Key");
                mailView.loadUrl(newsdataLogout);
                if(mAPIKey.length()<3){
                    finalApiKeyTV.setText("Login Failed\nCheck credentials");
                    return;
                }

                finalApiKeyTV.setText(mAPIKey);
                mailView.setVisibility(View.GONE);
                formSubmitButton.setEnabled(false);
                continueButton.setVisibility(View.VISIBLE);
                continueButton.setEnabled(true);
                Toast.makeText(ApiRegistration2.this, "Save API-KEY for future refrence!!", Toast.LENGTH_LONG).show();
                continueButton.setText("Save API KEY");
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save_api_key();
                    }
                });
                super.onPostExecute();
            }
        }.execute();
    }

    private void refresh(){
     new WebRegistration(ApiRegistration2.this){
         @Override
         public void doInBackground() {
             try{
                 refreshUrl = "https://email-fake.com/"+EmailId;
                 HttpConnection.Response refreshHtmlForm = (HttpConnection.Response) Jsoup.connect(refreshUrl).method(Connection.Method.GET).timeout(10000).execute();
                 Document refreshDoc = refreshHtmlForm.parse();
                 Element confirmation = refreshDoc.select("a[href^=h]").first();
                 confUrl = confirmation.attr("href");
             } catch (Exception e) {
                 Log.e("App", "APIRegistration " + e);
             }
         }

         @Override
         public void onPostExecute() {
             if(confUrl.length()>8){
                 confirmUrl();
             }
             else{
                 refresh();
             }
             super.onPostExecute();
         }
     }.execute();
    }

    public void confirmUrl(){
        mailView.loadUrl(confUrl);
        new WebRegistration(ApiRegistration2.this){
            @Override
            public void doInBackground() {
                try{
                    HttpConnection.Response confirmation = (HttpConnection.Response) Jsoup.connect(confUrl).method(Connection.Method.GET).timeout(10000).execute();
                    Document confForm = confirmation.parse();
                    ConfTest = confForm.select("input[id=remember]").attr("name");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                if(ConfTest.equals("remember")){
                    Toast.makeText(ApiRegistration2.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    login();
                }
                else{
                    Toast.makeText(ApiRegistration2.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                super.onPostExecute();
            }
        }.execute();
    }
}