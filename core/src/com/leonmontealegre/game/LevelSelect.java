package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Key;
import com.leonmontealegre.utils.Logger;

public class LevelSelect {

    private static final int BLUR_AMOUNT = 3;

    public Stage stage;
    public ScrollPane scrollPane;

    private Galaxy[] galaxies;

    private boolean stopClick = false;

    private Texture background;

    private OrthographicCamera camera;

    private int time;
    private Vector3[] positions;

    public LevelSelect(final Skin skin, final Game game) {
        background = Assets.getTexture("stars");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        stage = new Stage(new ScreenViewport());

        // Positions for motion blur background
        positions = new Vector3[BLUR_AMOUNT];


        galaxies = new Galaxy[4];
        galaxies[0] = new Galaxy(game, skin, "levels/galaxy_a/GalaxyA.xml");
        galaxies[1] = new Galaxy(game, skin, "levels/galaxy_b/GalaxyB.xml");
        galaxies[2] = new Galaxy(game, skin, "levels/galaxy_c/GalaxyC.xml");
        galaxies[3] = new Galaxy(game, skin, "levels/galaxy_d/GalaxyD.xml");


        // Galaxy-Button group
        Group buttonGroup = new Group();
        buttonGroup.setWidth(stage.getWidth()*2);
        buttonGroup.setHeight(stage.getHeight()*2);


        // Create a new style for the galaxies for custom background image
        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle();
        tbStyle.font = skin.getFont("default-font"); // set font
        Texture galaxyTex = Assets.getTexture("galaxy");
        tbStyle.up = new TextureRegionDrawable(new TextureRegion(galaxyTex)); // set background

        float galaxyW = Gdx.graphics.getWidth() / 8f;
        float galaxyH = galaxyW * galaxyTex.getHeight() / galaxyTex.getWidth();

        buttonGroup.addActor(createButton("Star System\nA", 50, 50, galaxyW, galaxyH, galaxies[0], tbStyle, buttonGroup.getHeight()));
        buttonGroup.addActor(createButton("Star System\nB", 450, 300, galaxyW, galaxyH, galaxies[1], tbStyle, buttonGroup.getHeight()));
        buttonGroup.addActor(createButton("Star System\nC", 750, 650, galaxyW, galaxyH, galaxies[2], tbStyle, buttonGroup.getHeight()));
        buttonGroup.addActor(createButton("Star System\nD", 500, 1000, galaxyW, galaxyH, galaxies[3], tbStyle, buttonGroup.getHeight()));

        // Create a blank style for ScrollPane so that it has no texture for scroll bars
        scrollPane = new ScrollPane(buttonGroup, new ScrollPane.ScrollPaneStyle());
        scrollPane.setWidth(stage.getWidth());
        scrollPane.setHeight(stage.getHeight());
        scrollPane.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!stopClick) {
                    for (Galaxy galaxy : galaxies)
                        galaxy.setVisible(false);
                }
                stopClick = false;
            }
        });
        stage.addActor(scrollPane);

        // Add galaxies
        for (Galaxy galaxy : galaxies) {
            stage.addActor(galaxy);
            galaxy.setVisible(false);
        }

        // Create back button
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getTexture("backArrow"))));
        backButton.setWidth(Gdx.graphics.getWidth() / 9);
        backButton.setHeight(Gdx.graphics.getHeight() / 9);
        backButton.setPosition(Gdx.graphics.getWidth() - backButton.getWidth() - 15, Gdx.graphics.getHeight() - backButton.getHeight() - 15);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setCurrentState(Game.State.Menu);
            }
        });
        stage.addActor(backButton);
    }

    private TextButton createButton(String text, float x, float y, float w, float h, final Galaxy galaxy, TextButton.TextButtonStyle skin, float gHeight) {
        TextButton button = new TextButton(text, skin);
        button.setPosition(x, gHeight-h-y);
        button.setWidth(w);
        button.setHeight(h);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (galaxy != null) {
                    galaxy.setVisible(true);
                    stopClick = true;
                }
            }
        });
        button.getLabel().setFontScale(0.5f);
        return button;
    }

    public void update() {
        if (Input.getKey(Key.BACK)) {
            Logger.log("back");
        }

        camera.position.set(scrollPane.getScrollX()/2f + scrollPane.getMaxX()/2f, scrollPane.getMaxY() - scrollPane.getScrollY()/2f, camera.position.z);
        camera.update();

        positions[time] = camera.position.cpy();
        time++;
        if (time >= positions.length)
            time = 0;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < positions.length; i++) {
            int t = (time + i) % positions.length;
            if (positions[t] != null) {
                camera.position.set(positions[t].cpy());
                camera.update();
                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.draw(background, 0, 0, 1.55f*Gdx.graphics.getWidth(), 1.55f*Gdx.graphics.getHeight());
                batch.end();
            }
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void setVisible(boolean b) {
        for (Actor actor : stage.getActors())
            actor.setVisible(b);

        for (Galaxy galaxy : galaxies)
            galaxy.setVisible(false);
    }

}