package com.leonmontealegre.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.leonmontealegre.game.Assets;

public class Utils {

    public static Button createButton(String up) {
        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(up)));
        return new Button(bs);
    }

    public static Button createCheckButton(String up, String down) {
        Button.ButtonStyle bs = new Button.ButtonStyle();
        bs.up = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(up)));
        bs.checked = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(down)));
        return new Button(bs);
    }

}
