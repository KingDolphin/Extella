package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static com.badlogic.gdx.math.MathUtils.random;

public class ParticleSystem {

    private Assets assets;

    private String[] particleTypes;

    public Vector2 position;

    private Vector2 minPosition, maxPosition;

    private Vector2 minSpeed, maxSpeed;

    private Vector2 minSize, maxSize;

    private Vector2 speedPerSecond, sizePerSecond;

    private float duration, minLife, maxLife;

    private float minRotation, maxRotation, rotationPerSecond;

    private boolean loop;

    private int maxParticles;

    private float particlesPerSecond;

    private ArrayList<Particle> particles;
    private Queue<Particle> deadParticles;

    private float spawnTimer = 0;
    private float timer = 0;

    private boolean isPaused;

    public ParticleSystem(Assets assets, String file) {
        load(file);
        this.assets = assets;
        position = new Vector2();
        particles = new ArrayList<Particle>();
        deadParticles = new LinkedList<Particle>();
        isPaused = false;

        Logger.log(spawnTimer + ", " + minSize + ", " + maxSize + ", " + duration + ", " + minLife + ", " + maxLife + ", " + loop + ", " + maxParticles + ", " + particlesPerSecond);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void clear() {
        timer = 0;
        spawnTimer = 0;
        particles.clear();
        deadParticles.clear();
    }

    public void update() {
        if (!loop && timer > duration)
            return;

        for (int i = 0; i < particles.size(); i++ ) {
            Particle particle = particles.get(i);
            particle.velocity.add(speedPerSecond.cpy().scl(1f / Options.TARGET_UPS));
            particle.size.add(sizePerSecond.cpy().scl(1f / Options.TARGET_UPS));
            particle.rotation += rotationPerSecond / Options.TARGET_UPS;
            particle.update();

            if (timer >= particle.startTime+particle.life) {
                // Remove particle
                deadParticles.add(particles.remove(i--));
            }
        }

        if (!isPaused) {
            while (spawnTimer >= 1f / particlesPerSecond) {
                spawnParticle();
                spawnTimer -= 1f / particlesPerSecond;
            }
        }

        timer += 1f / Options.TARGET_UPS;
        if (!isPaused)
            spawnTimer += 1f / Options.TARGET_UPS;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);

            Sprite sprite = new Sprite(particle.texture);
            sprite.setScale(particle.size.x / sprite.getWidth(), particle.size.y / sprite.getHeight());
            sprite.translate(particle.position.x - sprite.getWidth()/2, particle.position.y - sprite.getHeight()/2);
            sprite.rotate(particle.rotation);

            sprite.draw(batch);
        }
    }

    private void spawnParticle() {
        if (particles.size() < maxParticles) {
            Particle particle = deadParticles.peek() == null ? new Particle() : deadParticles.remove();
            particle.reset(assets,
                    particleTypes[(int)(random()*particleTypes.length)],
                    new Vector2(position.x+random(minPosition.x, maxPosition.x),
                                position.y+random(minPosition.y, maxPosition.y)),

                    new Vector2(random(minSpeed.x, maxSpeed.x),
                                random(minSpeed.y, maxSpeed.y)),

                    new Vector2(random(minSize.x, maxSize.x),
                                random(minSize.y, maxSize.y)),
                    timer,
                    random(minLife, maxLife),
                    random(minRotation, maxRotation)
            );
            particles.add(particle);
        }
    }

    public void spawn(int amount) {
        for (int i = 0; i < amount; i++)
            spawnParticle();
    }

    private void load(String file) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(file));

            Array<XmlReader.Element> particles = root.getChildByName("particles").getChildrenByName("particle");
            particleTypes = new String[particles.size];

            for (int i = 0; i < particles.size; i++)
                particleTypes[i] = particles.get(i).getText().trim();

            minPosition = getVectorByName(root, "min_pos");
            maxPosition = getVectorByName(root, "max_pos");

            minSpeed = getVectorByName(root, "min_speed");
            maxSpeed = getVectorByName(root, "max_speed");

            minSize = getVectorByName(root, "min_size");
            maxSize = getVectorByName(root, "max_size");

            speedPerSecond = getVectorByName(root, "speed_per_second");
            sizePerSecond = getVectorByName(root, "size_per_second");

            duration = getFloatByName(root, "duration");
            minLife = getFloatByName(root, "min_life");
            maxLife = getFloatByName(root, "max_life");

            minRotation = getFloatByName(root, "min_rotation");
            maxRotation = getFloatByName(root, "max_rotation");

            rotationPerSecond = getFloatByName(root, "rotation_per_second");

            loop = getBooleanByName(root, "loop");
            maxParticles = getIntByName(root, "max_particles");

            particlesPerSecond = getFloatByName(root, "particles_per_second");

            spawnTimer = getIntByName(root, "particles_on_start") / particlesPerSecond;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getBooleanByName(XmlReader.Element root, String name) {
        for (int i = 0; i < root.getChildCount(); i++) {
            XmlReader.Element child = root.getChild(i);
            if (child.getAttribute("name", "").equals(name))
                return Boolean.parseBoolean(child.getText());
        }
        return false;
    }

    private int getIntByName(XmlReader.Element root, String name) {
        for (int i = 0; i < root.getChildCount(); i++) {
            XmlReader.Element child = root.getChild(i);
            if (child.getAttribute("name", "").equals(name))
                return Integer.parseInt(child.getText());
        }
        return 0;
    }

    private float getFloatByName(XmlReader.Element root, String name) {
        for (int i = 0; i < root.getChildCount(); i++) {
            XmlReader.Element child = root.getChild(i);
            if (child.getAttribute("name", "").equals(name))
                return Float.parseFloat(child.getText());
        }
        return 0;
    }

    private Vector2 getVectorByName(XmlReader.Element root, String name) {
        for (int i = 0; i < root.getChildCount(); i++) {
            XmlReader.Element child = root.getChild(i);
            if (child.getAttribute("name", "").equals(name))
                return parseVector(child.getText());
        }
        return null;
    }

    private Vector2 parseVector(String str) {
        return new Vector2(Float.parseFloat(str.substring(0, str.indexOf(',')).trim()),
                           Float.parseFloat(str.substring(str.indexOf(',')+1).trim()));
    }

    public Vector2 getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(Vector2 minPosition) {
        this.minPosition = minPosition;
    }

    public Vector2 getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(Vector2 maxPosition) {
        this.maxPosition = maxPosition;
    }

    public Vector2 getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Vector2 minSpeed) {
        this.minSpeed = minSpeed;
    }

    public Vector2 getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Vector2 maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Vector2 getMinSize() {
        return minSize;
    }

    public void setMinSize(Vector2 minSize) {
        this.minSize = minSize;
    }

    public Vector2 getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Vector2 maxSize) {
        this.maxSize = maxSize;
    }

    public Vector2 getSpeedPerSecond() {
        return speedPerSecond;
    }

    public void setSpeedPerSecond(Vector2 speedPerSecond) {
        this.speedPerSecond = speedPerSecond;
    }

    public Vector2 getSizePerSecond() {
        return sizePerSecond;
    }

    public void setSizePerSecond(Vector2 sizePerSecond) {
        this.sizePerSecond = sizePerSecond;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getMinLife() {
        return minLife;
    }

    public void setMinLife(float minLife) {
        this.minLife = minLife;
    }

    public float getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(float maxLife) {
        this.maxLife = maxLife;
    }

    public float getMinRotation() {
        return minRotation;
    }

    public void setMinRotation(float minRotation) {
        this.minRotation = minRotation;
    }

    public float getMaxRotation() {
        return maxRotation;
    }

    public void setMaxRotation(float maxRotation) {
        this.maxRotation = maxRotation;
    }

    public float getRotationPerSecond() {
        return rotationPerSecond;
    }

    public void setRotationPerSecond(float rotationPerSecond) {
        this.rotationPerSecond = rotationPerSecond;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public float getParticlesPerSecond() {
        return particlesPerSecond;
    }

    public void setParticlesPerSecond(float particlesPerSecond) {
        this.particlesPerSecond = particlesPerSecond;
    }
}
