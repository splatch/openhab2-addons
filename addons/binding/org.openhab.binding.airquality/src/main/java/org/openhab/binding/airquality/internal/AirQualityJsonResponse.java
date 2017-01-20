/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.internal;

import java.util.Calendar;

/**
 * The {@link AirQualityJsonResponse} is the Java class used to map the JSON
 * response to the aqicn.org request.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityJsonResponse {

    private String status;

    private AirQualityJsonData data;

    public AirQualityJsonResponse() {
    }

    public String getStatus() {
        return status;
    }

    public AirQualityJsonData getData() {
        return data;
    }

    public int getAqiLevel() {
        return data.getAqi();
    }

    public String getAqiDescription() {
        int aqi = data.getAqi();

        if (aqi > 0 && aqi <= 50) {
            return "GOOD";
        } else if (aqi >= 51 && aqi <= 100) {
            return "MODERATE";
        } else if (aqi >= 101 && aqi <= 150) {
            return "UNHEALTHY_FOR_SENSITIVE";
        } else if (aqi >= 151 && aqi <= 200) {
            return "UNHEALTHY";
        } else if (aqi >= 201 && aqi < 300) {
            return "VERY_UNHEALTHY";
        } else if (aqi >= 300) {
            return "HAZARDOUS";
        }

        return "NO_DATA";
    }

    public int getPm25() {
        return data.getIaqi().getPm25();
    }

    public int getPm10() {
        return data.getIaqi().getPm10();
    }

    public int getO3() {
        return data.getIaqi().getO3();
    }

    public int getNo2() {
        return data.getIaqi().getNo2();
    }

    public int getCo() {
        return data.getIaqi().getCo();
    }

    public String getLocationName() {
        return data.getCity().getName();
    }

    public String getLocationUrl() {
        return data.getCity().getUrl();
    }

    public Calendar getObservationTime() throws Exception {
        return data.getTime().getS();
    }

    public int getStationId() {
        return data.getIdx();
    }

    public int getTemperature() {
        return data.getIaqi().getT();
    }

    public int getPressure() {
        return data.getIaqi().getP();
    }

    public int getHumidity() {
        return data.getIaqi().getH();
    }

}
