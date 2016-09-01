package com.leonmontealegre.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.Assets;
import com.leonmontealegre.game.Explosion;
import com.leonmontealegre.game.Galaxy;
import com.leonmontealegre.game.LevelUI;
import com.leonmontealegre.game.Options;
import com.leonmontealegre.game.levels.cutscenes.Cutscene;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Logger;
import com.leonmontealegre.utils.Touch;

import java.util.ArrayList;

public class Level {

    public static final int TIME_SCALE = 1;

    protected Assets assets;

    public Player player;

    public ArrayList<Planet> planets;
    public ArrayList<Debris> debris;
    public ArrayList<BlackHole> blackHoles;

    protected Explosion explosion;

    protected OrthographicCamera camera;

    protected Vector2 startPos;
    protected float startZoom;
    private boolean displayHelp = false;

    public boolean hasWon = false;

    protected boolean paused = false;

    protected LevelUI ui;

    public final Galaxy galaxy;

    public final int x, y;

    protected Background[] backgrounds;

    protected Cutscene cutscene;

    public int time = 0;
    public boolean deathAfterWin = false;

    public Level(Assets assets, Galaxy galaxy, int x, int y, LevelUI ui, OrthographicCamera camera, XmlReader.Element root) {
        this.assets = assets;
        this.galaxy = galaxy;
        this.x = x;
        this.y = y;
        this.ui = ui;
        this.camera = camera;
        planets = new ArrayList<Planet>();
        debris = new ArrayList<Debris>();
        blackHoles = new ArrayList<BlackHole>();
        BlackHole.count = 0;

        BlackHole.shader.begin();
        BlackHole.shader.setUniformf("screenSize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (root != null)
            load(root);
        for (int i = BlackHole.count; i < BlackHole.MAX_BLACK_HOLES; i++) {
            BlackHole.shader.setUniformf("blackHole[" + i + "].radius", 0);
            BlackHole.shader.setUniformf("blackHole[" + i + "].deformRadius", 0);
            BlackHole.shader.setUniformf("blackHole[" + i + "].position", new Vector2(0, 0));
            Logger.log("blackHole[" + i + "]");
        }
        BlackHole.shader.end();
    }

    public void restart() {
        time = 0;
        deathAfterWin = false;
        hasWon = false;
        paused = false;
        player = new Player(assets, startPos);
        ui.helpOverlay.setVisible(displayHelp);
        for (int i = 0; i < planets.size(); i++) {
            Planet p = planets.get(i);
            planets.set(i, new Planet(assets, this, p.startPosition, p.radius, p.force));
        }
        for (int i = 0; i < debris.size(); i++) {
            Debris d = debris.get(i);
            debris.set(i, new Debris(assets, this, new Vector2(d.sprite.getX(), d.sprite.getY()), d.radius));
        }
        for (int i = 0; i < blackHoles.size(); i++) {
            blackHoles.get(i).isOn = false;
        }

        explosion = null;

        if (cutscene != null)
            cutscene.restart();
    }

    public Vector2 unproject(Vector2 coords) {
        Vector3 vec = camera.unproject(new Vector3(coords.x, Gdx.graphics.getHeight()-coords.y, 0));
        return new Vector2(vec.x, vec.y);
    }

    public void update() {
        if (!hasWon)
            time++;

        for (Planet planet : planets) {
            planet.update();

            if (player != null)
                player.resolveCollisionWith(planet);
        }
        for (Debris d : debris)
            d.update();

        for (BlackHole bh : blackHoles)
            bh.update();

        if (player != null) {
            player.update();

            if (player.shouldDestroy) {
                float width = player.sprite.getScaleX() * player.sprite.getWidth();
                float height = player.sprite.getScaleY() * player.sprite.getHeight();
                float size = Math.max(width, height);
                explosion = new Explosion(assets, new Vector2(player.position).sub(size, size),
                        new Vector2(2 * size, 2 * size));
                player = null;

                if (!hasWon)
                    lose();
                else
                    deathAfterWin = true;
            }
        }

        if (ui.helpOverlay.isVisible()) {
            for (Touch t : Input.touches)
                if (t.isFirstPressed())
                    ui.helpOverlay.setVisible(false);
        }

        if (explosion != null)
            explosion.update();

        for (Background background : backgrounds)
            background.update();
    }

    protected void lose() {
        ui.showLoseScreen();
    }

    public void win() {
        if (!hasWon) {
            hasWon = true;
            ui.winOverlay.setVisible(true); // "You win!" text
            Options.TARGET_UPS /= 4; // Slow motion
        }
    }

    public void drawBackground(SpriteBatch batch) {
        batch.begin();
        for (Background background : backgrounds)
            background.render(batch);
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

        for (BlackHole blackHole : blackHoles) {
            sr.setColor(Color.BLUE);
            sr.circle(blackHole.getTapCircle().x, blackHole.getTapCircle().y, blackHole.getTapCircle().radius);
            sr.setColor(Color.GREEN);
            sr.circle(blackHole.getCircle().x, blackHole.getCircle().y, blackHole.getCircle().radius);
        }

        for (Debris d : debris) {
            sr.setColor(Color.GREEN);
            sr.circle(d.getCircle().x, d.getCircle().y, d.getCircle().radius);
        }

        if (player != null)
            sr.polygon(player.getPolygon().getTransformedVertices());
    }

    public void dispose() {
        for (Background bg : backgrounds)
            bg.dispose();
    }

    public boolean hasStarted() {
        return (cutscene == null || cutscene.isFinished());
    }

    protected void load(XmlReader.Element root) {
        displayHelp = root.getBoolean("displayTutorial", false);
        ui.helpOverlay.setVisible(displayHelp);

        camera.position.set(root.getFloat("x", 0)*Gdx.graphics.getWidth(),
                            root.getFloat("y", 0)*Gdx.graphics.getHeight(), camera.position.z);
        startZoom = camera.zoom = root.getFloat("zoom", 1);
        camera.zoom *= 1920f / Gdx.graphics.getWidth();
        camera.update();

        Array<XmlReader.Element> backgrounds = root.getChildrenByName("background");
        this.backgrounds = new Background[backgrounds.size];
        for (int i = 0; i < backgrounds.size; i++) {
            XmlReader.Element background = backgrounds.get(i);
            String type = background.get("type");

            String[] colors = background.get("color").split(" ");
            Color color = new Color(Integer.parseInt(colors[0].trim()) / 255f,
                    Integer.parseInt(colors[1].trim()) / 255f,
                    Integer.parseInt(colors[2].trim()) / 255f, 1f);

            if (type.equals("file")) {
                String texture = background.get("texture").trim();

                this.backgrounds[i] = new Background(assets.getTexture(texture), color);
            } else if (type.equals("dynamic")) {
                String texture = background.get("texture").trim();

                this.backgrounds[i] = new DynamicSpaceBackground(assets.getTexture(texture), color);
            }
        }

        XmlReader.Element player = root.getChildByName("player");
        this.player = new Player(assets, new Vector2(player.getInt("x"), player.getInt("y")));
        this.startPos = new Vector2(this.player.position);

        Array<XmlReader.Element> planets = root.getChildrenByName("planet");
        for (XmlReader.Element planet : planets) {
            Vector2 position = new Vector2(planet.getInt("x"), planet.getInt("y"));
            int radius = planet.getInt("radius");
            float force = planet.getFloat("force", 0.0f);
            this.planets.add(new Planet(assets, this, position, radius, force));
        }

        Array<XmlReader.Element> debris = root.getChildrenByName("debris");
        for (XmlReader.Element d : debris) {
            Vector2 position = new Vector2(d.getInt("x"), d.getInt("y"));
            int radius = d.getInt("radius");
            this.debris.add(new Debris(assets, this, position, radius));
        }

        Array<XmlReader.Element> blackHoles = root.getChildrenByName("black_hole");
        for (XmlReader.Element blackHole : blackHoles) {
            Vector2 position = new Vector2(blackHole.getInt("x"), blackHole.getInt("y"));
            int radius = blackHole.getInt("radius");
            int deformRadius = blackHole.getInt("deform_radius");
            this.blackHoles.add(new BlackHole(assets, this, position, radius, deformRadius));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            Logger.log("Garbage collecting the level...");
        } finally {
            super.finalize();
        }
    }

}
