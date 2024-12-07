package net.hollowed.antique;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    //Keybindings
    public static KeyBinding satchelBinding;

    public static void registerKeyBindings() {
        satchelBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.combatamenities.backslot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.combatamenities.keybinds"
        ));
    }

    public static void initialize() {
        registerKeyBindings();
    }
}
