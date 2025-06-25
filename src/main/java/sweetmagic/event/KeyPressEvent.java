package sweetmagic.event;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.MouseScrollingEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IWand;
import sweetmagic.handler.PacketHandler;
import sweetmagic.key.SMKeybind;
import sweetmagic.packet.KeyPressPKT;
import sweetmagic.packet.MouseSclorPKT;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class KeyPressEvent {

	private static ImmutableBiMap<KeyMapping, SMKeybind> keyMap = ImmutableBiMap.of();
	private static ImmutableBiMap<SMKeybind, KeyMapping> bindMap = ImmutableBiMap.of();

	@SubscribeEvent
	public static void keyPress(TickEvent.ClientTickEvent event) {
		for (KeyMapping k : keyMap.keySet()) {
			while (k.consumeClick()) {
				PacketHandler.sendToServer(new KeyPressPKT(keyMap.get(k)));
			}
		}
	}

	public static void registerKeybind(RegisterKeyMappingsEvent event) {
		ImmutableBiMap.Builder<KeyMapping, SMKeybind> build = ImmutableBiMap.builder();
		addKeyBinding(build, SMKeybind.OPEN, KeyModifier.NONE, GLFW.GLFW_KEY_H);
		addKeyBinding(build, SMKeybind.NEXT, KeyModifier.NONE, GLFW.GLFW_KEY_N);
		addKeyBinding(build, SMKeybind.BACK, KeyModifier.NONE, GLFW.GLFW_KEY_B);
		addKeyBinding(build, SMKeybind.POUCH, KeyModifier.NONE, GLFW.GLFW_KEY_P);
		addKeyBinding(build, SMKeybind.SPECIAL, KeyModifier.NONE, GLFW.GLFW_KEY_G);
		keyMap = build.build();
		bindMap = keyMap.inverse();
		keyMap.keySet().forEach(event::register);
	}

	private static void addKeyBinding(ImmutableBiMap.Builder<KeyMapping, SMKeybind> build, SMKeybind keyBind, KeyModifier mod, int keyCode) {
		build.put(new KeyMapping(keyBind.getName(), KeyConflictContext.IN_GAME, mod, InputConstants.Type.KEYSYM, keyCode, "key.sweetmagic.modname"), keyBind);
	}

	public static MutableComponent getKeyName(SMKeybind key) {
		return bindMap.containsKey(key) ? bindMap.get(key).getTranslatedKeyMessage().plainCopy() : Component.translatable("");
	}

	// マウススクロールイベント
	@SubscribeEvent
	public static void onMouseEvent(MouseScrollingEvent event) {

		// スペクターモードなら終了
		LocalPlayer player = Minecraft.getInstance().player;
		if (player.isSpectator()) { return; }

		// 杖を持っていないなら終了
		ItemStack stack = IWand.getWand(player);
		if (stack.isEmpty() || !(stack.getItem() instanceof IWand)) { return; }

		double scroll = event.getScrollDelta();

		if (scroll != 0 && player.isShiftKeyDown()) {
			PacketHandler.sendToServer(new MouseSclorPKT(scroll > 0));
			event.setCanceled(true);
		}
	}
}
