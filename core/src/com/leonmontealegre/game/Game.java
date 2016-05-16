package com.leonmontealegre.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leonmontealegre.utils.Input;

public class Game extends ApplicationAdapter {

	private long lastTime = System.nanoTime();
	private long timer = System.currentTimeMillis();
	private final float ns = 1000000000.0f / Constants.TARGET_UPS;
	private float delta = 0;
	private int frames = 0;
	private int updates = 0;

	private SpriteBatch batch, uiBatch;

	private Level level;

	private OrthographicCamera camera;

	private ShapeRenderer sr;

	private final boolean debug = true;

	private Stage stage;
	private Table table;
	private TextButton restartButton;
	private Skin skin;

	private Music backgroundMusic;

	// collect astronauts
	// debris that kills u
	// wall to break by flying at high speed into it
	// black holes, stars, things other than planets
	
	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());

		GestureDetector gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, Input.gestureInstance);
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(Input.instance);
		inputMultiplexer.addProcessor(gestureDetector);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();

		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		sr = new ShapeRenderer();
		level = new Level(camera);

		table = new Table();
		table.pad(15f);
		table.setWidth(stage.getWidth());
	 	table.align(Align.right | Align.top);
		table.setPosition(0, Gdx.graphics.getHeight());

		restartButton = new TextButton("Restart", skin);
		restartButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				level = new Level(camera);
			}
		});
		restartButton.getLabel().setFontScale(4f);
		float restartButtonWidth = Gdx.graphics.getWidth()/7;
		table.add(restartButton).width(restartButtonWidth).height(restartButtonWidth*9/16);

		stage.addActor(table);

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("spaceMusic.wav"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.25f);
		backgroundMusic.play();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	float initialScale = 1;

	private void update() {
		if (Input.isTouchDown())
			initialScale = camera.zoom;

		if (Input.getZoom() > 0)
			camera.zoom = initialScale*Input.getZoom();

		if (!Input.getPan().isZero())
			camera.translate(-Input.getPan().x, Input.getPan().y);

	}

	@Override
	public void render () {
		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
		while (delta >= 1) {
			level.update();
			this.update();
			Input.update();
			updates++;
			delta--;
		}

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		level.drawBackground(uiBatch);

		level.render(batch);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if (debug) {
			sr.setColor(Color.GREEN);
			sr.begin(ShapeRenderer.ShapeType.Line);
			level.debug(sr);
			sr.end();
		}

		frames++;

		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
//			System.out.println(Constants.TITLE + " | " + updates + " ups, " + frames + " fps");
			updates = frames = 0;
		}
	}

	@Override
	public void resume() {
		timer = System.nanoTime();
		super.resume();
	}
}
