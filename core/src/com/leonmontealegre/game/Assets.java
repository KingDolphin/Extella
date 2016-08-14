package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static final Map<String, Texture> textures = new HashMap<String, Texture>();

    public static void load() {
        Explosion.load();
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element assets = reader.parse(Gdx.files.internal("XML/assets.xml"));

            Array<XmlReader.Element> texs = assets.getChildrenByName("texture");
            for (XmlReader.Element tex : texs) {
                Texture texture = new Texture(tex.get("file"));
                String key = tex.get("key");
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
                textures.put(key, texture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Texture getTexture(String key) {
        return textures.get(key);
    }

}
