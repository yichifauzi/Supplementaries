package net.mehvahdjukaar.supplementaries.client.renderers.color;

import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

public class BrewingStandColor implements BlockColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tint) {
        if(world!=null&&pos!=null) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof BrewingStandBlockEntity) {
                ItemStack item = ((Container) te).getItem(tint);
                if(!item.isEmpty()){
                    if(!ClientConfigs.cached.COLORED_BWERING_STAND)return 0xff3434;
                    return PotionUtils.getColor(item);
                }
            }
        }
        return 0xffffff;
    }
}
