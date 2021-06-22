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

public class Test extends AppCompatActivity {

    private EditText temporaryEmailidET;
    private EditText userNameET;
    private EditText userPassword1ET;
    private EditText userPassword2ET;
    private TextView genTempMail;

    private String temporayEmailId;
    private String userName;
    private String userPassword;
    private String userPassword1;
    private String userPassword2;
    private String EmailId;

    private WebView mailView;
    private TextView finalApiKeyTV;
    private TextView loginTV;
    private LinearLayout layout;
    private Button formSubmitButton;
    private Button continueButton;
    private TextView title;

    String Testing;
    private String mAPIKey = "";
    private int loginFlag = 0;
    private int flag = 0;
    private static final String FILE_NAME = "api_key.txt";
    private int apiFlag = 0;

    private static final String tempMailUrl = "https://email-fake.com/";
    private static final String loginUrl = "https://newsdata.io/login";
    private static final String registrationUrl = "https://newsdata.io/register";
    private static final String newsdataLogout = "https://newsdata.io/logout";
    private static String confUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_registration2);

//        temporaryEmailidET = findViewById(R.id.email_id_edittext);
        userNameET = findViewById(R.id.name_edittext);
        userPassword1ET = findViewById(R.id.password1_edittext);
        userPassword2ET = findViewById(R.id.password2_edittext);
