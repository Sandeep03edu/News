package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    static EditText TopicName;
    static Button submitButton;
    static EditText ApiKey;

    String[] country = {"Select one option", "India", "United States", "United Kingdom", "Japan", "UAE", "South Africa", "Australia", "Canada", "Singapore"};
    String[] countryCode = {"", "in", "us", "gb", "jp", "ae", "sa", "au", "ca", "sg"};

    String[] language = {"Select one option","English", "Arabic", "Japanese" , "Indonesian", "Italian", "French"};
    String[] languageCode = {"","en", "ar", "jp", "in", "es", "fr"};

    String[] category = {"Select one option","Top", "Business" , "Science" , "Technology" , "Sports" , "Health" , "Entertainment"};
    String[] categoryCode = {"","top", "business" , "science" , "technology" , "sports" , "health" , "entertainment"};

    static String url = "https://newsdata.io/api/1/news?apikey=";
    static String UserAPIKEY = "";
    static String StringTopic="";
    static String StringCountry="";
    static String StringLanguage="";
    static String StringCategory="";
    static int PageNum;
    private static final String FILE_NAME = "api_key.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiKey = findViewById(R.id.api_edit_text);

        Spinner CountrySpinner = findViewById(R.id.country_spinner);
        CountrySpinner.setOnItemSelectedListener(this);

        Spinner CategorySpinner = findViewById(R.id.category_spinner);
        CategorySpinner.setOnItemSelectedListener(this);

        Spinner LanguageSpinner = findViewById(R.id.language_spinner);
        LanguageSpinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter CountrySpinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        CountrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        CountrySpinner.setAdapter(CountrySpinnerAdapter);

        ArrayAdapter CategorySpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, category);
        CategorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CategorySpinner.setAdapter(CategorySpinnerAdapter);

        ArrayAdapter LanguageSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, language);
        LanguageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LanguageSpinner.setAdapter(LanguageSpinnerAdapter);

        LoadAPiKey();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
        switch(arg0.getId()){
            case R.id.country_spinner:
                if(position!=0) {
                    StringCountry = "&country=" + countryCode[position];
                }
                else {
                    StringCountry = "";
                }
//                Toast.makeText(getApplicationContext(),country[position], Toast.LENGTH_SHORT).show();
                break;

            case R.id.category_spinner:
                if(position!=0) {
                    StringCategory = "&category=" + categoryCode[position];
                }
                else {
                    StringCategory = "";
                }
//                Toast.makeText(getApplicationContext(),category[position], Toast.LENGTH_SHORT).show();
                break;

            case R.id.language_spinner:
                if(position!=0){
                StringLanguage = "&language=" + languageCode[position];
                }
                else {
                    StringLanguage = "";
                }
//                Toast.makeText(getApplicationContext(),language[position], Toast.LENGTH_SHORT).show();
                break;
        }

        submitButton = (Button)findViewById(R.id.data_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicName = findViewById(R.id.topic_edit_text);
                UserAPIKEY = ApiKey.getText().toString();
                StringTopic="";
                if(TopicName.length()>0) {
                    StringTopic = "&q=" + TopicName.getText().toString();
                    StringTopic = StringTopic.replace(" ", "%20");
                }
                PageNum = 0;
                String StringPage="&page="+ PageNum;

                if(UserAPIKEY.length()>3) {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("UrlLink", url + UserAPIKEY + StringTopic + StringCategory + StringCountry + StringLanguage + StringPage);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Please generate a valid API-KEY", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(MainActivity.this, url+UserAPIKEY+StringTopic+StringCategory+StringCountry+StringLanguage+StringPage, Toast.LENGTH_LONG).show();
                }

        });
    }

    public void load_api_key(View v) {
        LoadAPiKey();
    }

    public void LoadAPiKey(){
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            sb.toString();
            ApiKey.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void generate_api_key(View v){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

        if(isConnected()) {
            Toast.makeText(MainActivity.this, "Please wait!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ApiRegistration2.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(MainActivity.this, "No Internet Connection!!", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getBackPageNum(){
        PageNum--;
        return PageNum;
    }

    public static int getForwardPageNum(){
        PageNum++;
        return PageNum;
    }

    public static String getSubUrl(){
        return url+UserAPIKEY+StringTopic+StringCategory+StringCountry+StringLanguage+"&page=";
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}
}

