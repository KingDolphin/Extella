package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

public class Galaxy extends Table {

    public String name;

    private String[][] levels;

    public Galaxy(final Game game, Skin skin, String file) {
        load(file);

        this.setLayoutEnabled(true);
        this.setFillParent(true);
        this.pad(50f);

        final Table group = new Table();
        group.setWidth(this.getWidth());
        group.setHeight(this.getHeight());
        ScrollPane scrollPaneA = new ScrollPane(group, skin);

        float size = (Gdx.graphics.getWidth() - 350) / 7;
        for (int y = 0; y < levels[0].length; y++) {
            for (int x = 0; x < levels.length; x++) {
                final String level = levels[x][y];

                TextButton button = new TextButton(""+(x + y * levels.length + 1), skin);
                if (level != null) {
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Galaxy.this.setVisible(false);
                            game.startLevel(level);
                        }
                    });
                } else {
                    button.setDisabled(true);
                    button.setColor(0.5f, 0.5f, 0.5f, 0.8f);
                }
                button.getLabel().setFontScale(4f);
                group.add(button).width(size).height(size).pad(10f);
            }
            group.row();
        }
        this.add(scrollPaneA);
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
