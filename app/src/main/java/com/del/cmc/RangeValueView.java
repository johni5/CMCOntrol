package com.del.cmc;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RangeValueView extends RelativeLayout {

    private TextView label;
    private TextView value;
    private Button up;
    private Button down;
    final private List<Integer> allowModes = new ArrayList<>();
    final private List<Integer> allowLightModes = new ArrayList<>();
    final private String cmdUp, cmdDown;
    final private SettingsUpdateListener listener;

    public RangeValueView(Context context, String label, String cmdUp, String cmdDown, int[] modes, SettingsUpdateListener listener) {
        this(context, label, cmdUp, cmdDown, modes, null, listener);
    }

    public RangeValueView(Context context, String label, String cmdUp, String cmdDown, int[] modes, int[] lightModes, SettingsUpdateListener listener) {
        super(context);
        initComponent();
        setLabel(label);
        this.cmdDown = cmdDown;
        this.cmdUp = cmdUp;
        this.listener = listener;
        if (modes != null) {
            for (int mode : modes) {
                allowModes.add(mode);
            }
        }
        if (lightModes != null) {
            for (int m : lightModes) {
                allowLightModes.add(m);
            }
        }
    }

    public Button getUp() {
        return up;
    }

    public Button getDown() {
        return down;
    }

    public String getCmdUp() {
        return cmdUp;
    }

    public String getCmdDown() {
        return cmdDown;
    }

    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.step_set, this);
        label = (TextView) findViewById(R.id.set_label);
        value = (TextView) findViewById(R.id.set_val);
        up = (Button) findViewById(R.id.set_up);
        down = (Button) findViewById(R.id.set_down);
        setVisibility(GONE);
    }

    public void setLabel(String t) {
        label.setText(t);
    }

    public void setValue(String v) {
        value.setText(v);
    }

    public void render(Settings settings) {
        int mode = Integer.parseInt(settings.getMode());
        int lMode = Integer.parseInt(settings.getLightMode());
        if ((allowModes.isEmpty() || allowModes.contains(mode))
                && (allowLightModes.isEmpty() || allowLightModes.contains(lMode))) {
            setVisibility(VISIBLE);
            System.out.println(label.getText() + " visible ON");
        } else {
            setVisibility(GONE);
            System.out.println(label.getText() + " visible OFF");
        }
        if (listener != null) {
            setValue(listener.update(settings));
        }
    }
}
