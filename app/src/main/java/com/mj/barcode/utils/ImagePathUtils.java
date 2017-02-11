package com.mj.barcode.utils;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.mj.barcode.MyApplication;

/**
 * Created by MengJie on 2017/1/18.
 * 获取真实路径
 */

public class ImagePathUtils {

    public static String getImagePath(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (Build.VERSION.SDK_INT <= 19) {
            imagePath = getPath(uri, null);
        } else {
            if (DocumentsContract.isDocumentUri(MyApplication.getContext(), uri)) {
                //如果是document类型的URI，则通过document id处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String[] split = docId.split(":");
                    String id = split[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(docId));
                    imagePath = getPath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果content类型的URI，则使用普通方式处理
                imagePath = getPath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的URI，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        return imagePath;
    }

    private static String getPath(Uri uri, String selection) {
        String path = null;
        //通过URI来获取真实的图片路径
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
