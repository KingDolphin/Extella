package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.ParticleSystem;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Touch;

public class Planet {

    private static Texture tex1 = new Texture("planet01.png"), tex2 = new Texture("planet02.png");

    public Sprite sprite;

    private Level level;

    public float radius, force;

    public Vector2 position;

    private Circle circle;
    private Circle tapCircle;

    private boolean isOn = false;

    private ParticleSystem system;

    public Planet(Level level, Vector2 position, float radius, float force) {
        this.level = level;
        this.radius = radius;
        this.force = force == 0 ? radius*radius / 2500f * 1e6f : force;

        Texture tex = (MathUtils.randomBoolean() ? tex1 : tex2);
        sprite = new Sprite(tex);
        sprite.setScale(2*radius/tex.getWidth(), 2*radius/tex.getHeight());
        sprite.setPosition(position.x, position.y);
        sprite.rotate(360 * MathUtils.random());

        system = new ParticleSystem("particle_systems/PlanetParticleSystem.xml");

        circle = new Circle(position.x + sprite.getWidth()/2, position.y + sprite.getHeight()/2, radius);
        tapCircle = new Circle(position.x + sprite.getWidth()/2, position.y + sprite.getHeight()/2, 4f*radius/3f);

        this.position = new Vector2(circle.x, circle.y);

        system.position = new Vector2((sprite.getX()+this.position.x)/2, (sprite.getY()+this.position.y)/2);
        system.setMinSize(new Vector2(2*radius, 2*radius));
        system.setMaxSize(new Vector2(2*radius, 2*radius));
        system.pause();
    }

    public void update() {
        Player player = level.player;

        for (Touch t : Input.touches) {
            if (t.isFirstPressed() && tapCircle.contains(level.unproject(t.position))) {
                isOn = !isOn;
                if (isOn) {
                    system.resume();
                } else {
                    system.pause();
                    system.clear();
                }
                break;
            }
        }

        if (isOn && player != null) {
            Vector2 dPos = new Vector2(position).sub(player.position);

            float gravForce = force / dPos.len2();

            player.addForce(new Vector2(dPos).nor().scl(gravForce));
        }
        system.update();
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
