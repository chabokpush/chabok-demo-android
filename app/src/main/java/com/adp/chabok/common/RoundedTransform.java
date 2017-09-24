package com.adp.chabok.common;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class RoundedTransform implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        Canvas canvas = new Canvas(output);


        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), 50, 50, paint);

        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
