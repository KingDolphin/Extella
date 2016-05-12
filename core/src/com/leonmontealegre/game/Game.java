package com.leonmontealegre.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.leonmontealegre.utils.Input;

public class Game extends ApplicationAdapter {

	private long lastTime = System.nanoTime();
	private long timer = System.currentTimeMillis();
	private final float ns = 1000000000.0f / Constants.TARGET_UPS;
	private float delta = 0;
	private int frames = 0;
	private int updates = 0;

	private SpriteBatch batch;

	private World world;
	
	@Override
	public void create () {
		Gdx.input.setInputProcessor(Input.instance);

		batch = new SpriteBatch();
		world = new World();
	}

	@Override
	public void render () {
		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
		while (delta >= 1) {
			world.update();
			Input.update();
			updates++;
			delta--;
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.render(batch);
		frames++;

		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			System.out.println(Constants.TITLE + " | " + updates + " ups, " + frames + " fps");
			updates = frames = 0;
		}
	}

}
