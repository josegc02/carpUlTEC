package com.dbp.democarpultec.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoServiceTest {

    @Test
    void shouldReturnZeroDistanceWhenUsingUtecCoordinates() {
        GeoService geoService = new GeoService(-12.068335, -77.080902);

        Double distance = geoService.distanceToUtecKm(-12.068335, -77.080902);

        assertNotNull(distance);
        assertEquals(0.0, distance);
    }

    @Test
    void shouldReturnNullWhenCoordinatesAreMissing() {
        GeoService geoService = new GeoService(-12.068335, -77.080902);

        Double distance = geoService.distanceToUtecKm(null, -77.08);

        assertNull(distance);
    }

    @Test
    void shouldReturnPositiveDistanceWhenCoordinatesAreDifferent() {
        GeoService geoService = new GeoService(-12.068335, -77.080902);

        Double distance = geoService.distanceToUtecKm(-12.046374, -77.042793);

        assertNotNull(distance);
        assertTrue(distance > 0.0);
    }
}
