package com.jakub.bone.ui.utills;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import lombok.Getter;

@Getter
public class Camera {
    private final PerspectiveCamera camera;
    private final Rotate rotateX;
    private final Rotate rotateY;
    private final Translate translate;

    public Camera() {
        this.camera = new PerspectiveCamera();
        this.translate = new Translate(0, -5000, -2200);
        this.rotateX = new Rotate(-60, Rotate.X_AXIS);
        this.rotateY = new Rotate(0, Rotate.Y_AXIS);
        this.camera.getTransforms().addAll(translate, rotateX, rotateY);
    }

    public void initializeRotationControls(Group group, Scene scene) {
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        group.getTransforms().addAll(rotateX, rotateY, rotateZ);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case X -> zoom(-200);   // Zoom in
                case Z -> zoom(200);    // Zoom out

                case UP -> rotateX.setAngle(rotateX.getAngle() + 25); // Rotation by X
                case DOWN -> rotateX.setAngle(rotateX.getAngle() - 25); // Rotation by X
                case LEFT -> rotateY.setAngle(rotateY.getAngle() - 25); // Rotation by Y
                case RIGHT -> rotateY.setAngle(rotateY.getAngle() + 25); // Rotation by Y

                case C -> getView(-45.0, 0.0, 0.0, -5000.0, -4200.0); // Scene 1
                case V -> getView(-60.0, 0.0, 0.0, -5000.0, -2200); // Scene 2
            }
        });
    }

    private void getView(double rotationX, double rotationY, double translationX, double translationY, double translationZ) {
        rotateX.setAngle(rotationX);
        rotateY.setAngle(rotationY);
        translate.setX(translationX);
        translate.setY(translationY);
        translate.setZ(translationZ);
    }

    private void zoom(double deltaZ) {
        translate.setZ(translate.getZ() + deltaZ);
    }
}