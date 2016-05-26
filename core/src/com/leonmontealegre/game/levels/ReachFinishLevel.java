package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

public class ReachFinishLevel extends Level {

    private FinishLine finishLine;

    public ReachFinishLevel(Camera camera, String file) {
        super(camera, file);
    }

    public void update() {
        finishLine.update();

        super.update();
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        if (player != null)
            player.render(batch);

        for (Planet planet : planets)
            planet.render(batch);

        finishLine.render(batch);

        if (explosion != null)
            explosion.render(batch);


        if (displayHelp) {
            float w = Gdx.graphics.getWidth()/2;
            float h = w * helpText.getHeight() / helpText.getWidth();
            batch.draw(helpText, 0, Gdx.graphics.getHeight()-h-15, w, h);
        }

        batch.end();
    }

    @Override
    protected void load(String file) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(file));

            XmlReader.Element finish = root.getChildByName("finish");
            this.finishLine = new FinishLine(this, new Vector2(finish.getInt("x"), finish.getInt("y")), finish.getInt("radius"));

            super.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
