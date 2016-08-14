package com.leonmontealegre.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.leonmontealegre.game.levels.Level;

public class WinScreen extends Table {

    private final Game game;

    public WinScreen(Stage stage, final Skin skin, final Game game) {
        super(skin);

        this.game = game;

        this.pad(5f);
        this.setHeight(stage.getHeight());
        this.setWidth(stage.getWidth());
        this.align(Align.center);

        float buttonWidth = stage.getWidth() * 2 / 3, buttonHeight = stage.getHeight() / 5;
        TextButton nextLevelButton = new TextButton("Next Level", skin);
        {
            nextLevelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Level lev = game.getLevel();
                    if (lev != null) {
                        int xx = (lev.x + 1) % lev.galaxy.getHorizontalLevels();
                        int yy = lev.y + (lev.x + 1) / lev.galaxy.getHorizontalLevels();
                        String nextLevel = lev.galaxy.getLevel(xx, yy);
                        if (nextLevel != null)
                            game.startLevel(lev.galaxy, xx, yy, nextLevel);
                    }
                }
            });
            nextLevelButton.getLabel().setColor(0, 0, 0, 1);
        }
        this.add(nextLevelButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        this.row();

        TextButton restartButton = new TextButton("Restart", skin);
        {
            restartButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null) {
                        game.getLevel().restart();
                        WinScreen.this.setVisible(false);
                    }
                }
            });
            restartButton.getLabel().setColor(0, 0, 0, 1);
        }
        this.add(restartButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        this.row();

        TextButton backToMenuButton = new TextButton("Back to Menu", skin);
        {
            backToMenuButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.finishLevel();
                }
            });
            backToMenuButton.getLabel().setColor(0, 0, 0, 1);
        }
        this.add(backToMenuButton).width(buttonWidth).height(buttonHeight - 6).pad(2);

        stage.addActor(this);
    }

}
