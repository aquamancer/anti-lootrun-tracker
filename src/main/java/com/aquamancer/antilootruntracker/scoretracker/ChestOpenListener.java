package com.aquamancer.antilootruntracker.scoretracker;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.TranslatableTextContent;

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

        private final long timeCreated;

        private long timeMatched;

        private Match(OpenScreenS2CPacket screen, String screenName) {
            this.screen = screen;
            this.screenName = screenName;
            this.syncId = screen.getSyncId();
            this.timeCreated = System.currentTimeMillis();
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
            return;
        }
        String text = ((TranslatableTextContent) screen.getName().getContent()).getKey();
        if (text.equals("container.chest") || text.equals("container.chestDouble")) {
            match = new Match(screen, text);
        }
    }

    public static void onInventory(InventoryS2CPacket inv) {
        if (inv.getSyncId() == 0
                || match == null
                || match.isExpired()
                || match.inv != null
                || inv.getSyncId() != match.syncId
        ) {
            return;
        }
        match.inv = inv;
        match.container = inv.getContents().subList(0, inv.getContents().size() - 9 * 4);
    }

    // chest open animation used to get its location
    public static void onBlockEvent(BlockEventS2CPacket packet) {
        if (match == null || match.isExpired()) return;

        if (System.currentTimeMillis() - match.timeMatched < 20 && isAdjacentToMatch(packet)) {
            // then the match chest is a double chest,
            // and this packet is for match's other half
            LootingTracker.registerDoubleChest(match, packet);
            return;
        }

        if (match.inv == null || match.blockEvent != null || packet.getData() == 0) {
            return;
        }
        match.blockEvent = packet;
        match.timeMatched = System.currentTimeMillis();
        LootingTracker.onChestOpened(match);
    }


    private static boolean isAdjacentToMatch(BlockEventS2CPacket packet) {
        double tolerance = 0.01;
        return Math.abs(packet.getPos().getSquaredDistance(match.blockEvent.getPos()) - 1) < tolerance;
    }
}
