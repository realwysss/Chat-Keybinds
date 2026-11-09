package dev.chkb;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Chkb implements ClientModInitializer {
	static final KeyMapping[] K = new KeyMapping[4];

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("chkb", "main"));
		for (int i = 0; i < 4; i++) K[i] = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.chkb.msg" + (i + 1), InputConstants.UNKNOWN.getValue(), cat));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			for (int i = 0; i < 4; i++) while (K[i].consumeClick()) {
				String t = Cfg.c.m[i];
				if (m.player == null || m.screen != null || t.isEmpty()) continue;
				if (t.startsWith("/")) m.player.connection.sendCommand(t.substring(1));
				else m.player.connection.sendChat(t);
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("chatkey")
				.executes(x -> {
					for (int i = 0; i < 4; i++) x.getSource().sendFeedback(Component.literal("key " + (i + 1) + ": " + (Cfg.c.m[i].isEmpty() ? "(empty)" : Cfg.c.m[i])));
					x.getSource().sendFeedback(Component.literal("set one with /chatkey <1-4> <message>, bind the keys in controls"));
					return 1;
				})
				.then(ClientCommandManager.argument("n", IntegerArgumentType.integer(1, 4))
						.executes(x -> {
							int n = IntegerArgumentType.getInteger(x, "n");
							Cfg.c.m[n - 1] = "";
							Cfg.sv();
							x.getSource().sendFeedback(Component.literal("key " + n + " cleared"));
							return 1;
						})
						.then(ClientCommandManager.argument("msg", StringArgumentType.greedyString()).executes(x -> {
							int n = IntegerArgumentType.getInteger(x, "n");
							Cfg.c.m[n - 1] = StringArgumentType.getString(x, "msg");
							Cfg.sv();
							x.getSource().sendFeedback(Component.literal("key " + n + " sends: " + Cfg.c.m[n - 1]));
							return 1;
						})))));
	}
}
