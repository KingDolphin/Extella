package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Particle {

    public static final Map<String, String[]> PARTICLE_MAP = new HashMap<String, String[]>();

    public String name;

    public Texture texture;

    public Vector2 position;
    public Vector2 velocity;
    public Vector2 size;
    public float startTime;
    public float life;
    public float rotation;

    public Particle() {}

    public Particle(Assets assets, String name, Vector2 position, Vector2 velocity, Vector2 size, float startTime, float life, float rotation) {
        this.reset(assets, name, position, velocity, size, startTime, life, rotation);
    }

    public void update() {
        this.position.add(velocity);
    }

    public void reset(Assets assets, String name, Vector2 position, Vector2 velocity, Vector2 size, float startTime, float life, float rotation) {
        this.name = name;
        this.texture = assets.getTexture(PARTICLE_MAP.get(name)[(int)(MathUtils.random()*PARTICLE_MAP.get(name).length)]);
        this.position = new Vector2(position);

        this.velocity = velocity;
        this.size = size;
        this.startTime = startTime;
        this.life = life;
        this.rotation = rotation;
    }

    public static void load(String fileName) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(fileName));

            String name = root.get("name");

            XmlReader.Element textures = root.getChildByName("textures");
            Array<XmlReader.Element> files = textures.getChildrenByName("texture");
            String[] texs = new String[files.size];

            for (int i = 0; i < files.size; i++)
                texs[i] = files.get(i).getText().trim();

            PARTICLE_MAP.put(name, texs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
