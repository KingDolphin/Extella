package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.leonmontealegre.utils.FrameBufferManager;

public class DynamicSpaceBackground extends Background {

    private static final ShaderProgram shader;

    static {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/pass.vert"), Gdx.files.internal("shaders/space.frag"));
        System.err.println(shader.getLog());
    }

    private FrameBuffer buffer;
    private OrthographicCamera cam;

    public DynamicSpaceBackground(Texture texture,  Color color) {
        super(texture, color);
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4, false);
        buffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, buffer.getWidth(), buffer.getHeight());
        cam.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();

        Matrix4 prevMat = batch.getProjectionMatrix().cpy();

        shader.begin();
        shader.setUniformf("time", time/120f); // Send time uniform
        shader.end();

        FrameBufferManager.begin(buffer);

        batch.setProjectionMatrix(cam.combined);
        batch.setShader(shader);

        batch.setColor(this.color);
        batch.begin();
        batch.draw(texture, 0, 0, buffer.getWidth(), buffer.getHeight());
        batch.end();

        FrameBufferManager.end();


        batch.setProjectionMatrix(prevMat);
        batch.setShader(null);

        batch.setColor(1f, 1f, 1f, 1f);
        batch.begin();
        batch.draw(buffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        buffer.dispose();
    }

}
