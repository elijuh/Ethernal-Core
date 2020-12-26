package me.elijuh.core.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class ChatUtil {
    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String getState(boolean state) {
        return color(state ? "&aEnabled" : "&cDisabled");
    }

    public String clean(String s) {
        return (s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).replace("_", " ");
    }

    public String toLines(String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(i == 0 ? "" : "\n").append(color(lines[i]));
        }

        return builder.toString();
    }

    public String formatMillis(long millis) {
        long seconds = millis / 1000;
        long days = 0, hours = 0, minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        String format = pluralize(days, "Day", ", ")
                + pluralize(hours, "Hour", ", ")
                + pluralize(minutes, "Minute", ", ");
        return format.isEmpty() ? "" : format.substring(0, format.length() - 2);
    }

    private String pluralize(long amount, String name, String... extra) {
        StringBuilder format = new StringBuilder();
        if (amount == 1) {
            format.append(amount).append(" ").append(name);
        } else {
            format.append(amount > 0 ? amount + " " + name + "s" : "");
        }
        if (!format.toString().isEmpty()) {
            for (String s : extra) {
                format.append(s);
            }
        }
        return format.toString();
    }
}
