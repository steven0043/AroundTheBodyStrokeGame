package com.atbsg.atbsg;

/**
 * Created by Steven on 01/02/2016.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.os.AsyncTask;

public class ScorePoster extends AsyncTask<String,Void,String>{

    public ScorePoster(){

    }

    protected void onPreExecute(){

    }

    /**
     * Sends the scores to the MySQL database in the background.
     * @param arg0
     * @return
     */
    @Override
    protected String doInBackground(String... arg0) {
        try{
            String userId = (String)arg0[0];
            String score = (String)arg0[1];
            String mode = (String)arg0[2];

            String link="https://devweb2014.cis.strath.ac.uk/~emb12161/WAD/ATBSG/atbsginsert.php"; //The link to the php script that inserts the game daat
            String parameters  = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            parameters += "&" + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
            parameters += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8"); //Encode the game data

            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection(); //Open connection to the php page

            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

            wr.write(parameters); //Write the encoded parameters
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result){

    }
}