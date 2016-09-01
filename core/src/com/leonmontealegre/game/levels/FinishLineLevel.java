package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.Assets;
import com.leonmontealegre.game.Galaxy;
import com.leonmontealegre.game.LevelUI;
import com.leonmontealegre.game.levels.cutscenes.FinishLineLevelCutscene;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Touch;

public class FinishLineLevel extends Level {

    public FinishLine finishLine;

    public FinishLineLevel(Assets assets, Galaxy galaxy, int x, int y, LevelUI ui, OrthographicCamera camera, XmlReader.Element root) {
        super(assets, galaxy, x, y, ui, camera, root);

        this.cutscene = new FinishLineLevelCutscene(camera, this);
    }

    public void update() {
        if (cutscene.isFinished()) {
            finishLine.update();

            super.update();
        } else {
            cutscene.update();

            for (Touch touch : Input.touches) {
                if (touch.isFirstPressed()) {
                    cutscene.skip();
                    touch.release();
                    break;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        finishLine.render(batch);

        super.render(batch);
    }

    @Override
    protected void load(XmlReader.Element root) {
        XmlReader.Element finish = root.getChildByName("finish");
        this.finishLine = new FinishLine(assets, this, new Vector2(finish.getInt("x"), finish.getInt("y")), finish.getInt("radius"));

        super.load(root);
    }

}
