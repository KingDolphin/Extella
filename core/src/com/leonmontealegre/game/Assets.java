package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.utils.GifDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private AssetManager assetManager;

    private Map<String, String> filesNames = new HashMap<String, String>();

    private Animation explosionAnimation;

    public Assets() {
        assetManager = new AssetManager();
        load();
    }

    private void load() {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element assets = reader.parse(Gdx.files.internal("XML/assets.xml"));

            // Load explosion
            explosionAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.NORMAL, Gdx.files.internal("textures/level_objects/explosion.gif").read());

            // Load all textures
            Array<XmlReader.Element> texs = assets.getChildrenByName("texture");
            for (XmlReader.Element tex : texs) {
                assetManager.load(tex.get("file"), Texture.class);
                String key = tex.get("key");
                filesNames.put(key, tex.get("file"));
            }

            // Load all particles
            FileHandle particles = Gdx.files.internal("particle_systems/particles");
            for (FileHandle particle : particles.list())
                Particle.load(particle.path());

            assetManager.finishLoading();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Texture getTexture(String key) {
        return assetManager.get(filesNames.get(key));
    }

    public Animation getExplosionAnimation() { return explosionAnimation; }

}
