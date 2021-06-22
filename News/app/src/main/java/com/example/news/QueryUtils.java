package com.example.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    private QueryUtils(){}
    static int TotalResult;

    public static List<News> fetchNewsData(String reqUrl){
        URL url = createUrl(reqUrl);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
        }

        List<News> news = extractFeaturesFromJson(jsonResponse);
        return news;
    }


    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse="";
        if(url==null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

        }catch (IOException e){

        } finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader= new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    private static List<News> extractFeaturesFromJson(String newsJson){
        if(TextUtils.isEmpty(newsJson)){
            return null;
        }

        List<News> news = new ArrayList<>();
        try{
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONArray newsArray = baseJsonResponse.getJSONArray("results");
            TotalResult = baseJsonResponse.getInt("totalResults");

            for(int i=0; i<newsArray.length(); i++){
              JSONObject currentNews = newsArray.getJSONObject(i);
              String title = currentNews.getString("title");
              String creator = currentNews.getString("creator");
              String description = currentNews.getString("description");
              String content = currentNews.getString("content");
              String pubDate = currentNews.getString("pubDate");
              String image_url = currentNews.getString("image_url");
              String link = currentNews.getString("link");
              News newsdata = new News(title, image_url, pubDate, creator, description, content, link);
              news.add(newsdata);
            }
        }catch (JSONException e){
        }
        return news;
    }
    public static int ArrayLength(){
        return TotalResult/10;
    }
}
