package com.leonmontealegre.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Touch {

    public Vector2 position;

    private boolean firstPressed, released;

    public final int pointer, button;

    private int lifeTime;

    public Touch(int x, int y, int pointer, int button) {
        this.move(x, y);
        this.pointer = pointer;
        this.button = button;
        this.firstPressed = true;
        this.released = false;
        this.lifeTime = 0;
    }

    public void move(int x, int y) {
        this.position = new Vector2(x, Gdx.graphics.getHeight() - y);
    }

    public void release() {
        this.released = true;
    }

    public void update() {
        this.firstPressed = false;
        this.lifeTime++;
    }

    public boolean isFirstPressed() {
        return this.firstPressed;
    }

    public boolean isReleased() {
        return this.released;
    }

    public int getLifeTime() {
        return lifeTime;
    }

}
