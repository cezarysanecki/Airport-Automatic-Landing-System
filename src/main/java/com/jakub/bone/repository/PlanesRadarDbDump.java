package com.jakub.bone.repository;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlanesRadarDbDump {

    private final PlaneRepository planeRepository;
    private final CollisionRepository collisionRepository;

    public PlanesRadarDbDump(PlaneRepository planeRepository, CollisionRepository collisionRepository) {
        this.planeRepository = planeRepository;
        this.collisionRepository = collisionRepository;
    }

//    public void store(List<ServerPlane> planes) {
//         store timestamp with JSON of current planes with their positions (like save in game)
//    }

//    public void store(Collision collision) {
//         store collision
//    }

}