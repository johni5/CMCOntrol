package com.del.cmc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Settings {

    private final Value mode = new Value("MODE");
    private final Value lightMode = new Value("LMODE");
    private final Value setMode = new Value("SMODE");
    private final Value freq_strobe_mode = new Value("freq_strobe_mode");
    private final Value rainbow_step = new Value("RAINBOW_STEP");
    private final Value max_coef_freq = new Value("MAX_COEF_FREQ");
    private final Value strobe_period = new Value("STROBE_PERIOD");
    private final Value light_sat = new Value("LIGHT_SAT");
    private final Value rainbow_step_2 = new Value("RAINBOW_STEP_2");
    private final Value hue_start = new Value("HUE_START");
    private final Value smooth = new Value("SMOOTH");
    private final Value smooth_freq = new Value("SMOOTH_FREQ");
    private final Value strobe_smooth = new Value("STROBE_SMOOTH");
    private final Value light_color = new Value("LIGHT_COLOR");
    private final Value color_speed = new Value("COLOR_SPEED");
    private final Value rainbow_period = new Value("RAINBOW_PERIOD");
    private final Value running_speed = new Value("RUNNING_SPEED");
    private final Value hue_step = new Value("HUE_STEP");
    private final Value empty_bright = new Value("EMPTY_BRIGHT");
    private final Value brightness = new Value("BRIGHTNESS");
    private final Value onState = new Value("ONstate");


    private final Map<String, Value> index = new HashMap<>();

    public Settings() {
        for (Field df : getClass().getDeclaredFields()) {
            if (df.getType().isAssignableFrom(Value.class)) {
                try {
                    Value v = (Value) df.get(this);
                    index.put(v.name.trim().toUpperCase(), v);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // MODE = 1 | LMODE = 2 | RAINBOW_STEP_2 = 1.2
    synchronized public void update(String pack) {
        if (pack != null) {
            StringTokenizer st1 = new StringTokenizer(pack, "|");
            while (st1.hasMoreTokens()) {
                String s = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(s, "=");
                if (st2.countTokens() > 1) {
                    String name = st2.nextToken().trim().toUpperCase();
                    String val = st2.nextToken();
                    if (index.containsKey(name)) {
                        index.get(name).setVal(val != null ? val.trim() : "");
                    }
                }
            }
        }
    }

    synchronized public boolean isReady() {
        return getMode() != null && getMode().matches("^[0-9]$");
    }

    public String getMode() {
        return mode.getVal();
    }

    public String getLightMode() {
        return lightMode.getVal();
    }

    public String getSetMode() {
        return setMode.getVal();
    }

    public String getBrightness() {
        return brightness.getVal();
    }

    public String getFreq_strobe_mode() {
        return freq_strobe_mode.getVal();
    }

    public String getRainbow_step() {
        return rainbow_step.getVal();
    }

    public String getMax_coef_freq() {
        return max_coef_freq.getVal();
    }

    public String getStrobe_period() {
        return strobe_period.getVal();
    }

    public String getLight_sat() {
        return light_sat.getVal();
    }

    public String getRainbow_step_2() {
        return rainbow_step_2.getVal();
    }

    public String getHue_start() {
        return hue_start.getVal();
    }

    public String getSmooth() {
        return smooth.getVal();
    }

    public String getSmooth_freq() {
        return smooth_freq.getVal();
    }

    public String getStrobe_smooth() {
        return strobe_smooth.getVal();
    }

    public String getLight_color() {
        return light_color.getVal();
    }

    public String getColor_speed() {
        return color_speed.getVal();
    }

    public String getRainbow_period() {
        return rainbow_period.getVal();
    }

    public String getRunning_speed() {
        return running_speed.getVal();
    }

    public String getHue_step() {
        return hue_step.getVal();
    }

    public String getEmpty_bright() {
        return empty_bright.getVal();
    }

    public String getOnState() {
        return onState.getVal();
    }

    private class Value {

        String name;
        String val;

        public Value(String name) {
            this.name = name;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getName() {
            return name;
        }

        public String getVal() {
            return val;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "name='" + name + '\'' +
                    ", val='" + val + '\'' +
                    '}';
        }
    }

}
