package com.nbs.nbsback.utils;

public class Utils {

    /**
     * Formats the pace as mm:ss/km.
     *
     * @param pace The pace in km/h.
     * @return The formatted pace as mm:ss/km.
     */
    public static String formatPace(double pace) {
        if (pace <= 0) {
            return "00:00/km";
        }

        double paceInMinutesPerKm = 60 / pace;
        int minutes = (int) paceInMinutesPerKm;
        int seconds = (int) ((paceInMinutesPerKm - minutes) * 60);

        return String.format("%02d:%02d", minutes, seconds);
    }
}