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
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.utils.Utils;

public class LevelUI {

    public Stage stage;

    private Button pauseButton;

    private Table pauseMenuTable;
    private Table winTable;
    private Table loseTable;

    public Label winOverlay, astronautsLabel, helpOverlay;

    private boolean losing = false;

    public LevelUI(final Skin skin, final Game game) {
        stage = new Stage(new ScreenViewport());

        pauseButton = Utils.createButton("textures/UI/pause.png");
        {
            pauseButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.getLevel() != null && !game.getLevel().hasWon && !losing && !loseTable.isVisible()) {
                        helpOverlay.setVisible(false);
                        if (game.getLevel().isPaused()) {
                            game.getLevel().resume();
                            pauseMenuTable.setVisible(false);
                        } else {
                            game.getLevel().pause();
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

        pauseMenuTable = new Table();
        {
            pauseMenuTable.pad(5f);
            pauseMenuTable.setHeight(stage.getHeight());
            pauseMenuTable.setWidth(stage.getWidth());
            pauseMenuTable.align(Align.center);
            float buttonWidth = pauseMenuTable.getWidth() * 2 / 3, buttonHeight = pauseMenuTable.getHeight() / 5;

            TextButton resumeButton = new TextButton("Resume", skin);
            {
                resumeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (game.getLevel() != null) {
                            game.getLevel().resume();
                            pauseMenuTable.setVisible(false);
                        }
                    }
                });
                resumeButton.getLabel().setColor(0, 0, 0, 1);
            }
            pauseMenuTable.add(resumeButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
            pauseMenuTable.row();

            TextButton restartButton = new TextButton("Restart", skin);
            {
                restartButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (game.getLevel() != null) {
                            game.getLevel().restart();
                            pauseMenuTable.setVisible(false);
                        }
                    }
                });
                restartButton.getLabel().setColor(0, 0, 0, 1);
            }
            pauseMenuTable.add(restartButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
            pauseMenuTable.row();

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
            pauseMenuTable.add(backToMenuButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        }
        stage.addActor(pauseMenuTable);


        winTable = new Table();
        {
            winTable.pad(5f);
            winTable.setHeight(stage.getHeight());
            winTable.setWidth(stage.getWidth());
            winTable.align(Align.center);
            float buttonWidth = winTable.getWidth() * 2 / 3, buttonHeight = winTable.getHeight() / 5;

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
            winTable.add(nextLevelButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
            winTable.row();

            TextButton restartButton = new TextButton("Restart", skin);
            {
                restartButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (game.getLevel() != null) {
                            game.getLevel().restart();
                            winTable.setVisible(false);
                        }
                    }
                });
                restartButton.getLabel().setColor(0, 0, 0, 1);
            }
            winTable.add(restartButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
            winTable.row();

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
            winTable.add(backToMenuButton).width(buttonWidth).height(buttonHeight - 6).pad(2);
        }
        stage.addActor(winTable);


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
                winTable.setVisible(true);
                final float endY = winTable.getY();

                try {
                    winTable.setY(winTable.getHeight());
                    float y = winTable.getHeight();
                    while (y > endY) {
                        winTable.setY(y);
                        y -= 30;
                        Thread.sleep(10);
                    }
                    winTable.setY(endY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showLoseScreen() {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    losing = true;
                    Thread.sleep(1000);
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
