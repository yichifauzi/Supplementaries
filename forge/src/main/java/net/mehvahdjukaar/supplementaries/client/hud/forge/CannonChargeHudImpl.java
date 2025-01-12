package net.mehvahdjukaar.supplementaries.client.hud.forge;

import net.mehvahdjukaar.supplementaries.client.cannon.CannonChargeHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.violetmoon.quark.base.handler.WoodSetHandler;

public class CannonChargeHudImpl extends CannonChargeHud implements IGuiOverlay {

    public CannonChargeHudImpl() {
        super(Minecraft.getInstance());
    }

    @Override
    public void render(ForgeGui forgeGui, GuiGraphics graphics, float f, int i, int j) {
        render(graphics, f, i, j);
    }
}
