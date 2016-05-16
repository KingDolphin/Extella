package com.leonmontealegre.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Particle {
    private Vector2 position;
    private Vector2 velocity;
    private Texture texture;

    public Particle(Vector2 position, int xPosRange, int minSpeed, int speedRange, Texture texture, Vector2 direction) {
        this.position = position;

        this.velocity = (direction.scl(minSpeed + MathUtils.random() * speedRange));
        this.texture = texture;
    }

    public void updatePhysics() {
        position.y += velocity.y;
        position.x += velocity.x;
    }

    public void doDraw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public boolean OutOfSight() {
        return position.y <= -1 * texture.getHeight();
    }
}
