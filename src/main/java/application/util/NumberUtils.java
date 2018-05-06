package application.util;


import static application.OutputWriter.staticPrint;

public class NumberUtils {

    public static int toNumber(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            staticPrint("%s nem szám!", string);
            throw e;
        }
    }
}
