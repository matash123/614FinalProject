package src.config;

import java.awt.Color;

public class Theme {

    public final String name;

    public final Color bg;
    public final Color fg;
    public final Color inputBg;
    public final Color inputFg;
    public final Color buttonBg;
    public final Color buttonFg;
    public final Color errorFg;

    public Theme(String name,
                 Color bg, Color fg, Color inputBg, Color inputFg,
                 Color buttonBg, Color buttonFg, Color errorFg) {

        this.name = name;
        this.bg = bg;
        this.fg = fg;
        this.inputBg = inputBg;
        this.inputFg = inputFg;
        this.buttonBg = buttonBg;
        this.buttonFg = buttonFg;
        this.errorFg = errorFg;
    }

    public void printTheme() {
        System.out.println("Theme: " + name);
        System.out.println("bg=" + bg);
        System.out.println("fg=" + fg);
        System.out.println("inputBg=" + inputBg);
        System.out.println("inputFg=" + inputFg);
        System.out.println("buttonBg=" + buttonBg);
        System.out.println("buttonFg=" + buttonFg);
        System.out.println("errorFg=" + errorFg);
    }

public static final Theme LIGHT = new Theme(
    "LIGHT",
    new Color(245,245,245),     // bg
    new Color(20,20,20),        // fg
    Color.WHITE,                // inputBg
    Color.BLACK,                // inputFg

    new Color(225,225,225),     // buttonBg  (light grey)
    new Color(20,20,20),        // buttonFg  (dark text)

    new Color(180,0,0)          // errorFg
);

public static final Theme DARK = new Theme(
    "DARK",
    new Color(32,32,32),        // bg
    new Color(245,245,245),     // fg (slightly brighter for better contrast)
    new Color(55,55,55),        // inputBg
    new Color(245,245,245),     // inputFg (brighter text in inputs)

    new Color(70,70,70),        // buttonBg  (soft dark grey)
    new Color(250,250,250),     // buttonFg  (very light text for clarity)

    new Color(255,80,80)        // errorFg
);
}
