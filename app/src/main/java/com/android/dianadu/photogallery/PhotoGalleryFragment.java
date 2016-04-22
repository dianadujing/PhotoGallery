package com.android.dianadu.photogallery;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.view.*;
import android.os.*;

import java.io.IOException;

/**
 * Created by dianadu on 4/21/16.
 */
public class PhotoGalleryFragment extends Fragment{
    GridView mGridView;

    private static final String TAG = "PhotoGalleryFragment";

    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... Params) {
//            try{
//                String result = new FlickrFetcher().getUrl("http://www.baidu.com");
//                Log.i(TAG, "Fetched contents of URL:" + result);
//            } catch (IOException e) {
//                Log.e(TAG, "Failed to fetch exception", e);
//            }
            new FlickrFetcher().fetchItems();
            return null;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new FetchItemsTask().execute();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);
        return view;
    }
}
