package com.recordrepeat.bot;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkflowStorage {

    private static final String PREF = "record_repeat_bot";
    private static final String KEY = "workflow";

    public static void save(Context context, List<ActionStep> steps) {

        JSONArray array = new JSONArray();

        try {
            for (ActionStep step : steps) {

                JSONObject obj = new JSONObject();

                obj.put("action", step.action);
                obj.put("x", step.x);
                obj.put("y", step.y);
                obj.put("text", step.text);
                obj.put("delay", step.delay);

                array.put(obj);
            }

            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ActionStep> load(Context context) {

        ArrayList<ActionStep> result = new ArrayList<>();

        SharedPreferences pref =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String raw = pref.getString(KEY, "[]");

        try {

            JSONArray array = new JSONArray(raw);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                result.add(
                        new ActionStep(
                                obj.getString("action"),
                                obj.getInt("x"),
                                obj.getInt("y"),
                                obj.optString("text", ""),
                                obj.optLong("delay", 500)
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
