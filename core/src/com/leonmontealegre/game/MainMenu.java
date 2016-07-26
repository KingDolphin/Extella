package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Utils;

public class MainMenu {

    public Stage stage;
    public Table table, table2, table3, table4;
    public Button startButton;
    public Button soundButton;
    private Button optionButtons;

    public Texture background;

    public Image title;

    public MainMenu(final Game game) {
        stage = new Stage(new ScreenViewport());
        background = Assets.getTexture("menuBackground");

        table = new Table();
        table.pad(15f);
        table.setWidth(stage.getWidth());
        table.align(Align.center);
        {
            startButton = Utils.createButton("startButton");
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setCurrentState(Game.State.LevelSelect);
                }
            });

            float buttonWidth = Gdx.graphics.getWidth() / 7;
            table.add(startButton).width(buttonWidth).height(buttonWidth * 0.4f).pad(5f);

            table.setPosition(0, buttonWidth * 9 / 16);
        }
        stage.addActor(table);


        table2 = new Table();
        table2.pad(45f);
        table2.setWidth(stage.getWidth());
        table2.align(Align.top);
        {
            float buttonWidth = Gdx.graphics.getWidth() * 2 / 7;
            title = new Image(Assets.getTexture("logo"));
            table2.add(title).width(buttonWidth).height(buttonWidth * title.getHeight() / title.getWidth()).align(Align.center);

            table2.setPosition(0, Gdx.graphics.getHeight());
        }
        stage.addActor(table2);


        table3 = new Table();
        table3.pad(35f);
        table3.setWidth(stage.getWidth());
        table3.align(Align.top | Align.left);
        {
            float buttonSize = Gdx.graphics.getWidth() / 15;

            soundButton = Utils.createCheckButton("volumeOn", "volumeOff");
            soundButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.backgroundMusic.isPlaying()) {
                        game.backgroundMusic.pause();
                        soundButton.setChecked(true);
                        game.prefs.putBoolean("soundOn", false);
                    } else {
                        game.backgroundMusic.play();
                        soundButton.setChecked(false);
                        game.prefs.putBoolean("soundOn", true);
                    }
                }
            });

            table3.add(soundButton).width(buttonSize).height(buttonSize);

            table3.setPosition(0, Gdx.graphics.getHeight());
        }
        stage.addActor(table3);

        table4 = new Table();
        table4.pad(35f);
        table4.setWidth(stage.getWidth());
        table4.align(Align.top | Align.right);
        {
            float buttonSize = Gdx.graphics.getWidth() / 15;

            optionButtons = Utils.createButton("optionsButton");
            optionButtons.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // do something eventually
                }
            });

            table4.add(optionButtons).width(buttonSize).height(buttonSize);

            table4.setPosition(0, Gdx.graphics.getHeight());
        }
        stage.addActor(table4);
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void setVisible(boolean b) {
        for (Actor actor : stage.getActors())
            actor.setVisible(b);
    }

}
