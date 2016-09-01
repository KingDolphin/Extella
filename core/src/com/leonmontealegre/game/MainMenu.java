package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Logger;
import com.leonmontealegre.utils.Utils;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.after;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MainMenu {

    public Stage stage;
    public Table table;
    public Button soundButton;
    private Button optionsButton;

    private Image infiniteLevelImage;
    private Image levelSelectionImage;

    public Texture background;

    public Image title;

    public MainMenu(final Assets assets, final Skin skin, final Game game) {
        stage = new Stage(new ScreenViewport());
        background = assets.getTexture("menuBackground");

        table = new Table(skin);
        table.pad(15f);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        table.setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        float buttonWidth = Gdx.graphics.getWidth() / 7;
        float iconSize = Gdx.graphics.getWidth() / 15;

        soundButton = Utils.createCheckButton(assets.getTexture("volumeOn"), assets.getTexture("volumeOff"));
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
        table.add(soundButton).width(iconSize).height(iconSize).align(Align.left);

        title = new Image(assets.getTexture("logo"));
        table.add(title).width(2 * buttonWidth).height(iconSize).align(Align.center).colspan(2);

        optionsButton = Utils.createButton(assets.getTexture("optionsButton"));
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // do something eventually
            }
        });
        table.add(optionsButton).width(iconSize).height(iconSize).align(Align.right);

        table.row();

        float size = Gdx.graphics.getWidth() / 4.5f;

        final Stack infiniteLevelStack = new Stack();
        {
            infiniteLevelImage = new Image(assets.getTexture("blackHoleImage"));
            infiniteLevelImage.setOrigin(size/2, size/2);
            infiniteLevelStack.add(infiniteLevelImage);

            rotateScaleMove(infiniteLevelImage, false);

            Table overlay = new Table();
            Button infiniteLevelButton = Utils.createButton(assets.getTexture("playIcon"));
            infiniteLevelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    infiniteLevelImage.setZIndex(10);
                    infiniteLevelStack.setZIndex(10);
                    float size = Gdx.graphics.getWidth() / infiniteLevelImage.getWidth() * 1.5f;
                    ScaleByAction fillScreen = scaleBy(size, size, 1f);
                    infiniteLevelImage.clearActions();
                    infiniteLevelImage.addAction(fillScreen);

                    infiniteLevelImage.addAction(after(run(new Runnable() {
                        @Override
                        public void run() {
                            Logger.log("hi");
                            game.setCurrentState(Game.State.InfiniteLevelMenu);
                        }
                    })));
                }
            });
            overlay.add(infiniteLevelButton).size(size-50f);
            infiniteLevelStack.add(overlay);
        }
        table.add(infiniteLevelStack).align(Align.right).padRight(65f).width(size).height(size).expand().colspan(2);

        final Stack levelSelectionStack = new Stack();
        {
            levelSelectionImage = new Image(assets.getTexture("planet1"));
            levelSelectionImage.setOrigin(size/2, size/2);
            levelSelectionStack.add(levelSelectionImage);

            rotateScaleMove(levelSelectionImage, true);

            Table overlay = new Table();
            Button levelSelectionButton = Utils.createButton(assets.getTexture("levelSelectionIcon"));
            levelSelectionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setCurrentState(Game.State.LevelSelect);
                }
            });
            overlay.add(levelSelectionButton).size(size-90f);
            levelSelectionStack.add(overlay);
        }
        table.add(levelSelectionStack).align(Align.left).padLeft(65f).width(size).height(size).expand().colspan(2);

        stage.addActor(table);
        /*table = new Table(skin);
        table.pad(15f);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        {
            float buttonWidth = Gdx.graphics.getWidth() / 7;
            float iconWidth = Gdx.graphics.getWidth() / 15;

            soundButton = Utils.createCheckButton(assets.getTexture("volumeOn"), assets.getTexture("volumeOff"));
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
            table.add(soundButton).width(iconWidth).height(iconWidth).align(Align.left);

            title = new Image(assets.getTexture("logo"));
            table.add(title).width(2 * buttonWidth).height(iconWidth).align(Align.center);

            optionsButton = Utils.createButton(assets.getTexture("optionsButton"));
            optionsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // do something eventually
                }
            });
            table.add(optionsButton).width(iconWidth).height(iconWidth).align(Align.right);

            table.row();
            table.add("").expand().colspan(3);
            table.row();

            TextButton startButton = new TextButton("Start", skin);
            startButton.getLabel().setColor(0, 0, 0, 1);
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setCurrentState(Game.State.LevelSelect);

                }
            });
            table.add(startButton).width(buttonWidth).height(buttonWidth * 0.4f).colspan(3);
        }
        stage.addActor(table);*/
    }

    public void render(SpriteBatch batch) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void setVisible(boolean b) {
        for (Actor actor : stage.getActors())
            actor.setVisible(b);

        infiniteLevelImage.setScale(1);
        infiniteLevelImage.setZIndex(0);
        infiniteLevelImage.clearActions();
        rotateScaleMove(infiniteLevelImage, false);

        levelSelectionImage.clearActions();
        rotateScaleMove(levelSelectionImage, true);
    }

    private void rotateScaleMove(Actor actor, boolean clockwise) {
        RotateByAction rotate = rotateBy((clockwise ? -1 : 1) * 360, 20);
        ScaleToAction scaleActionUp = scaleTo(1.15f, 1.15f, 0.4f, Interpolation.pow2);
        ScaleToAction scaleActionDown = scaleTo(1, 1, 0.4f, Interpolation.pow2);
        Action action = parallel(forever(rotate), forever(sequence(scaleActionUp, delay(0.05f), scaleActionDown, delay(0.05f))));
        actor.addAction(action);
    }

}