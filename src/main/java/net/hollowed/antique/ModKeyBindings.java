package net.hollowed.antique;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    // Keybindings
    public static KeyBinding showSatchel;
    public static KeyBinding swapStacks;

    public static void registerKeyBindings() {
        showSatchel = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antique.show_satchel",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.antique.keybinds"
        ));
        swapStacks = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antique.swap_stacks",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                "category.antique.keybinds"
        ));
    }

    public static void initialize() {
        registerKeyBindings();
    }
}
