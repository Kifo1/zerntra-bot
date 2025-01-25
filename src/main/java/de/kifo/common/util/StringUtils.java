package de.kifo.common.util;

public class StringUtils {

    public static String getTimeStringBySeconds(long seconds) {
        long minutes = seconds/60;
        long hours = minutes/60;
        minutes %= 60;
        seconds %= 60;

        return (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s";
    }
}
