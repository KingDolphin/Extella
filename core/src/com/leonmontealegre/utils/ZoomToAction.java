package com.leonmontealegre.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.action;

public class ZoomToAction extends TemporalAction {
    private float startZoom;
    private float endZoom;

    protected void begin () {
        startZoom = target.getScaleY();
    }

    protected void update (float percent) {
        target.setScale(target.getScaleX(), startZoom + (endZoom - startZoom) * percent);
    }

    public void setZoom (float z) {
        endZoom = z;
    }

    public float getZoom () {
        return endZoom;
    }

    /** Zooms the camera instantly. */
    static public ZoomToAction zoomTo (float z) {
        return zoomTo(z, 0, null);
    }

    static public ZoomToAction zoomTo (float z, float duration) {
        return zoomTo(z, duration, null);
    }

    static public ZoomToAction zoomTo(float z, float duration, Interpolation interpolation) {
        ZoomToAction action = action(ZoomToAction.class);
        action.setZoom(z);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

}