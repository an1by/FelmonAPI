package ru.aniby.felmonapi.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.aniby.felmonapi.category.FelmonComponent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

public class ConfiguratorUtils {
    public static String tab = "    ";
    public static YamlConfiguration get(@NotNull Path path) {
        return get(path.toFile());
    }
    public static YamlConfiguration get(@NotNull String name, JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), name);
        return get(file);
    }

    public static YamlConfiguration get(@NotNull File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
//            plugin.saveResource(file.getName(), true);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static @NotNull String formatFieldName(@NotNull String name) {
        name = name.replaceAll("([A-Z])", "_$1").toLowerCase(Locale.ROOT);
        if (name.charAt(0) == '_')
            name = name.substring(1);
        return name;
    }

    public static @NotNull String formatValue(@Nullable Object value) {
        if (value == null)
            return "";

        if (value instanceof List<?> list) {
            return Arrays.toString(list.stream().map(ConfiguratorUtils::formatValue).toArray());
        }

        String valueString = "";
        if (value instanceof FelmonComponent) {
            valueString = "\"" + ((FelmonComponent) value).getText() + "\"";
        } else if (value instanceof String || value instanceof Short || value instanceof Character) {
            valueString = "\"" + value + "\"";
        } else valueString += value;
        return valueString;
    }

    public static @NotNull String formatValue(@NotNull Field field, @Nullable String prefix) {
        if (prefix == null || prefix.isEmpty())
            prefix = "";
        else
            prefix = "\n" + tab + prefix;
        try {
            Object value = field.get(null);
            StringBuilder valueString = new StringBuilder();
            if (field.getType().equals(List.class)) {
                prefix += "- ";
                if (value == null)
                    return prefix;
                List<?> list = (List<?>) value;
                if (list.isEmpty())
                    return prefix;
                for (Object o : list) {
                    valueString.append(prefix).append(formatValue(o));
                }
                return valueString.toString();
            }
            if (field.getType().equals(Map.class) || field.getType().equals(HashMap.class)) {
                Map<String, Object> map = (Map<String, Object>) field.get(null);
                if (map != null && !map.isEmpty()) {
                    for (String key : map.keySet()) {
                        Object val = map.get(key);
                        valueString.append(prefix).append(key).append(": ").append(formatValue(val));
                    }
                    return valueString.toString();
                } else {
                    return prefix + "mapKey: \"mapValue\"";
                }
            }

            if (value == null)
                value = "";

            if (field.getType().equals(FelmonComponent.class)) {
                valueString.append("\"" + ((FelmonComponent) value).getSource() + "\"");
            } else if (field.getType().equals(String.class) || field.getType().equals(Short.class) || field.getType().equals(short.class) || field.getType().equals(Character.class) || field.getType().equals(char.class)) {
                valueString = new StringBuilder("\"" + value + "\"");
            } else valueString.append(value);
            return valueString.toString();
        } catch (IllegalAccessException ignored) {
            return "";
        }
    }
}
