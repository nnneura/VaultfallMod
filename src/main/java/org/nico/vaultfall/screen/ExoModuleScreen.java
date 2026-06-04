package org.nico.vaultfall.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.Vaultfall;

public class ExoModuleScreen extends HandledScreen<ExoModuleScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(Vaultfall.MOD_ID, "textures/gui/exo_gui256.png");

    public ExoModuleScreen(ExoModuleScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        // Ajustar dimensiones de la GUI
        this.backgroundHeight = 176;
        this.backgroundWidth = 192;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 136;
    }

    @Override
    protected void init() {
        super.init();
        // Centrar la GUI en la pantalla
        this.x = (width - backgroundWidth) / 2;
        this.y = (height - backgroundHeight) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}