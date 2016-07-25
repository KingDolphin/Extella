package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class FinishLine {

    private static Texture tex1 = new Texture("textures/level_objects/finishFlag.png");

    public Sprite sprite;

    private Level level;

    private Vector2 position;

    private int radius;

    private Circle circle;

    public FinishLine(Level level, Vector2 pos, int radius) {
        this.level = level;
        this.position = pos;
        this.radius = radius;

        sprite = new Sprite(tex1);
        sprite.setScale(2 * radius / tex1.getWidth(), 2 * radius / tex1.getHeight());
        sprite.setPosition(position.x, position.y);

        circle = new Circle(position.x + sprite.getWidth() / 2, position.y + sprite.getHeight() / 2, radius);
    }

    public void update() {
        if (level.player != null && level.player.collidesWith(this.circle)) {
            level.win();
        }
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
