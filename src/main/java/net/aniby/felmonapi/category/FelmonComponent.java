package net.aniby.felmonapi.category;

import net.aniby.felmonapi.FelmonUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class FelmonComponent {
    private final @NotNull String source;

    public String getSource() {
        return source;
    }

    public FelmonComponent(@NotNull String source) {
        this.source = source;
    }

    public @NotNull Component getComponent() {
        return FelmonUtils.Text.deserialize(this.source);
    }

    public @NotNull String getText() {
        return MiniMessage.miniMessage().serialize(
                this.getComponent()
        ).replaceAll("<[^>]*>", "");
    }
}
