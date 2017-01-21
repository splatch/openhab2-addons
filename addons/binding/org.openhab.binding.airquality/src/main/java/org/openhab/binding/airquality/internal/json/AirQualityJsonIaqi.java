/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.internal.json;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link AirQualityJsonIaqi} is responsible for storing
 * "iaqi" node from the waqi.org JSON response
 * It contains information on air pollution particles
 * as well as some basic weather metrics.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityJsonIaqi {

    private AirQualityValue<Integer> pm25;
    private AirQualityValue<Integer> pm10;
    private AirQualityValue<Integer> o3;
    private AirQualityValue<Integer> no2;
    private AirQualityValue<Integer> co;
    private AirQualityValue<Integer> t;

    @SerializedName("p")
    private AirQualityValue<Integer> pressure;
    private AirQualityValue<Integer> h;

    public AirQualityJsonIaqi() {

    }

    public int getPm25() {
        return pm25.getValue();
    }

    public int getPm10() {
        return pm10.getValue();
    }

    public int getO3() {
        return o3.getValue();
    }

    public int getNo2() {
        return no2.getValue();
    }

    public int getCo() {
        return co.getValue();
    }

    public int getT() {
        return t.getValue();
    }

    public int getP() {
        return pressure.getValue();
    }

    public int getH() {
        return h.getValue();
    }

}
