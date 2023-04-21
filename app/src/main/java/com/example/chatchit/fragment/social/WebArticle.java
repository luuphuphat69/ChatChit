package com.example.chatchit.fragment.social;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebArticle {
    private String title;
    private String thumbnail;

    public WebArticle( String title ,String thumbnail) {
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public WebArticle() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail( String thumbnail ) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "WebArticle{" +
                "title='" + title + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
