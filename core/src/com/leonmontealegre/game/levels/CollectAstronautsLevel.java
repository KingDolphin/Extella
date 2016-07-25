package com.leonmontealegre.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.leonmontealegre.game.Galaxy;
import com.leonmontealegre.game.LevelUI;
import com.leonmontealegre.game.Options;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CollectAstronautsLevel extends Level {

    private List<Astronaut> astronauts;
    private Queue<Astronaut> rescuedAstronauts;

    private int astronautsRemaining;

    public CollectAstronautsLevel(Galaxy galaxy, int x, int y, LevelUI ui, OrthographicCamera camera, XmlReader.Element root) {
        super(galaxy, x, y, ui, camera, root);

        rescuedAstronauts = new LinkedList<Astronaut>();
        astronautsRemaining = astronauts.size();
        ui.astronautsLabel.setVisible(true);
        ui.astronautsLabel.setText(Options.astronautsRemainingText + astronautsRemaining);
    }

    @Override
    public void restart() {
        while (!rescuedAstronauts.isEmpty())
            astronauts.add(rescuedAstronauts.remove());
        astronautsRemaining = astronauts.size();
        ui.astronautsLabel.setText(Options.astronautsRemainingText + astronautsRemaining);

        super.restart();
    }

    public void update() {
        for (int i = 0; i < astronauts.size(); i++)
            astronauts.get(i).update();

        super.update();
    }

    public void collectAstronaut(Astronaut astronaut) {
        rescuedAstronauts.add(astronaut);
        astronauts.remove(astronaut);
        astronautsRemaining--;

        ui.astronautsLabel.setText(Options.astronautsRemainingText + astronautsRemaining);

        if (astronautsRemaining <= 0)
            this.win();
    }

    public void render(SpriteBatch batch) {
        batch.begin();

        for (Astronaut astronaut : astronauts)
            astronaut.render(batch);

        super.render(batch);
    }

    @Override
    protected void load(XmlReader.Element root) {
        astronauts = new ArrayList<Astronaut>();
        Array<XmlReader.Element> nauts = root.getChildrenByName("astronaut");
        for (XmlReader.Element naut : nauts) {
            Vector2 pos = new Vector2(naut.getInt("x"), naut.getInt("y"));
            int radius = naut.getInt("radius");
            astronauts.add(new Astronaut(this, pos, radius));
        }

        super.load(root);
    }

}
