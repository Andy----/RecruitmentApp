package recruitapp.ittproject3.com.recruitmentapp.helper;

/**
 * Created by Cloud on 13/04/2015.
 */
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageCache mImageCache;


    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(VolleyApplication.getAppContext());

        mImageLoader = new ImageLoader(this.mRequestQueue, mImageCache  = new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void flushLruCache(){ mCache.evictAll();};
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });


    }

    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();

        }
        return mInstance;
    }

    public ImageLoader evictAllImages() {
        if (mImageCache != null) {
            mImageCache.flushLruCache();
        }
        return this.mImageLoader;
    }

    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }


    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

}
