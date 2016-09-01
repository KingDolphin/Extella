package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Utils;

public class LevelUI {

    public Stage stage;

    private Game game;

    private Button pauseButton;

    private Table pauseMenuTable;
    private WinScreen winTable;
    private Table loseTable;

    public Label winOverlay, astronautsLabel, helpOverlay;

    private boolean losing = false;

    public LevelUI(final Assets assets, final Skin skin, final Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        pauseButton = Utils.createButton(assets.getTexture("pauseButton"));
        {
            pauseButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null && !game.getLevel().hasWon && !losing && !loseTable.isVisible()) {
                        helpOverlay.setVisible(false);
                        if (game.getLevel().isPaused()) {
                            game.setCurrentState(Game.State.Playing);
                            pauseMenuTable.setVisible(false);
                        } else {
                            game.setCurrentState(Game.State.Paused);
                            pauseMenuTable.setVisible(true);
                        }
                    }
                }
            });
            float restartButtonSize = stage.getWidth() / 15;
            pauseButton.setWidth(restartButtonSize);
            pauseButton.setHeight(restartButtonSize);
            pauseButton.setPosition(stage.getWidth() - pauseButton.getWidth() - 15, stage.getHeight() - pauseButton.getHeight() - 15);
        }
        stage.addActor(pauseButton);

        pauseMenuTable = new PauseMenu(stage, skin, game);

        winTable = new WinScreen(assets, stage, skin, game);

        loseTable = new Table();
        {
            loseTable.pad(5f);
            loseTable.setHeight(stage.getHeight());
            loseTable.setWidth(stage.getWidth());
            loseTable.align(Align.center);
            float buttonWidth = loseTable.getWidth() * 2 / 3, buttonHeight = loseTable.getHeight() / 5;

            TextButton restartButton = new TextButton("Restart", skin);
            {
                restartButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (game.getLevel() != null) {
                            game.setCurrentState(Game.State.Playing);
                            game.getLevel().restart();
                            loseTable.setVisible(false);
                        }
                    }
                });
                restartButton.getLabel().setColor(0, 0, 0, 1);
            }
            loseTable.add(restartButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
            loseTable.row();

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
            loseTable.add(backToMenuButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        }
        stage.addActor(loseTable);


        astronautsLabel = new Label(Options.astronautsRemainingText, skin);
        {
            float w = astronautsLabel.getWidth();
            float ar = astronautsLabel.getHeight() / astronautsLabel.getWidth();
            astronautsLabel.setWidth(stage.getWidth() / 2);
            astronautsLabel.setHeight(astronautsLabel.getWidth() * ar);
            astronautsLabel.setFontScale(astronautsLabel.getWidth() / w);
            astronautsLabel.setPosition(stage.getWidth() / 2 - astronautsLabel.getWidth() / 2, stage.getHeight() - astronautsLabel.getHeight() - 15);
            astronautsLabel.setVisible(false);
        }
        stage.addActor(astronautsLabel);

        pauseMenuTable.setVisible(false);

        helpOverlay = new Label(Options.tutorialText0, skin);
        {
            float w = helpOverlay.getWidth();
            float ar = helpOverlay.getHeight() / helpOverlay.getWidth();
            helpOverlay.setWidth(stage.getWidth() / 2);
            helpOverlay.setHeight(helpOverlay.getWidth() * ar);
            helpOverlay.setFontScale(helpOverlay.getWidth() / w);
            helpOverlay.setPosition(stage.getWidth() / 2 - helpOverlay.getWidth() / 2, stage.getHeight() / 2 - helpOverlay.getHeight() / 2);
            helpOverlay.setAlignment(Align.center);
            helpOverlay.setVisible(false);
        }
        stage.addActor(helpOverlay);

        winOverlay = new Label(Options.winText, skin);
        {
            float w = winOverlay.getWidth();
            float ar = winOverlay.getHeight() / winOverlay.getWidth();
            winOverlay.setWidth(stage.getWidth() / 2);
            winOverlay.setHeight(winOverlay.getWidth() * ar);
            winOverlay.setFontScale(winOverlay.getWidth() / w);
            winOverlay.setPosition(stage.getWidth() / 2 - winOverlay.getWidth() / 2, stage.getHeight() / 2 - winOverlay.getHeight() / 2);
            winOverlay.setVisible(false);
        }
        stage.addActor(winOverlay);
    }

    public void showWinScreen() {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                winOverlay.setVisible(false);

                winTable.show();
            }
        }).start();
    }

    public void showLoseScreen() {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    losing = true;
                    Thread.sleep(300);
                    loseTable.setVisible(true);
                    final float endY = loseTable.getY();

                    loseTable.setY(loseTable.getHeight());
                    float y = loseTable.getHeight();
                    while (y > endY) {
                        loseTable.setY(y);
                        y -= 30;
                        Thread.sleep(10);
                    }

                    loseTable.setY(endY);
                    losing = false;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Because I'm too lazy to send the reference of Game to the infinite astronaut level
    public void backToInfiniteMenu() {
        game.setCurrentState(Game.State.InfiniteLevelMenu);
    }

    public void update() {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public void render() {
        stage.draw();
    }

    public void setVisible(boolean b) {
        pauseButton.setVisible(b);

        pauseMenuTable.setVisible(false);
        winTable.setVisible(false);
        loseTable.setVisible(false);
        astronautsLabel.setVisible(false);
        winOverlay.setVisible(false);
        helpOverlay.setVisible(false);
    }

}
