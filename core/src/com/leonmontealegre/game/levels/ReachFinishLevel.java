package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.Galaxy;
import com.leonmontealegre.game.LevelUI;

public class ReachFinishLevel extends Level {

    private FinishLine finishLine;

    public ReachFinishLevel(Galaxy galaxy, int x, int y, LevelUI ui, OrthographicCamera camera, XmlReader.Element root) {
        super(galaxy, x, y, ui, camera, root);
    }

    public void update() {
        finishLine.update();

        super.update();
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        finishLine.render(batch);

        super.render(batch);
    }

    @Override
    protected void load(XmlReader.Element root) {
        XmlReader.Element finish = root.getChildByName("finish");
        this.finishLine = new FinishLine(this, new Vector2(finish.getInt("x"), finish.getInt("y")), finish.getInt("radius"));

        super.load(root);
    }

}
