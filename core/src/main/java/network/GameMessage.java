package network;

import java.util.Arrays;



public final class GameMessage {

    private final String type;
    private final String[] args;

    public GameMessage(String raw) {
        String[] parts = raw.trim().split(":");
        this.type = parts[0];
        this.args = (parts.length > 1) ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
    }

    public GameMessage(String type, String... args) {
        this.type = type;
        this.args = (args != null) ? args : new String[0];
    }

    public String getType() { return type; }
    public String[] getArgs() { return args; }
    public int argCount() { return args.length; }

    public String getArg(int index) {
        return (index >= 0 && index < args.length) ? args[index] : null;
    }

    public int getIntArg(int index, int defaultValue) {
        try {
            return Integer.parseInt(getArg(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloatArg(int index, float defaultValue) {
        try {
            return Float.parseFloat(getArg(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String toPacketString() {
        if (args.length == 0) return type;
        return type + ":" + String.join(":", args);
    }

    @Override
    public String toString() { return toPacketString(); }

    public static GameMessage of(String type) {
        return new GameMessage(type);
    }

    public static GameMessage of(String type, String arg) {
        return new GameMessage(type, arg);
    }

    public static GameMessage of(String type, int arg) {
        return new GameMessage(type, String.valueOf(arg));
    }

    public static GameMessage of(String type, float arg) {
        return new GameMessage(type, String.valueOf(arg));
    }
}
