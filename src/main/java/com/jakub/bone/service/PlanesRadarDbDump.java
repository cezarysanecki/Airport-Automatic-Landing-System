package com.jakub.bone.service;

import com.jakub.bone.plane.server.ServerPlane;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PlanesRadarDbDump {

    private final PlaneRepository planeRepository;
    private final CollisionRepository collisionRepository;

    public PlanesRadarDbDump(PlaneRepository planeRepository, CollisionRepository collisionRepository) {
        this.planeRepository = planeRepository;
        this.collisionRepository = collisionRepository;
    }

    public void store(List<ServerPlane> planes) {
        // store timestamp with JSON of current planes with their positions (like save in game)
    }

    public void store(Collision collision) {
        // store collision
    }

}