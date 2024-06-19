package com.j_ssh.view;

public enum Colors {
    //Color end string, color reset
    RESET("\033[0m"),

    // Regular Colors. Normal color, no bold, background color etc.
    BLACK("\033[0;30m"),    // BLACK
    RED("\033[0;31m"),      // RED
    GREEN("\033[0;32m"),    // GREEN
    YELLOW("\033[0;33m"),   // YELLOW
    BLUE("\033[0;34m"),     // BLUE
    MAGENTA("\033[0;35m"),  // MAGENTA
    CYAN("\033[0;36m"),     // CYAN
    WHITE("\033[0;37m"),    // WHITE

    // Bold
    BLACK_BOLD("\033[1;30m"),   // BLACK
    RED_BOLD("\033[1;31m"),     // RED
    GREEN_BOLD("\033[1;32m"),   // GREEN
    YELLOW_BOLD("\033[1;33m"),  // YELLOW
    BLUE_BOLD("\033[1;34m"),    // BLUE
    MAGENTA_BOLD("\033[1;35m"), // MAGENTA
    CYAN_BOLD("\033[1;36m"),    // CYAN
    WHITE_BOLD("\033[1;37m"),   // WHITE

    // Underline
    BLACK_UNDERLINED("\033[4;30m"),     // BLACK
    RED_UNDERLINED("\033[4;31m"),       // RED
    GREEN_UNDERLINED("\033[4;32m"),     // GREEN
    YELLOW_UNDERLINED("\033[4;33m"),    // YELLOW
    BLUE_UNDERLINED("\033[4;34m"),      // BLUE
    MAGENTA_UNDERLINED("\033[4;35m"),   // MAGENTA
    CYAN_UNDERLINED("\033[4;36m"),      // CYAN
    WHITE_UNDERLINED("\033[4;37m"),     // WHITE

    // Background
    BLACK_BACKGROUND("\033[40m"),   // BLACK
    RED_BACKGROUND("\033[41m"),     // RED
    GREEN_BACKGROUND("\033[42m"),   // GREEN
    YELLOW_BACKGROUND("\033[43m"),  // YELLOW
    BLUE_BACKGROUND("\033[44m"),    // BLUE
    MAGENTA_BACKGROUND("\033[45m"), // MAGENTA
    CYAN_BACKGROUND("\033[46m"),    // CYAN
    WHITE_BACKGROUND("\033[47m"),   // WHITE

    // High Intensity
    BLACK_BRIGHT("\033[0;90m"),     // BLACK
    RED_BRIGHT("\033[0;91m"),       // RED
    GREEN_BRIGHT("\033[0;92m"),     // GREEN
    YELLOW_BRIGHT("\033[0;93m"),    // YELLOW
    BLUE_BRIGHT("\033[0;94m"),      // BLUE
    MAGENTA_BRIGHT("\033[0;95m"),   // MAGENTA
    CYAN_BRIGHT("\033[0;96m"),      // CYAN
    WHITE_BRIGHT("\033[0;97m"),     // WHITE

    // Bold High Intensity
    BLACK_BOLD_BRIGHT("\033[1;90m"),    // BLACK
    RED_BOLD_BRIGHT("\033[1;91m"),      // RED
    GREEN_BOLD_BRIGHT("\033[1;92m"),    // GREEN
    YELLOW_BOLD_BRIGHT("\033[1;93m"),   // YELLOW
    BLUE_BOLD_BRIGHT("\033[1;94m"),     // BLUE
    MAGENTA_BOLD_BRIGHT("\033[1;95m"),  // MAGENTA
    CYAN_BOLD_BRIGHT("\033[1;96m"),     // CYAN
    WHITE_BOLD_BRIGHT("\033[1;97m"),    // WHITE

    // High Intensity backgrounds
    BLACK_BACKGROUND_BRIGHT("\033[0;100m"),     // BLACK
    RED_BACKGROUND_BRIGHT("\033[0;101m"),       // RED
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),     // GREEN
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),    // YELLOW
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),      // BLUE
    MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),   // MAGENTA
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),      // CYAN
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");     // WHITE

    private final String code;

    Colors(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static String getColorCode(Colors color) {
        switch (color) {
            case BLACK:
            case BLACK_BOLD:
            case BLACK_UNDERLINED:
            case BLACK_BACKGROUND:
            case BLACK_BRIGHT:
            case BLACK_BOLD_BRIGHT:
            case BLACK_BACKGROUND_BRIGHT:
                return "#000000";
            case RED:
            case RED_BOLD:
            case RED_UNDERLINED:
            case RED_BACKGROUND:
            case RED_BRIGHT:
            case RED_BOLD_BRIGHT:
            case RED_BACKGROUND_BRIGHT:
                return "#FF0000";
            case GREEN:
            case GREEN_BOLD:
            case GREEN_UNDERLINED:
            case GREEN_BACKGROUND:
            case GREEN_BRIGHT:
            case GREEN_BOLD_BRIGHT:
            case GREEN_BACKGROUND_BRIGHT:
                return "#00FF00";
            case YELLOW:
            case YELLOW_BOLD:
            case YELLOW_UNDERLINED:
            case YELLOW_BACKGROUND:
            case YELLOW_BRIGHT:
            case YELLOW_BOLD_BRIGHT:
            case YELLOW_BACKGROUND_BRIGHT:
                return "#FFFF00";
            case BLUE:
            case BLUE_BOLD:
            case BLUE_UNDERLINED:
            case BLUE_BACKGROUND:
            case BLUE_BRIGHT:
            case BLUE_BOLD_BRIGHT:
            case BLUE_BACKGROUND_BRIGHT:
                return "#0000FF";
            case MAGENTA:
            case MAGENTA_BOLD:
            case MAGENTA_UNDERLINED:
            case MAGENTA_BACKGROUND:
            case MAGENTA_BRIGHT:
            case MAGENTA_BOLD_BRIGHT:
            case MAGENTA_BACKGROUND_BRIGHT:
                return "#FF00FF";
            case CYAN:
            case CYAN_BOLD:
            case CYAN_UNDERLINED:
            case CYAN_BACKGROUND:
            case CYAN_BRIGHT:
            case CYAN_BOLD_BRIGHT:
            case CYAN_BACKGROUND_BRIGHT:
                return "#00FFFF";
            case WHITE:
            case WHITE_BOLD:
            case WHITE_UNDERLINED:
            case WHITE_BACKGROUND:
            case WHITE_BRIGHT:
            case WHITE_BOLD_BRIGHT:
            case WHITE_BACKGROUND_BRIGHT:
                return "#FFFFFF";
            case RESET:
            default:
                return "#000000";
        }
    }
}
