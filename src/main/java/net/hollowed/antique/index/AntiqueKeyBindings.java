package net.hollowed.antique.index;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.hollowed.antique.Antiquities;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AntiqueKeyBindings {

    public static KeyBinding.Category ANTIQUE = KeyBinding.Category.create(Antiquities.id("category.antique.keybinds"));

    // Keybindings
    public static KeyBinding showSatchel;
    public static KeyBinding crawl;

    public static void registerKeyBindings() {
        showSatchel = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antique.show_satchel",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                ANTIQUE
        ));
        crawl = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antique.crawl",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                ANTIQUE
        ));
    }

    public static void initialize() {
        registerKeyBindings();
    }
}
