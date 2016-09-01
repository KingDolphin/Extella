package com.leonmontealegre.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CameraActor extends Actor {

    private OrthographicCamera camera;

    public CameraActor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        camera.position.set(this.getX(), this.getY(), camera.position.z);
        camera.zoom = this.getScaleY();
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

}
