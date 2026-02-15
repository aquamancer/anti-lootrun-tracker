package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.regex.Pattern;

public class AntiLootrunTracker implements ClientModInitializer {
	public static final String MOD_ID = "anti-lootrun-tracker";
//	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int MOB_SEARCH_RADIUS = 12;

	public static ModConfig config;
	private static boolean inValidShard;

	// tick counters for performance (don't run every tick) or timing logistics
	private static int mobListDisableDurationTicks = 0;
	private static int shardUpdateCounter = 0;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigHolder<ModConfig> configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();
//		configHolder.registerSaveListener((c, modConfig) -> {
//			shardsEnabledIn = Pattern.compile(config.getShardsEnabledIn());
//			return ActionResult.SUCCESS;
//		});
//		shardsEnabledIn = Pattern.compile(config.getShardsEnabledIn());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mobListDisableDurationTicks > 0) {
				mobListDisableDurationTicks--;
			}
            if (shardUpdateCounter <= 0) {
                inValidShard = Pattern.matches(config.getShardsEnabledIn(), ShardInfo.getCurrentShard());
                shardUpdateCounter = 40;
            }
            shardUpdateCounter--;
        });
	}

	public static List<MobEntity> getMobsNearby(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			return List.of();
		}

		return client.world.getEntitiesByClass(
				MobEntity.class,
				new Box(pos).expand(AntiLootrunTracker.MOB_SEARCH_RADIUS),
				mob -> mob.isAlive() && config.getIgnoredMobs().stream().noneMatch(ignored -> ignored.equals(EntityType.getId(mob.getType()).getPath()))
		);
	}

	// temporarily disables action bar enumeration and rendering of mob list
	public static void disableMobList() {
		int ticks = config.getMobListDisableDuration();
		if (ticks > mobListDisableDurationTicks) {
			mobListDisableDurationTicks = ticks;
		}
	}

	private static boolean isModEnabled() {
		return config.isModEnabled() && inValidShard;
	}

	public static boolean shouldRenderMobList() {
		return isModEnabled() && config.isMobListEnabled() && mobListDisableDurationTicks <= 0;
	}

	public static boolean shouldRenderMobCountOnChest() {
		return isModEnabled() && config.renderNumber();
	}

	public static boolean shouldRecolorFreeChest() {
		return isModEnabled() && config.recolorChest();
	}
}