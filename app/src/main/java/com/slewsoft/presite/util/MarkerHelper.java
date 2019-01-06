package com.slewsoft.presite.util;

import com.slewsoft.presite.Marker;

import java.util.UUID;

public class MarkerHelper {

    public enum MarkerType {
        Unit, Crane, FlatBed
    }

    public Marker createCraneInfo(String... details) {
        return new Marker(UUID.randomUUID().toString(), MarkerType.Crane, details[0], details[1]);
    }

    public Marker createUnitInfo(String... details) {
        return new Marker(UUID.randomUUID().toString(), MarkerType.Unit, details[0], details[1]);
    }
}
