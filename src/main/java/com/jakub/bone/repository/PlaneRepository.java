package com.jakub.bone.repository;

import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;

import static jooq.Tables.PLANES;

public class PlaneRepository {
    private final DSLContext CONTEXT;

    public PlaneRepository(DSLContext context) {
        CONTEXT = context;
    }

    public void insertPlane(String flightNumber) {
        CONTEXT.insertInto(
                        PLANES,
                        PLANES.FLIGHT_NUMBER,
                        PLANES.START_TIME)
                .values(
                        flightNumber,
                        LocalDateTime.now()
                )
                .execute();
    }

    public int countFlyingPlanes() {
        return CONTEXT.fetchCount(
                PLANES
                        .where(PLANES.LANDING_TIME.isNull())
        );
    }

    public void updateLandingTime(String flightNumber, LocalDateTime landingTime) {
        CONTEXT.update(PLANES)
                .set(PLANES.LANDING_TIME, landingTime)
                .where(PLANES.FLIGHT_NUMBER.eq(flightNumber))
                .execute();
    }

    public List<String> getLandedPlanes() {
        return CONTEXT.select(PLANES.FLIGHT_NUMBER)
                .from(PLANES)
                .where(PLANES.LANDING_TIME.isNotNull())
                .fetchInto(String.class);
    }
}
