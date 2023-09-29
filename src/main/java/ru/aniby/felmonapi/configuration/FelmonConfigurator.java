package ru.aniby.felmonapi.configuration;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.aniby.felmonapi.category.FelmonComponent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FelmonConfigurator {
    @Getter
    private final @NotNull Path path;
    private FileConfiguration fileConfiguration;
    private final Class<?> configurationClass;
    public FelmonConfigurator(@NotNull String fileName, @NotNull JavaPlugin plugin, Class<?> configurationClass) {
        File dir = new File(plugin.getDataFolder().toURI());
        if (!dir.exists())
            dir.mkdirs();

        this.path = Path.of(plugin.getDataFolder().getPath(), fileName);
        this.fileConfiguration = ConfiguratorUtils.get(this.path);
        this.configurationClass = configurationClass;
    }

    private @NotNull String getStandardName(Class<?> _class) {
        String rootName = configurationClass.getCanonicalName();
        String canonicalName = _class.getCanonicalName();
        return canonicalName.equals(rootName)
                ? ""
                : canonicalName.replace(rootName + ".", "").toLowerCase(Locale.ROOT);
    }

    private void loadClass(Class<?> rootClass) {
        loadFields(rootClass);
        for (Class<?> _class : rootClass.getDeclaredClasses()) {
            loadClass(_class);
        }
    }

    private void loadFields(Class<?> rootClass)  {
        String rootName = getStandardName(rootClass);
        try {
            for (Field field : rootClass.getDeclaredFields()) {
                String path = rootName.isEmpty() ? field.getName() : rootName + "." + field.getName();
                path = ConfiguratorUtils.formatFieldName(path);

                Object value = null;
                if (field.getType().equals(HashMap.class) || field.getType().equals(Map.class)) {
                    ConfigurationSection section = fileConfiguration.getConfigurationSection(path);
                    if (section != null)
                        value = section.getValues(false);
                } else if (field.getType().equals(FelmonComponent.class)) {
                    String stringVal = fileConfiguration.getString(path, "");
                    value = new FelmonComponent(stringVal);
                } else {
                    value = fileConfiguration.get(path, null);
                }

                field.setAccessible(true);
                field.set(null, value);
            }
        } catch (IllegalAccessException ignored) {}
    }

    public void load() {
        loadClass(configurationClass);
    }

    private @NotNull List<String> saveClass(Class<?> rootClass) {
        List<String> list = new ArrayList<>();
        String rootName = getStandardName(rootClass);
        if (!rootName.isEmpty()) {
            int spaceLength = rootName.split("\\.").length - 1;
            String namespace = ConfiguratorUtils.tab.repeat(spaceLength) + rootClass.getSimpleName().toLowerCase() + ":";
            list.add(namespace);
        }
        list.addAll(saveFields(rootClass));
        for (Class<?> _class : rootClass.getDeclaredClasses()) {
            list.addAll(saveClass(_class));
        }
        return list;
    }

    private @NotNull List<String> saveFields(Class<?> rootClass)  {
        List<String> list = new ArrayList<>();
        String standardName = getStandardName(rootClass);
        int spaceLength = standardName.isEmpty() ? 0 : standardName.split("\\.").length;
        String spaces = spaceLength > 0 ? ConfiguratorUtils.tab.repeat(spaceLength) : "";
        for (Field field : rootClass.getDeclaredFields()) {
            field.setAccessible(true);
            String key = ConfiguratorUtils.formatFieldName(field.getName());
            String value = ConfiguratorUtils.formatValue(field, spaces);
            list.add(spaces + key + ": " + value);
        }
        return list;
    }

    public void saveDefault(boolean replace) {
        if (!replace) {
            if (!fileConfiguration.saveToString()
                    .replaceAll(" ", "")
                    .replaceAll("\n", "")
                    .replaceAll("\r", "")
                    .isEmpty()
            )
                return;
        }
        List<String> lines = saveClass(configurationClass);
        try {
            Files.write(getPath(), lines, StandardCharsets.UTF_8);
            this.fileConfiguration = ConfiguratorUtils.get(this.path);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
