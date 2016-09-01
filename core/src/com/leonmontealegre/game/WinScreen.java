package com.leonmontealegre.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.leonmontealegre.game.levels.Level;
import com.leonmontealegre.utils.Utils;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class WinScreen extends Table {

    private final Game game;

    private Label levelCompleteLabel;
    private Image star1, star2, star3;

    private Vector2 star1Pos, star2Pos, star3Pos;

    public WinScreen(final Assets assets, Stage stage, final Skin skin, final Game game) {
        super(skin);

        this.game = game;

        this.setWidth(stage.getWidth() / 2);
        this.setHeight(stage.getHeight());
        this.setPosition(stage.getWidth() / 2 - this.getWidth() / 2, 0);
        
        // Create background texture, just a single color
        Pixmap backgroundPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        backgroundPixmap.drawPixel(0, 0, 0xDCDCDC9C);

        // Set background
        this.setWidth(stage.getWidth() / 2);
        this.setHeight(stage.getHeight());
        this.setPosition(stage.getWidth() / 2 - this.getWidth() / 2, 0);
        this.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(backgroundPixmap))));
        backgroundPixmap.dispose();

        // Relative to original design
        float relativeHeight = this.getHeight() / 1104f;

        // Create "Level Complete!" text at the top
        levelCompleteLabel = new Label("Level Complete!", skin);
        levelCompleteLabel.setFontScale(this.getWidth() / levelCompleteLabel.getWidth() / 2);
        this.add(levelCompleteLabel).padTop(40f).padBottom(20f).align(Align.top).colspan(3);
        this.row().height(this.getHeight() * 600 / 1100);

        // Create stars
        Texture starTex = assets.getTexture("starIcon");
        star1 = new Image(starTex);
        this.add(star1).size(200 * relativeHeight);
        star2 = new Image(starTex);
        this.add(star2).size(200 * relativeHeight).padBottom(100 * relativeHeight);
        star3 = new Image(starTex);
        this.add(star3).size(200 * relativeHeight);
        this.row();

        // Large space between stars and buttons
        this.add("").expand().colspan(3);
        this.row();

        // Create buttons
        Button levelSel = Utils.createButton(assets.getTexture("levelSelectionIcon"));
        levelSel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.finishLevel();
            }
        });
        this.add(levelSel).size(150 * relativeHeight).padBottom(100 * relativeHeight);


        Button replay = Utils.createButton(assets.getTexture("replayIcon"));
        replay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.getLevel() != null) {
                    game.getLevel().restart();
                    WinScreen.this.setVisible(false);
                }
            }
        });
        this.add(replay).size(150 * relativeHeight).padBottom(100 * relativeHeight);

        Button next = Utils.createButton(assets.getTexture("fastForwardIcon"));
        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Level lev = game.getLevel();
                if (lev != null) {
                    int xx = (lev.x + 1) % lev.galaxy.getHorizontalLevels();
                    int yy = lev.y + (lev.x + 1) / lev.galaxy.getHorizontalLevels();
                    String nextLevel = lev.galaxy.getLevel(xx, yy);
                    if (nextLevel != null)
                        game.startLevel(lev.galaxy, xx, yy, nextLevel);
                }
            }
        });
        this.add(next).size(150 * relativeHeight).padBottom(100 * relativeHeight);

        stage.addActor(this);

        stage.draw();

        star1Pos = new Vector2(star1.getX(), star1.getY());
        star2Pos = new Vector2(star2.getX(), star2.getY());
        star3Pos = new Vector2(star3.getX(), star3.getY());

        this.setVisible(false);
    }

    public void show() {
        this.setVisible(true);

        Stage stage = this.getStage();

        // Move it
        TemporalAction moveAction = moveTo(this.getX(), this.getY(), 0.3f, Interpolation.linear);
        this.setPosition(this.getX(), stage.getHeight());

        float starDur = 0.3f;
        Action star1Scale = scaleTo(star1.getScaleX(), star1.getScaleY(), starDur, Interpolation.pow2);
        Action star1Move = moveTo(star1Pos.x, star1Pos.y, starDur, Interpolation.pow2);
        star1.setPosition(-stage.getWidth(), stage.getHeight());
        star1.scaleBy(10);

        Action star1Action = sequence(delay(moveAction.getDuration()), parallel(star1Move, star1Scale));

        Action star2Scale = scaleTo(star2.getScaleX(), star2.getScaleY(), starDur, Interpolation.pow2);
        Action star2Move = moveTo(star2Pos.x, star2Pos.y, starDur, Interpolation.pow2);
        star2.setPosition(0, stage.getHeight() + 150);
        star2.scaleBy(10);

        Action star2Action = sequence(delay(moveAction.getDuration() + starDur), parallel(star2Move, star2Scale));

        Action star3Scale = scaleTo(star3.getScaleX(), star3.getScaleY(), starDur, Interpolation.pow2);
        Action star3Move = moveTo(star3Pos.x, star3Pos.y, starDur, Interpolation.pow2);
        star3.setPosition(stage.getWidth(), stage.getHeight());
        star3.scaleBy(10);

        Action star3Action = sequence(delay(moveAction.getDuration() + starDur*2), parallel(star3Move, star3Scale));

        this.addAction(moveAction);
        star1.addAction(star1Action);
        star2.addAction(star2Action);
        star3.addAction(star3Action);

//        final float endY = this.getY();
//
//        try {
//            this.setY(this.getHeight());
//            float y = this.getHeight();
//            while (y > endY) {
//                this.setY(y);
//                y -= 30;
//                Thread.sleep(10);
//            }
//            this.setY(endY);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
