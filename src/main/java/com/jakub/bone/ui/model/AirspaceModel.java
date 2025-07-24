package com.jakub.bone.ui.model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class AirspaceModel {
    private Rectangle floor;
    private Rectangle leftWall;
    private Rectangle rightWall;

    public AirspaceModel() {
        setupFloor();
        setupLeftWall();
        setupRightWall();
    }

    private void setupFloor() {
        Image image = new Image(getClass().getResource("/images/wall.png").toExternalForm());
        this.floor = createWall(6000, 6000, image);
        this.floor.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.floor.setTranslateX(-3000);
        this.floor.setTranslateY(0);
        this.floor.setTranslateZ(-3000);
    }

    private void setupLeftWall() {
        Image image = new Image(getClass().getResource("/images/newYork.png").toExternalForm());
        this.leftWall = createWall(6000, 2500, image);
        this.leftWall.getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        this.leftWall.setTranslateX(-3000);
        this.leftWall.setTranslateY(0);
        this.leftWall.setTranslateZ(3000);
    }

    private void setupRightWall() {
        Image image = new Image(getClass().getResource("/images/departures.png").toExternalForm());
        this.rightWall = createWall(2500, 6000, image);
        this.rightWall.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
        this.rightWall.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        this.rightWall.setTranslateX(3000);
        this.rightWall.setTranslateY(0);
        this.rightWall.setTranslateZ(3000);
    }

    private Rectangle createWall(int width, int height, Image image) {
        Rectangle wall = new Rectangle(width, height);
        ImagePattern wallPattern = new ImagePattern(image);
        wall.setFill(wallPattern);
        return wall;
    }
}
