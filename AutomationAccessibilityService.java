package com.example.recordrepeatbot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutomationAccessibilityService extends AccessibilityService {
    private static final String PREFS = "bot_prefs";
    private static final String WORKFLOW_FILE = "workflow.json";
    private static volatile AutomationAccessibilityService instance;

    private final Object workflowLock = new Object();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private JSONArray workflow = new JSONArray();
    private volatile boolean recording = false;
    private volatile boolean running = false;
    private volatile Thread playbackThread;
    private String recordedEmail = "";
    private String lastRecordedPackage = "";
    private long lastStepAt = 0L;
    private int lastScrollY = Integer.MIN_VALUE;

    private WindowManager windowManager;
    private LinearLayout overlayView;

    public static AutomationAccessibilityService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        setStatus("Idle");
        toast("Record Repeat Bot accessibility service is ON.");
    }

    @Override
    public void onDestroy() {
        stopPlayback();
        if (recording) stopRecording();
        removeOverlay();
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        // Android may temporarily interrupt accessibility feedback. Keep workflow state intact.
    }

    public void startRecording(String emailUsedDuringRecording) {
        if (running) stopPlayback();
        synchronized (workflowLock) {
            workflow = new JSONArray();
        }
        recordedEmail = emailUsedDuringRecording == null ? "" : emailUsedDuringRecording.trim();
        lastRecordedPackage = "";
        lastStepAt = System.currentTimeMillis();
        lastScrollY = Integer.MIN_VALUE;
        recording = true;
        setStatus("Recording");
        showRecordOverlay();
    }

    public void stopRecording() {
        if (!recording) return;
        recording = false;
        saveWorkflow();
        setStatus("Recorded " + workflowLength() + " steps");
        removeOverlay();
        toast("Workflow saved: " + workflowLength() + " steps.");
    }

    public void startPlayback(int requestedRepeat, String emailLines) {
        if (recording) stopRecording();
        if (running) return;

        JSONArray loaded = loadWorkflow();
        if (loaded == null || loaded.length() == 0) {
            toast("No saved workflow. Record once first.");
            return;
        }
        synchronized (workflowLock) {
            workflow = loaded;
        }

        List<String> emails = parseEmails(emailLines);
        int repeat = Math.max(1, requestedRepeat);
        int totalRuns = emails.isEmpty() ? repeat : Math.min(repeat, emails.size());
        running = true;
        setStatus("Running 0/" + totalRuns);
        showRunOverlay();

        playbackThread = new Thread(() -> {
            try {
                for (int run = 0; run < totalRuns && running; run++) {
                    String email = emails.isEmpty() ? recordedEmail : emails.get(run);
                    setStatus("Running " + (run + 1) + "/" + totalRuns);
                    JSONArray snapshot;
                    synchronized (workflowLock) {
                        snapshot = new JSONArray(workflow.toString());
                    }
                    for (int i = 0; i < snapshot.length() && running; i++) {
                        JSONObject step = snapshot.optJSONObject(i);
                        if (step == null) continue;
                        long delay = clamp(step.optLong("delayMs", 500), 250, 3500);
                        safeSleep(delay);
                        if (!running) break;
                        executeStep(step, email);
                    }
                    safeSleep(700);
                }
            } catch (Throwable t) {
                setStatus("Stopped: " + shortMessage(t));
            } finally {
                running = false;
                playbackThread = null;
                mainHandler.post(this::removeOverlay);
                if (!getStatus().startsWith("Stopped:")) setStatus("Finished");
            }
        }, "workflow-playback");
        playbackThread.start();
    }

    public void stopPlayback() {
        running = false;
        Thread t = playbackThread;
        if (t != null) t.interrupt();
        playbackThread = null;
        setStatus("Stopped");
        removeOverlay();
    }

    public void clearWorkflow() {
        if (recording) recording = false;
        if (running) stopPlayback();
        synchronized (workflowLock) {
            workflow = new JSONArray();
        }
        try {
            File f = new File(getFilesDir(), WORKFLOW_FILE);
            if (f.exists()) f.delete();
        } catch (Exception ignored) {}
        setStatus("Idle");
        removeOverlay();
        toast("Saved workflow cleared.");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!recording || running || event == null) return;
        CharSequence p = event.getPackageName();
        String pkg = p == null ? "" : p.toString();
        if (pkg.isEmpty() || pkg.equals(getPackageName()) || pkg.equals(currentImePackage())) return;

        try {
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    recordAppSwitch(pkg);
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    recordClick(event, pkg);
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    recordText(event, pkg);
                    break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    recordScroll(event, pkg);
                    break;
                default:
                    break;
            }
        } catch (Throwable ignored) {
            // A single inaccessible view should not terminate the recording session.
        }
    }

    private void recordAppSwitch(String pkg) {
        if (pkg.equals(lastRecordedPackage)) return;
        JSONObject step = baseStep("APP", pkg, null);
        stepPut(step, "package", pkg);
        addStep(step, false);
        lastRecordedPackage = pkg;
    }

    private void recordClick(AccessibilityEvent event, String pkg) {
        AccessibilityNodeInfo node = event.getSource();
        JSONObject step = baseStep("CLICK", pkg, node);
        if (node == null) {
            String text = firstEventText(event);
            stepPut(step, "text", text);
        }
        addStep(step, false);
    }

    private void recordText(AccessibilityEvent event, String pkg) {
        AccessibilityNodeInfo node = event.getSource();
        if (node == null || node.isPassword()) return;
        if (!node.isEditable() && !"android.widget.EditText".contentEquals(node.getClassName())) return;

        String text = node.getText() == null ? firstEventText(event) : node.getText().toString();
        if (!recordedEmail.isEmpty() && recordedEmail.equals(text)) text = "${EMAIL}";

        JSONObject step = baseStep("TYPE", pkg, node);
        stepPut(step, "value", text);

        synchronized (workflowLock) {
            int n = workflow.length();
            if (n > 0) {
                JSONObject last = workflow.optJSONObject(n - 1);
                if (last != null && "TYPE".equals(last.optString("type")) && sameTarget(last, step)) {
                    stepPut(last, "value", text);
                    stepPut(last, "delayMs", clampedDelay());
                    lastStepAt = System.currentTimeMillis();
                    return;
                }
            }
        }
        addStep(step, false);
    }

    private void recordScroll(AccessibilityEvent event, String pkg) {
        AccessibilityNodeInfo node = event.getSource();
        int direction = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
        if (Build.VERSION.SDK_INT >= 28) {
            int dy = event.getScrollDeltaY();
            if (dy < 0) direction = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
            else if (dy == 0 && lastScrollY != Integer.MIN_VALUE && event.getScrollY() < lastScrollY) {
                direction = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
            }
        } else if (lastScrollY != Integer.MIN_VALUE && event.getScrollY() < lastScrollY) {
            direction = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
        }
        lastScrollY = event.getScrollY();

        JSONObject step = baseStep("SCROLL", pkg, node);
        stepPut(step, "direction", direction == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD ? "BACKWARD" : "FORWARD");
        addStep(step, true);
    }

    private JSONObject baseStep(String type, String pkg, AccessibilityNodeInfo node) {
        JSONObject o = new JSONObject();
        stepPut(o, "type", type);
        stepPut(o, "package", pkg);
        stepPut(o, "delayMs", clampedDelay());
        if (node != null) {
            stepPut(o, "viewId", safe(node.getViewIdResourceName()));
            stepPut(o, "text", node.getText() == null ? "" : node.getText().toString());
            stepPut(o, "desc", node.getContentDescription() == null ? "" : node.getContentDescription().toString());
            stepPut(o, "hint", Build.VERSION.SDK_INT >= 26 && node.getHintText() != null ? node.getHintText().toString() : "");
            stepPut(o, "className", node.getClassName() == null ? "" : node.getClassName().toString());
            Rect r = new Rect();
            node.getBoundsInScreen(r);
            stepPut(o, "x", r.centerX());
            stepPut(o, "y", r.centerY());
        }
        return o;
    }

    private void addStep(JSONObject step, boolean mergeScroll) {
        synchronized (workflowLock) {
            if (mergeScroll && workflow.length() > 0) {
                JSONObject last = workflow.optJSONObject(workflow.length() - 1);
                if (last != null && "SCROLL".equals(last.optString("type")) && sameTarget(last, step)
                        && last.optString("direction").equals(step.optString("direction"))) {
                    lastStepAt = System.currentTimeMillis();
                    return;
                }
            }
            workflow.put(step);
        }
        lastStepAt = System.currentTimeMillis();
    }

    private boolean sameTarget(JSONObject a, JSONObject b) {
        if (!a.optString("package").equals(b.optString("package"))) return false;
        String ida = a.optString("viewId");
        String idb = b.optString("viewId");
        if (!ida.isEmpty() || !idb.isEmpty()) return ida.equals(idb);
        return a.optString("className").equals(b.optString("className"))
                && Math.abs(a.optInt("x") - b.optInt("x")) < 40
                && Math.abs(a.optInt("y") - b.optInt("y")) < 40;
    }

    private long clampedDelay() {
        long now = System.currentTimeMillis();
        long d = lastStepAt == 0 ? 500 : now - lastStepAt;
        return clamp(d, 250, 5000);
    }

    private void executeStep(JSONObject step, String email) {
        String type = step.optString("type");
        switch (type) {
            case "APP":
                openPackage(step.optString("package"));
                break;
            case "CLICK":
                doClick(step);
                break;
            case "TYPE":
                String value = step.optString("value");
                if ("${EMAIL}".equals(value)) value = email == null ? "" : email;
                doType(step, value);
                break;
            case "SCROLL":
                doScroll(step);
                break;
            case "GLOBAL_BACK":
                performGlobalAction(GLOBAL_ACTION_BACK);
                break;
            case "GLOBAL_HOME":
                performGlobalAction(GLOBAL_ACTION_HOME);
                break;
            default:
                break;
        }
    }

    private void openPackage(String pkg) {
        if (pkg == null || pkg.isEmpty() || pkg.equals(getPackageName())) return;
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch == null) return;
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(launch);
            waitForPackage(pkg, 7000);
        } catch (Throwable ignored) {}
    }

    private void waitForPackage(String pkg, long timeoutMs) {
        long end = System.currentTimeMillis() + timeoutMs;
        while (running && System.currentTimeMillis() < end) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root != null && root.getPackageName() != null && pkg.equals(root.getPackageName().toString())) return;
            safeSleep(180);
        }
    }

    private void doClick(JSONObject step) {
        AccessibilityNodeInfo node = waitForNode(step, false, 7000);
        if (node != null) {
            AccessibilityNodeInfo clickable = node;
            int hops = 0;
            while (clickable != null && !clickable.isClickable() && hops++ < 6) {
                clickable = clickable.getParent();
            }
            if (clickable != null && clickable.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return;
            Rect r = new Rect();
            node.getBoundsInScreen(r);
            if (!r.isEmpty()) {
                tap(r.centerX(), r.centerY());
                return;
            }
        }
        tap(step.optInt("x", 0), step.optInt("y", 0));
    }

    private void doType(JSONObject step, String value) {
        AccessibilityNodeInfo node = waitForNode(step, true, 7000);
        if (node == null) return;
        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value == null ? "" : value);
        if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)) {
            Rect r = new Rect();
            node.getBoundsInScreen(r);
            if (!r.isEmpty()) tap(r.centerX(), r.centerY());
        }
    }

    private void doScroll(JSONObject step) {
        AccessibilityNodeInfo node = waitForNode(step, false, 2500);
        if (node == null) node = getRootInActiveWindow();
        if (node == null) return;
        int action = "BACKWARD".equals(step.optString("direction"))
                ? AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                : AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
        AccessibilityNodeInfo cur = node;
        int hops = 0;
        while (cur != null && !cur.isScrollable() && hops++ < 7) cur = cur.getParent();
        if (cur != null) cur.performAction(action);
    }

    private AccessibilityNodeInfo waitForNode(JSONObject step, boolean editableOnly, long timeoutMs) {
        long end = System.currentTimeMillis() + timeoutMs;
        while (running && System.currentTimeMillis() < end) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            AccessibilityNodeInfo found = findNode(root, step, editableOnly);
            if (found != null) return found;
            safeSleep(180);
        }
        return null;
    }

    private AccessibilityNodeInfo findNode(AccessibilityNodeInfo root, JSONObject step, boolean editableOnly) {
        if (root == null) return null;
        String viewId = step.optString("viewId");
        if (!viewId.isEmpty()) {
            try {
                List<AccessibilityNodeInfo> matches = root.findAccessibilityNodeInfosByViewId(viewId);
                AccessibilityNodeInfo best = firstUsable(matches, editableOnly);
                if (best != null) return best;
            } catch (Throwable ignored) {}
        }

        String desc = step.optString("desc");
        String hint = step.optString("hint");
        String text = step.optString("text");
        String cls = step.optString("className");
        int x = step.optInt("x", -1);
        int y = step.optInt("y", -1);

        AccessibilityNodeInfo exact = bfsFind(root, desc, hint, text, cls, editableOnly, x, y);
        if (exact != null) return exact;

        if (!text.isEmpty()) {
            try {
                List<AccessibilityNodeInfo> matches = root.findAccessibilityNodeInfosByText(text);
                AccessibilityNodeInfo best = firstUsable(matches, editableOnly);
                if (best != null) return best;
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private AccessibilityNodeInfo bfsFind(AccessibilityNodeInfo root, String desc, String hint, String text,
                                          String cls, boolean editableOnly, int targetX, int targetY) {
        ArrayDeque<AccessibilityNodeInfo> q = new ArrayDeque<>();
        q.add(root);
        AccessibilityNodeInfo closest = null;
        long closestDistance = Long.MAX_VALUE;
        int seen = 0;
        while (!q.isEmpty() && seen++ < 2500) {
            AccessibilityNodeInfo n = q.removeFirst();
            boolean editableOk = !editableOnly || n.isEditable() || "android.widget.EditText".contentEquals(n.getClassName());
            if (editableOk) {
                String nd = n.getContentDescription() == null ? "" : n.getContentDescription().toString();
                String nt = n.getText() == null ? "" : n.getText().toString();
                String nh = Build.VERSION.SDK_INT >= 26 && n.getHintText() != null ? n.getHintText().toString() : "";
                String nc = n.getClassName() == null ? "" : n.getClassName().toString();

                if (!desc.isEmpty() && desc.equals(nd)) return n;
                if (!hint.isEmpty() && hint.equals(nh)) return n;
                if (!text.isEmpty() && text.equals(nt)) return n;

                if (!cls.isEmpty() && cls.equals(nc) && targetX >= 0 && targetY >= 0) {
                    Rect r = new Rect();
                    n.getBoundsInScreen(r);
                    long dx = (long) r.centerX() - targetX;
                    long dy = (long) r.centerY() - targetY;
                    long dist = dx * dx + dy * dy;
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        closest = n;
                    }
                }
            }
            for (int i = 0; i < n.getChildCount(); i++) {
                AccessibilityNodeInfo child = n.getChild(i);
                if (child != null) q.add(child);
            }
        }
        return closest;
    }

    private AccessibilityNodeInfo firstUsable(List<AccessibilityNodeInfo> nodes, boolean editableOnly) {
        if (nodes == null) return null;
        for (AccessibilityNodeInfo n : nodes) {
            if (n == null) continue;
            if (!editableOnly || n.isEditable() || "android.widget.EditText".contentEquals(n.getClassName())) return n;
        }
        return null;
    }

    private void tap(int x, int y) {
        if (x <= 0 || y <= 0) return;
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 80);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        try { dispatchGesture(gesture, null, null); }
        catch (Throwable ignored) {}
    }

    private void addGlobalStep(String type, int globalAction) {
        if (!recording) return;
        JSONObject step = new JSONObject();
        stepPut(step, "type", type);
        stepPut(step, "delayMs", clampedDelay());
        addStep(step, false);
        performGlobalAction(globalAction);
    }

    private void showRecordOverlay() {
        mainHandler.post(() -> {
            removeOverlayNow();
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            overlayView = new LinearLayout(this);
            overlayView.setOrientation(LinearLayout.VERTICAL);
            overlayView.setPadding(dp(4), dp(4), dp(4), dp(4));

            TextView rec = new TextView(this);
            rec.setText("● REC");
            rec.setTextSize(13);
            rec.setGravity(Gravity.CENTER);
            overlayView.addView(rec);

            Button back = smallButton("BACK");
            back.setOnClickListener(v -> addGlobalStep("GLOBAL_BACK", GLOBAL_ACTION_BACK));
            overlayView.addView(back);

            Button home = smallButton("HOME");
            home.setOnClickListener(v -> addGlobalStep("GLOBAL_HOME", GLOBAL_ACTION_HOME));
            overlayView.addView(home);

            Button stop = smallButton("STOP REC");
            stop.setOnClickListener(v -> stopRecording());
            overlayView.addView(stop);

            addOverlayToWindow();
        });
    }

    private void showRunOverlay() {
        mainHandler.post(() -> {
            removeOverlayNow();
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            overlayView = new LinearLayout(this);
            overlayView.setOrientation(LinearLayout.VERTICAL);
            overlayView.setPadding(dp(4), dp(4), dp(4), dp(4));

            TextView run = new TextView(this);
            run.setText("▶ RUN");
            run.setTextSize(13);
            run.setGravity(Gravity.CENTER);
            overlayView.addView(run);

            Button stop = smallButton("STOP");
            stop.setOnClickListener(v -> stopPlayback());
            overlayView.addView(stop);

            addOverlayToWindow();
        });
    }

    private Button smallButton(String text) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextSize(11);
        b.setMinWidth(dp(78));
        b.setMinHeight(dp(42));
        return b;
    }

    private void addOverlayToWindow() {
        if (windowManager == null || overlayView == null) return;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                android.graphics.PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.x = dp(8);
        lp.y = dp(110);
        try { windowManager.addView(overlayView, lp); }
        catch (Throwable ignored) {}
    }

    private void removeOverlay() {
        mainHandler.post(this::removeOverlayNow);
    }

    private void removeOverlayNow() {
        if (windowManager != null && overlayView != null) {
            try { windowManager.removeView(overlayView); }
            catch (Throwable ignored) {}
        }
        overlayView = null;
    }

    private void saveWorkflow() {
        try {
            JSONArray snapshot;
            synchronized (workflowLock) {
                snapshot = new JSONArray(workflow.toString());
            }
            File f = new File(getFilesDir(), WORKFLOW_FILE);
            FileOutputStream out = new FileOutputStream(f);
            out.write(snapshot.toString(2).getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        } catch (Exception e) {
            toast("Could not save workflow: " + shortMessage(e));
        }
    }

    private JSONArray loadWorkflow() {
        try {
            File f = new File(getFilesDir(), WORKFLOW_FILE);
            if (!f.exists()) return new JSONArray();
            FileInputStream in = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            in.close();
            return new JSONArray(out.toString(StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            toast("Could not read workflow: " + shortMessage(e));
            return new JSONArray();
        }
    }

    private int workflowLength() {
        synchronized (workflowLock) { return workflow.length(); }
    }

    private List<String> parseEmails(String lines) {
        List<String> out = new ArrayList<>();
        if (lines == null) return out;
        for (String s : lines.split("\\r?\\n")) {
            s = s.trim();
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private String currentImePackage() {
        try {
            String ime = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            if (TextUtils.isEmpty(ime)) return "";
            ComponentName cn = ComponentName.unflattenFromString(ime);
            return cn == null ? "" : cn.getPackageName();
        } catch (Throwable t) {
            return "";
        }
    }

    private String firstEventText(AccessibilityEvent event) {
        if (event.getText() == null || event.getText().isEmpty() || event.getText().get(0) == null) return "";
        return event.getText().get(0).toString();
    }

    private void setStatus(String status) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString("status", status).apply();
    }

    private String getStatus() {
        return getSharedPreferences(PREFS, MODE_PRIVATE).getString("status", "Idle");
    }

    private void toast(String message) {
        mainHandler.post(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void safeSleep(long ms) {
        if (ms <= 0) return;
        try { Thread.sleep(ms); }
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private long clamp(long v, long min, long max) {
        return Math.max(min, Math.min(max, v));
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String shortMessage(Throwable t) {
        String s = t == null ? "unknown error" : t.getMessage();
        if (s == null || s.trim().isEmpty()) s = t == null ? "unknown error" : t.getClass().getSimpleName();
        if (s.length() > 80) s = s.substring(0, 80);
        return s;
    }

    private void stepPut(JSONObject obj, String key, Object value) {
        try { obj.put(key, value); }
        catch (Exception ignored) {}
    }
}
