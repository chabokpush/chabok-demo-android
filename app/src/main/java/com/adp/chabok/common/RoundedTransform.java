package com.adp.chabok.common;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class RoundedTransform implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {

        int w = source.getWidth();
        int h = source.getHeight();

        int pixels = 15;


        Bitmap output = Bitmap.createBitmap(w, h, source.getConfig());
        Canvas canvas = new Canvas(output);


        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        canvas.drawRoundRect(rectF, pixels, pixels, paint);
        canvas.drawRect(0, h / 2, w / 2, h, paint);
        canvas.drawRect(w / 2, h / 2, w, h, paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);

        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
