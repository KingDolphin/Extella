package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.utils.FrameBufferManager;
import com.leonmontealegre.utils.Logger;

import java.io.IOException;

public class Galaxy extends Table {

    public String name;

    private String[][] levels;
    private Texture[][] thumbnails;

    public Galaxy(final Game game, Skin skin, String file) {
        load(file);

        this.setLayoutEnabled(true);
        this.setFillParent(true);
        this.pad(50f);

        final Table group = new Table();
        group.setWidth(this.getWidth());
        group.setHeight(this.getHeight());
        ScrollPane scrollPaneA = new ScrollPane(group, skin);

        int size = (Gdx.graphics.getWidth() - 350) / 7;
        int bufSize = size;
        int bufWidth = bufSize * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        int bufHeight = bufSize;
        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGB888, bufWidth, bufHeight, false);
        for (int y = 0; y < levels[0].length; y++) {
            for (int x = 0; x < levels.length; x++) {
                final String level = levels[x][y];

                TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
                tbs.font = skin.getFont("default-font");
                TextButton button = new TextButton(""+(x + y * levels.length + 1), tbs);
                if (level != null) {
                    final int xx = x, yy = y;
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Galaxy.this.setVisible(false);
                            game.startLevel(Galaxy.this, xx, yy, level);
                        }
                    });

                    // To stop printing during the temporary level creation
                    boolean log = Logger.debug;
                    Logger.debug = false;
                    Level lev = game.loadLevel(this, x, y, level);
                    FrameBufferManager.begin(buffer);

                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                    game.renderLevel(lev);

                    Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, buffer.getWidth(), buffer.getHeight());

                    FrameBufferManager.end();
                    Logger.debug = log;

                    TextureRegion texReg = new TextureRegion(new Texture(pixmap));
                    texReg.setRegion(pixmap.getWidth()/2-bufSize/2, pixmap.getHeight()/2-bufSize/2, bufSize, bufSize);
                    texReg.flip(false, true);
                    tbs.up = new TextureRegionDrawable(texReg);

                    lev.dispose();
                    pixmap.dispose();
                } else {
                    button.setDisabled(true);
                    button.setColor(0.5f, 0.5f, 0.5f, 0.8f);
                }
                group.add(button).width(size).height(size).pad(10f);
            }
            group.row();
        }
        this.add(scrollPaneA);

        buffer.dispose();
    }

    public String getLevel(int x, int y) {
        if (x >= 0 && x < levels.length && y >= 0 && y < levels[0].length)
            return levels[x][y];

        return null;
    }

    public int getHorizontalLevels() {
        return levels.length;
    }

    public int getVerticalLevels() {
        return levels[0].length;
    }

    protected void load(String file) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(file));

            this.levels = new String[root.getInt("w")][root.getInt("h")];

            Array<XmlReader.Element> levs = root.getChildrenByName("level");
            for (XmlReader.Element level : levs)
                levels[level.getInt("x")][level.getInt("y")] = level.get("file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
