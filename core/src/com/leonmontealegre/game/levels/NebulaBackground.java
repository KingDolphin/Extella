package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.leonmontealegre.utils.Noise;

public class NebulaBackground extends Background {

    public final float size1, sizeH, size2;
    public final float shiftSpeed, flowSpeed;
    public final int frameRate, resolution;
    public final float intensity, iIntensity;

    protected float dx, dy;

    public NebulaBackground(Color color, float size1, float size2, float shiftSpeed, float flowSpeed, int frameRate, float intensity, int resolution) {
        super(null, color);
        this.size1 = size1;
        this.sizeH = size1 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        this.size2 = size2;
        this.shiftSpeed = shiftSpeed;
        this.flowSpeed = flowSpeed;
        this.frameRate = frameRate;
        this.resolution = resolution;
        this.intensity = intensity;
        this.iIntensity = 1f / intensity;

        dx = MathUtils.random(-100000, 100000);
        dy = MathUtils.random(-100000, 100000);

        generate();
    }

    @Override
    public void update() {
        super.update();

        if (time % (60 / frameRate) == 0) // Update every 'frameRate' times per second
            generate();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    protected void generate() {
        if (texture != null)
            texture.dispose();

        Pixmap map = new Pixmap(Gdx.graphics.getWidth() / resolution, Gdx.graphics.getHeight() / resolution, Pixmap.Format.RGBA8888);
        float sizeH = size1 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        float z = time * flowSpeed * 0.01f;
        float t = time * shiftSpeed * 0.01f;
        float iW = 1f / map.getWidth();
        float iH = 1f / map.getHeight();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                float noise = Noise.octaveNoise(2, x * iW * size1 + dx + t, y * iH * sizeH + dy, z);
                noise += Noise.simplex(x * iW * size2 + dx + t, y * iH * 4 + dy, z);
                map.drawPixel(x, y, Color.rgba8888(color.r, color.g, color.b, Math.min(noise * iIntensity, 1)));
            }
        }
        texture = new Texture(map);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        map.dispose();
    }

}
