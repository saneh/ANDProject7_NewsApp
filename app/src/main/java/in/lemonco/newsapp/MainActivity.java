package in.lemonco.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsApiRequest.AsyncResponse {
    private ArrayList<News> mNews;
    private static final String SEARCH_QUERY ="artificial intelligence";
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewsApiRequest newsApiRequest = new NewsApiRequest(this);
        newsApiRequest.delegate =this; //to set delegate/listener back to this class
        newsApiRequest.execute(SEARCH_QUERY);



    }

    //this methods is call in onPostExecute() method of AsyncTask ( GoogleBooksApiRequest)
    public void processFinish(JSONObject jsonObject){
        try {
            mNews=new ArrayList<News>();
            JSONArray jArray = jsonObject.getJSONObject("response").getJSONArray("results");
            for(int i = 0; i < jArray.length(); i++){
                String webUrl = jArray.getJSONObject(i).optString("webUrl");
                JSONObject fields = jArray.getJSONObject(i).getJSONObject("fields");
                String headline = fields.optString("headline");
                String thumbnail = fields.optString("thumbnail");
                JSONArray tags = jArray.getJSONObject(i).getJSONArray("tags");
                ArrayList<String> contributor_list=new ArrayList<>();
                for(int j =0; j< tags.length(); j++){
                    String contributor = tags.getJSONObject(j).optString("webTitle");
                    contributor_list.add(contributor);
                }
                mNews.add(new News(headline,contributor_list,thumbnail,webUrl));
            }
            //set listview adapter
            mListView = (ListView)findViewById(R.id.main_news_list);
            NewsAdapter newsAdapter = new NewsAdapter(this,mNews);

            mListView.setAdapter(newsAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = mNews.get(position).getMwebUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

        }catch(JSONException e)
        {
            Log.i("IN PROCESS FINISH", "SOMETHING WRONG WITH PARSNING");
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
