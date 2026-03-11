package com.aquamancer.antilootruntracker.scoretracker;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.TranslatableTextContent;

import java.util.List;

public abstract class ChestOpenListener {
    static class Match {
        private static final long MATCH_WINDOW_MILLIS = 100L;

        final OpenScreenS2CPacket screen;
        final String screenName;
        InventoryS2CPacket inv;
        List<ItemStack> container;
        int syncId;
        BlockEventS2CPacket blockEvent;

        private final long createdAt;
        private boolean expired = false;

        private Match(OpenScreenS2CPacket screen, String screenName) {
            this.screen = screen;
            this.screenName = screenName;
            this.syncId = screen.getSyncId();
            this.createdAt = System.currentTimeMillis();
        }

        private boolean isExpired() {
            if (expired) {
                return true;
            }
            return expired = (System.currentTimeMillis() > createdAt + MATCH_WINDOW_MILLIS);
        }
    }

    private static Match match;

    public static void onTick() {
        // expire unclaimed screen packets
    }

    public static void onOpenScreen(OpenScreenS2CPacket screen) {
        if (screen.getSyncId() == 0 || screen.getName() == null) {
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

    public static void onBlockEvent(BlockEventS2CPacket packet) {
        if (match == null
                || match.isExpired()
                || match.inv == null
                || match.blockEvent != null
                || packet.getData() == 0
        ) {
            return;
        }
        match.blockEvent = packet;
        ScoreTracker.onChestOpened(match);
        match.expired = true;
    }
}
