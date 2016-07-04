package in.lemonco.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Custom arrayadapter for News objects
 */
public class NewsAdapter extends ArrayAdapter<News>{
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    private static class ViewHolder{
        TextView headline;
        ImageView thumbnail;
        TextView contributor;
    }
    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get data item for this position
        News currentNews = getItem(position);
        ViewHolder viewHolder;  //view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater= LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_list_item_layout, parent, false);
            viewHolder.headline=(TextView)convertView.findViewById(R.id.headline);
            viewHolder.contributor=(TextView)convertView.findViewById(R.id.contributor);
            viewHolder.thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //Populate data into the template view using the data object
        viewHolder.headline.setText(currentNews.getmHeadline());
        viewHolder.contributor.setText(currentNews.getmContributor());

        DownloadImageTask downloadImage = new DownloadImageTask(viewHolder.thumbnail);
        downloadImage.execute(currentNews.getmThumbnail());

        return convertView;


    }

    // show The Image in a ImageView

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
