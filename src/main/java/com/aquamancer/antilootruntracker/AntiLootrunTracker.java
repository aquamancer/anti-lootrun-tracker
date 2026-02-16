package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.config.ModConfig;
import com.aquamancer.antilootruntracker.moblist.MobDistanceList;
import com.aquamancer.antilootruntracker.moblist.MobListManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AntiLootrunTracker implements ClientModInitializer {
	public static final String MOD_ID = "anti-lootrun-tracker";
//	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int MOB_SEARCH_RADIUS = 12;
	public static ModConfig config;

	private static boolean inValidShard;
	private static final Map<BlockPos, List<MobEntity>> entityCache = new HashMap<>();
	private static MobDistanceList actionBarMobList;

	// tick counters for performance (don't run every tick) or timing logistics
	private static int shardUpdateCounter = 0;
	private static int entityCacheCounter = 0;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigHolder<ModConfig> configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shardUpdateCounter <= 0) {
                inValidShard = Pattern.matches(config.getShardsEnabledIn(), ShardInfo.getCurrentShard());
                shardUpdateCounter = 40;
            }
            shardUpdateCounter--;
			if (entityCacheCounter <= 0) {
				entityCache.clear();
				entityCacheCounter = config.getEntityScanInterval();
			}
			entityCacheCounter--;
			MobListManager.onTick();
        });
	}

	public static Stream<MobEntity> getMobsNearby(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			return Stream.of();
		}

		// only return values in the cache, except for newly rendered chests.
		// onInitializeClient() will clear the cache every n ticks, specified by the config
		// which effectively updates the cache
		return entityCache.computeIfAbsent(pos, (v) -> {
			return client.world.getEntitiesByClass(
					MobEntity.class,
					new Box(pos).expand(AntiLootrunTracker.MOB_SEARCH_RADIUS),
					mob -> {
						return mob.isAlive()
								&& config.getIgnoredBaseMobs().stream().noneMatch(ignored -> ignored.equalsIgnoreCase(EntityType.getId(mob.getType()).toString()))
								&& config.getIgnoredMobNames().stream().noneMatch(ignored -> ignored.equalsIgnoreCase(
										(mob.getCustomName() != null ? mob.getCustomName() : (mob.getDisplayName() != null ? mob.getDisplayName() : Text.empty())).getString()));
					}
			);
		}).stream().filter(LivingEntity::isAlive);
	}

	public static boolean isModEnabled() {
		return config.isModEnabled() && inValidShard;
	}

	public static boolean shouldRenderMobCountOnChest() {
		return isModEnabled() && config.renderNumber();
	}

	public static boolean shouldRecolorFreeChest() {
		return isModEnabled() && config.recolorChest();
	}
}