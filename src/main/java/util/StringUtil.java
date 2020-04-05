package util;

public class StringUtil {

    public static String formatAndTruncate(final String name, int trucanteAt) {
        String returnValue = name;

        if(returnValue.length() > trucanteAt){
            returnValue = returnValue.substring(0, trucanteAt);
        }
        return String.format("%10s", returnValue);
    }
}
