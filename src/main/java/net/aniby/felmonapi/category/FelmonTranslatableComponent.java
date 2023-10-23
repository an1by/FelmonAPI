package net.aniby.felmonapi.category;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class FelmonTranslatableComponent extends FelmonComponent {
    public FelmonTranslatableComponent(@NotNull String source) {
        super(source);
    }

    @Override
    public @NotNull TranslatableComponent getComponent() {
        return Component.translatable(this.getSource());
    }
}
