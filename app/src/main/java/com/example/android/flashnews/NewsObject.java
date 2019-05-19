package com.example.android.flashnews;

public class NewsObject {

    private String mSectionName;
    private String mWebPublicationDate;
    private String mWebTitle;
    private String mWebUrl;
    private String mHeadline;
    private String mThumbnailUrl;
    private String mContributor;
    static String searchWord;
    static int totalFindings;
    static int currentPage;
    static int totalNumberOfPages;


    public NewsObject(String sectionName, String webPublicationDate, String webTitle, String webUrl, String headline, String thumbnailUrl, String contributor) {
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mHeadline = headline;
        mThumbnailUrl = thumbnailUrl;
        mContributor = contributor;
    }

    public String getSectionName(){return mSectionName;}
    public String getWebPublicationDate(){return mWebPublicationDate;}
    public String getWebTitle(){return mWebTitle;}
    public String getWebUrl(){return mWebUrl;}
    public String getHeadline(){return mHeadline;}
    public String getThumbnailUrl(){return mThumbnailUrl;}
    public String getContributor(){return mContributor;}
    public int getTotalFindings(){return totalFindings;}
    public int getCurrentPage(){return currentPage;}
    public int getTotalNumberOfPages(){return totalNumberOfPages;}

    @Override
    public String toString() {
        return "NewsObject{" +
                "mSectionName='" + mSectionName + '\'' +
                ", mWebPublicationDate='" + mWebPublicationDate + '\'' +
                ", mWebTitle='" + mWebTitle + '\'' +
                ", mWebUrl='" + mWebUrl + '\'' +
                ", mHeadline='" + mHeadline + '\'' +
                ", mThumbnailUrl='" + mThumbnailUrl + '\'' +
                ", mContributor='" + mContributor + '\'' +
                '}';
    }
}

