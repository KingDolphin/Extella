package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;
import com.leonmontealegre.game.Options;

public class Player {

    public Sprite sprite;

    private Vector2 velocity;

    private float mass;

    public Vector2 position;

    private Polygon polygon;

    public boolean shouldDestroy = false;

    public Player(Assets assets, Vector2 startPosition) {
        sprite = new Sprite(assets.getTexture("spaceship"));
        sprite.scale(1);
        sprite.translate(startPosition.x, startPosition.y);

        position = new Vector2(startPosition);
        velocity = new Vector2();
        mass = 10;

        float width = sprite.getTexture().getWidth(), height = sprite.getTexture().getHeight();
        polygon = new Polygon(new float[]{5, 0, width-5, 0, width-5, height, 5, height});
        polygon.setOrigin(width / 2, height / 2);
        polygon.scale(1);
        sprite.setRotation(-90);
    }

    public void resolveCollisionWith(Planet planet) {
        polygon.setPosition(position.x, position.y);
        polygon.setRotation(sprite.getRotation());
        if (overlaps(polygon, planet.getCircle())) {
            shouldDestroy = true;
        }
    }

    public boolean collidesWith(Circle circle) {
        polygon.setPosition(position.x,position.y);
        polygon.setRotation(sprite.getRotation());
        return overlaps(polygon, circle);
    }

    public void update() {
        position.add(velocity);
        sprite.setPosition(position.x, position.y);

        float theta = MathUtils.atan2(velocity.y, velocity.x);
        sprite.setRotation(MathUtils.radiansToDegrees*theta - 90);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void addForce(Vector2 force) {
        //a = F/m
        Vector2 acceleration = new Vector2(force).scl(1f / mass);

        float time = (float)Level.TIME_SCALE / Options.TARGET_UPS;

        //vf = v0 + at
        velocity.add(acceleration.scl(time));
    }

    public Polygon getPolygon() {
        polygon.setPosition(position.x, position.y);
        polygon.setRotation(sprite.getRotation());
        return this.polygon;
    }

    public static boolean overlaps(Polygon polygon, Circle circle) {
        float[] vertices = polygon.getTransformedVertices();
        Vector2 center = new Vector2(circle.x, circle.y);
        float squareRadius = circle.radius*circle.radius;
        for (int i = 0; i < vertices.length; i += 2){
            if (i==0) {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            } else {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[i-2], vertices[i-1]), new Vector2(vertices[i], vertices[i+1]), center, squareRadius))
                    return true;
            }
        }
        return polygon.contains(circle.x, circle.y);
    }

}
