package com.chestdrop.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    @Shadow protected abstract <T extends net.minecraft.client.gui.Element
            & net.minecraft.client.gui.Selectable
            & net.minecraft.client.gui.Drawable> T addDrawableChild(T drawableElement);

    @Shadow protected net.minecraft.screen.ScreenHandler handler;

    @Inject(method = "init", at = @At("TAIL"))
    private void chestDrop$addDropAllButton(CallbackInfo ci) {
        if (!(handler instanceof GenericContainerScreenHandler)) {
            return;
        }

        int buttonX = x + backgroundWidth + 8;
        int buttonY = y + 8;

        ButtonWidget button = ButtonWidget.builder(
                Text.literal("Drop All"),
                b -> chestDrop$dropAll()
        ).dimensions(buttonX, buttonY, 86, 20).build();

        addDrawableChild(button);
    }

    private void chestDrop$dropAll() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.interactionManager == null) {
            return;
        }

        if (!(handler instanceof GenericContainerScreenHandler container)) {
            return;
        }

        int containerSlots = container.getRows() * 9;

        for (int slot = 0; slot < containerSlots; slot++) {
            if (!container.getSlot(slot).getStack().isEmpty()) {
                client.interactionManager.clickSlot(
                        container.syncId,
                        slot,
                        1,
                        SlotActionType.THROW,
                        client.player
                );
            }
        }
    }
}
