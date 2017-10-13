package com.yelelen.sfish.helper;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.yelelen.sfish.utils.Utils;

/**
 * Created by yelelen on 17-9-14.
 */

public class MmImageCache {
    public static final int DEFAULT_SIZE = Utils.getMaxMemory() / 8;
    private int mCacheSize;
    private LruCache<String, Bitmap> mCache;

    public MmImageCache(int cacheSize) {
        if (cacheSize > 0)
            mCacheSize = cacheSize;
        mCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null)
            return;

        if (getBitmapFromMemoryCache(key) == null) {
            mCache.put(key, bitmap);
        }
    }


    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mCache.get(key);
    }

    public Bitmap removeBitmapFromMemoryCache(String key) {
        if (key != null)
            return mCache.remove(key);
        return null;
    }
}
