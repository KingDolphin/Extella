package com.leonmontealegre.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Touch;

public class Planet {

    private static Texture tex1 = new Texture("planet01.png"), tex2 = new Texture("planet02.png");

    public Sprite sprite;

    private Level level;

    public float radius, force;

    public Vector2 position;

    private Circle circle;

    private boolean isOn = false;

    public Planet(Level level, Vector2 position, float radius, float force) {
        this.level = level;
        this.radius = radius;
        this.force = force == 0 ? radius*radius / 2500f * 1e6f : force;

        Texture tex = (MathUtils.randomBoolean() ? tex1 : tex2);
        sprite = new Sprite(tex);
        sprite.setScale(2*radius/tex.getWidth(), 2*radius/tex.getHeight());
        sprite.setPosition(position.x, position.y);
        sprite.rotate(360 * MathUtils.random());

        this.position = new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2);

        circle = new Circle(position.x, position.y, radius);
    }

    public void update() {
        Player player = level.player;

        for (Touch t : Input.touches) {
            if (t.isFirstPressed() && circle.contains(level.unproject(t.position))) {
                isOn = !isOn;
                break;
            }
        }

        if (isOn) {
            Vector2 dPos = new Vector2(position).sub(player.position);

            float gravForce = force / dPos.len2();

            player.addForce(new Vector2(dPos).nor().scl(gravForce));
        }

    }

    public Circle getCircle() {
        return this.circle;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
