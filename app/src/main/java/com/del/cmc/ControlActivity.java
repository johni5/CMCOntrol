package com.del.cmc;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    public static final String BLUETOOTH_DEVICE = "BluetoothDevice";
    public static final String BUTT_LOG = "l";//   108 // 'l'
    public static final String BUTT_1 = "1";//      49
    public static final String BUTT_2 = "2";//      50
    public static final String BUTT_3 = "3";//      51
    public static final String BUTT_4 = "4";//      52
    public static final String BUTT_5 = "5";//     53
    public static final String BUTT_6 = "6";//     54
    public static final String BUTT_7 = "7";//     55
    public static final String BUTT_8 = "8";//     56
    public static final String BUTT_9 = "9";//     57
    public static final String BUTT_0 = "0";//     48
    public static final String BUTT_STAR = "i";//  105 // 'i' *
    public static final String BUTT_HASH = "u";//  117 // 'u' #
    public static final String BUTT_OK = "o";//   111 // 'o'
    public static final String BUTT_UP = "w";//   119 // 'w'
    public static final String BUTT_DOWN = "s";//  115 // 's'
    public static final String BUTT_LEFT = "a";//  97  // 'a'
    public static final String BUTT_RIGHT = "d";// 100 // 'd'
    public static final String BUTT_UP_1 = "t";//    116 // 't'
    public static final String BUTT_DOWN_1 = "g";//  103 // 'g'
    public static final String BUTT_LEFT_1 = "f";//  102 // 'f'
    public static final String BUTT_RIGHT_1 = "h";//  104 // 'h'

    private Mode[] modeItems = new Mode[]{
            new Mode(BUTT_1, "0", "VUM полоса"),
            new Mode(BUTT_2, "1", "VUM Радуга"),
            new Mode(BUTT_3, "2", "Светомузыка 5"),
            new Mode(BUTT_4, "3", "Светомузыка 3"),
            new Mode(BUTT_5, "4", "Светомузыка 1"),
            new Mode(BUTT_6, "5", "Стробоскоп"),
            new Mode(BUTT_7, "6", "Подсветка"),
            new Mode(BUTT_8, "7", "Бегущие частоты"),
            new Mode(BUTT_9, "8", "Анализатор спектра"),
    };

    private Map<String, String> modeLightIndex = new HashMap<String, String>() {
        {
            put("0", "Постоянный цвет");
            put("1", "Плавная смена цвета");
            put("2", "Бегущая радуга");
            put("3", "случайная смена цветов");
            put("4", "бегающий светодиод");
            put("5", "бегающий паровозик светодиодов");
            put("6", "вращаются красный и синий");
            put("7", "вращается половина красных и половина синих");
            put("8", "случайный стробоскоп");
            put("9", "пульсация одним цветом");
            put("10", "волны");
            put("11", "красные вспышки");
            put("12", "полумесяц");
            put("13", "безумие красных светодиодов");
            put("14", "безумие случайных цветов");
            put("15", "радиация");
            put("16", "эффект пламени");
            put("17", "пакман");
            put("18", "безумие случайных вспышек");
            put("19", "полицейская мигалка");
            put("20", "RGB пропеллер");
            put("21", "мячики");
            put("22", "мячики цветные");
            put("23", "плавная радуга");
            put("24", "Разноцветие");
        }
    };

    private Map<String, String> freqStrobeIndex = new HashMap<String, String>() {
        {
            put("0", "3 частоты");
            put("1", "Низкие");
            put("2", "Средние");
            put("3", "Высокие");
        }
    };

    private Button bPower;
    private RangeValueView[] stepSettingsList;
    private BluetoothDevice device;
    public TextView textInfo;
    public TextView messages;
    public TextView textLightMode;
    private LinearLayout lightModeView;
    private UUID myUUID;
    private Spinner modeList;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    final private Settings settings = new Settings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_control);
        this.device = getIntent().getExtras().getParcelable(BLUETOOTH_DEVICE);

        final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
        bPower = (Button) findViewById(R.id.b_power);
        Button bUpdate = (Button) findViewById(R.id.b_update);
        Button bTune = (Button) findViewById(R.id.b_tune);
        Button bLightMode = (Button) findViewById(R.id.b_light_mode);
        textInfo = (TextView) findViewById(R.id.textInfo);
        textLightMode = (TextView) findViewById(R.id.t_light_mode);
        messages = (TextView) findViewById(R.id.messages);
        LinearLayout buttonsPanel = (LinearLayout) findViewById(R.id.panel_set);
        lightModeView = (LinearLayout) findViewById(R.id.light_mode);
        modeList = (Spinner) findViewById(R.id.mode_list);
        final ArrayAdapter<Mode> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modeItems);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeList.setAdapter(modeAdapter);
        modeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sendMode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
        bPower.setOnClickListener(cmdListener(BUTT_STAR));
        bLightMode.setOnClickListener(cmdListener(BUTT_HASH));
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMode();
            }
        });
        bTune.setOnClickListener(cmdListener(BUTT_0));

        String stInfo = device.getName();
        textInfo.setText(String.format("Подключение к: %s ...", stInfo));

        stepSettingsList = new RangeValueView[]{
                new RangeValueView(this, "Плавность", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{0, 1}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getSmooth();
                    }
                }), // SMOOTH
                new RangeValueView(this, "Шаг радуги", BUTT_UP_1, BUTT_DOWN_1, new int[]{1}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getRainbow_step();
                    }
                }), // RAINBOW_STEP
                new RangeValueView(this, "Плавность", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{2, 3, 4}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getSmooth_freq();
                    }
                }), // SMOOTH_FREQ
                new RangeValueView(this, "Пиковая частота", BUTT_UP_1, BUTT_DOWN_1, new int[]{2, 3, 4, 7}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getMax_coef_freq();
                    }
                }), // MAX_COEF_FREQ
                new RangeValueView(this, "Плавность", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{5}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getStrobe_smooth();
                    }
                }), // STROBE_SMOOTH
                new RangeValueView(this, "Период", BUTT_UP_1, BUTT_DOWN_1, new int[]{5}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getStrobe_period();
                    }
                }), // STROBE_PERIOD
                new RangeValueView(this, "Цвет", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{6}, new int[]{0, 4, 5, 6, 7, 8, 9, 10, 11, 12}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getLight_color();
                    }
                }), // LIGHT_COLOR
                new RangeValueView(this, "Насыщенность", BUTT_UP_1, BUTT_DOWN_1, new int[]{6}, new int[]{0, 1, 8}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getLight_sat();
                    }
                }), // LIGHT_SAT
                new RangeValueView(this, "Скорость", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{6}, new int[]{1}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getColor_speed();
                    }
                }), // COLOR_SPEED
                new RangeValueView(this, "Скорость", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{6}, new int[]{2}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getRainbow_period();
                    }
                }), // RAINBOW_PERIOD
                new RangeValueView(this, "Шаг радуги", BUTT_UP_1, BUTT_DOWN_1, new int[]{6}, new int[]{2}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getRainbow_step_2();
                    }
                }), // RAINBOW_STEP_2
                new RangeValueView(this, "Скорость", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{7}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getRunning_speed();
                    }
                }), // RUNNING_SPEED
                new RangeValueView(this, "Шаг", BUTT_RIGHT_1, BUTT_LEFT_1, new int[]{8}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getHue_step();
                    }
                }), // HUE_STEP
                new RangeValueView(this, "Чувствительность", BUTT_UP_1, BUTT_DOWN_1, new int[]{8}, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getHue_start();
                    }
                }), // HUE_START
                new RangeValueView(this, "Яркость", BUTT_RIGHT, BUTT_LEFT, null, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getBrightness();
                    }
                }), // BRIGHTNESS
                new RangeValueView(this, "Подсветка", BUTT_UP, BUTT_DOWN, null, new SettingsUpdateListener() {
                    @Override
                    public String update(Settings settings) {
                        return settings.getEmpty_bright();
                    }
                }) // EMPTY_BRIGHT
        };
        for (RangeValueView b : stepSettingsList) {
            buttonsPanel.addView(b);
            b.getDown().setOnClickListener(cmdListener(b.getCmdDown()));
            b.getUp().setOnClickListener(cmdListener(b.getCmdUp()));
        }

    }

    private void sendMode() {
        Mode m = (Mode) modeList.getSelectedItem();
        if (m != null) {
            send(m.getKey());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
        myThreadConnectBTdevice.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThreadConnectBTdevice != null) {
            Toast.makeText(this, "Закрываем соединение", Toast.LENGTH_LONG).show();
            myThreadConnectBTdevice.close();
        }
        myThreadConnectBTdevice = null;
        myThreadConnected = null;
    }

    private void send(final String m) {
        messages.setText("Отправил: " + m);
        if (myThreadConnected != null) {
            myThreadConnected.write(m.getBytes());
        }
    }

    private View.OnClickListener cmdListener(final String cmd) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(cmd);
            }
        };
    }

    private void update(Settings settings) {
        if (settings.getOnState().equals("1")) {
            bPower.setBackgroundColor(Color.GREEN);
        } else {
            bPower.setBackgroundColor(Color.RED);
        }
        String mode = settings.getMode();
        for (Mode modeItem : modeItems) {
            if (modeItem.getCode().equals(mode)) {
                ArrayAdapter<Mode> modeAdapter = (ArrayAdapter<Mode>) modeList.getAdapter();
                modeList.setSelection(modeAdapter.getPosition(modeItem));
                break;
            }
        }

        if (mode.equals("6")) {
            lightModeView.setVisibility(View.VISIBLE);
            String modeName = "Режим " + settings.getLightMode();
            if (modeLightIndex.containsKey(settings.getLightMode())) {
                modeName = modeLightIndex.get(settings.getLightMode());
            }
            textLightMode.setText(modeName);
        } else if (mode.equals("4") || mode.equals("7")) {
            lightModeView.setVisibility(View.VISIBLE);
            textLightMode.setText(freqStrobeIndex.get(settings.getFreq_strobe_mode()));
        } else {
            lightModeView.setVisibility(View.GONE);
        }

        for (RangeValueView b : stepSettingsList) {
            b.render(settings);
        }

    }

    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;

        public ThreadConnectBTdevice(BluetoothDevice d) {
            try {
                this.bluetoothSocket = d.createInsecureRfcommSocketToServiceRecord(myUUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ControlActivity.this, "Отсутствует соединение, проверьте устройство!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ControlActivity.this, MainActivity.class));
                    }
                });
                try {
                    bluetoothSocket.close();
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
            }
            if (success) {
                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start();
                int count = 5;
                try {
                    do {
                        myThreadConnected.write(BUTT_LOG.getBytes());
                        SystemClock.sleep(1000);
                    } while (!settings.isReady() && --count > 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!settings.isReady()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ControlActivity.this, "Устройство не отвечает!", Toast.LENGTH_LONG).show();
                            close();
                            startActivity(new Intent(ControlActivity.this, MainActivity.class));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textInfo.setText(String.format("Подключено к: %s", device.getName()));
                        }
                    });
                }
            }
        }

        public void close() {
            try {
                if (bluetoothSocket != null) bluetoothSocket.close();
            } catch (Exception ioException) {
                //
            }
        }


    }

    private class ThreadConnected extends Thread {
        private final InputStream in;
        private final OutputStream out;
        private String sbprint;

        public ThreadConnected(BluetoothSocket socket) {
            InputStream _in = null;
            OutputStream _out = null;
            try {
                _in = socket.getInputStream();
                _out = socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.in = _in;
            this.out = _out;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    if (in.available() > 0) {
                        byte[] buffer = new byte[in.available()];
                        int bytes = in.read(buffer);
                        String strIncom = new String(buffer, 0, bytes);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messages.setText(sbprint);
                                    settings.update(sbprint);
                                    update(settings);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(ControlActivity.this, "Ошибка связи, проверьте устройство!", Toast.LENGTH_LONG).show();
                    if (myThreadConnectBTdevice != null) myThreadConnectBTdevice.close();
                    startActivity(new Intent(ControlActivity.this, MainActivity.class));
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                out.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}