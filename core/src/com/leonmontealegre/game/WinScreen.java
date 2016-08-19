package com.leonmontealegre.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.utils.Utils;

public class WinScreen extends Table {

    private final Game game;

    public WinScreen(Stage stage, final Skin skin, final Game game) {
        super(skin);

        this.game = game;

        this.setWidth(stage.getWidth() / 2);
        this.setHeight(stage.getHeight());
        this.setPosition(stage.getWidth() / 2 - this.getWidth() / 2, 0);

//        Label levelCompleteLabel = new Label(Options.levelCompleteText, skin);
//        {
//            float w = levelCompleteLabel.getWidth();
//            float ar = levelCompleteLabel.getHeight() / levelCompleteLabel.getWidth();
//            levelCompleteLabel.setWidth(this.getWidth()*0.75f);
//            levelCompleteLabel.setHeight(levelCompleteLabel.getWidth() * ar);
//            levelCompleteLabel.setFontScale(levelCompleteLabel.getWidth() / w);
//            levelCompleteLabel.setPosition(this.getWidth() / 2 - levelCompleteLabel.getWidth() / 2, this.getHeight() - levelCompleteLabel.getHeight() - 40);
//        }
//        this.addActor(levelCompleteLabel);
        Label levelCompleteLabel = new Label(Options.levelCompleteText, skin);
        this.add(levelCompleteLabel).padTop(40f).align(Align.top).expand();
        this.row().height(600f);
        this.add("TEST").expand();
        this.row();

        Button backToMenuButton = Utils.createButton("levelSelectionIcon");
        {
            backToMenuButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.finishLevel();
                }
            });
        }
        this.add(backToMenuButton).padBottom(100f).width(stage.getWidth() / 4).padRight(30f);

        Button replayButton = Utils.createButton("replayIcon");
        {
            replayButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null) {
                        game.getLevel().restart();
                        WinScreen.this.setVisible(false);
                    }
                }
            });
        }
        this.add(replayButton).padBottom(100f).width(stage.getWidth() / 4).padRight(30f);

        Button nextLevelButton = Utils.createButton("fastForwardIcon");
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
        }
        this.add(nextLevelButton).padBottom(100f).width(stage.getWidth() / 4);

//        float buttonWidth = stage.getWidth() * 2 / 3, buttonHeight = stage.getHeight() / 5;
//        TextButton nextLevelButton = new TextButton("Next Level", skin);
//        {
//            nextLevelButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    Level lev = game.getLevel();
//                    if (lev != null) {
//                        int xx = (lev.x + 1) % lev.galaxy.getHorizontalLevels();
//                        int yy = lev.y + (lev.x + 1) / lev.galaxy.getHorizontalLevels();
//                        String nextLevel = lev.galaxy.getLevel(xx, yy);
//                        if (nextLevel != null)
//                            game.startLevel(lev.galaxy, xx, yy, nextLevel);
//                    }
//                }
//            });
//            nextLevelButton.getLabel().setColor(0, 0, 0, 1);
//        }
//        this.add(nextLevelButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
//        this.row();
//
//        TextButton restartButton = new TextButton("Restart", skin);
//        {
//            restartButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    if (game.getLevel() != null) {
//                        game.getLevel().restart();
//                        WinScreen.this.setVisible(false);
//                    }
//                }
//            });
//            restartButton.getLabel().setColor(0, 0, 0, 1);
//        }
//        this.add(restartButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
//        this.row();
//
//        TextButton backToMenuButton = new TextButton("Back to Menu", skin);
//        {
//            backToMenuButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    game.finishLevel();
//                }
//            });
//            backToMenuButton.getLabel().setColor(0, 0, 0, 1);
//        }
//        this.add(backToMenuButton).width(buttonWidth).height(buttonHeight - 6).pad(2);

        stage.addActor(this);
    }

    public void show() {
        this.setVisible(true);
        final float endY = this.getY();

        try {
            this.setY(this.getHeight());
            float y = this.getHeight();
            while (y > endY) {
                this.setY(y);
                y -= 30;
                Thread.sleep(10);
            }
            this.setY(endY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
