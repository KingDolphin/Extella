package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;

public class Debris {

    private static Texture textures[] = {Assets.getTexture("debris1"), Assets.getTexture("debris2"), Assets.getTexture("debris3")};

    public Sprite sprite;

    private Level level;

    private Vector2 position;

    private Circle circle;

    public int radius;

    private float speed;

    public Debris(Level level, Vector2 pos, int radius) {
        this.level = level;
        this.position = pos;
        this.radius = radius;
        this.speed = MathUtils.random(-0.5f, 0.5f);

        sprite = new Sprite(textures[(int)(MathUtils.random()*textures.length)]);
        sprite.setScale(2 * radius / sprite.getTexture().getWidth(), 2 * radius / sprite.getTexture().getHeight());
        sprite.setPosition(position.x, position.y);

        circle = new Circle(position.x + sprite.getWidth() / 2, position.y + sprite.getHeight() / 2, radius);
    }

    public void update() {
        if (level.player != null && level.player.collidesWith(this.circle)) {
            level.player.shouldDestroy = true;
        }

        this.sprite.rotate(speed);
    }

    public Circle getCircle() {
        return this.circle;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
