package com.arvi.btScan.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.arvi.btScan.common.GraphicOverlay.Graphic;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

/** Draw camera image to background. */
public class CameraImageGraphic extends GraphicOverlay.Graphic {

    private final Bitmap bitmap;

    public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
        super(overlay);
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
    }
}

