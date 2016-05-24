package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;
import java.util.ArrayList;

public class Level {

    public static final int TIME_SCALE = 1;

    private Texture background, stars;

    public Player player;

    public ArrayList<Planet> planets;

    private Explosion explosion;

    private Camera camera;

    public Level(Camera camera, String file) {
        this.camera = camera;
        background = new Texture("background.jpg");
        stars = new Texture("stars.png");
        planets = new ArrayList<Planet>();

        load(file);

//        player = new Player(new Vector2(50, 50));
//
//        planets.add(new Planet(this, new Vector2(750, 300), 75, 0));
//        planets.add(new Planet(this, new Vector2(300, 750), 50, 0));
//        planets.add(new Planet(this, new Vector2(900, 550), 60, 0));

        Explosion.load();
    }

    public Vector2 unproject(Vector2 coords) {
        Vector3 vec = camera.unproject(new Vector3(coords.x, Gdx.graphics.getHeight()-coords.y, 0));
        return new Vector2(vec.x, vec.y);
    }

    public void update() {
        for (Planet planet : planets) {
            planet.update();

            if (player != null)
                player.resolveCollisionWith(planet);
        }
        if (player != null) {

            player.update();

            if (player.shouldDestroy) {
                float width = player.sprite.getScaleX()*player.sprite.getWidth();
                float height = player.sprite.getScaleY()*player.sprite.getHeight();
                float size = Math.max(width,height);
                explosion = new Explosion(new Vector2(player.position).sub(size, size),
                                          new Vector2(2*size, 2*size));
                player = null;
            }
        }

        if (explosion != null)
            explosion.update();
    }

    public void drawBackground(SpriteBatch batch) {
        batch.begin();

        batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.draw(stars, 0, 0, camera.viewportWidth, camera.viewportHeight);

        batch.end();
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        if (player != null)
         player.render(batch);

        for (Planet planet : planets)
            planet.render(batch);

        if (explosion != null)
            explosion.render(batch);


        batch.end();
    }

    public void debug(ShapeRenderer sr) {
        for (Planet planet : planets)
            sr.circle(planet.getCircle().x, planet.getCircle().y, planet.getCircle().radius);

        if (player != null)
            sr.polygon(player.getPolygon().getTransformedVertices());
    }

    private void load(String file) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(file));

            XmlReader.Element player = root.getChildByName("player");
            this.player = new Player(new Vector2(player.getInt("x"), player.getInt("y")));

            Array<XmlReader.Element> planets = root.getChildrenByName("planet");
            for (XmlReader.Element planet : planets) {
                Vector2 position = new Vector2(planet.getInt("x"), planet.getInt("y"));
                int radius = planet.getInt("radius");
                float force = planet.getFloat("force", 0.0f);
                this.planets.add(new Planet(this, position, radius, force));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
