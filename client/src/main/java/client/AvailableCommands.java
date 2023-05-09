package client;

import java.util.HashMap;

public class AvailableCommands {
    private static final HashMap<String, Integer> COMMANDS_OBJ = new HashMap<>();
    static {
        COMMANDS_OBJ.put("help", 0);
        COMMANDS_OBJ.put("info", 0);
        COMMANDS_OBJ.put("show", 0);
        COMMANDS_OBJ.put("add", 1);
        COMMANDS_OBJ.put("update", 1);
        COMMANDS_OBJ.put("remove_by_id", 0);
        COMMANDS_OBJ.put("clear", 0);
        COMMANDS_OBJ.put("insert_at", 1);
        COMMANDS_OBJ.put("add_if_min", 1);
        COMMANDS_OBJ.put("remove_greater", 1);
        COMMANDS_OBJ.put("average_of_minimal_point", 0);
        COMMANDS_OBJ.put("min_by_id", 0);
        COMMANDS_OBJ.put("print_field_ascending_minimal_point", 0);
    }

    public static Integer get(String command) {
        return COMMANDS_OBJ.get(command);
    }
}
