package com.aquamancer.antilootruntracker.scoretracker;

import com.aquamancer.antilootruntracker.ShardTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.World;

import java.util.List;

public class ChestOpenListener {
    static class Match {
        private static final long MATCH_WINDOW_MILLIS = 100L;

        final OpenScreenS2CPacket screen;
        final String screenName;
        InventoryS2CPacket inv;
        List<ItemStack> container;
        int syncId;
        BlockEventS2CPacket blockEvent;
        final World world;

        private final long timeCreated;

        private Match(OpenScreenS2CPacket screen, String screenName, World world) {
            this.screen = screen;
            this.screenName = screenName;
            this.syncId = screen.getSyncId();
            this.timeCreated = System.currentTimeMillis();
            this.world = world;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > timeCreated + MATCH_WINDOW_MILLIS;
        }
    }

    private static Match match;

    public static void onOpenScreen(OpenScreenS2CPacket screen) {
        if (screen == null || screen.getSyncId() == 0 || screen.getName() == null) {
            return;
        }
        if (!(screen.getName().getContent() instanceof TranslatableTextContent)) {
            // filter for containers with generic (vanilla) names,
            // which are names that will be translated by the client
            // e.g. container.chest -> Chest, container.chestDouble -> Large Chest
            // this filters out chests with custom names, which are likely not lootrun protected
            return;
        }
        String text = ((TranslatableTextContent) screen.getName().getContent()).getKey();
        if (text.equals("container.chest") || text.equals("container.chestDouble")) {
            match = new Match(screen, text, MinecraftClient.getInstance().world);
        }
    }

    public static void onInventory(InventoryS2CPacket inv) {
        if (inv.getSyncId() == 0
                || match == null
                || match.isExpired()
                || match.inv != null
                || inv.getSyncId() != match.syncId
                || match.world != MinecraftClient.getInstance().world
        ) {
            return;
        }
        match.inv = inv;
        match.container = inv.getContents().subList(0, inv.getContents().size() - 9 * 4);
    }

    // chest open animation used to get its location
    public static void onBlockEvent(BlockEventS2CPacket packet) {
        if (match == null || match.isExpired() || match.world != MinecraftClient.getInstance().world) return;
        if (match.inv == null || match.blockEvent != null || packet.getData() == 0) return;

        match.blockEvent = packet;
        LootingTracker.onChestOpened(match, ShardTracker.getCurrentShard());
    }


    private static boolean isAdjacentToMatch(BlockEventS2CPacket packet) {
        double tolerance = 0.01;
        return Math.abs(packet.getPos().getSquaredDistance(match.blockEvent.getPos()) - 1) < tolerance;
    }
}
