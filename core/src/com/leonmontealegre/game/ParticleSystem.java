package com.leonmontealegre.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ParticleSystem {
    private Particle particles[];

    private Vector2 position;
    private int minSpeed;
    private int speedRange;
    private int xPosRange;
    private Vector2 direction;
    private Texture texture;

    public ParticleSystem(int numParticles, Vector2 position, int xPosRange, int minSpeed, int speedRange, Vector2 direction, Texture texture){
        this.position = position;
        this.xPosRange = xPosRange;
        this.minSpeed = minSpeed;
        this.speedRange = speedRange;
        this.direction = direction;
        this.texture = texture;

        particles = new Particle[numParticles];

        for(int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(position, xPosRange, minSpeed, speedRange, texture, direction);
        }
    }

    public void doDraw(SpriteBatch batch) {
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            particle.doDraw(batch);
        }
    }

    public void updatePhysics() {
        for(int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            particle.updatePhysics();

            if(particle.OutOfSight()) {
                particles[i] = new Particle(position, xPosRange, minSpeed, speedRange, texture, direction);
            }
        }
    }




}
