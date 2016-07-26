package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;

public class Astronaut {

    private static Texture tex1 = Assets.getTexture("astronaut");

    public Sprite sprite;

    private CollectAstronautsLevel level;

    private Vector2 position;

    private int radius;

    private Circle circle;

    private float speed;

    public Astronaut(CollectAstronautsLevel level, Vector2 pos, int radius) {
        this.level = level;
        this.position = pos;
        this.radius = radius;
        this.speed = MathUtils.random(-0.5f, 0.5f);

        sprite = new Sprite(tex1);
        sprite.setScale(2 * radius / tex1.getWidth(), 2 * radius / tex1.getHeight());
        sprite.setPosition(position.x, position.y);

        circle = new Circle(position.x + sprite.getWidth() / 2, position.y + sprite.getHeight() / 2, radius);
    }

    public void update() {
        if (level.player != null && level.player.collidesWith(this.circle)) {
            level.collectAstronaut(this);
        }

        this.sprite.rotate(speed);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
