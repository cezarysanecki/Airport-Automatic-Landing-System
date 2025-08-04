package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.jakub.bone.config.Constant.HOLDING_ENTRY_ALTITUDE;
import static com.jakub.bone.config.Constant.MAX_CAPACITY;

@Log4j2
public class ControlTowerServiceDump {

    private final PlaneRepository planeRepository;
    private final CollisionRepository collisionRepository;

    public ControlTowerServiceDump(PlaneRepository planeRepository, CollisionRepository collisionRepository) {
        this.planeRepository = planeRepository;
        this.collisionRepository = collisionRepository;
    }

    public void store(List<Plane> planes) {
        // store timestamp with JSON of current planes with their positions (like save in game)
    }

    public void store(Collision collision) {
        // store collision
    }

}