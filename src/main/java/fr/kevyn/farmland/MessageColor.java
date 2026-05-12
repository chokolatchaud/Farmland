package fr.kevyn.farmland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum MessageColor {

	BLACK("<black>"),
    DARK_BLUE("<dark_blue>"),
    DARK_GREEN("<dark_green>"),
    DARK_AQUA("<dark_aqua>"),
    DARK_RED("<dark_red>"),
    DARK_PURPLE("<dark_purple>"),
    GOLD("<gold>"),
    GRAY("<gray>"),
    DARK_GRAY("<dark_gray>"),
    BLUE("<blue>"),
    GREEN("<green>"),
    AQUA("<aqua>"),
    RED("<red>"),
    LIGHT_PURPLE("<light_purple>"),
    YELLOW("<yellow>"),
    WHITE("<white>");

    private final String tag;

    MessageColor(String tag) {
        this.tag = tag;
    }

    public Component apply(String message) {
        return MiniMessage.miniMessage().deserialize(tag + message);
    }
}