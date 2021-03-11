package zyx.existent.module.data;

import java.lang.reflect.Type;

import com.google.gson.annotations.Expose;

public class Setting {
    private final String name;

    public String getDesc() {
        return desc;
    }

    private final String desc;
    private final Type type;
    private final double inc;
    private String[] options;
    private String selectedOption;
    private final double min;
    private final double max;
    @Expose
    private Object value;

    public Setting(String name, Object value, String desc) {
        this.name = name;
        this.value = value;
        this.type = value.getClass().getGenericSuperclass();
        this.desc = desc;
        if (value instanceof Number) {
            inc = 0.5;
            min = 1;
            max = 20;
        } else {
            inc = 0;
            min = 0;
            max = 0;
        }
    }

    public Setting(String name, Object value, String desc, double inc, double min, double max) {
        this.name = name;
        this.value = value;
        this.type = value.getClass().getGenericSuperclass();
        this.desc = desc;
        this.inc = inc;
        this.min = min;
        this.max = max;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public double getInc() {
        return inc;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void update(Setting setting) {
        value = (Object) setting.value;
    }
}
