package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.config.ModConfig;
import com.aquamancer.antilootruntracker.moblist.MobListManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AntiLootrunTracker implements ClientModInitializer {
	//	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String MOD_ID = "anti-lootrun-tracker";
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