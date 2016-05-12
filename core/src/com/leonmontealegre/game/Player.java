package com.leonmontealegre.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private final Texture tex = new Texture("spaceship.png");

    public Sprite sprite;

    private Vector2 velocity;

    private float mass;

    public Vector2 position;

    private Polygon polygon;

    public boolean shouldDestroy = false;

    public Player(float startX, float startY) {
        sprite = new Sprite(tex);
        sprite.scale(3);
        sprite.translate(startX, startY);

        position = new Vector2();
        velocity = new Vector2();
        mass = 10;

        System.out.println(tex.getWidth() + ", " + sprite.getWidth() + ", " + tex.getHeight() + ", " + sprite.getHeight());
        float width = tex.getWidth(), height = tex.getHeight();
        polygon = new Polygon(new float[]{0,0,width,0,width,height,0,height});
        polygon.setOrigin(width/2, height/2);
        polygon.scale(3);
    }

    public void resolveCollisionWith(Planet planet) {
        polygon.setPosition(position.x, position.y);
        polygon.setRotation(sprite.getRotation());
        if (overlaps(polygon, planet.getCircle())) {
            shouldDestroy = true;
        }
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

        float time = (float)World.TIME_SCALE / Constants.TARGET_UPS;

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