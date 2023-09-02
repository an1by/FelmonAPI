package ru.aniby.felmonapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.*;

public class FelmonUtils {
    public static class Text {
        public static String standardName(String name) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public static @NotNull String formatForDiscord(@NotNull String text) {
            return text.replaceAll("_", "\\_");
        }
    }

    public static class Completer {
        public static @NotNull List<String> players(@Nullable String argument) {
            List<String> list = new ArrayList<>();
            if (argument != null) {
                for (Player player : Bukkit.getOnlinePlayers())
                    if (argument.isEmpty() || player.getName().startsWith(argument))
                        list.add(player.getName());
            }
            return list;
        }

        public static @NotNull List<String> time(@Nullable String argument) {
            List<String> list = new ArrayList<>();
            if (argument != null && argument.length() > 0 && Character.isDigit(
                    argument.charAt(argument.length() - 1)
            )) {
                for (char ch : new char[] {'d', 'h', 'm', 's'}) {
                    list.add(argument + ch);
                }
            } else {
                for(int i = 1; i <= 10; i++) {
                    list.add(String.valueOf(i));
                }
            }
            return list;
        }
    }

    public static class Time {
        public static final long day = 86400000L;
        public static final PeriodFormatter timeFormatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix("d")
                .appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m")
                .appendSeconds().appendSuffix("s")
                .toFormatter();

        public static long parseTime(String string) {
            return timeFormatter.parsePeriod(string).toStandardDuration().getStandardSeconds() * 1000L;
        }

        public static String toDisplay(long time) {
            var instance = java.time.Instant.ofEpochMilli(time);
            var zonedDateTime = java.time.ZonedDateTime.ofInstant(instance,java.time.ZoneId.of("Europe/Moscow"));
            var formatter = java.time.format.DateTimeFormatter.ofPattern("d.M.u HH:mm O");
            return zonedDateTime.format(formatter);
        }

        public static long currentTime() {
            return new Date().getTime();
        }
    }

    public static class Convert {
        public static @NotNull <T> List<T> rightList(@NotNull String string, @NotNull Class<T> _class) {
            string = string
                    .replaceAll("^\\[|]$", "") // [] removing
                    .replaceAll(", ", ","); // all types of ,
            List<String> splitted = new ArrayList<>(Arrays.stream(string.split(",")).toList());
            try {
                if (_class == int.class || _class == Integer.class) {
                    return new ArrayList<>(
                            (Collection<? extends T>) splitted.stream().map(Integer::parseInt).toList()
                    );
                }
                if (_class == double.class || _class == Double.class) {
                    return new ArrayList<>(
                            (Collection<? extends T>) splitted.stream().map(Double::parseDouble).toList()
                    );
                }
                if (_class == float.class || _class == Float.class) {
                    return new ArrayList<>(
                            (Collection<? extends T>) splitted.stream().map(Float::parseFloat).toList()
                    );
                }
                if (_class == String.class || _class == short.class || _class == Short.class) {
                    splitted.replaceAll(w -> w.substring(1, w.length() - 1));
                    return (ArrayList<T>) splitted;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
}
