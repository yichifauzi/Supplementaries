package net.mehvahdjukaar.supplementaries.client.renderers.color;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.client.renderer.BiomeColors;

public class DefaultWaterColor implements ItemColor, BlockColor{

    @Override
    public int getColor(ItemStack stack, int color) {
        return 0x3F76E4;
    }

    @Override
    public int getColor(BlockState state, BlockAndTintGetter reader, BlockPos pos, int color) {
        return reader != null && pos != null ? BiomeColors.getAverageWaterColor(reader, pos) : -1;
    }
}