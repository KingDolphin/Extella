package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {

    protected Texture texture;

    public final Color color;

    protected float time = 0;

    public Background(Texture texture, Color color) {
        this.texture = texture;
        this.color = color;
    }

    public void update() {
        time += 1;
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {}

}
