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

public class ScorePoster  extends AsyncTask<String,Void,String>{

    public ScorePoster(){

    }

    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String userId = (String)arg0[0];
            String score = (String)arg0[1];
            String time = (String)arg0[2];
            String mode = (String)arg0[3];

            String link="https://devweb2014.cis.strath.ac.uk/~emb12161/WAD/ATBSG/atbsginsert.php";
            String data  = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
            data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
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