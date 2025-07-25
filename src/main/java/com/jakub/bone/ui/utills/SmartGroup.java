package com.jakub.bone.ui.utills;

import javafx.scene.Group;
import javafx.scene.transform.Scale;

public class SmartGroup extends Group {
    public SmartGroup(double v, double v1, double v2) {
        this.getTransforms().add(new Scale(v, v1, v2));
    }
}
