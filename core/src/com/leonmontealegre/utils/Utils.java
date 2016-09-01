package com.leonmontealegre.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Utils {

    public static Button createButton(Texture up) {
        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(up));
        return new Button(bs);
    }

    public static Button createCheckButton(Texture up, Texture down) {
        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(up));
        bs.checked = new TextureRegionDrawable(new TextureRegion(down));
        return new Button(bs);
    }

}
