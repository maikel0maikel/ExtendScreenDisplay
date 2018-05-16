package com.sinohb.lib.extendscreen;

import android.graphics.Bitmap;

/**
 * Interface for Views or other objects that wish to mirror the contents of
 * a MirroringFrameLayout.
 */
public interface MirrorSink extends
        AspectLockedFrameLayout.AspectRatioSource {
    /**
     * Consume a bitmap representing a pushed update to the contents to be mirrored.
     *
     * @param bmp a Bitmap
     */
    void update(Bitmap bmp);
}
