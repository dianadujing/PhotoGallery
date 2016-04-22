package com.android.dianadu.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.*;
import java.net.*;

/**
 * Created by dianadu on 4/21/16.
 */
public class FlickrFetcher {

    public static final String TAG = "FlickrFetcher";

    public static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    public static final String API_KEY = "57f016fdfec19c7ee2c128b563c02193";
    public static final String METHORD_GET_RECENT = "flickr.photos.getRecent";
    public static final String PARAM_EXTRAS = "extras";
    public static final String EXTRA_SMALL_URL = "url_s";
    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItems() {
        try {
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHORD_GET_RECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                    .build().toString();
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml:" + xmlString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }

    }
}
