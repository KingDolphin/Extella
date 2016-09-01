package com.leonmontealegre.game.levels.cutscenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.leonmontealegre.game.Options;
import com.leonmontealegre.utils.CameraActor;

public abstract class Cutscene {

    protected CameraActor cameraActor;

    public Cutscene(OrthographicCamera camera) {
        this.cameraActor = new CameraActor(camera);
    }

    public abstract void restart();

    public void update() {
        cameraActor.act(1f / Options.TARGET_UPS);
    }

    public void skip() {
        cameraActor.clearActions();
    }

    public boolean isFinished() {
        return cameraActor.getActions().size == 0;
    }

}
