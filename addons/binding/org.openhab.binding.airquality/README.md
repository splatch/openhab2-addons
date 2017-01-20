---
layout: documentation
---

{% include base.html %}

# Air Quality Binding

This binding uses the [AQIcn.org service](https://www.wunderground.com/AirQuality/api/) for providing air quality information for any location worldwide.

The World Air Quality Index project is a social enterprise project started in 2007. Its mission is to promote Air Pollution awareness and provide a unified Air Quality information for the whole world. 

The project is proving a transparent Air Quality information for more than 70 countries, covering more than 9000 stations in 600 major cities, via those two websites: [aqicn.org](http://aqicn.org) and [waqi.info](http://waqi.info).

To use this binding, you first need to [register and get your API token](http://aqicn.org/data-platform/token/).

## Supported Things

There is exactly one supported thing type, which represents the air quality information for an observation location. It has the `aqi` id. Of course, you can add multiple Things, e.g. for measuring AQI for different locations.

## Discovery

There is no discovery implemented. You have to create your things manually.

## Binding Configuration
 
The binding has no configuration options, all configuration is done at Thing level.
 
## Thing Configuration

The thing has a few configuration parameters:

| Parameter | Description                                                              |
|-----------|------------------------------------------------------------------------- |
| apikey    | Data-platform token to access the AQIcn.org service. Mandatory.            |
| location  | Geo coordinates to be considered by the service. Mandatory. |
| refresh   | Refresh interval in minutes. Optional, the default value is 60 minutes.  |

For the location parameter, the following syntax is allowed:

`37.8;-122.4`


## Channels

The AirQuality information that is retrieved is available as these channels:


| Channel ID | Item Type    | Description              |   |
|------------|--------------|------------------------- |---|
| aqiLevel | Number | Air Quality Index | |
| aqiDescription | String | AQI Description |
| locationName | String | Nearest measuring station location |
| locationUrl | String | Direct URL to the station results |
| stationId | Number |
| pm25 | Number | Fine particles pollution level (PM2.5) |
| pm10 | Number | Coarse dust particles pollution level (PM10) |
| no2 | Number | Nitrogen Dioxide level (NO2) |
| co | Number | Carbon monoxide level (CO) |
| observationTime | DateTime | Observation date and time |
| temperature | Number | Temperature in Celsius degrees |
| pressure | Number | Pressure level |
| humidity | Number | Humidity level |

`AQI Description` item provides a human-readable output that can be interpreted e.g. by MAP transformation:
 
airquality.map
```
NO_DATA=No data
GOOD=Good
MODERATE=Moderate
UNHEALTHY_FOR_SENSITIVE=Unhealthy for sensitive groups
UNHEALTHY=Unhealthy
VERY_UNHEALTHY=Very unhealthy
HAZARDOUS=Hazardous
```

## Full Example

airquality.things:

```
airquality:aqi:Krakow "AirQuality in Krakow" [ apikey="XXXXXXXXXXXX", location="50.06465,19.94498", refresh=60 ]
```

airquality.items:

```
Number AqiLevel "Air Quality Index" (AirQuality) { channel="airquality:aqi:krk:aqiLevel" }
String AqiDescription "AQI Level [MAP(airquality.map):%s]" (AirQuality) { channel="airquality:aqi:krk:aqiDescription" }

Number Pm25 "PM25 Level" (AirQuality) { channel="airquality:aqi:krk:pm25" }
Number Pm10 "PM10 Level" (AirQuality) { channel="airquality:aqi:krk:pm10" }
Number O3 "O3 Level" (AirQuality) { channel="airquality:aqi:krk:o3" }
Number No2 "NO2 Level" (AirQuality) { channel="airquality:aqi:krk:no2" }
Number Co "CO Level" (AirQuality) { channel="airquality:aqi:krk:co" }

String AqiLocationName "Measuring Location" (AirQuality) { channel="airquality:aqi:krk:locationName" }
String AqiLocationUrl "URL" (AirQuality) { channel="airquality:aqi:krk:locationUrl" }

DateTime AqiObservationTime "Time of observation [%1$tH:%1$tM]" (AirQuality) { channel="airquality:aqi:krk:observationTime" }

Number StationId "Station ID" (AirQuality) { channel="airquality:aqi:krk:stationId" }
Number AqiTemperature "Temperature" (AirQuality) { channel="airquality:aqi:krk:temperature" }
Number AqiPressure "Pressure" (AirQuality) { channel="airquality:aqi:krk:pressure" }
Number AqiHumidity "Humidity" (AirQuality) { channel="airquality:aqi:krk:humidity" }
```

airquality.sitemap:

```
sitemap demo label="Air Quality"
{
    Frame {
        Text item=AqiDescription
    }
}
```
