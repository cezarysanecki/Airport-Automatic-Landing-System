package com.jakub.bone.repository;

import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;

import static jooq.Tables.COLLISIONS;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class CollisionRepository {
    private final DSLContext CONTEXT;

    public CollisionRepository(DSLContext CONTEXT) {
        this.CONTEXT = CONTEXT;
    }

    public void registerCollisionToDB(String[] planesIds) {
        CONTEXT.insertInto(table("collisions"),
                        field("involved_planes"),
                        field("time"))
                .values(planesIds,
                        LocalDateTime.now())
                .execute();
    }

    public List<String> getCollidedPlanes() {
        return CONTEXT.select(COLLISIONS.INVOLVED_PLANES)
                .from(COLLISIONS)
                .where(COLLISIONS.TIME.isNotNull())
                .fetchInto(String.class);
    }
}
