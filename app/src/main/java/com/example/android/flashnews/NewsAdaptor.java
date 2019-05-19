package com.example.android.flashnews;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdaptor extends ArrayAdapter<NewsObject> {

    private Context mContext;



    public NewsAdaptor(Context context, List<NewsObject> newsList) {
        super(context, 0, newsList);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        NewsObject currentNews = getItem(position);
        ImageView image = (ImageView) listItemView.findViewById(R.id.article_image);
        TextView sectionNameTextView = (TextView) listItemView.findViewById(R.id.section_name);
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.article_title);
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.published_date);
        View container_01 = listItemView.findViewById(R.id.container_01);
        View container_02 = listItemView.findViewById(R.id.container_02);
        View container_03 = listItemView.findViewById(R.id.container_03);

        sectionNameTextView.setText(currentNews.getSectionName() + " :");
        titleTextView.setText(currentNews.getWebTitle());
        authorTextView.setText(currentNews.getContributor());

        String date = formatDate(currentNews.getWebPublicationDate());
        dateTextView.setText(date);

        if(!currentNews.getThumbnailUrl().equalsIgnoreCase("")){
            image.setVisibility(View.VISIBLE);
            Picasso.get().load(currentNews.getThumbnailUrl()).resize(120, 120).centerCrop().into(image);
        }else {
            image.setVisibility(View.GONE);
        }

        int sectionColor = getSectionColor(currentNews.getSectionName());
        sectionNameTextView.setBackgroundColor(sectionColor);
        container_01.setBackgroundColor(sectionColor);
        container_02.setBackgroundColor(sectionColor);
        container_03.setBackgroundColor(sectionColor);

        return listItemView;
    }


    private String formatDate(String webPublicationDateString) {
        String date = webPublicationDateString.substring(0, webPublicationDateString.indexOf("Z"));
        SimpleDateFormat webPublicationDateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date webPublicationDate;
        try {
            webPublicationDate = webPublicationDateFormater.parse(webPublicationDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat showedDateFormater = new SimpleDateFormat("LLL dd, yyyy");
        return showedDateFormater.format(webPublicationDate);
    }

    private int getSectionColor(String sectionName) {
        int categoryColorId;
        if (mContext.getString(R.string.news_section_container).contains(sectionName)) {
            categoryColorId = R.color.news;
        } else if (mContext.getString(R.string.opinion_section_container).contains(sectionName)) {
            categoryColorId = R.color.opinion;
        } else if (mContext.getString(R.string.sports_section_container).contains(sectionName)) {
            categoryColorId = R.color.sports;
        } else if (mContext.getString(R.string.culture_section_container).contains(sectionName)) {
            categoryColorId = R.color.culture;
        } else if (mContext.getString(R.string.lifestyle_section_container).contains(sectionName)) {
            categoryColorId = R.color.lifestyle;
        } else {
            categoryColorId = R.color.unclassified;
        }

        return ContextCompat.getColor(getContext(), categoryColorId);
    }

}
