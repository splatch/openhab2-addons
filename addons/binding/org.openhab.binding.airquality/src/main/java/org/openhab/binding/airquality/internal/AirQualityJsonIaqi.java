/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.internal;

import com.google.gson.JsonObject;

/**
 * The {@link AirQualityJsonIaqi} is responsible for storing
 * "iaqi" node from the waqi.org JSON response
 * It contains information on air pollution particles
 * as well as some basic weather metrics.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityJsonIaqi {

    private JsonObject pm25;
    private JsonObject pm10;
    private JsonObject o3;
    private JsonObject no2;
    private JsonObject co;
    private JsonObject t;
    private JsonObject p;
    private JsonObject h;

    public AirQualityJsonIaqi() {

    }

    public int getPm25() {
        return pm25.get("v").getAsInt();
    }

    public int getPm10() {
        return pm10.get("v").getAsInt();
    }

    public int getO3() {
        return o3.get("v").getAsInt();
    }

    public int getNo2() {
        return no2.get("v").getAsInt();
    }

    public int getCo() {
        return co.get("v").getAsInt();
    }

    public int getT() {
        return t.get("v").getAsInt();
    }

    public int getP() {
        return p.get("v").getAsInt();
    }

    public int getH() {
        return h.get("v").getAsInt();
    }

}
