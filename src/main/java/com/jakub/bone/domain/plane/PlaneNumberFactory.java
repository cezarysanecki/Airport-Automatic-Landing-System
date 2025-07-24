package com.jakub.bone.domain.plane;

import java.util.concurrent.ThreadLocalRandom;

public class PlaneNumberFactory {

    public static PlaneNumber generateFlightNumber() {
        String[] airlineCodes = {"MH", "AA", "BA", "LH", "AF", "EK", "QR", "KL", "UA", "DL"};
        String code = airlineCodes[ThreadLocalRandom.current().nextInt(airlineCodes.length)];
        int number = ThreadLocalRandom.current().nextInt(100, 999);
        return new PlaneNumber(code + number);
    }

}
