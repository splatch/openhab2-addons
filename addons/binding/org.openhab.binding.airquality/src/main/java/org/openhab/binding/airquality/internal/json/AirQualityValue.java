package org.openhab.binding.airquality.internal.json;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper type around values reported by aqicn index values.
 */
public class AirQualityValue<T extends Number> {

    @SerializedName("v")
    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
