package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Key;
import com.leonmontealegre.utils.Logger;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;

public class InfiniteLevelMenu {

    private Assets assets;

    private Game game;
    public Stage stage;

    private OrthographicCamera camera;

    private Stack stack;

    private Image blackHoleImage;

    private Label highScoreLabel;

    public InfiniteLevelMenu(final Assets assets, final Skin skin, final Game game) {
        this.assets = assets;
        this.game = game;
        Texture background = assets.getTexture("stars");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        stage = new Stage(new ScreenViewport());
        stage.setDebugAll(false);

        stack = new Stack();
        stack.setSize(stage.getWidth(), stage.getHeight());

        // Setup table with background
        Table table = new Table(skin);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        table.setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        // Create back button
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(assets.getTexture("backArrow"))));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                back();
            }
        });
        table.add(backButton).width(stage.getWidth() / 9).expand().align(Align.topRight).pad(15);
        table.row();

        // Get high score from prefs
        int highscore = game.prefs.getInteger("highscore", 0);

        // Create high-score label
        highScoreLabel = new Label("High Score\n" + highscore, skin);
        highScoreLabel.setFontScale(2f);
        highScoreLabel.setAlignment(Align.top);
        table.add(highScoreLabel).align(Align.center).align(Align.top).expand();
        table.row();

        // Create play button
        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(assets.getTexture("playIcon"))));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startInfiniteLevel();
            }
        });
        table.add(playButton).width(stage.getWidth() / 6).height(stage.getWidth() / 6).align(Align.center).pad(stage.getHeight()/10);
        stack.addActor(table);

        // Create black hole shrink animation
        Table table2 = new Table();
        table2.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table2.setPosition(0, 0);

        float size = stage.getWidth() * 1.5f;
        blackHoleImage = new Image(assets.getTexture("blackHoleImage"));
        blackHoleImage.setSize(size, size);
        blackHoleImage.setOrigin(size/2, size/2);
        blackHoleImage.setPosition((Gdx.graphics.getWidth() - size)/2, (Gdx.graphics.getHeight() - size)/2);
        table2.addActor(blackHoleImage);

        stack.add(table2);

        stage.addActor(stack);
    }

    public void update() {
        if (Input.getKey(Key.BACK))
            back();
    }

    public void render(SpriteBatch batch) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void setVisible(boolean b, boolean intro) {
        for (Actor actor : stage.getActors())
            actor.setVisible(b);

        if (b) {
            int highscore = game.prefs.getInteger("highscore", 0);
            highScoreLabel.setText("High Score\n" + highscore);
        }

        if (intro) {
            blackHoleImage.clearActions();
            blackHoleImage.setScale(1);
            ScaleToAction shrink = scaleTo(0, 0, 1f);
            blackHoleImage.addAction(shrink);
        }
    }

    private void back() {
        game.setCurrentState(Game.State.Menu);
    }

}