package me.elijuh.core.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public double roundTo(double value, int decimals) {
        double divisor = Math.pow(10, decimals);
        return Math.round(value * divisor) / divisor;
    }

    public long parseDate(String input) throws NumberFormatException {
        long length = 0;
        long amount = Integer.parseInt(input.substring(0, input.length() - 1));
        switch (input.toLowerCase().toCharArray()[input.length() - 1]) {
            case 'y': {
                return amount * 31536000000L;
            }
            case 'm': {
                return amount * 2592000000L;
            }
            case 'w': {
                return amount * 604800000L;
            }
            case 'd': {
                return amount * 86400000L;
            }
            case 'h': {
                return amount * 3600000L;
            }
        }

        return length;
    }
}
