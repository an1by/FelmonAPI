package ru.aniby.felmonapi.category;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public record FelmonComponent(@Getter @NotNull String source) {
    public @NotNull Component getComponent() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(this.source);
    }

    public @NotNull String getText() {
        return MiniMessage.miniMessage().serialize(
                this.getComponent()
        ).replaceAll("<[^>]*>", "");
    }
}
