package com.example.amosh.newsfeedguardian;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<Feed> {

    public FeedAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final Feed feed = getItem(position);
        View listItemView = view;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        TextView section = (TextView) view.findViewById(R.id.article_section);
        section.setText(feed.getmArticleSection());

        TextView title = (TextView) view.findViewById(R.id.article_title);
        title.setText(feed.getmArticleTitle());

        TextView date = (TextView) view.findViewById(R.id.article_date);
        date.setText(feed.getmArticleDate());

        return view;
    }

}

