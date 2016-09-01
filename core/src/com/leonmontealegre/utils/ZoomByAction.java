package com.leonmontealegre.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.action;

public class ZoomByAction extends RelativeTemporalAction {
    private float amount;

    protected void updateRelative (float percentDelta) {
        target.scaleBy(0, amount * percentDelta);
    }

    public void setAmount (float z) {
        amount = z;
    }

    public float getAmount () {
        return amount;
    }

    /** Scales the actor instantly. */
    static public ZoomByAction zoomBy (float amount) {
        return zoomBy(amount, 0, null);
    }

    static public ZoomByAction zoomBy (float amount, float duration) {
        return zoomBy(amount, duration, null);
    }

    static public ZoomByAction zoomBy (float amount, float duration, Interpolation interpolation) {
        ZoomByAction action = action(ZoomByAction.class);
        action.setAmount(amount);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

}
