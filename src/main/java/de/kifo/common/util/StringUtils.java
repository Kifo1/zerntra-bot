package de.kifo.common.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class StringUtils {

    public static String getTimeStringBySeconds(long seconds) {
        long minutes = seconds/60;
        long hours = minutes/60;
        minutes %= 60;
        seconds %= 60;

        return (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s";
    }

    public static String getProgressBar(long current, long total, int size) {
        double progress = (double) current / total;
        int pos = (int) (progress * size);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < size; i++) {
            bar.append(i == pos ? "🔵" : "▬");
        }

        long curMinutes = MILLISECONDS.toMinutes(current);
        long curSeconds = MILLISECONDS.toSeconds(current) % 60;
        long totalMinutes = MILLISECONDS.toMinutes(total);
        long totalSeconds = MILLISECONDS.toSeconds(total) % 60;

        bar.append(String.format(" %d:%02d / %d:%02d", curMinutes, curSeconds, totalMinutes, totalSeconds));
        return bar.toString();
    }
}
