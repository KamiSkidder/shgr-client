package com.kamiskidder.shgr.module;

import java.util.function.Predicate;

public class Setting<T> {
    private final String name;
    private T value;
    private String[] values;
    private T maxValue, minValue;
    private Predicate<T> visible;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public Setting(String name, T value, T maxValue, T minValue) {
        this.name = name;
        this.value = value;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public Setting(String name, T value, String[] values) {
        this.name = name;
        this.value = value;
        this.values = values;
    }

    public Setting(String name, T value, Predicate<T> visible) {
        this.name = name;
        this.value = value;
        this.visible = visible;
    }

    public Setting(String name, T value, T maxValue, T minValue, Predicate<T> visible) {
        this.name = name;
        this.value = value;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.visible = visible;
    }

    public Setting(String name, T value, String[] values, Predicate<T> visible) {
        this.name = name;
        this.value = value;
        this.values = values;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public T setValue(T value) {
        return this.value = value;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public T getMinValue() {
        return minValue;
    }

    public String[] getValues() {
        return values;
    }

    public boolean isVisible() {
        if (this.visible == null) {
            return true;
        }

        return this.visible.test(getValue());
    }
}
