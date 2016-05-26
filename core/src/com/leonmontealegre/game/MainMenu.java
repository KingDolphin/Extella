package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu {

    public Stage stage;
    public Table table, table2, table3;
    public TextButton startButton;
    public TextButton optionsButton;
    public TextButton quitButton;
    public ImageButton soundButton;

    public Texture background;

    public Image title;

    public MainMenu(final Skin skin, final Game game) {
        stage = new Stage(new ScreenViewport());
        background = new Texture("menuBackground.jpg");

        table = new Table();
        table.pad(15f);
        table.setWidth(stage.getWidth());
        table.align(Align.center);
        {
            startButton = new TextButton("Start", skin);
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.startLevel("levels/LevelTestFinishLine.xml");
                     }
            });
            startButton.getLabel().setFontScale(4f);

            optionsButton = new TextButton("Options", skin);
            optionsButton.getLabel().setFontScale(4f);

            quitButton = new TextButton("Quit", skin);
            quitButton.getLabel().setFontScale(4f);

            float buttonWidth = Gdx.graphics.getWidth() / 7;
            table.add(startButton).width(buttonWidth).height(buttonWidth * 9 / 16).pad(5f);
            table.add(optionsButton).width(buttonWidth).height(buttonWidth * 9 / 16).pad(5f);
            table.add(quitButton).width(buttonWidth).height(buttonWidth * 9 / 16).pad(5f);

            table.setPosition(0, buttonWidth * 9 / 16);
        }
        stage.addActor(table);


        table2 = new Table();
        table2.pad(45f);
        table2.setWidth(stage.getWidth());
        table2.align(Align.center | Align.top);
        {
            float buttonWidth = Gdx.graphics.getWidth() / 7;
            title = new Image(new Texture("title.png"));
            table2.add(title).width(buttonWidth * 2).height(buttonWidth * 9 / 16);

            table2.setPosition(0, Gdx.graphics.getHeight());
        }
        stage.addActor(table2);


        table3 = new Table();
        table3.pad(5f);
        table3.setWidth(stage.getWidth());
        table3.align(Align.top | Align.left);
        {
            float buttonWidth = Gdx.graphics.getWidth() / 9;

            final TextureRegionDrawable soundOn = new TextureRegionDrawable(new TextureRegion(new Texture("volumeOn.png")));
            final TextureRegionDrawable soundOff = new TextureRegionDrawable(new TextureRegion(new Texture("volumeOff.png")));
            soundButton = new ImageButton(soundOn, null, soundOff);
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

            table3.add(soundButton).width(buttonWidth).height(buttonWidth);

            table3.setPosition(0, Gdx.graphics.getHeight());
        }
        stage.addActor(table3);
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

}
