package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.LootingTracker;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at=@At("HEAD"), method="Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity instance;
        try {
            instance = (LivingEntity)(Object) this;
        } catch (ClassCastException ex) {
            return;
        }
        LootingTracker.onEntityDeath(instance, damageSource);
    }

    @Unique
    private static void prettyPrint(boolean death, DamageSource d, ClientPlayerEntity player) {
        if (death) {
            player.sendMessage(Text.literal("Died-------------"));
        } else {
            player.sendMessage(Text.literal("Damaged-------------"));
        }
        player.sendMessage(Text.literal("Attacker: " + d.getAttacker()));
        player.sendMessage(Text.literal("Name: " + d.getName()));
        player.sendMessage(Text.literal("Position: " + d.getPosition()));
        player.sendMessage(Text.literal("Source: " + d.getSource()));
        player.sendMessage(Text.literal("StoredPosition: " + d.getStoredPosition()));
        player.sendMessage(Text.literal("Type: " + d.getType()));
        player.sendMessage(Text.literal("RegistryEntry: " + d.getTypeRegistryEntry()));
        player.sendMessage(Text.literal("issourcecreativeplayer: " + d.isSourceCreativePlayer()));
    }
}
