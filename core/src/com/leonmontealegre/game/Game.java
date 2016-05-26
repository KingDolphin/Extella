package com.leonmontealegre.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.game.levels.CollectAstronautsLevel;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.game.levels.ReachFinishLevel;
import com.leonmontealegre.utils.Input;

import java.io.IOException;

public class Game extends ApplicationAdapter {

	private static final boolean debug = false;

	private long lastTime = System.nanoTime();
	private long timer = System.currentTimeMillis();
	private float delta = 0;
	private int frames = 0;
	private int updates = 0;

	private OrthographicCamera camera, uiCamera;

	private SpriteBatch batch, uiBatch;
	private ShapeRenderer sr;

	private State currentState;
	private Skin skin;

	private Level level;

	private MainMenu menu;
	private Stage levelStage;
	private Table levelTable;
	private TextButton levelRestartButton;
	private Texture levelWinOverlay;

	public Music backgroundMusic;
	public Preferences prefs;

	// collect astronauts
	// debris that kills u
	// wall to break by flying at high speed into it
	// black holes, stars, things other than planets
	// check points, race, must make orbit to go through all flags
	// orbit specific planet
	// planets without gravity – hit flag to enable turning on and off of gravity – use that planet to finish level
	
	@Override
	public void create () {
		FileHandle particles = Gdx.files.internal("particle_systems/particles"); // Load all particles
		for (FileHandle particle : particles.list())
			Particle.load(particle.path());

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		levelStage = new Stage(new ScreenViewport());

		loadLevelUI();
		menu = new MainMenu(skin, this);
		levelWinOverlay = new Texture("winOverlay.png");
		levelWinOverlay.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

		GestureDetector gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, Input.gestureInstance);
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(Input.instance);
		inputMultiplexer.addProcessor(gestureDetector);
		inputMultiplexer.addProcessor(levelStage);
		inputMultiplexer.addProcessor(menu.stage);
		Gdx.input.setInputProcessor(inputMultiplexer);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.zoom = 1.25f;
		camera.update();

		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		uiCamera.update();

		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		sr = new ShapeRenderer();

		currentState = State.Menu;

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("spaceMusic.wav"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.25f);

		prefs = Gdx.app.getPreferences("Prefs");
		if (prefs.getBoolean("soundOn", true))
			backgroundMusic.play();
		else
			menu.soundButton.setChecked(true);
	}

	public void startLevel(String file) {
		level = loadLevel(file);
		currentState = State.Playing;
	}

	private Level loadLevel(String file) {
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(Gdx.files.internal(file));

			String type = root.get("type");
			if (type.equals("FinishLine"))
				return new ReachFinishLevel(camera, file);
			else if (type.equals("CollectAstronauts"))
				return new CollectAstronautsLevel();
			else
				return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	float initialScale = 1;
	float winTimer = 0;

	private void update() {

		if (!level.hasWon) {
			if (Input.isTouchDown())
				initialScale = camera.zoom;

			if (Input.getZoom() > 0)
				camera.zoom = initialScale * Input.getZoom();

			if (!Input.getPan().isZero())
				camera.translate(-camera.zoom * Input.getPan().x, camera.zoom * Input.getPan().y);
		} else {
			winTimer++;
			if (winTimer >= Options.TARGET_UPS * 4) { // 4 seconds has passed
				winTimer = 0;
				level = null;
				camera.zoom = 1;
				camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
				camera.update();
				currentState = State.Menu;
				Options.TARGET_UPS *= 4;
			}
		}

	}

	@Override
	public void render () {
		final float ns = 1000000000.0f / Options.TARGET_UPS;

		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
		while (delta >= 1) {
			if (currentState == State.Playing) {
				level.update();
				this.update();
			}

			Input.update();
			updates++;
			delta--;
		}

		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (currentState == State.Menu) {
			batch.setProjectionMatrix(uiCamera.combined);
			menu.render(batch);
		} else if (currentState == State.Playing) {
			batch.setProjectionMatrix(camera.combined);
			sr.setProjectionMatrix(camera.combined);

			level.drawBackground(uiBatch);

			level.render(batch);

			levelStage.act(Gdx.graphics.getDeltaTime());
			levelStage.draw();

			if (level.hasWon) {
				batch.setProjectionMatrix(uiCamera.combined);
				batch.begin();
				batch.draw(levelWinOverlay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch.end();
			}
		}

		if (debug) {
			sr.setColor(Color.GREEN);
			sr.begin(ShapeRenderer.ShapeType.Line);

			if (currentState == State.Playing)
				level.debug(sr);

			sr.end();
		}

		frames++;

		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			System.out.println(Options.TITLE + " | " + updates + " ups, " + frames + " fps");
			System.out.println(Options.TITLE + " | " + Gdx.app.getJavaHeap()/1000000.0 + " mb, " + Gdx.app.getNativeHeap()/1000000.0 + " mb");
			updates = frames = 0;
		}
	}

	@Override
	public void resume() {
		timer = System.nanoTime();
		super.resume();
	}

	@Override
	public void pause() {
		prefs.flush();
		super.pause();
	}

	private void loadLevelUI() {
		levelTable = new Table();
		levelTable.pad(15f);
		levelTable.setWidth(levelStage.getWidth());
		levelTable.align(Align.right | Align.top);
		levelTable.setPosition(0, Gdx.graphics.getHeight());

		levelRestartButton = new TextButton("Restart", skin);
		levelRestartButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (level != null)
					level.restart();
			}
		});
		levelRestartButton.getLabel().setFontScale(4f);
		float restartButtonWidth = Gdx.graphics.getWidth()/7;
		levelTable.add(levelRestartButton).width(restartButtonWidth).height(restartButtonWidth*9/16);

		levelStage.addActor(levelTable);
	}

	enum State {
		Menu,
		Playing
	}
}
