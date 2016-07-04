package in.lemonco.newsapp;

import java.util.ArrayList;

/**
 * Class define the News class.
 */
public class News {
    private String mwebUrl;
    private String mHeadline;
    private String mThumbnail;
    private ArrayList<String> mContributor;

    public News(String headline,ArrayList<String> contributor,String thumbNail,String webUrl){
        mwebUrl=webUrl;
        mHeadline=headline;
        mThumbnail=thumbNail;
        mContributor=contributor;
    }

    //getter methods
    public String getMwebUrl() {
        return mwebUrl;
    }

    public String getmHeadline() {
        return mHeadline;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public String getmContributor() {
        String contributors="";
        for(String contributor:mContributor){
            contributors += contributor +"\n";
        }
        return contributors;
    }
}