//        genTempMail = findViewById(R.id.gen_temp_mail);

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

    public void generate_temp_id(View v) {
        mailView.setVisibility(View.VISIBLE);
//        mailView.scrollTo(0,1000);
        layout.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        Toast.makeText(Test.this, "Copy Mail Id\nPress Continue", Toast.LENGTH_LONG).show();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToForm();
            }
        });
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
            genTempMail.setVisibility(View.INVISIBLE);
            title.setText("API Login");
        } else if (flag == 1) {
            finish();
            startActivity(getIntent());
        }
    }

    public void SubmitRegForm(View v) {
        Toast.makeText(Test.this, "Please wait!!", Toast.LENGTH_LONG).show();
//        temporayEmailId = temporaryEmailidET.getText().toString();
//        if (loginFlag == 0) {
            userName = userNameET.getText().toString();
//        }
        userPassword1 = userPassword1ET.getText().toString();
//        if (loginFlag == 0) {
            userPassword2 = userPassword2ET.getText().toString();
//        } else {
//            userPassword2 = userPassword1;
//        }

        if (flag == 1) {
            Toast.makeText(Test.this, "Please wait!!\nLogging you in", Toast.LENGTH_LONG).show();
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

        new WebRegistration(Test.this).execute();

        new WebRegistration(Test.this) {
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
                    doc = Jsoup.connect(registrationUrl).data("name", userName).data("email", EmailId).data("_token", tokenValue).data("password", userPassword).data("password_confirmation", userPassword).cookies(registrationForm.cookies()).post();
                    Testing = doc.select("input[id=remember]").attr("name");

                    Elements confirmation = mail.select("a");
                    for(int i=0; i<confirmation.size(); i++){
                        String temp = confirmation.get(i).attr("href");
                        if(temp.startsWith("https://newsdata.io/user/verify")){
                            confUrl = temp;
                        }
                    }

                    HttpConnection.Response loginForm = (HttpConnection.Response) Jsoup.connect(loginUrl).method(Connection.Method.GET).timeout(10000).execute();
                    Document Ldoc = loginForm.parse();
                    String LtokenValue = Ldoc.select("input[name=_token]").attr("value");
                    String redirectToValue = Ldoc.select("input[name=redirectTo]").attr("value");

                    Ldoc = Jsoup.connect(loginUrl).data("email", EmailId).data("password", userPassword).data("_token", LtokenValue).data("redirectTo", redirectToValue).data("submit", "Log in").cookies(loginForm.cookies()).post();
                    mAPIKey = Ldoc.select("input[id=nwd-api-key]").attr("value");

                } catch (Exception e) {
                    Log.e("App", "APIRegistration " + e);
                }
            }

            @Override
            public void onPostExecute() {
                if(Testing.equals("remember")) {
                    register();
                }
                Toast.makeText(Test.this,  Testing + "\n"  + EmailId +"\n" + userPassword + "\n" + confUrl, Toast.LENGTH_LONG).show();

//                Toast.makeText(ApiRegistration2.this, Testing + "  " + flag, Toast.LENGTH_SHORT).show();
//                if (Testing.equals("remember") && flag==0) {
//                    register();
//                    formSubmitButton.setEnabled(true);
////                    if(flag==0) {
//                        Toast.makeText(ApiRegistration2.this, "Registration Successful \nCheck Inbox downwards &\nConfirm your email address", Toast.LENGTH_LONG).show();
//                        mailView.setVisibility(View.VISIBLE);
////                        mailView.scrollTo(0,1000);
////                    }
////                    if(flag==1 || flag==0){
//                        continueButton.setText("Press to get API-Key");
////                    }
//                    continueButton.setVisibility(View.VISIBLE);
//                    continueButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            formSubmitButton.setText("API Key");
//                            mailView.loadUrl(newsdataLogout);
////                            if(mAPIKey.length()>0) {
//                                finalApiKeyTV.setText(mAPIKey);
////                            }
////                            else{
////                                finalApiKeyTV.setText("Registration failed");
////                            }
//                            mailView.setVisibility(View.GONE);
//                            formSubmitButton.setEnabled(false);
//                            continueButton.setVisibility(View.VISIBLE);
//                            if(apiFlag==0) {
//                                Toast.makeText(ApiRegistration2.this, "Save API-KEY for future refrence!!", Toast.LENGTH_LONG).show();
//                                continueButton.setText("Save API KEY");
//                            }
//                            else if(apiFlag==-1){
//                                continueButton.setEnabled(false);
//                            }
//                            continueButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    save_api_key();
//                                }
//                            });
//                        }
//                    });
//                }
//                else if(flag==0 && !Testing.equals("remember")){
//                    finalApiKeyTV.setText("Registration Failed\nCheck credentials");
//                    return;
//                }
//
//                if(flag==1){
//                    login();
//                }
                super.onPostExecute();
            }
        }.execute();
    }

    public void BackToForm() {
        mailView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);
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
        String text = mAPIKey;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            Intent intent = new Intent(Test.this, MainActivity.class);
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
        formSubmitButton.setEnabled(true);
        Toast.makeText(Test.this, "Registration Successful \nCheck Inbox downwards &\nConfirm your email address", Toast.LENGTH_LONG).show();
        mailView.setVisibility(View.VISIBLE);
        continueButton.setText("Press to get API-Key");
        continueButton.setVisibility(View.VISIBLE);
//        continueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                formSubmitButton.setText("API Key");
//                mailView.loadUrl(newsdataLogout);
////                            if(mAPIKey.length()>0) {
//                finalApiKeyTV.setText(mAPIKey);
////                            }
////                            else{
////                                finalApiKeyTV.setText("Registration failed");
////                            }
//                mailView.setVisibility(View.GONE);
//                formSubmitButton.setEnabled(false);
//                continueButton.setVisibility(View.VISIBLE);
////                if(apiFlag==0) {
//                Toast.makeText(ApiRegistration2.this, "Save API-KEY for future refrence!!", Toast.LENGTH_LONG).show();
//                continueButton.setText("Save API KEY");
////                }
////                else if(apiFlag==-1){
////                    continueButton.setEnabled(false);
////                }
//                continueButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        save_api_key();
//                    }
//                });
//            }
//                login();
//            });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Test.this, "Registration successful\nLogin to get API-KEY", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void login() {
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
//                if(apiFlag==0) {
        Toast.makeText(Test.this, "Save API-KEY for future refrence!!", Toast.LENGTH_LONG).show();
        continueButton.setText("Save API KEY");
//                }
//                else if(apiFlag==-1){
//                    continueButton.setEnabled(false);
//                }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_api_key();
            }
        });
    }
}