/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.internal;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

/**
 * The {@link AirQualityJsonData} is responsible for storing
 * the "data" node from the waqi.org JSON response
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityJsonData {

    private int idx;
    private int aqi;
    private AirQualityJsonTime time;
    private AirQualityJsonCity city;
    private JsonArray attributions;
    private AirQualityJsonIaqi iaqi;

    public AirQualityJsonData() {
    }

    /**
     * Air Quality Index
     * 
     * @return
     */
    public int getAqi() {
        return aqi;
    }

    /**
     * Receives a unique Station ID that's sharing the measurements
     * 
     * @return {Integer}
     */
    public int getIdx() {
        return idx;
    }

    /**
     * Receives "time" node from the "data" object in JSON response
     * 
     * @return {AirQualityJsonTime}
     */
    public AirQualityJsonTime getTime() {
        return time;
    }

    /**
     * Receives "city" node from the "data" object in JSON response
     * 
     * @return {AirQualityJsonCity}
     */
    public AirQualityJsonCity getCity() {
        return city;
    }

    /**
     * Collects a list of attributions (vendors making data available)
     * and transforms it into readable string.
     * Currently displayed in Thing Status description when ONLINE
     * 
     * @return {String}
     */
    public String getAttributions() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < attributions.size(); i++) {
            list.add(attributions.get(i).getAsJsonObject().get("name").getAsString());
        }
        return "Attributions: " + String.join(", ", list);
    }

    /**
     * Receives "iaqi" node from the "data" object in JSON response
     * 
     * @return {AirQualityJsonIaqi}
     */
    public AirQualityJsonIaqi getIaqi() {
        return iaqi;
    }

}
