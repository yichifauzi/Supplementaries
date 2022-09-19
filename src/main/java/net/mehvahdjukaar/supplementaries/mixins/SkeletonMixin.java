package net.mehvahdjukaar.supplementaries.mixins;

import net.mehvahdjukaar.supplementaries.common.entities.IQuiverEntity;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Skeleton.class)
public abstract class SkeletonMixin extends AbstractSkeleton implements IQuiverEntity {
    //frick it going full mixin here. I could have used caps and spawn events...

    //server
    @Unique
    private ItemStack quiver = ItemStack.EMPTY;
    @Unique
    private float quiverDropChance = 0.6f;

    //for just used to sync this to client
    private static final EntityDataAccessor<Boolean> HAS_QUIVER =
            SynchedEntityData.defineId(Skeleton.class, EntityDataSerializers.BOOLEAN);

    protected SkeletonMixin(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(HAS_QUIVER, false);
    }

    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean hitByPlayer, CallbackInfo ci) {
        if (this.quiver != null && hitByPlayer) {
            ItemStack itemStack = this.quiver;
            if (Math.max(this.random.nextFloat() - (float) looting * 0.02F, 0.0F) < quiverDropChance) {
                this.spawnAtLocation(itemStack);
                this.quiver = ItemStack.EMPTY;
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (!this.quiver.isEmpty()) {
            compound.put("Quiver", quiver.save(new CompoundTag()));
            compound.putFloat("QuiverDropChance", quiverDropChance);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("Quiver")) {
            this.setQuiver(ItemStack.of(compound.getCompound("Quiver")));
            this.quiverDropChance = compound.getFloat("QuiverDropChance");
        }
    }

    @Override
    public ItemStack getQuiver() {
        return quiver;
    }

    @Override
    public boolean hasQuiver() {
        if (this.level != null && this.level.isClientSide) {
            return this.getEntityData().get(HAS_QUIVER);
        }
        return IQuiverEntity.super.hasQuiver();
    }

    @Override
    public void setQuiver(ItemStack quiver) {
        this.quiver = quiver;
        this.getEntityData().set(HAS_QUIVER, !quiver.isEmpty());
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        if (this.quiver == null && stack.getItem() == ModRegistry.QUIVER_ITEM.get()) return true;
        return super.wantsToPickUp(stack);
    }

    @Override
    public boolean equipItemIfPossible(ItemStack stack) {
        if (stack.getItem() == ModRegistry.QUIVER_ITEM.get()) {
            if (this.quiver != null) {
                this.spawnAtLocation(quiver);
            }
            this.setQuiver(stack);
            this.quiverDropChance = 1;
            return true;
        }
        return super.equipItemIfPossible(stack);
    }
}