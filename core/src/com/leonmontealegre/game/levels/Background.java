package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.leonmontealegre.utils.Noise;

public class Background {

    public static Texture background = new Texture("textures/UI/background.jpg");
    public static Texture stars = new Texture("textures/UI/stars.png");

    static {
        background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        stars.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public Texture nebula;

    public final Color color;
    public final float size1, sizeH, size2;
    public final float shiftSpeed, flowSpeed;
    public final int frameRate, resolution;
    public final float intensity, iIntensity;

    private float time = 0;
    private float dx, dy;

    public Background(Color color, float size1, float size2, float shiftSpeed, float flowSpeed, int frameRate, float intensity, int resolution) {
        this.color = color;
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

    public Background() {
        this.color = null;
        this.size1 = size2 = sizeH = shiftSpeed = flowSpeed = intensity = iIntensity = 0;
        this.frameRate = resolution = 0;
        this.nebula = null;
    }

    public void update() {
        if (nebula != null) {
            time += 1;

            if (time % (60 / frameRate) == 0) // Update every 'frameRate' times per second
                generate();
        }
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (nebula != null)
            batch.draw(nebula, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(stars, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    private void generate() {
        if (nebula != null)
            nebula.dispose();

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
        nebula = new Texture(map);
        nebula.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

}
