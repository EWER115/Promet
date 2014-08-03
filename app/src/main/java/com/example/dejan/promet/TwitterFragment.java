package com.example.dejan.promet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by Dejan on 3.8.2014.
 */
public class TwitterFragment extends Fragment {

    private static final String baseURL = "https://twitter.com";
    private static final String widgetInfo = "<center><a class=\"twitter-timeline\" href=\"https://twitter.com/DARS_SI\" data-widget-id=\"495894829583179776\">Tweets by @DARS_SI</a>\n" +
            "<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.twitter_fragment, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.twitter);
        webView.setBackgroundColor(0);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(baseURL, widgetInfo,"text/html", "UTF-8", null);

        return rootView;

    }

}
