package in.lemonco.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsApiRequest.AsyncResponse {
    private ArrayList<News> mNews;
    private static final String SEARCH_QUERY ="Entreprenuership";


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
            JSONArray jArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject fields = jArray.getJSONObject(i).getJSONObject("fields");
                String webUrl = jArray.getJSONObject(i).getString("webUrl");
                String headline = fields.getString("headline");
                String thumbnail = fields.getString("thumbnail");
                JSONArray contributors = fields.getJSONArray("tags");
                ArrayList<String> reporters= new ArrayList<String>();  //List of contributors or reporters
                for(int j =0; j< contributors.length(); j++){
                    JSONObject tags = contributors.getJSONObject(j);
                    reporters.add(tags.getString("webTitle"));
                }
                mNews.add(new News(headline,reporters,thumbnail,webUrl));
            }
            //set listview adapter
            ListView listView = (ListView)findViewById(R.id.main_news_list);
            NewsAdapter newsAdapter = new NewsAdapter(this,mNews);

            listView.setAdapter(newsAdapter);

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
