package com.jakub.bone.ui;

import javafx.scene.Group;
import javafx.scene.transform.Scale;

class SmartGroup extends Group {
    SmartGroup(double v, double v1, double v2) {
        this.getTransforms().add(new Scale(v, v1, v2));
    }
}
