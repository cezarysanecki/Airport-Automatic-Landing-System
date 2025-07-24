package com.jakub.bone.ui.model;

import com.jakub.bone.domain.airport.Runway;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

import static com.jakub.bone.config.Constant.RUNWAY_HEIGHT;
import static com.jakub.bone.config.Constant.RUNWAY_WIDTH;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;

    public RunwayModel(Runway runway) {
        this.runwayRect = new Rectangle(RUNWAY_WIDTH / 2.0, RUNWAY_HEIGHT/ 2.0);

        Image runwayImage = new Image(getClass().getResource("/images/runway.png").toExternalForm());
        ImagePattern runwayPattern = new ImagePattern(runwayImage);
        this.runwayRect.setFill(runwayPattern);

        this.runwayRect.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.runwayRect.setTranslateX(-1000);
        this.runwayRect.setTranslateY(0);
        this.runwayRect.setTranslateZ((runway.getLandingPoint().y() / 2.0) - 250);
    }
}