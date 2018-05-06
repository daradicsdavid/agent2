package application;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputWriter {

    private final String prefix;

    public OutputWriter(String prefix) {

        this.prefix = prefix;
    }

    public void print(String message, Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTime());
        stringBuilder.append(" ");
        stringBuilder.append(prefix);
        stringBuilder.append(" ");
        stringBuilder.append(message);
        System.out.println(String.format(stringBuilder.toString(), args));
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public static void staticPrint(String message, Object... args) {
        System.out.println(String.format(message, args));
    }
}
