package com.example.news;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {
    static String ImageId;
    static String noImageFound = "data:image/png;base64,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";
    public NewsAdapter(Activity context, ArrayList<News> News) {
        super(context, 0, News);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_view, parent, false);
        }

        News currentNews = getItem(position);

        String Title = currentNews.getnTitle();

        ImageId = currentNews.getnImageSrc();
        if(ImageId=="null"){
            ImageId = noImageFound;
        }

        String PubDate = currentNews.getnPubDate();
        if(PubDate=="null"){
            PubDate="No data avialable";
        }
        String Creator = currentNews.getnCreator();
        if(Creator=="null"){
            Creator = "No creator found";
        }
        Creator = Creator.replace("[","");
        Creator = Creator.replace("]","");

        String Description = currentNews.getnDescription();
        String Content = currentNews.getnContent();

        if(Content=="null"){
            Content=Description;
        }


        Description="";

        if(Content=="null"){
            Content="";
        }

        if(Content.length()>400) {
            Content = Content.substring(0, 400);
        }

        TextView TitleView = listView.findViewById(R.id.title);
        TitleView.setText(Title);

        TextView PubDateView = listView.findViewById(R.id.pub_date);
        PubDateView.setText(PubDate);

        TextView CreatorView = listView.findViewById(R.id.creator);
        CreatorView.setText(Creator);

        TextView DescriptionView = listView.findViewById(R.id.description);
        DescriptionView.setText(Description);

        TextView ContentView = listView.findViewById(R.id.content);
        ContentView.setText(Content);

        Context context = getContext();
        ImageView imageV = listView.findViewById(R.id.image);

        Glide.with(context).load(ImageId).into(imageV);

        return listView;
    }
}
