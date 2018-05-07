package application;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.*;

public class OutputWriter {

    private final String prefix;

    public OutputWriter(String prefix) {

        this.prefix = prefix;
    }

    public void print(String message, Object... args) {
        String stringBuilder = getTime() + " " + prefix + " " + message;
        out.println(String.format(stringBuilder, args));
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public static void staticPrint(String message, Object... args) {
        out.println(String.format(message, args));
    }
}
