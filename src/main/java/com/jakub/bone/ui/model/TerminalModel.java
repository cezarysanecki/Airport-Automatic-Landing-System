package com.jakub.bone.ui.model;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import lombok.Getter;

@Getter
public class TerminalModel {
    private final Group planeGroup;
    private final PhongMaterial material;
    private MeshView[] meshViews;

    public TerminalModel(int transX, int transY, int transZ) {
        this.planeGroup = new Group();
        this.material = new PhongMaterial(Color.WHITE);
        loadTerminalModel(transX, transY, transZ);
    }

    public void loadTerminalModel(int transX, int transY, int transZ) {
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(getClass().getResource("/models/terminal/terminal_main.obj"));

        this.meshViews = importer.getImport();
        for (MeshView meshView : meshViews) {
            meshView.setMaterial(material);
        }
        planeGroup.getChildren().addAll(meshViews);
        setLocation(transX, transY, transZ);
    }

    private void setLocation(int transX, int transY, int transZ) {
        this.planeGroup.setTranslateX(transX);
        this.planeGroup.setTranslateY(-transY);
        this.planeGroup.setTranslateZ(transZ);
    }
}
