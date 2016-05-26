package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.utils.GifDecoder;

public class Explosion {

    private static Animation animation;

    private Vector2 pos, size;

    private float time;

    public Explosion(Vector2 pos, Vector2 size) {
        this.pos = pos;
        this.size = size;
    }

    public void update() {
        time += 1f / Options.TARGET_UPS;
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(time), pos.x, pos.y, size.x, size.y);
    }

    public static void load() {
        animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.NORMAL, Gdx.files.internal("explosion.gif").read());
    }

}
