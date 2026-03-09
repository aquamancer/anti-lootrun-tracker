package com.aquamancer.antilootruntracker.scoretracker;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public abstract class LootingListener {
    private static final Map<String, Map<BlockPos, List<ItemStack>>> seen = new HashMap<>();

    private static final Queue<OpenScreenS2CPacket> screenPackets = new PriorityQueue<>();

    public static void onTick() {
        // expire unclaimed screen packets
    }

    public static void onOpenScreen(OpenScreenS2CPacket packet) {

    }

    public static void onInventory(InventoryS2CPacket inv) {
        if (inv.getSyncId() == 0) {
            // 0 is always just the player inventory packet (not container/chest packet)
            return;
        }
        OpenScreenS2CPacket screen = screenPackets.peek();
        if (inv.getSyncId() < screen.getSyncId()) {
            
        }
    }

    public static void onBlockEvent(BlockEventS2CPacket packet) {

    }
}
