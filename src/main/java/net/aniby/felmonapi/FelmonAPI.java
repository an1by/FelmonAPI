package net.aniby.felmonapi;

import org.bukkit.plugin.java.JavaPlugin;
import net.aniby.felmonapi.database.MySQL;

public final class FelmonAPI extends JavaPlugin {
    @Override
    public void onEnable() {
        MySQL.loadDriver();
    }

    @Override
    public void onDisable() {}
}
