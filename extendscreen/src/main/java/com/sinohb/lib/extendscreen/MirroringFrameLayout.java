package com.sinohb.lib.extendscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
/**
 * A FrameLayout that locks its aspect ratio (courtesy of AspectLockedFrameLayout)
 * and supplies "screenshots" of its contents to an associated MirrorSink,
 * such as a Mirror.
 *
 * Principally, MirroringFrameLayout and Mirror are designed for use with
 * Android's Presentation system. The MirroringFrameLayout would be part of the
 * UI of the activity on the mobile device, allowing for user interaction. The
 * Mirror would be used in the Presentation to show an audience (e.g., via a
 * projector) what is shown inside the MirroringFrameLayout on the mobile
 * device.
 */
public class MirroringFrameLayout extends AspectLockedFrameLayout
        implements ViewTreeObserver.OnPreDrawListener, ViewTreeObserver.OnScrollChangedListener {
    private MirrorSink mirror=null;
    private Bitmap bmp=null;
    private Canvas bmpBackedCanvas=null;
    private Rect rect=new Rect();

    /**
     * {@inheritDoc}
     */
    public MirroringFrameLayout(Context context) {
        this(context, null);
    }

    /**
     * {@inheritDoc}
     */
    public MirroringFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
    }

    /**
     * Associate a MirrorSink; this sink will be given bitmaps representing
     * updated contents of the MirroringFrameLayout as those contents change.
     *
     * @param mirror a Mirror or other MirrorSink implementation
     */
    public void setMirror(MirrorSink mirror) {
        this.mirror=mirror;

        if (mirror != null) {
            setAspectRatioSource(mirror);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewTreeObserver().addOnPreDrawListener(this);
        getViewTreeObserver().addOnScrollChangedListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetachedFromWindow() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        getViewTreeObserver().removeOnScrollChangedListener(this);

        super.onDetachedFromWindow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(Canvas canvas) {
        if (mirror != null) {
            bmp.eraseColor(0);

            super.draw(bmpBackedCanvas);
            getDrawingRect(rect);
            canvas.drawBitmap(bmp, null, rect, null);
            mirror.update(bmp);
        }
        else {
            super.draw(canvas);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initBitmap(w, h);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPreDraw() {
        if (mirror != null) {
            if (bmp == null) {
                requestLayout();
            }
            else {
                invalidate();
            }
        }

        return(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollChanged() {
        onPreDraw();
    }

    private void initBitmap(int w, int h) {
        if (mirror != null) {
            if (bmp == null || bmp.getWidth() != w || bmp.getHeight() != h) {
                if (bmp != null) {
                    bmp.recycle();
                }

                bmp=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bmpBackedCanvas=new Canvas(bmp);
            }
        }
    }
}
