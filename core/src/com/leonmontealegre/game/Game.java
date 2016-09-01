package com.leonmontealegre.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.leonmontealegre.game.levels.InfiniteAstronautLevel;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.game.levels.FinishLineLevel;
import com.leonmontealegre.utils.FrameBufferManager;
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

	private SpriteBatch batch, uiBatch, bgBatch;
	private ShapeRenderer sr;

	private State currentState;
	private Skin skin;

	private Level level;

	private MainMenu menu;
	private LevelSelect levelSelect;
	private LevelUI levelUI;
	private InfiniteLevelMenu infiniteLevelMenu;

	public Music backgroundMusic;
	public Preferences prefs;

	private FrameBuffer screenBuffer;
	private TextureRegion bufferRegion;

	private Assets assets;
	
	@Override
	public void create () {
		Logger.log(Gdx.gl.glGetString(GL20.GL_VERSION));
		Logger.log(Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());

		// Load Assets
		assets = new Assets();

		// Retrieve prefs
		prefs = Gdx.app.getPreferences("Prefs");

		// Prevents pressing of 'back' key on android
		Gdx.input.setCatchBackKey(true);

		// Setup font
		skin = new Skin();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textures/UI/font.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 50;
		BitmapFont font = generator.generateFont(parameter);
		font.setColor(0, 0, 0, 1);
		generator.dispose();

		// Setup skin
		skin.add("default-font", font);
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
		for (Texture t : atlas.getTextures())
			t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal("uiskin.json"));

		// Create level camera
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.zoom = 1.25f;
		camera.update();

		// Create UI camera
		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		uiCamera.update();

		// Load sprite batches and shape renderers
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		bgBatch = new SpriteBatch();
		sr = new ShapeRenderer();

		// Create frame buffer
		screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		bufferRegion = new TextureRegion(screenBuffer.getColorBufferTexture(), screenBuffer.getWidth(), screenBuffer.getHeight());
		bufferRegion.flip(false, true);

		// Create other scenes/UI objects
		levelUI = new LevelUI(assets, skin, this);
		menu = new MainMenu(assets, skin, this);
		levelSelect = new LevelSelect(assets, skin, this);
		infiniteLevelMenu = new InfiniteLevelMenu(assets, skin, this);

		// Setup input for all stages
		GestureDetector gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, Input.gestureInstance);
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(levelUI.stage);
		inputMultiplexer.addProcessor(menu.stage);
		inputMultiplexer.addProcessor(levelSelect.stage);
		inputMultiplexer.addProcessor(infiniteLevelMenu.stage);
		inputMultiplexer.addProcessor(Input.instance);
		inputMultiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(inputMultiplexer);

		setCurrentState(State.Menu);

		// Load background music
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/ExtellaTheme.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.25f);

		// Load preferences
		if (prefs.getBoolean("soundOn", true))
			backgroundMusic.play();
		else
			menu.soundButton.setChecked(true);
	}

	public void startInfiniteLevel() {
		setCurrentState(State.Playing);
		Logger.log("Starting new level...");
		if (level != null)
			level = null;
		level = new InfiniteAstronautLevel(assets, levelUI, camera);
		initialScale = camera.zoom;
	}

	public void startLevel(Galaxy galaxy, int x, int y, String file) {
		setCurrentState(State.Playing);
		if (level != null) {
			Logger.log("Starting new level...");
			level = null;
		}
		level = loadLevel(galaxy, x, y, file);
		initialScale = camera.zoom;
		Logger.log("CAMERA ZOOM : " + camera.zoom);
	}

	public Level loadLevel(Galaxy galaxy, int x, int y, String file) {
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(Gdx.files.internal(file));

			String type = root.get("type");
			if (type.equals("FinishLine")) {
				return new FinishLineLevel(assets, galaxy, x, y, levelUI, camera, root);
			} else if (type.equals("CollectAstronauts")) {
				return new CollectAstronautsLevel(assets, galaxy, x, y, levelUI, camera, root);
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
		if (!level.isPaused()) {
			level.update();
			if (!level.hasWon) {
				if (level.hasStarted()) {
					if (Input.isTouchDown())
						initialScale = camera.zoom;

					if (Input.getZoom() > 0)
						camera.zoom = initialScale * Input.getZoom();

					if (!Input.getPan().isZero())
						camera.translate(-camera.zoom * Input.getPan().x, camera.zoom * Input.getPan().y);
				}
			} else {
				winTimer++;
				if (winTimer >= Options.TARGET_UPS * 4) { // 4 seconds have passed
					winTimer = 0;
					Options.TARGET_UPS *= 4;
					level.pause();
					levelUI.showWinScreen();
				}
			}
		}
	}

	@Override
	public void render () {
		final float ns = 1000000000.0f / Options.TARGET_UPS;

		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (currentState == State.Menu) {
			batch.setProjectionMatrix(uiCamera.combined);
			menu.render(batch);
		} else if (currentState == State.LevelSelect) {
			levelSelect.update();

			batch.setProjectionMatrix(uiCamera.combined);
			levelSelect.render(batch);
		} else if (currentState == State.InfiniteLevelMenu) {
			infiniteLevelMenu.update();

			batch.setProjectionMatrix(uiCamera.combined);
			infiniteLevelMenu.render(batch);
		} else if (currentState == State.Playing || currentState == State.Paused) {
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

			renderLevel(level);

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

		if (currentState == State.Playing && System.currentTimeMillis() - timer > 5000) {
			timer += 5000;
			Logger.log(Options.TITLE + " | " + updates/5 + " ups, " + frames/5 + " fps");
			Logger.log(Options.TITLE + " | " + Gdx.app.getJavaHeap()/1000000.0 + " mb, " + Gdx.app.getNativeHeap()/1000000.0 + " mb");
			updates = frames = 0;
		}
	}

	public void renderLevel(Level level) {
		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);

		// Render game to frame buffer
		FrameBufferManager.begin(screenBuffer);

		level.drawBackground(bgBatch);
		level.render(batch);

		FrameBufferManager.end();


		// Setup black hole shader if there is a black hole
		if (!level.blackHoles.isEmpty()) {
			BlackHole.shader.begin();
			BlackHole.shader.setUniformf("cameraPos", camera.position);
			BlackHole.shader.setUniformf("cameraZoom", camera.zoom*100f);
			BlackHole.shader.end();
			uiBatch.setShader(BlackHole.shader);
		}

		// Draw frame buffer to screen
		bufferRegion.getTexture().setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		uiBatch.begin();
		uiBatch.draw(bufferRegion, 0, 0);
		uiBatch.end();

		uiBatch.setShader(null);
	}

	@Override
	public void resume() {
		timer = System.currentTimeMillis();
		lastTime = System.nanoTime();
		updates = frames = 0;
		delta = 0;
		super.resume();
	}

	@Override
	public void pause() {
		prefs.flush(); // Save preferences
		super.pause();
	}

	@Override
	public void dispose() {
		batch.dispose();
		bgBatch.dispose();
		uiBatch.dispose();
	}

	public void setCurrentState(State state) {
		State prevState = currentState;
		this.currentState = state;

		Gdx.graphics.setContinuousRendering(false);
		if (state == State.Menu) {
			menu.setVisible(true);
			levelSelect.setVisible(false);
			levelUI.setVisible(false);
			infiniteLevelMenu.setVisible(false, false);
		} else if (state == State.LevelSelect) {
			levelSelect.setVisible(true);
			menu.setVisible(false);
			levelUI.setVisible(false);
			infiniteLevelMenu.setVisible(false, false);
		} else if (state == State.InfiniteLevelMenu) {
			levelSelect.setVisible(false);
			menu.setVisible(false);
			levelUI.setVisible(false);
			infiniteLevelMenu.setVisible(true, prevState != State.Playing);
		}else if (state == State.Playing) {
			levelUI.setVisible(true);
			levelSelect.setVisible(false);
			menu.setVisible(false);
			infiniteLevelMenu.setVisible(false, false);

			if (level != null && level.isPaused())
				level.resume();

			timer = System.currentTimeMillis();
			lastTime = System.nanoTime();
			updates = frames = 0;
			delta = 0;
			Gdx.graphics.setContinuousRendering(true);
		} else if (state == State.Paused) {
			if (level != null && !level.isPaused())
				level.pause();
		}
	}

	public void finishLevel() {
		camera.zoom = 1;
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		level.dispose();
		this.setCurrentState(State.Menu);
	}

	public Level getLevel() {
		return level;
	}

	enum State {
		Menu,
		LevelSelect,
		InfiniteLevelMenu,
		Playing,
		Paused
	}
}