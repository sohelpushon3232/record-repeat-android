package com.recordrepeat.bot;

import android.content.Context;
import java.util.ArrayList;

public class WorkflowStorage {

    private static final String FILE_NAME = "workflow";

    public static void save(
            Context context,
            ArrayList<ActionStep> steps
    ) {

        // Future: JSON save system
        // এখানে recorded workflow save হবে

    }


    public static ArrayList<ActionStep> load(
            Context context
    ) {

        // Future: saved workflow load হবে

        return new ArrayList<>();
    }
}
