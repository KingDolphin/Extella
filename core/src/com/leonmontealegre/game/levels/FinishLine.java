package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;

public class FinishLine {

    public Sprite sprite;

    private Level level;

    private Vector2 position;

    private int radius;

    private Circle circle;

    public FinishLine(Assets assets, Level level, Vector2 pos, int radius) {
        this.level = level;
        this.position = pos;
        this.radius = radius;

        sprite = new Sprite(assets.getTexture("finishFlag"));
        sprite.setScale(2 * radius / sprite.getTexture().getWidth(), 2 * radius / sprite.getTexture().getHeight());
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

    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

}
