package com.example.amosh.newsfeedguardian;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed implements Parcelable {
    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };
    private String mUrl;
    private String mArticleTitle;
    private String mArticleDate;
    private String mArticleSection;

    public Feed (String url,String articleTitle,String articleDate,String articleSection){
        mUrl = url;
        mArticleTitle = articleTitle;
        mArticleDate = articleDate;
        mArticleSection = articleSection;
    }
    private Feed(Parcel in) {
        mUrl = in.readString();
        mArticleTitle = in.readString();
        mArticleSection = in.readString();
        mArticleDate = in.readString();
    }

    public String getmArticleDate() {
        return mArticleDate;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmArticleTitle() {
        return mArticleTitle;
    }

    public String getmArticleSection() {
        return mArticleSection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mArticleDate);
        parcel.writeString(mArticleSection);
        parcel.writeString(mArticleTitle);
        parcel.writeString(mUrl);
    }
}

