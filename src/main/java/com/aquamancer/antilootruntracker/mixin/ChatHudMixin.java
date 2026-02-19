package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.ShardInfo;
import com.aquamancer.antilootruntracker.PoiRespawnTracker;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    private static final int FORMATTING_GOLD_COLOR = 16755200;
    private static final Pattern CONQUER_REGEX = Pattern.compile("(?<poi>.+) has been conquered! It will respawn in (?<minutes>\\d+) minutes.*");

    @ModifyVariable(at = @At("LOAD"), method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V", name="message", ordinal=0, argsOnly = true)
    public Text addMessage(Text message) {
        if (message == null) {
            return null;
        }

        Style style = message.getStyle();
        String rawText = message.getString();
        if (!style.isBold() || style.getColor() == null || style.getColor().getRgb() != FORMATTING_GOLD_COLOR) {
            return message;
        }
        if (rawText == null) {
            return message;
        }
        Matcher matcher = CONQUER_REGEX.matcher(rawText);
        if (!matcher.matches()) {
            return message;
        }

        try {
            PoiRespawnTracker.addPoi(matcher.group("poi"), Integer.parseInt(matcher.group("minutes")), ShardInfo.getCurrentShard());
        } catch (RuntimeException ex) {
            AntiLootrunTracker.LOGGER.error("Parsing conquer message failed", ex);
        }

        if (!AntiLootrunTracker.config.appendShardToConquerMessage()) {
            return message;
        }
        MutableText newMessage = message.copy();
        MutableText appendMessage = Text.literal(" (").append(ShardInfo.getCurrentShard()).append(")").formatted(Formatting.ITALIC, Formatting.AQUA);
        newMessage.append(appendMessage);
        return newMessage;
    }
}
