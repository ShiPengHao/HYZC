package com.yimeng.hyzc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * bitmap工具类
 */
public class BitmapUtils {
    /**
     * 通过资源id获取bitmap对象，宽高适应手机屏幕
     * @param activity
     * @param resId 资源id
     * @return bitmap对象
     */
    public static Bitmap getResImg(Activity activity, int resId) {
        // 获得本地图片宽高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置开关
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), resId, opts);
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;

        // 获得缩放比例

        int scale = (int)(Math.max(imgWidth / DensityUtil.SCREEN_WIDTH, imgHeight / DensityUtil.SCREEN_HEIGHT) + 0.5f);
        opts.inSampleSize = scale;

        // 设置开关
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(activity.getResources(), resId, opts);
    }

    /**
     * 缩放bitmap
     * @param oldBitmap 输入bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = Math.min(scaleHeight,scaleWidth);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix,
                true);
        return newbm;
    }
    /**
     * 缩放网络图片 依赖于zoomBitmap
     * @param path
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomFileImg(String path, int newWidth, int newHeight) {
        // 图片源
        Bitmap bm = BitmapFactory.decodeFile(path);
        if (null != bm) {
            return zoomBitmap(bm, newWidth, newHeight);
        }
        return null;
    }
    /**
     * 缩放网络图片 依赖于zoomBitmap
     * @param context
     * @param fileName
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomAssertImg(Context context, String fileName, int newWidth,
                                       int newHeight) {
        // 图片源
        try {
            Bitmap bm = BitmapFactory.decodeStream(context.getAssets()
                    .open(fileName));
            if (null != bm) {
                return zoomBitmap(bm, newWidth, newHeight);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 判断bitmap是否存在
     * @param bitmap
     * @return 存在true，不存在false
     */
    public static boolean bitmapAvailable(Bitmap bitmap) {
        return bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
    }
    /**
     * drawable 转成bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * Bitmap转换成Drawable
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Context context,Bitmap bitmap){
        //因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        BitmapDrawable bd= new BitmapDrawable(context.getResources(), bitmap);
        return bd;
    }

    /**
     * 从资源中获取Bitmap
     * @param context
     * @param resId  R.drawable.icon(eg.)
     * @return
     */
    public Bitmap getBitmapFromResources(Context context,int resId){
        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return bmp;
    }

    /**
     * Byte[] -> Bitmap的转换
     */
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
    /**
     * Bitmap->Byte[]的转换
     * @param bm
     * @return
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
