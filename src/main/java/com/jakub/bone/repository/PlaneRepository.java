package com.jakub.bone.repository;

import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;

import static jooq.Tables.PLANES;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class PlaneRepository {
    private final DSLContext CONTEXT;

    public PlaneRepository(DSLContext context) {
        CONTEXT = context;
    }

    public void registerPlaneInDB(String flightNumber) {
        CONTEXT.insertInto(table("planes"),
                        field("flight_number"),
                        field("start_time"))
                .values(flightNumber, LocalDateTime.now())
                .execute();
    }

    public void registerLandingInDB(String flightNumber) {
        CONTEXT.update(table("planes"))
                .set(field("landing_time"), LocalDateTime.now())
                .where(field("flight_number").eq(flightNumber))
                .execute();
    }

    public List<String> getLandedPlanes() {
        return CONTEXT.select(PLANES.FLIGHT_NUMBER)
                .from(PLANES)
                .where(PLANES.LANDING_TIME.isNotNull())
                .fetchInto(String.class);
    }
}
