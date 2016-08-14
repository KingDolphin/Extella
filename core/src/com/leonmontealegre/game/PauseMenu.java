package com.leonmontealegre.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PauseMenu extends Table {

    private final Game game;

    public PauseMenu(Stage stage, final Skin skin, final Game game) {
        super(skin);

        this.game = game;

        this.pad(5f);
        this.setHeight(stage.getHeight());
        this.setWidth(stage.getWidth());
        this.align(Align.center);
        float buttonWidth = this.getWidth() * 1 / 3, buttonHeight = this.getHeight() / 5;

        TextButton resumeButton = new TextButton("Resume", skin);
        {
            resumeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null) {
                        game.getLevel().resume();
                        PauseMenu.this.setVisible(false);
                    }
                }
            });
            resumeButton.getLabel().setColor(0, 0, 0, 1);
        }
        this.add(resumeButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        this.row();

        TextButton restartButton = new TextButton("Restart", skin);
        {
            restartButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null) {
                        game.getLevel().restart();
                        PauseMenu.this.setVisible(false);
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
