package com.example.news;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int News_Loader_Id = 1;
    private NewsAdapter nAdapter;

    private ImageButton arrowBack;
    private ImageButton arrowForward;
    private static int PageNum;
    private TextView pageData;

    private int TotalPages;
    private TextView mEmptyStateTextView;

    String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        TotalPages=QueryUtils.ArrayLength()+1;
        Intent intent = getIntent();
        url = intent.getStringExtra("UrlLink");

        pageData = findViewById(R.id.page_data);
//        pageData.setText("Page : "+(PageNum+1) + "/" +(TotalPages));
        pageData.setText("Page : "+ (PageNum+1));

        ListView NewsListView = (ListView) findViewById(R.id.list);
        nAdapter = new NewsAdapter(this, new ArrayList<News>());
        NewsListView.setAdapter(nAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        NewsListView.setEmptyView(mEmptyStateTextView);

        NewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = nAdapter.getItem(position);
                Uri NewsUri = Uri.parse(currentNews.getnUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, NewsUri);
                startActivity(websiteIntent);
            }
        });

        arrowBack = (ImageButton)findViewById(R.id.arrow_back);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PageNum>=0) {
                    finish();
                    String suburl = MainActivity.getSubUrl();
                    if(PageNum!=0) {
                        PageNum = MainActivity.getBackPageNum();
                    }
                    String newUrl = suburl + (PageNum);
                    intent.removeExtra("UrlLink");
                    intent.putExtra("UrlLink", newUrl);
                    startActivity(getIntent());
                }
//                if(PageNum==0){
//                    arrowBack.setVisibility(View.GONE);
//                    arrowBack.setEnabled(false);
//                }
            }
        });

        arrowForward = findViewById(R.id.arrow_forward);
        arrowForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PageNum<TotalPages) {
                    finish();
                    String suburl = MainActivity.getSubUrl();
                    PageNum = MainActivity.getForwardPageNum();
                    String newUrl = suburl + (PageNum);
                    intent.removeExtra("UrlLink");
                    intent.putExtra("UrlLink", newUrl);
                    startActivity(getIntent());
                }

//                if(PageNum==TotalPages){
//                    arrowForward.setVisibility(View.GONE);
//                    arrowForward.setEnabled(false);
//                }
            }
        });

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(News_Loader_Id, null, this);
    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        String noNews = "\t\t\t\tNo news found\n\n\n\n" + "1) Check Internet Connection\n\n" +"2) Check topic search \n\n" + "3) Check API Key\n\n" + "4) Regenerate API Key\n\n";
        mEmptyStateTextView.setText(noNews);

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        nAdapter.clear();

        if(news!=null && !news.isEmpty()){
            nAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
            nAdapter.clear();
    }
}
