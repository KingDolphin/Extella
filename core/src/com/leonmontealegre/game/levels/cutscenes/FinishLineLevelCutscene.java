package com.leonmontealegre.game.levels.cutscenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.leonmontealegre.game.levels.FinishLineLevel;
import com.leonmontealegre.utils.ZoomToAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.leonmontealegre.utils.ZoomToAction.zoomTo;

public class FinishLineLevelCutscene extends Cutscene {

    private FinishLineLevel level;

    private final Vector2 finalPosition;
    private final float finalZoom;

    public FinishLineLevelCutscene(OrthographicCamera camera, FinishLineLevel level) {
        super(camera);
        this.level = level;
        this.finalPosition = new Vector2(camera.position.x, camera.position.y);
        this.finalZoom = camera.zoom;

        restart();
    }

    @Override
    public void restart() {
        OrthographicCamera camera = cameraActor.getCamera();

        // Clear all actions if restarted in the middle of cutscene
        cameraActor.clearActions();

        // Zoom on player
        camera.position.set(level.player.position.x + level.player.sprite.getOriginX(), level.player.position.y + level.player.sprite.getOriginY(), camera.position.z);
        camera.zoom = 0.4f;
        camera.update();

        cameraActor.setX(camera.position.x);
        cameraActor.setY(camera.position.y);
        cameraActor.setScaleY(camera.zoom);

        // Delays for player and then flag
        DelayAction delayAction1 = delay(1.0f);
        DelayAction delayAction2 = delay(delayAction1.getDuration());

        // Move to flag
        MoveToAction moveToFlagAction = moveTo(level.finishLine.getPosition().x + level.finishLine.sprite.getOriginX(), level.finishLine.getPosition().y + level.finishLine.sprite.getOriginY(), 3);

        // Zoom out and move
        float zoomOutDuration = 2f;
        ZoomToAction zoomOutAction = zoomTo(finalZoom, zoomOutDuration);
        MoveToAction moveOutAction = moveTo(finalPosition.x, finalPosition.y, zoomOutDuration);

        SequenceAction compositeAction = sequence(delayAction1, moveToFlagAction, delayAction2, parallel(zoomOutAction, moveOutAction));
        cameraActor.addAction(compositeAction);
    }

    @Override
    public void skip() {
        OrthographicCamera camera = cameraActor.getCamera();

        camera.position.set(finalPosition.x, finalPosition.y, camera.position.z);
        camera.zoom = finalZoom;
        camera.update();

        cameraActor.setX(camera.position.x);
        cameraActor.setY(camera.position.y);
        cameraActor.setScaleY(camera.zoom);

        super.skip();
    }

}
