package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class World {

    public static final int TIME_SCALE = 1;

    private Texture background, stars;

    private OrthographicCamera camera;

    public Player player;

    public ArrayList<Planet> planets;

    ShapeRenderer sr;

    boolean debug = true;

    private Explosion explosion;

    public World() {
        background = new Texture("background.jpg");
        stars = new Texture("stars.png");
        planets = new ArrayList<Planet>();

        camera = new OrthographicCamera();

        player = new Player(0, 0);

        planets.add(new Planet(this, 750, 300, 150, 1e7f));
        planets.add(new Planet(this, 300, 750, 100, 1e6f));

        sr = new ShapeRenderer();

        Explosion.load();
    }

    public void update() {
        if (player != null) {
            for (Planet planet : planets) {
                planet.update();

                player.resolveCollisionWith(planet);
            }

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

    public void render(SpriteBatch batch) {
        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(stars, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (player != null)
         player.render(batch);

        for (Planet planet : planets)
            planet.render(batch);

        if (explosion != null)
            explosion.render(batch);

        batch.end();


        if (debug) {
            sr.setColor(Color.GREEN);
            sr.begin(ShapeRenderer.ShapeType.Line);
            for (Planet planet : planets)
                 sr.circle(planet.getCircle().x, planet.getCircle().y, planet.getCircle().radius);

            if (player != null)
                sr.polygon(player.getPolygon().getTransformedVertices());
            sr.end();
        }
    }

}
