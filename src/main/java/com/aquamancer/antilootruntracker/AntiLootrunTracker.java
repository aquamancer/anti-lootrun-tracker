package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.config.ModConfig;
import com.aquamancer.antilootruntracker.moblist.MobListManager;
import com.aquamancer.antilootruntracker.poirespawn.PoiRespawnTracker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntiLootrunTracker implements ClientModInitializer {
	public static final String MOD_ID = "anti-lootrun-tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig config;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigHolder<ModConfig> configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MobScanner.onTick();
			ShardInfo.onTick();
			MobListManager.onTick();
			PoiRespawnTracker.onTick();
        });

		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, lines) -> {
			PoiRespawnTracker.renderTimersInTooltip(itemStack, tooltipContext, lines);
		});
	}

	public static boolean isModEnabled() {
		return config.isModEnabled() && ShardInfo.inValidShard();
	}

	public static boolean shouldRenderMobCountOnChest() {
		return isModEnabled() && config.renderNumber();
	}

	public static boolean shouldRecolorAllChests() {
		return isModEnabled() && config.recolorAllChests();
	}

	public static boolean shouldRecolorFreeChests() {
		return isModEnabled() && config.recolorFreeChests();
	}
}