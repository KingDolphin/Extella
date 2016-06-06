package com.leonmontealegre.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Utils {

    public static Button createButton(String up) {
        Texture upTexture = new Texture(up);
        upTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        return new Button(bs);
    }

    public static Button createCheckButton(String up, String down) {
        Texture upTexture = new Texture(up);
        upTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Texture downTexture = new Texture(down);
        downTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        bs.checked = new TextureRegionDrawable(new TextureRegion(downTexture));
        return new Button(bs);
    }

}
