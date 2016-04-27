package com.android.dianadu.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.*;
import java.net.*;

import org.xmlpull.v1.*;

import java.util.ArrayList;

/**
 * Created by dianadu on 4/21/16.
 */
public class FlickrFetcher {

    public static final String TAG = "FlickrFetcher";

    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID = "lastResultId";
    public static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    public static final String API_KEY = "57f016fdfec19c7ee2c128b563c02193";
    public static final String METHORD_GET_RECENT = "flickr.photos.getRecent";
    public static final String METHOD_SEARCH = "flickr.photos.search";
    public static final String PARAM_EXTRAS = "extras";
    public static final String PARAM_TEXT = "text";
    public static final String EXTRA_SMALL_URL = "url_s";

    public static final String XML_PHOTO = "photo";

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

    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
//            String url = Uri.parse(ENDPOINT).buildUpon()
//                    .appendQueryParameter("method", METHORD_GET_RECENT)
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
//                    .build().toString();
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml:" + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));

            parseItems(items, parser);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (XmlPullParserException xmlexp) {
            Log.e(TAG, "Failed to parse items", xmlexp);
        }
        return items;
    }

    public ArrayList<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHORD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();
        return downloadGalleryItems(url);
    }

    public ArrayList<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();
        return downloadGalleryItems(url);
    }

    public void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                GalleryItem item = new GalleryItem();
                item.setmId(id);
                item.setmCaption(caption);
                item.setmUrl(smallUrl);
                items.add(item);
            }
            eventType = parser.next();
        }
    }
}
