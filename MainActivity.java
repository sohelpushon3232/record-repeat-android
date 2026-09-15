package com.example.recordrepeatbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final String PREFS = "bot_prefs";
    private TextView statusText;
    private TextView workflowText;
    private EditText repeatInput;
    private EditText recordedEmailInput;
    private EditText emailsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(30));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Record → Repeat Bot");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title, matchWrap(0, 12));

        TextView note = new TextView(this);
        note.setText("For your own/authorized apps and websites. Security checks such as CAPTCHA, OTP, biometrics and anti-bot controls are not bypassed. Password fields are not recorded.");
        note.setTextSize(14);
        root.addView(note, matchWrap(0, 14));

        statusText = new TextView(this);
        statusText.setTextSize(16);
        root.addView(statusText, matchWrap(0, 10));

        Button access = button("1) Enable Accessibility Service");
        access.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(access, matchWrap(0, 8));

        TextView emailLabel = label("Email used during recording (optional)");
        root.addView(emailLabel, matchWrap(10, 4));
        recordedEmailInput = edit(false);
        recordedEmailInput.setHint("example@gmail.com");
        recordedEmailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        recordedEmailInput.setText(prefs.getString("recorded_email", ""));
        root.addView(recordedEmailInput, matchWrap(0, 8));

        Button record = button("2) RECORD ONCE");
        record.setOnClickListener(v -> {
            AutomationAccessibilityService svc = AutomationAccessibilityService.getInstance();
            if (svc == null) {
                toast("Enable the Accessibility Service first.");
                return;
            }
            saveInputs();
            svc.startRecording(recordedEmailInput.getText().toString().trim());
            toast("Recording started. Do the workflow once. Use the floating STOP REC button when finished.");
            refreshStatus();
        });
        root.addView(record, matchWrap(0, 6));

        Button stopRecord = button("STOP RECORDING");
        stopRecord.setOnClickListener(v -> {
            AutomationAccessibilityService svc = AutomationAccessibilityService.getInstance();
            if (svc != null) svc.stopRecording();
            refreshStatus();
        });
        root.addView(stopRecord, matchWrap(0, 12));

        TextView repeatLabel = label("Repeat count");
        root.addView(repeatLabel, matchWrap(8, 4));
        repeatInput = edit(false);
        repeatInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        repeatInput.setText(String.valueOf(prefs.getInt("repeat_count", 1)));
        root.addView(repeatInput, matchWrap(0, 8));

        TextView listLabel = label("Email list (optional — one email per line)");
        root.addView(listLabel, matchWrap(8, 4));
        emailsInput = edit(true);
        emailsInput.setHint("email1@gmail.com\nemail2@gmail.com\nemail3@gmail.com");
        emailsInput.setText(prefs.getString("emails", ""));
        root.addView(emailsInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(150)));

        Button run = button("3) RUN AUTOMATICALLY");
        run.setOnClickListener(v -> {
            AutomationAccessibilityService svc = AutomationAccessibilityService.getInstance();
            if (svc == null) {
                toast("Enable the Accessibility Service first.");
                return;
            }
            saveInputs();
            int repeat = 1;
            try { repeat = Math.max(1, Integer.parseInt(repeatInput.getText().toString().trim())); }
            catch (Exception ignored) {}
            svc.startPlayback(repeat, emailsInput.getText().toString());
            refreshStatus();
        });
        root.addView(run, matchWrap(12, 6));

        Button stop = button("STOP RUN");
        stop.setOnClickListener(v -> {
            AutomationAccessibilityService svc = AutomationAccessibilityService.getInstance();
            if (svc != null) svc.stopPlayback();
            refreshStatus();
        });
        root.addView(stop, matchWrap(0, 12));

        Button clear = button("Clear saved workflow");
        clear.setOnClickListener(v -> {
            AutomationAccessibilityService svc = AutomationAccessibilityService.getInstance();
            if (svc != null) svc.clearWorkflow();
            else {
                File f = new File(getFilesDir(), "workflow.json");
                if (f.exists()) f.delete();
            }
            refreshStatus();
        });
        root.addView(clear, matchWrap(0, 12));

        workflowText = new TextView(this);
        workflowText.setTextSize(13);
        root.addView(workflowText, matchWrap(0, 10));

        TextView tips = new TextView(this);
        tips.setText("Recording tips:\n• Switch apps normally; the bot records the destination app and reopens it during replay.\n• Use normal on-screen buttons when possible.\n• For Android Back/Home while recording, use the floating BACK/HOME buttons so those actions are saved.\n• Keep the phone unlocked. The floating control keeps the screen awake while recording/running.\n• Some banking/protected screens, WebViews, games, OEM interfaces, CAPTCHA/OTP/biometric screens may not expose automatable controls.");
        tips.setTextSize(14);
        root.addView(tips, matchWrap(8, 0));

        setContentView(scroll);
        refreshStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void saveInputs() {
        int repeat = 1;
        try { repeat = Math.max(1, Integer.parseInt(repeatInput.getText().toString().trim())); }
        catch (Exception ignored) {}
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString("recorded_email", recordedEmailInput.getText().toString().trim())
                .putString("emails", emailsInput.getText().toString())
                .putInt("repeat_count", repeat)
                .apply();
    }

    private void refreshStatus() {
        boolean enabled = isAccessibilityEnabled(this);
        String mode = getSharedPreferences(PREFS, MODE_PRIVATE).getString("status", "Idle");
        statusText.setText("Accessibility: " + (enabled ? "ON" : "OFF") + "   |   Status: " + mode);
        workflowText.setText("Saved workflow: " + workflowSummary());
    }

    private String workflowSummary() {
        try {
            File f = new File(getFilesDir(), "workflow.json");
            if (!f.exists()) return "none";
            FileInputStream in = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            in.close();
            String raw = out.toString(StandardCharsets.UTF_8.name());
            JSONArray arr = new JSONArray(raw);
            return arr.length() + " step(s)";
        } catch (Exception e) {
            return "unreadable";
        }
    }

    private static boolean isAccessibilityEnabled(Context context) {
        String enabled = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabled == null) return false;
        String expected = context.getPackageName() + "/" + AutomationAccessibilityService.class.getName();
        return enabled.toLowerCase().contains(expected.toLowerCase());
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    private TextView label(String text) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(15);
        return v;
    }

    private EditText edit(boolean multiLine) {
        EditText e = new EditText(this);
        if (multiLine) {
            e.setSingleLine(false);
            e.setGravity(Gravity.TOP | Gravity.START);
        } else {
            e.setSingleLine(true);
        }
        return e;
    }

    private LinearLayout.LayoutParams matchWrap(int top, int bottom) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(top);
        lp.bottomMargin = dp(bottom);
        return lp;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
