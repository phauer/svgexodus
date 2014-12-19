package de.philipphauer.svgexodus.gui;

import com.jgoodies.binding.value.BindingConverter;

public class FloatToStringConverter implements BindingConverter<Float, String> {

    private static FloatToStringConverter instance = new FloatToStringConverter();

    private FloatToStringConverter() {
    }

    public static FloatToStringConverter get() {
        return instance;
    }

    @Override
    public String targetValue(Float sourceValue) {
        if (sourceValue == null || sourceValue.equals(-1.0f)) {
            return "";
        }
        return sourceValue.toString();
    }

    @Override
    public Float sourceValue(String targetValue) {
        if (targetValue == null || targetValue.trim().isEmpty()) {
            return -1.0f;
        }
        return Float.parseFloat(targetValue);
    }

}
