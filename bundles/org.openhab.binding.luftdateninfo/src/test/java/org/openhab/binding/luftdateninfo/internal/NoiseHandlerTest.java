/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.luftdateninfo.internal;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;
import org.openhab.binding.luftdateninfo.internal.handler.BaseSensorHandler.UpdateStatus;
import org.openhab.binding.luftdateninfo.internal.mock.NoiseHandlerExtension;
import org.openhab.binding.luftdateninfo.internal.mock.ThingMock;
import org.openhab.binding.luftdateninfo.internal.util.FileReader;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SmartHomeUnits;

/**
 * The {@link NoiseHandlerTest} Test Noise Handler updates
 *
 * @author Bernd Weymann - Initial contribution
 */
@NonNullByDefault
public class NoiseHandlerTest {

    @Test
    public void testValidUpdate() {
        ThingMock t = new ThingMock();

        HashMap<String, Object> properties = new HashMap<String, Object>();
        // String sensorid taken from thing-types.xml
        properties.put("sensorid", 12345);
        t.setConfiguration(properties);

        NoiseHandlerExtension noiseHandler = new NoiseHandlerExtension(t);
        String pmJson = FileReader.readFileInString("src/test/resources/noise-result.json");
        if (pmJson != null) {
            UpdateStatus result = noiseHandler.updateChannels(pmJson);
            assertEquals("Valid update", UpdateStatus.OK, result);
            assertEquals("Noise EQ", QuantityType.valueOf(51.0, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseEQCache());
            assertEquals("Noise Min", QuantityType.valueOf(47.2, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseMinCache());
            assertEquals("Noise Max", QuantityType.valueOf(57.0, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseMaxCache());
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testInvalidUpdate() {
        ThingMock t = new ThingMock();

        HashMap<String, Object> properties = new HashMap<String, Object>();
        // String sensorid taken from thing-types.xml
        properties.put("sensorid", 12345);
        t.setConfiguration(properties);

        NoiseHandlerExtension noiseHandler = new NoiseHandlerExtension(t);
        String pmJson = FileReader.readFileInString("src/test/resources/condition-result-no-pressure.json");
        if (pmJson != null) {
            UpdateStatus result = noiseHandler.updateChannels(pmJson);
            assertEquals("Valid update", UpdateStatus.VALUE_ERROR, result);
            assertEquals("Values undefined", QuantityType.valueOf(-1, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseEQCache());
            assertEquals("Values undefined", QuantityType.valueOf(-1, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseMinCache());
            assertEquals("Values undefined", QuantityType.valueOf(-1, SmartHomeUnits.DECIBEL),
                    noiseHandler.getNoiseMaxCache());
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testEmptyUpdate() {
        ThingMock t = new ThingMock();

        HashMap<String, Object> properties = new HashMap<String, Object>();
        // String sensorid taken from thing-types.xml
        properties.put("sensorid", 12345);
        t.setConfiguration(properties);

        NoiseHandlerExtension noiseHandler = new NoiseHandlerExtension(t);
        UpdateStatus result = noiseHandler.updateChannels("[]");
        assertEquals("Valid update", UpdateStatus.VALUE_EMPTY, result);
    }

    @Test
    public void testNullUpdate() {
        ThingMock t = new ThingMock();

        HashMap<String, Object> properties = new HashMap<String, Object>();
        // String sensorid taken from thing-types.xml
        properties.put("sensorid", 12345);
        t.setConfiguration(properties);

        NoiseHandlerExtension noiseHandler = new NoiseHandlerExtension(t);
        UpdateStatus result = noiseHandler.updateChannels(null);
        assertEquals("Valid update", UpdateStatus.CONNECTION_ERROR, result);
    }
}
