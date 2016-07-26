package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;
import com.leonmontealegre.game.Options;
import com.leonmontealegre.game.ParticleSystem;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Touch;

public class Planet {

    protected static Texture tex1 = Assets.getTexture("planet1"), tex2 = Assets.getTexture("planet2");

    public Sprite sprite;

    protected Level level;

    public final Vector2 startPosition;
    public Vector2 position;
    protected Vector2 velocity;

    public float radius, force;

    protected Circle circle;
    protected Circle tapCircle;

    protected boolean isOn = false;

    protected ParticleSystem system;

    protected boolean wasZooming = false;
    protected boolean wasPanning = false;

    public Planet(Level level, Vector2 position, float radius, float force) {
        this.startPosition = new Vector2(position);
        this.level = level;
        this.radius = radius;
        this.force = force == 0 ? radius*radius / 2500f * 1e6f : force;

        Texture tex = (MathUtils.randomBoolean() ? tex1 : tex2);
        sprite = new Sprite(tex);
        sprite.setScale(2*radius/tex.getWidth(), 2*radius/tex.getHeight());
        sprite.setPosition(position.x, position.y);
        sprite.rotate(360 * MathUtils.random());

        system = new ParticleSystem("particle_systems/PlanetParticleSystem.xml");
        system.setSizePerSecond(new Vector2(radius / 75 * system.getSizePerSecond().x, radius / 75 * system.getSizePerSecond().y));

        circle = new Circle(position.x + sprite.getWidth()/2, position.y + sprite.getHeight()/2, radius);

        float extraSize = 1200f / ((radius + 10) * (radius + 10));
        tapCircle = new Circle(position.x + sprite.getWidth()/2, position.y + sprite.getHeight()/2, radius + radius * extraSize);

        this.position = new Vector2(circle.x, circle.y);
        this.velocity = new Vector2();

        system.position = new Vector2(this.position.x, this.position.y);
        system.setMinSize(new Vector2(2*radius, 2*radius));
        system.setMaxSize(new Vector2(2*radius, 2*radius));
        system.pause();
    }

    public void update() {
        Player player = level.player;

        position.add(velocity);
        sprite.translate(velocity.x, velocity.y);
        circle.x += velocity.x;
        circle.y += velocity.y;
        tapCircle.x += velocity.x;
        tapCircle.y += velocity.y;
        system.position.add(velocity);

        int numReleased = 0;
        for (Touch t : Input.touches) {
            if (t.isReleased() && !wasZooming && !wasPanning && tapCircle.contains(level.unproject(t.position))) {
                isOn = !isOn;
                if (isOn) {
                    system.spawn(1);
                    system.resume();
                } else {
                    system.pause();
                    system.clear();
                }
                break;
            }
            if (t.isReleased())
                numReleased++;
        }
        if (numReleased == Input.touches.size()) {
            wasZooming = false;
            wasPanning = false;
        }

        if (Input.getZoom() > 0)
            wasZooming = true;
        if (Math.abs(Input.getPan().x) > 0 || Math.abs(Input.getPan().y) > 0)
            wasPanning = true;

        if (isOn && player != null) {
            Vector2 dPos = new Vector2(position).sub(player.position);

            float gravForce = force / dPos.len2();

            player.addForce(new Vector2(dPos).nor().scl(gravForce));
        }
        system.update();
    }

    public void addForce(Vector2 force) {
        //a = F/m
        Vector2 acceleration = new Vector2(force).scl(1f / radius);

        float time = (float)Level.TIME_SCALE / Options.TARGET_UPS;

        //vf = v0 + at
        velocity.add(acceleration.scl(time));
    }

    public Circle getCircle() {
        return this.circle;
    }

    public Circle getTapCircle() {
        return this.tapCircle;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);

        system.render(batch);
    }

}
