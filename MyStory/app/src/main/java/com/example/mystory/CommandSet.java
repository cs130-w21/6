package com.example.mystory;

import java.util.HashMap;
import java.util.Map;

public class CommandSet {
    public static Map<String, Command> mCommand = new HashMap<String, Command>();

    public static void addCommand(String commandName, Command command) {
        mCommand.put(commandName, command);
    }

    public static void removeCommand(String commandName) {
        mCommand.remove(commandName);
    }

    public static void trigger(String commandName) {
        try {
            mCommand.get(commandName).myExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
