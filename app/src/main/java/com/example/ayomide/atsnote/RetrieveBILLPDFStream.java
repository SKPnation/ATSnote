package com.example.ayomide.atsnote;

import android.os.AsyncTask;

import com.example.ayomide.atsnote.Common.Common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveBILLPDFStream extends AsyncTask<String,Void,InputStream> {
    @Override
    protected InputStream doInBackground(String... strings) {
        InputStream inputStream = null;
        try{
            URL url = new URL( strings[0] );
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode()==200)
            {
                inputStream = new BufferedInputStream( urlConnection.getInputStream() );
            }
        }
        catch (IOException e)
        {
            return null;
        }
        return inputStream;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        Common.billView.fromStream( inputStream ).load();
    }
}
