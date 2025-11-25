package net.hollowed.antique.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public record MyriadToolComponent(ItemStack toolBit, Identifier clothType, Identifier clothPattern, int clothColor, int patternColor) implements TooltipAppender {

    public static final Codec<MyriadToolComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("tool_bit").forGetter(MyriadToolComponent::toolBit),
            Identifier.CODEC.fieldOf("cloth_type").forGetter(MyriadToolComponent::clothType),
            Identifier.CODEC.fieldOf("cloth_pattern").forGetter(MyriadToolComponent::clothPattern),
            Codec.INT.fieldOf("cloth_color").forGetter(MyriadToolComponent::clothColor),
            Codec.INT.fieldOf("pattern_color").forGetter(MyriadToolComponent::patternColor)
    ).apply(instance, MyriadToolComponent::new));


    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {

    }
}