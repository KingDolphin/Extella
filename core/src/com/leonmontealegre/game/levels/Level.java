package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.Explosion;
import com.leonmontealegre.game.Options;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Touch;

import java.io.IOException;
import java.util.ArrayList;

public class Level {

    public static final int TIME_SCALE = 1;

    public static Texture background = new Texture("background.jpg");
    public static Texture stars = new Texture("stars.png");

    public Player player;

    public ArrayList<Planet> planets;
    public ArrayList<Debris> debris;

    protected Explosion explosion;

    protected Camera camera;

    private Vector2 startPos;

    public boolean hasWon = false;

    protected Texture helpText;
    protected boolean displayHelp = false;
    protected boolean paused = false;

    public Level(Camera camera, String file) {
        this.camera = camera;
        helpText = new Texture("helpText.png");
        planets = new ArrayList<Planet>();
        debris = new ArrayList<Debris>();

        load(file);

        Explosion.load();
    }

    public void restart() {
        paused = false;
        player = new Player(startPos);
        for (int i = 0; i < planets.size(); i++) {
            Planet p = planets.get(i);
            planets.set(i, new Planet(this, new Vector2(p.sprite.getX(), p.sprite.getY()), p.radius, p.force));
        }
        for (int i = 0; i < debris.size(); i++) {
            Debris d = debris.get(i);
            debris.set(i, new Debris(this, new Vector2(d.sprite.getX(), d.sprite.getY()), d.radius));
        }

        explosion = null;
    }

    public Vector2 unproject(Vector2 coords) {
        Vector3 vec = camera.unproject(new Vector3(coords.x, Gdx.graphics.getHeight()-coords.y, 0));
        return new Vector2(vec.x, vec.y);
    }

    public void update() {
        if (!paused) {
            for (Planet planet : planets) {
                planet.update();

                if (player != null)
                    player.resolveCollisionWith(planet);
            }
            for (Debris d : debris) {
                d.update();
            }

            if (player != null) {
                player.update();

                if (player.shouldDestroy) {
                    float width = player.sprite.getScaleX() * player.sprite.getWidth();
                    float height = player.sprite.getScaleY() * player.sprite.getHeight();
                    float size = Math.max(width, height);
                    explosion = new Explosion(new Vector2(player.position).sub(size, size),
                            new Vector2(2 * size, 2 * size));
                    player = null;
                }
            }

            if (displayHelp) {
                for (Touch t : Input.touches)
                    if (t.isFirstPressed())
                        displayHelp = false;
            }

            if (explosion != null)
                explosion.update();
        }
    }

    public void win() {
        if (!hasWon) {
            hasWon = true;
            Options.TARGET_UPS /= 4;
        }
    }

    public void drawBackground(SpriteBatch batch) {
        batch.begin();

        batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.draw(stars, 0, 0, camera.viewportWidth, camera.viewportHeight);

        batch.end();
    }

    public void render(SpriteBatch batch) {
        if (!batch.isDrawing())
            batch.begin();

        if (player != null)
            player.render(batch);

        for (Planet planet : planets)
            planet.render(batch);

        for (Debris d : debris)
            d.render(batch);

        if (explosion != null)
            explosion.render(batch);


        if (displayHelp) {
            float w = Gdx.graphics.getWidth()/2;
            float h = w * helpText.getHeight() / helpText.getWidth();
            batch.draw(helpText, 0, Gdx.graphics.getHeight()-h-15, w, h);
        }

        batch.end();
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void debug(ShapeRenderer sr) {
        for (Planet planet : planets) {
            sr.setColor(Color.BLUE);
            sr.circle(planet.getTapCircle().x, planet.getTapCircle().y, planet.getTapCircle().radius);
            sr.setColor(Color.GREEN);
            sr.circle(planet.getCircle().x, planet.getCircle().y, planet.getCircle().radius);
        }

        for (Debris d : debris) {
            sr.setColor(Color.GREEN);
            sr.circle(d.getCircle().x, d.getCircle().y, d.getCircle().radius);
        }

        if (player != null)
            sr.polygon(player.getPolygon().getTransformedVertices());
    }

    protected void load(String file) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(file));

            displayHelp = root.getBoolean("displayTutorial", false);

            XmlReader.Element player = root.getChildByName("player");
            this.player = new Player(new Vector2(player.getInt("x"), player.getInt("y")));
            this.startPos = new Vector2(this.player.position);

            Array<XmlReader.Element> planets = root.getChildrenByName("planet");
            for (XmlReader.Element planet : planets) {
                Vector2 position = new Vector2(planet.getInt("x"), planet.getInt("y"));
                int radius = planet.getInt("radius");
                float force = planet.getFloat("force", 0.0f);
                this.planets.add(new Planet(this, position, radius, force));
            }

            Array<XmlReader.Element> debris = root.getChildrenByName("debris");
            for (XmlReader.Element d : debris) {
                Vector2 position = new Vector2(d.getInt("x"), d.getInt("y"));
                int radius = d.getInt("radius");
                this.debris.add(new Debris(this, position, radius));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
