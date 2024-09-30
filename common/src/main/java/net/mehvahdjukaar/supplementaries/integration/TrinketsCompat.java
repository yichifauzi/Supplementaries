package net.mehvahdjukaar.supplementaries.integration;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.mehvahdjukaar.supplementaries.common.block.IKeyLockable;
import net.mehvahdjukaar.supplementaries.common.block.tiles.KeyLockableTile;
import net.mehvahdjukaar.supplementaries.common.items.KeyItem;
import net.mehvahdjukaar.supplementaries.common.utils.SlotReference;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.mehvahdjukaar.supplementaries.reg.ModTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//this is actually for trinkets
public class TrinketsCompat {

    static KeyLockableTile.KeyStatus getKey(Player player, String password) {

        TrinketComponent trinket = TrinketsApi.getTrinketComponent(player).orElse(null);
        if (trinket != null) {
            var found = trinket.getEquipped(i ->
                    i.is(ModTags.KEYS) || i.getItem() instanceof KeyItem);
            if (found.isEmpty()) return KeyLockableTile.KeyStatus.NO_KEY;
            for (var slot : found) {
                ItemStack stack = slot.getB();
                if (IKeyLockable.getKeyStatus(stack, password).isCorrect()) {
                    return KeyLockableTile.KeyStatus.CORRECT_KEY;
                }
            }
            return KeyLockableTile.KeyStatus.INCORRECT_KEY;
        }
        return IKeyLockable.KeyStatus.NO_KEY;
    }

    static SlotReference getQuiver(Player player) {
        TrinketComponent trinket = TrinketsApi.getTrinketComponent(player).orElse(null);
        if (trinket != null) {
            var found = trinket.getEquipped(ModRegistry.QUIVER_ITEM.get());
            if (!found.isEmpty()) {
                return Trinket.of(found.getFirst().getA(), trinket);
            }
        }
        return SlotReference.EMPTY;
    }

    public record Trinket(String name1, String name2, int id) implements SlotReference {

        public static final StreamCodec<RegistryFriendlyByteBuf, Trinket> CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Trinket::name1,
                ByteBufCodecs.STRING_UTF8, Trinket::name2,
                ByteBufCodecs.INT, Trinket::id,
                Trinket::new
        );

        public static Trinket of(dev.emi.trinkets.api.SlotReference ref, TrinketComponent comp) {
            int id = ref.index();
            TrinketInventory inv = ref.inventory();
            for (var g : comp.getInventory().entrySet()) {
                String first = g.getKey();
                for (var s : g.getValue().entrySet()) {
                    String second = s.getKey();
                    if (s.getValue() == inv) {
                        return new Trinket(first, second, id);
                    }
                }
            }
            throw new IllegalStateException("Trinket inventory not found. How?");
        }

        @Override
        public ItemStack get(LivingEntity player) {
            TrinketComponent trinket = TrinketsApi.getTrinketComponent(player).orElse(null);
            if (trinket != null) {
                var i = trinket.getInventory().get(name1);
                if (i != null) {
                    var inv = i.get(name2);
                    if (inv != null) {
                        return inv.getItem(id);
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends SlotReference> getCodec() {
            return CODEC;
        }
    }

    static {
        SlotReference.REGISTRY.register("trinket", Trinket.CODEC);
    }
}
