package ru.aniby.felmonapi.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FelmonConfigurator {
    public static String tab = "   ";
    private final FileConfiguration fileConfiguration;
    private final Class<?> configurationClass;
    public FelmonConfigurator(FileConfiguration fileConfiguration, Class<?> configurationClass) {
        this.fileConfiguration = fileConfiguration;
        this.configurationClass = configurationClass;
    }
    public FelmonConfigurator(@NotNull String fileName, JavaPlugin plugin, Class<?> configurationClass) {
        this.fileConfiguration = get(fileName, plugin);
        this.configurationClass = configurationClass;
    }

    private @NotNull String getStandardName(Class<?> _class) {
        String rootName = configurationClass.getCanonicalName();
        String canonicalName = _class.getCanonicalName();
        return canonicalName.equals(rootName)
                ? ""
                : canonicalName.replace(rootName + ".", "").toLowerCase(Locale.ROOT);
    }

    private void foreachClassForLoad(Class<?> rootClass) {
        foreachFieldsForLoad(rootClass);
        for (Class<?> _class : rootClass.getDeclaredClasses()) {
            foreachClassForLoad(_class);
        }
    }

    private void foreachFieldsForLoad(Class<?> rootClass)  {
        String rootName = getStandardName(rootClass);
        try {
            for (Field field : rootClass.getDeclaredFields()) {
                String path = rootName + "." + field.getName();
                Object value = fileConfiguration.get(path, null);
                field.setAccessible(true);
                field.set(null, value);
            }
        } catch (IllegalAccessException ignored) {}
    }

    public void load() {
        foreachClassForLoad(configurationClass);
    }

    public static YamlConfiguration get(@NotNull String name, JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static @NotNull String formatFieldName(@NotNull String name) {
        name = name.replaceAll("([A-Z])", "_$1").toLowerCase(Locale.ROOT);
        if (name.charAt(0) == '_')
            name = name.substring(1);
        return name;
    }

    private @NotNull List<String> getClassMapAsList(Class<?> rootClass) {
        List<String> list = new ArrayList<>();
        String rootName = getStandardName(rootClass);
        if (!rootName.isEmpty()) {
            int spaceLength = rootName.split("\\.").length - 1;
            String namespace = tab.repeat(spaceLength) + rootClass.getSimpleName().toLowerCase() + ":";
            list.add(namespace);
        }
        list.addAll(getFieldMapAsList(rootClass));
        for (Class<?> _class : rootClass.getDeclaredClasses()) {
            list.addAll(getClassMapAsList(_class));
        }
        return list;
    }

    private @NotNull List<String> getFieldMapAsList(Class<?> rootClass)  {
        List<String> list = new ArrayList<>();
        int spaceLength = getStandardName(rootClass).split("\\.").length;
        String spaces = tab.repeat(spaceLength);
        try {
            for (Field field : rootClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(null);
                if (value == null)
                    value = "";
                String valueString = "";
                if (field.getType().equals(String.class) || field.getType().equals(Short.class) || field.getType().equals(short.class) || field.getType().equals(Character.class) || field.getType().equals(char.class)) {
                    valueString = "\"" + value + "\"";
                } else valueString += value;

                String key = formatFieldName(field.getName());
                list.add(spaces + key + ": " + valueString);
            }
        } catch (IllegalAccessException ignored) {}
        return list;
    }

    public void saveDefault(boolean replace) {
        if (!replace) {
            if (fileConfiguration.saveToString()
                    .replaceAll(" ", "")
                    .replaceAll("\n", "")
                    .replaceAll("\r", "")
                    .isEmpty()
            )
                return;
        }
        List<String> lines = getClassMapAsList(configurationClass);
        Path path = Path.of(fileConfiguration.getCurrentPath());
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
