package in.lemonco.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * AsynTask subclass for accessing Guardian News API, passes the returned JSON object back to MainActivity class.
 */
public class NewsApiRequest extends AsyncTask<String,Object,JSONObject> {
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    //interface to pass the output data back to BookListActivity
    public interface AsyncResponse{
        void processFinish(JSONObject output);
    }
    public AsyncResponse delegate = null;
    public NewsApiRequest(Context context){
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Check network connection.
        if(isNetworkConnected() == false){
            // Cancel request.
            Log.i(getClass().getName(), "Not connected to the internet");
            cancel(true);
            return;
        }
    }
    @Override
    protected JSONObject doInBackground(String... searchQuery) {
        // Stop if cancelled
        if(isCancelled()){
            return null;
        }
        String finalsearchQuery = searchQuery[0].replace(" ","%20");
        String apiUrlString = "https://content.guardianapis.com/search?q="+finalsearchQuery+"&format=json&show-fields=thumbnail,headline&show-tags=contributor&api-key=test" ;
        Log.i("search query", finalsearchQuery);

        try{
            HttpURLConnection connection = null;
            // Build Connection.
            try{
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds
                Log.i("Connection Status","Conncetionsuccess");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(getClass().getName(), "Response String: " + responseString);
            Log.i("SUCCESS", "Eagle has landed");
            Log.i("search query",finalsearchQuery);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();
            return responseJson;
        } catch (SocketTimeoutException e) {
            Log.w(getClass().getName(), "Connection timed out. Returning null");
            return null;
        } catch(IOException e){
            Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(JSONObject responseJson) {
        super.onPostExecute(responseJson);
        if(isCancelled()){
            // Request was cancelled due to no network connection.
            Log.i("GoogleBooksApiRequest","NO CONNECTION");
        } else if(responseJson == null){
            Log.i("GoogleBooksAPiRequest", "NO RESULTS");

            //showSimpleDialog(getResources().getString(R.string.dialog_null_response));
        }
        else{
            Log.i("JSON CREATED","ALL IS WELL");

            delegate.processFinish(responseJson); //passes the output data back to BookList Activity
        }
    }


    protected boolean isNetworkConnected(){

        // Instantiate mConnectivityManager if necessary
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        } else {
            return false;
        }
    }
}
