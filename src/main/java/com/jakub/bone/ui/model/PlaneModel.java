package com.jakub.bone.ui.model;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import lombok.Getter;


@Getter
public class PlaneModel {
    private final Plane plane;
    private final Group planeGroup;
    private MeshView[] meshViews;
    private Text label;

    public PlaneModel(Plane plane) {
        this.plane = plane;
        this.planeGroup = new Group();
        loadPlaneModel();
        createLabel();
        updatePosition(plane.getNavigator().getCoordinates());
    }

    public void loadPlaneModel() {
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(getClass().getResource("/models/boeing737/boeingModel.obj"));

        meshViews = importer.getImport();
        planeGroup.getChildren().addAll(meshViews);
    }

    public void createLabel() {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.WHITE);
        this.label.setText(plane.getFlightNumber());
    }

    private void updatePosition(Coordinates coordinates) {
        this.planeGroup.setTranslateX(coordinates.getX() / 2.0);
        this.planeGroup.setTranslateY(-coordinates.getAltitude() / 2.0);
        this.planeGroup.setTranslateZ(coordinates.getY() / 2.0);

        this.label.setTranslateX(((coordinates.getX() + 150)) / 2.0);
        this.label.setTranslateY(-((coordinates.getAltitude() + 150)) / 2.0);
        this.label.setTranslateZ((coordinates.getY()) / 2.0);
    }

    public void animateMovement(Coordinates nextCoordinates) {
        double currentX = planeGroup.getTranslateX();
        double currentZ = planeGroup.getTranslateZ();

        double toPlaneX = nextCoordinates.getX() / 2.0;
        double toPlaneY = -nextCoordinates.getAltitude() / 2.0;
        double toPlaneZ = nextCoordinates.getY() / 2.0;

        calculateAndSetHeading(currentX, currentZ, toPlaneX, toPlaneZ);

        setInterpolation(planeGroup, toPlaneX, toPlaneY, toPlaneZ);
        setInterpolation(label, (nextCoordinates.getX() + 150) / 2.0, toPlaneY, (nextCoordinates.getY() + 150) / 2.0);
    }

    private void calculateAndSetHeading(double currentX, double currentZ, double targetX, double targetZ) {
        double deltaX = targetX - currentX;
        double deltaZ = targetZ - currentZ;

        double angleRadians = Math.atan2(deltaZ, deltaX);
        double angleDegrees = Math.toDegrees(angleRadians);

        double correctedAngle = angleDegrees + 90;

        Rotate headingRotate = new Rotate();
        headingRotate.setAxis(Rotate.Y_AXIS);
        headingRotate.setAngle(-correctedAngle);

        planeGroup.getTransforms().clear();
        planeGroup.getTransforms().add(headingRotate);
    }

    public void setInterpolation(Node node, double toX, double toY, double toZ) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(node);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setToZ(toZ);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.play();
    }

    public void setPlaneModelColor(Color color) {
        for (MeshView meshView : meshViews) {
            meshView.setMaterial(new PhongMaterial(color));
        }
        label.setFill(color);
    }
}
