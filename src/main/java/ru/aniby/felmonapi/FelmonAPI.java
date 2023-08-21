package ru.aniby.felmonapi;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.aniby.felmonapi.database.MySQL;

public final class FelmonAPI extends JavaPlugin {
    @Getter
    private static FelmonAPI instance;

    @Override
    public void onEnable() {
        instance = this;

        MySQL.loadDriver();
    }

    @Override
    public void onDisable() {}
}
