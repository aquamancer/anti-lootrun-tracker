package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.config.ModConfig;
import com.aquamancer.antilootruntracker.moblist.MobListManager;
import com.aquamancer.antilootruntracker.poirespawn.PoiRespawnTracker;
import com.aquamancer.antilootruntracker.scoretracker.LootingTracker;
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

		PoiRespawnTracker.init();
		ShardTracker.init();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			WorldChangeTracker.onTick(client);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MobScanner.onTick();
			ShardTracker.onTick();
			MobListManager.onTick();
			PoiRespawnTracker.onTick(client);
//			if (client.player != null) {
//				client.player.sendMessage(Text.literal("End of tick--"));
//			}
        });

		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, lines) -> {
			PoiRespawnTracker.renderTimersInTooltip(itemStack, lines);
		});
	}
}