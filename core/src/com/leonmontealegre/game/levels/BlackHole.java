package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;

public class BlackHole extends Planet {

    public static final int MAX_BLACK_HOLES = 4;

    public static final ShaderProgram shader;

    static {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/pass.vert"), Gdx.files.internal("shaders/blackHole.frag"));
        System.err.println(shader.getLog());
    }

    public static int count = 0;

    public float deformRadius;

    public BlackHole(Assets assets, Level level, Vector2 position, float radius, float deformRadius) {
        super(assets, level, position, radius, 0);
        this.force *= 10;
        this.deformRadius = deformRadius;

        if (count >= MAX_BLACK_HOLES)
            throw new IllegalStateException("Cannot have more than " + MAX_BLACK_HOLES + " black holes in one level!");

        shader.setUniformf("blackHole[" + count + "].radius", radius);
        shader.setUniformf("blackHole[" + count + "].deformRadius", deformRadius);
        shader.setUniformf("blackHole[" + count + "].position", new Vector2(circle.x, circle.y));

        count++;
    }

    @Override
    public void update() {
        super.update();

        if (isOn) {
            for (Planet planet : level.planets) {
                Vector2 dPos = new Vector2(position).sub(planet.position);

                float gravForce = force / dPos.len2();

                planet.addForce(new Vector2(dPos).nor().scl(gravForce));
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {}

}
