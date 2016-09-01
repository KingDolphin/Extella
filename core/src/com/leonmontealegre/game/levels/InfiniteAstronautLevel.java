package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.leonmontealegre.game.Assets;
import com.leonmontealegre.game.LevelUI;
import com.leonmontealegre.game.Options;

import java.util.ArrayList;

public class InfiniteAstronautLevel extends CollectAstronautsLevel {

    private static final int astronautSize = 48;

    private static final float extra = 1000;

    private Rectangle boundingBox;

    public InfiniteAstronautLevel(Assets assets, LevelUI ui, OrthographicCamera camera) {
        super(assets, null, 0, 0, ui, camera, null);

        backgrounds = new Background[1];
        backgrounds[0] = new Background(assets.getTexture("levelBackground"), Color.WHITE);//DynamicSpaceBackground(assets.getTexture("levelBackground"), Color.WHITE);

        startPos = new Vector2();
        player = new Player(assets, new Vector2());

        camera.position.set(0, 0, camera.position.z);
        camera.zoom = 1;
        camera.update();

        astronauts = new ArrayList<Astronaut>();
        planets = new ArrayList<Planet>();
        ui.astronautsLabel.setText(Options.astronautsCollectedText + rescuedAstronauts.size());
        astronautsRemaining = Integer.MAX_VALUE; // If someone manages to collect this many, they seriously deserve to win

        generate(true);
    }

    @Override
    public void restart() {
        planets.clear();

        astronauts.clear();
        rescuedAstronauts.clear();
        astronautsRemaining = Integer.MAX_VALUE;
        ui.astronautsLabel.setText(Options.astronautsCollectedText + rescuedAstronauts.size());
        ui.astronautsLabel.setVisible(true);

        player = new Player(assets, new Vector2());

        camera.position.set(0, 0, camera.position.z);
        camera.zoom = 1;
        camera.update();

        explosion = null;

        generate(true);
    }

    @Override
    protected void lose() {
        Preferences prefs = Gdx.app.getPreferences("Prefs");
        int score = Integer.MAX_VALUE - astronautsRemaining;
        if (score > prefs.getInteger("highscore", 0)) {
            prefs.putInteger("highscore", score);
            prefs.flush();
        }

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    Thread.sleep(1000);
                    ui.backToInfiniteMenu();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void update() {
        super.update();

        if (player != null) {
            camera.position.set(player.position.x, player.position.y, camera.position.z);
            camera.zoom = 1;
            camera.update();

            boundingBox = new Rectangle(camera.position.x - extra, camera.position.y - extra, Gdx.graphics.getWidth() + extra, Gdx.graphics.getHeight() + extra);

            boolean removed = false;
            boundingBox = new Rectangle(-extra / 2 + camera.position.x - Gdx.graphics.getWidth() / 2, -extra / 2 + camera.position.y - Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() + extra, Gdx.graphics.getHeight() + extra);
            for (int i = 0; i < planets.size(); i++) {
                Planet planet = planets.get(i);
                if (!Intersector.overlaps(planet.circle, boundingBox)) {
                    planets.remove(i);
                    i--;
                    removed = true;
                }
            }
            for (int i = 0; i < astronauts.size(); i++) {
                Rectangle rect = new Rectangle(astronauts.get(i).getPosition().x, astronauts.get(i).getPosition().y, astronautSize, astronautSize);
                if (!boundingBox.contains(rect)) {
                    astronauts.remove(i);
                    i--;
                    removed = true;
                }
            }
            if (removed)
                generate(false);
        }
    }

    public void debug(ShapeRenderer sr) {
        sr.setProjectionMatrix(camera.combined);
        Rectangle screenBox = new Rectangle(camera.position.x - Gdx.graphics.getWidth()/2, camera.position.y - Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.setColor(1, 0, 0, 1);
        sr.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        sr.setColor(0, 1, 0, 1);
        sr.rect(screenBox.x, screenBox.y, screenBox.width, screenBox.height);
    }

    @Override
    public void collectAstronaut(Astronaut astronaut) {
        astronautsRemaining--;
        astronauts.remove(astronaut);
        ui.astronautsLabel.setText(Options.astronautsCollectedText + (Integer.MAX_VALUE - astronautsRemaining));

        if (astronautsRemaining <= 0) // If someone actually manages to collect 2,147,483,648 astronauts
            this.win();
    }

    private void generate(boolean first) {
        Rectangle screenBox = new Rectangle(camera.position.x - Gdx.graphics.getWidth()/2, camera.position.y - Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        boundingBox = new Rectangle(camera.position.x - extra, camera.position.y - extra, Gdx.graphics.getWidth() + extra, Gdx.graphics.getHeight() + extra);

        // Generate planets
        int iterations = 0;
        while (iterations < (first ? 50 : 5)) {
            Vector2 vec = new Vector2(MathUtils.random(boundingBox.x, boundingBox.x+boundingBox.width), MathUtils.random(boundingBox.y, boundingBox.y+boundingBox.height));
            float rad = MathUtils.random(60f, 120f);
            Circle circle = new Circle(vec, rad);

            boolean okay = true;
            for (int i = 0; i < planets.size(); i++) {
                if (vec.dst(planets.get(i).position.x, planets.get(i).position.y) < rad+planets.get(i).radius+150) {
                    okay = false;
                    break;
                }
            }

            if (okay) {
                for (int i = 0; i < astronauts.size(); i++) {
                    if (circle.contains(astronauts.get(i).getPosition())) {
                        okay = false;
                        break;
                    }
                }
            }

            if (okay && vec.dst(0, 0) < 250)
                okay = false;

            if (okay && !first && Intersector.overlaps(circle, screenBox))
                okay = false;

            if (okay)
                planets.add(new Planet(assets, this, vec, rad, 0));

            iterations++;
        }

        // Generate astronauts
        iterations = 0;
        while (iterations < (first ? 30 : 5)) {
            Vector2 vec = new Vector2(MathUtils.random(boundingBox.x, boundingBox.x+boundingBox.width), MathUtils.random(boundingBox.y, boundingBox.y+boundingBox.height));

            boolean okay = true;
            for (int i = 0; i < planets.size(); i++) {
                if (planets.get(i).circle.contains(vec)) {
                    okay = false;
                    break;
                }
            }

            if (okay) {
                for (int i = 0; i < astronauts.size(); i++) {
                    if (vec.dst(astronauts.get(i).getPosition()) < 400) {
                        okay = false;
                        break;
                    }
                }
            }

            if (okay && vec.dst(0, 0) < 250)
                okay = false;

            if (okay && !first && screenBox.contains(new Rectangle(vec.x, vec.y, astronautSize, astronautSize)))
                okay = false;

            if (okay)
                astronauts.add(new Astronaut(assets, this, vec, astronautSize));

            iterations++;
        }
    }

}
