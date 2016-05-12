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

    private Texture tex = new Texture("planet.png");

    public Sprite sprite;

    private World world;

    public float radius, force;

    public Vector2 position;

    private Circle circle;

    public Planet(World world, float x, float y, float radius, float force) {
        this.world = world;
        this.radius = radius;
        this.force = force;

        sprite = new Sprite(tex);
        sprite.setScale(2*radius/tex.getWidth(), 2*radius/tex.getHeight());
        sprite.setPosition(x, y);
        sprite.rotate(360 * MathUtils.random());

        position = new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2);

        circle = new Circle(position.x, position.y, radius);
    }

    public void update() {
        Player player = world.player;

        for (Touch t : Input.touches) {
            Vector2 pos = t.position;
            if (circle.contains(pos)) { // if touch is in planet
                Vector2 dPos = new Vector2(position).sub(player.position);

                float gravForce = force / dPos.len2();

                player.addForce(new Vector2(dPos).nor().scl(gravForce));
                break;
            }
        }

    }

    public Circle getCircle() {
        return this.circle;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
