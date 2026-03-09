package com.aquamancer.antilootruntracker.scoretracker;

import com.aquamancer.antilootruntracker.ShardInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;

public class ScoreTracker {
    private static final int MAX_BANKED_CHESTS = 4;
    private static final int RING_MOB_COST = 16;
    private static final int RING_SPAWNER_COST = 8;
    private static final int ISLES_MOB_COST = 5;
    private static final int ISLES_SPAWNER_COST = 2;
    private static final int VALLEY_MOB_COST = 4;
    private static final int VALLEY_SPAWNER_COST = 2;


    private static int mobScore;
    private static int spawnerScore;

    public static void onTick(MinecraftClient client) {
        if (client.player != null && client.currentScreen instanceof GenericContainerScreen) {
//            client.player.sendMessage(Text.of(client.currentScreen.toString()));
        }
    }

    public static void onEntityDeath(LivingEntity entity, DamageSource lastDamageSource) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (entity == null || lastDamageSource == null || client.player == null) {
            return;
        }
        if (lastDamageSource.getAttacker() != client.player || !ShardInfo.inLootrunProtectedShard()) {
            return;
        }

        mobScore++;
    }
}
