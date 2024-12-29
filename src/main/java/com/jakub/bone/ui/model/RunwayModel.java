package com.jakub.bone.ui.model;

import com.jakub.bone.domain.airport.Runway;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;
    public RunwayModel(Runway runway) {
        this.runwayRect = new Rectangle(runway.getWidth() / 2.0, runway.getHeight()/ 2.0);

        Image runwayImage = new Image(getClass().getResource("/images/runway.png").toExternalForm());
        ImagePattern runwayPattern = new ImagePattern(runwayImage);
        this.runwayRect.setFill(runwayPattern);

        this.runwayRect.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.runwayRect.setTranslateX(-1000);
        this.runwayRect.setTranslateY(0);
        this.runwayRect.setTranslateZ((runway.getLandingPoint().getY() / 2.0) - 250);
    }
}