/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.airquality.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.airquality.AirQualityBindingConstants;
import org.openhab.binding.airquality.internal.AirQualityConfiguration;
import org.openhab.binding.airquality.internal.json.AirQualityJsonData;
import org.openhab.binding.airquality.internal.json.AirQualityJsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link AirQualityHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class AirQualityHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(AirQualityHandler.class);

    private static final String URL = "http://api.waqi.info/feed/geo:%LOCATION%/?token=%APIKEY%";

    private static final int DEFAULT_REFRESH_PERIOD = 30;

    private ScheduledFuture<?> refreshJob;

    private Gson gson;

    public AirQualityHandler(Thing thing) {
        super(thing);
        gson = new Gson();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Air Quality handler.");
        super.initialize();

        AirQualityConfiguration config = getConfigAs(AirQualityConfiguration.class);
        logger.debug("config apikey = {}", config.apikey);
        logger.debug("config location = {}", config.location);
        logger.debug("config refresh = {}", config.refresh);

        boolean validConfig = true;

        if (StringUtils.trimToNull(config.apikey) == null) {
            logger.error("Parameter 'apikey' is mandatory and must be configured, disabling thing '{}'",
                    getThing().getUID());
            validConfig = false;
        }
        if (StringUtils.trimToNull(config.location) == null) {
            logger.error("Parameter 'location' is mandatory and must be configured, disabling thing '{}'",
                    getThing().getUID());
            validConfig = false;
        }
        if (config.refresh != null && config.refresh < 5) {
            logger.error("Parameter 'refresh' must be at least 5 minutes, disabling thing '{}'", getThing().getUID());
            validConfig = false;
        }

        if (validConfig) {
            startAutomaticRefresh();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
        }
    }

    /**
     * Start the job refreshing the Air Quality data
     */
    private void startAutomaticRefresh() {
        if (refreshJob == null || refreshJob.isCancelled()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // Request new air quality data to the aqicn.org service
                        AirQualityJsonData aqiData = updateAirQualityData();

                        // Update all channels from the updated AQI data
                        for (Channel channel : getThing().getChannels()) {
                            updateChannel(channel.getUID().getId(), aqiData);
                        }
                    } catch (Exception e) {
                        logger.error("Exception occurred during execution: {}", e.getMessage());
                    }
                }
            };

            AirQualityConfiguration config = getConfigAs(AirQualityConfiguration.class);
            int period = (config.refresh != null) ? config.refresh.intValue() : DEFAULT_REFRESH_PERIOD;
            refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Disposing the Air Quality handler.");

        if (refreshJob != null && !refreshJob.isCancelled()) {
            refreshJob.cancel(true);
            refreshJob = null;
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateChannel(channelUID.getId());
        } else {
            logger.warn("The Air Quality binding is a read-only and can not handle command {}", command);
        }
    }

    /**
     * Update the channel from the last Air Quality data retrieved
     *
     * @param channelId the id identifying the channel to be updated
     */
    private void updateChannel(String channelId, AirQualityJsonData aqiData) {
        if (isLinked(channelId)) {
            Object value;
            try {
                value = getValue(channelId, aqiData);
            } catch (Exception e) {
                logger.warn("Update channel {}: Can't get value: {}", channelId, e.getMessage());
                return;
            }

            State state = null;
            if (value == null) {
                state = UnDefType.UNDEF;
            } else if (value instanceof Calendar) {
                state = new DateTimeType((Calendar) value);
            } else if (value instanceof Integer) {
                state = new DecimalType(BigDecimal.valueOf(((Integer) value).longValue()));
            } else if (value instanceof String) {
                state = new StringType(value.toString());
            } else {
                logger.warn("Update channel {}: Unsupported value type {}", channelId,
                        value.getClass().getSimpleName());
            }
            logger.debug("Update channel {} with state {} ({})", channelId, (state == null) ? "null" : state.toString(),
                    (value == null) ? "null" : value.getClass().getSimpleName());

            // Update the channel
            if (state != null) {
                updateState(channelId, state);
            }
        }
    }

    /**
     * Get new data from aqicn.org service
     *
     * @return {boolean}
     */
    private AirQualityJsonData updateAirQualityData() {
        AirQualityConfiguration config = getConfigAs(AirQualityConfiguration.class);
        return getAirQualityData(StringUtils.trimToEmpty(config.location));
    }

    /**
     * Request new air quality data to the aqicn.org service
     *
     * @param location geo-coordinates from config
     *
     * @return the air quality data object mapping the JSON response or null in case of error
     */
    private AirQualityJsonResponse getAirQualityData(String location) {
        AirQualityJsonResponse result = null;
        boolean resultOk = false;
        String errorMsg = null;

        try {

            // Build a valid URL for the aqicn.org service
            AirQualityConfiguration config = getConfigAs(AirQualityConfiguration.class);

            String geoStr = location.replace(" ", "").replace(",", ";").trim();

            String urlStr = URL.replace("%APIKEY%", StringUtils.trimToEmpty(config.apikey));
            urlStr = urlStr.replace("%LOCATION%", geoStr);
            logger.debug("URL = {}", urlStr);

            // Run the HTTP request and get the JSON response from aqicn.org
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            String repsonse = IOUtils.toString(connection.getInputStream());
            logger.debug("aqiData = {}", repsonse);

            // Map the JSON response to an object
            result = gson.fromJson(repsonse, AirQualityJsonResponse.class);
            if (result == null) {
                errorMsg = "no data returned";
            } else if (result.getStatus() == "error") {
                errorMsg = result.getData().toString();
            } else {
                resultOk = true;

                if (result.getData() == null) {
                    resultOk = false;
                    errorMsg = "missing data sub-object";
                }
            }

            if (!resultOk) {
                logger.warn("Error in aqicn.org (Air Quality) response: {}", errorMsg);
            }

        } catch (MalformedURLException e) {
            errorMsg = e.getMessage();
            logger.warn("Constructed url is not valid: {}", errorMsg);
        } catch (IOException e) {
            errorMsg = e.getMessage();
            logger.warn("Error running aqicn.org (Air Quality) request: {}", errorMsg);
        } catch (JsonSyntaxException e) {
            errorMsg = e.getMessage();
            logger.warn("Error parsing aqicn.org (Air Quality) response: {}", errorMsg);
        }

        // Update the thing status
        if (resultOk && result != null) {
            String attributions = result.getData().getAttributions();
            logger.info("Received AirQuality data from aqicn.org. {}", attributions);
            updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, attributions);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, errorMsg);
        }

        return resultOk ? result : null;
    }

    public static Object getValue(String channelId, AirQualityJsonData data) throws Exception {
        String[] fields = StringUtils.split(channelId, "#");

        if (data == null) {
            return null;
        }

        String fieldName = fields[0];

        switch (fieldName) {
            case AirQualityBindingConstants.PM25:
                return data.getIaqi().getPm25();
            case AirQualityBindingConstants.PM10:
                return data.getIaqi().getPm10();
            case AirQualityBindingConstants.CO:
                return data.getIaqi().getCo();
            // and so on...
        }

        return null;
    }

    /**
     * Converts the string to a getter method.
     */
    private static String toGetterString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("get");
        sb.append(Character.toTitleCase(str.charAt(0)));
        sb.append(str.substring(1));
        return sb.toString();
    }

}
