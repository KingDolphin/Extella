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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.levels.BlackHole;
import com.leonmontealegre.game.levels.CollectAstronautsLevel;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.game.levels.ReachFinishLevel;
import com.leonmontealegre.utils.Input;
import com.leonmontealegre.utils.Logger;

import java.io.IOException;

public class Game extends ApplicationAdapter {

	private static final boolean debug = false;

	private long lastTime = System.nanoTime();
	private long timer = System.currentTimeMillis();
	private float delta = 0;
	private int frames = 0;
	private int updates = 0;

	public OrthographicCamera camera, uiCamera;

	private SpriteBatch batch, uiBatch;
	private ShapeRenderer sr;

	private State currentState;
	private Skin skin;

	private Level level;

	private MainMenu menu;
	private LevelSelect levelSelect;
	private LevelUI levelUI;

	public Music backgroundMusic;
	public Preferences prefs;

	private FrameBuffer screenBuffer;
	private TextureRegion bufferRegion;
	
	@Override
	public void create () {
		Logger.log(Gdx.gl.glGetString(GL20.GL_VERSION));
		Logger.log(Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());

		Gdx.input.setCatchBackKey(true);
		FileHandle particles = Gdx.files.internal("particle_systems/particles"); // Load all particles
		for (FileHandle particle : particles.list())
			Particle.load(particle.path());

		skin = new Skin();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textures/UI/font.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 50;
		BitmapFont font = generator.generateFont(parameter);
		font.setColor(0, 0, 0, 1);
		generator.dispose();

		skin.add("default-font", font);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		skin.load(Gdx.files.internal("uiskin.json"));

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.zoom = 1.25f;
		camera.update();

		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		uiCamera.update();

		Explosion.load();
		levelUI = new LevelUI(skin, this);
		menu = new MainMenu(this);
		levelSelect = new LevelSelect(skin, this);

		GestureDetector gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, Input.gestureInstance);
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(levelUI.stage);
		inputMultiplexer.addProcessor(menu.stage);
		inputMultiplexer.addProcessor(levelSelect.stage);
		inputMultiplexer.addProcessor(Input.instance);
		inputMultiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(inputMultiplexer);

		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		sr = new ShapeRenderer();

		currentState = State.Menu;

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/spaceMusic.wav"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.25f);

		prefs = Gdx.app.getPreferences("Prefs");
		if (prefs.getBoolean("soundOn", true))
			backgroundMusic.play();
		else
			menu.soundButton.setChecked(true);

		screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		bufferRegion = new TextureRegion(screenBuffer.getColorBufferTexture(), screenBuffer.getWidth(), screenBuffer.getHeight());
		bufferRegion.flip(false, true);
	}

	public void startLevel(Galaxy galaxy, int x, int y, String file) {
		setCurrentState(State.Playing);
		level = loadLevel(galaxy, x, y, file);
	}

	public Level loadLevel(Galaxy galaxy, int x, int y, String file) {
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(Gdx.files.internal(file));

			String type = root.get("type");
			if (type.equals("FinishLine")) {
				return new ReachFinishLevel(galaxy, x, y, levelUI, camera, root);
			} else if (type.equals("CollectAstronauts")) {
				return new CollectAstronautsLevel(galaxy, x, y, levelUI, camera, root);
			} else {
				return null;
			}
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
		if (currentState == State.Playing && !level.isPaused()) {
			level.update();
			if (!level.hasWon) {
				if (Input.isTouchDown())
					initialScale = camera.zoom;


				if (Input.getZoom() > 0)
					camera.zoom = initialScale * Input.getZoom();

				if (!Input.getPan().isZero())
					camera.translate(-camera.zoom * Input.getPan().x, camera.zoom * Input.getPan().y);
			} else {
				winTimer++;
				if (winTimer >= Options.TARGET_UPS * 4) { // 4 seconds have passed
					winTimer = 0;
					Options.TARGET_UPS *= 4;
					level.pause();
					levelUI.showWinScreen();
				}
			}
		} else if (currentState == State.LevelSelect) {
			levelSelect.update();
		}
	}

	@Override
	public void render () {
		final float ns = 1000000000.0f / Options.TARGET_UPS;

		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
		while (delta >= 1) {
			this.update();
			Input.update();
			levelUI.update();
			updates++;
			delta--;
		}

		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (currentState == State.Menu) {
			batch.setProjectionMatrix(uiCamera.combined);
			menu.render(batch);
		} else if (currentState == State.LevelSelect) {
			batch.setProjectionMatrix(uiCamera.combined);
			levelSelect.render(batch);
		} else if (currentState == State.Playing) {
			batch.setProjectionMatrix(camera.combined);
			sr.setProjectionMatrix(camera.combined);

			// Render game to frame buffer
			screenBuffer.begin();

			level.drawBackground(uiBatch);
			level.render(batch);

			screenBuffer.end();


			// Setup black hole shader if there is a black hole
			if (!level.blackHoles.isEmpty()) {
				BlackHole.shader.begin();
				BlackHole.shader.setUniformf("cameraPos", camera.position);
				BlackHole.shader.setUniformf("cameraZoom", camera.zoom);
				BlackHole.shader.end();
				uiBatch.setShader(BlackHole.shader);
			}


			// Draw frame buffer to screen
			uiBatch.begin();
			uiBatch.draw(bufferRegion, 0, 0);
			uiBatch.end();

			uiBatch.setShader(null);

			// Render UI
			levelUI.render();
		}

		if (debug) {
			sr.setColor(Color.GREEN);
			sr.begin(ShapeRenderer.ShapeType.Line);

			if (currentState == State.Playing)
				level.debug(sr);

			sr.end();
		}

		frames++;

		if (System.currentTimeMillis() - timer > 5000) {
			timer += 5000;
			Logger.log(Options.TITLE + " | " + updates/5 + " ups, " + frames/5 + " fps");
			Logger.log(Options.TITLE + " | " + Gdx.app.getJavaHeap()/1000000.0 + " mb, " + Gdx.app.getNativeHeap()/1000000.0 + " mb");
			updates = frames = 0;
		}
	}

	@Override
	public void resume() {
		timer = System.currentTimeMillis();
		super.resume();
	}

	@Override
	public void pause() {
		prefs.flush(); // Save preferences
		super.pause();
	}

	public void setCurrentState(State state) {
		this.currentState = state;

		if (state == State.Menu) {
			menu.setVisible(true);
			levelSelect.setVisible(false);
			levelUI.setVisible(false);
		} else if (state == State.LevelSelect) {
			levelSelect.setVisible(true);
			menu.setVisible(false);
			levelUI.setVisible(false);
		} else if (state == State.Playing) {
			levelUI.setVisible(true);
			levelSelect.setVisible(false);
			menu.setVisible(false);
		}
	}

	public void finishLevel() {
		camera.zoom = 1;
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		this.setCurrentState(State.Menu);
	}

	public Level getLevel() {
		return level;
	}

	enum State {
		Menu,
		LevelSelect,
		Playing
	}
}