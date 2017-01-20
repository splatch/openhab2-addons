/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The {@link AirQualityJsonTime} is responsible for storing
 * the "time" node from the waqi.org JSON response
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityJsonTime {

    private String s;

    public Calendar getS() throws Exception {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(s);
        calendar.setTime(date);
        return calendar;
    }

}
