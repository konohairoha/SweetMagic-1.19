package sweetmagic.init.tile.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.info.PhoneInfo;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.ItemInit;
import sweetmagic.init.MenuInit;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileNotePC;
import sweetmagic.init.tile.sm.TileNotePC.TradeInfo;
import sweetmagic.packet.AddSPtoServerPKT;

public class PhoneMenu extends BaseItemMenu {

	public boolean isSingle = false;
	public List<List<TradeInfo>> tradeList = new ArrayList<>();
	public List<PlayerInfo> infoList;
	public Map<UUID, ICookingStatus> statusMap = new HashMap<>();
	private Map<UUID, Player> playerMap = new HashMap<>();
	public ICookingStatus cook;
	public ItemStack stack;
	public int sp = 0;

	public PhoneMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, pInv.player.getMainHandItem());
	}

	public PhoneMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.phoneMenu, windowId, pInv, new PhoneInfo(stack).getInv());
		this.addSlot(new SMSlot(this.inventory, 0, 132, 88));
		this.setPInv(pInv, 6, 110);
		Player player = pInv.player;
		ICookingStatus.sendPKT(player);
		boolean isClient = player.getLevel().isClientSide();
		this.cook = ICookingStatus.getState(player);
		this.infoList = isClient ? Minecraft.getInstance().getConnection().getOnlinePlayers().stream().filter(p -> !p.getProfile().getId().equals(player.getUUID())).toList() : new ArrayList<>();
		this.stack = player.getMainHandItem();
		this.tradeList = this.getTradeInfo();

		if(!isClient && player instanceof ServerPlayer server) {

			for (Entity entity : server.getLevel().players()) {
				if(!(entity instanceof Player pl) || pl == player) { continue; }
				this.playerMap.put(pl.getUUID(), pl);
				this.statusMap.put(pl.getUUID(), ICookingStatus.getState(pl));
			}
		}

		if(isClient && player instanceof LocalPlayer local) {

			for (Entity entity : local.clientLevel.entitiesForRendering()) {
				if(!(entity instanceof Player pl) || pl.getUUID() == player.getUUID()) { continue; }
				this.playerMap.put(pl.getUUID(), pl);
			}
		}

		this.isSingle = isClient ? this.infoList.isEmpty() : this.playerMap.isEmpty();

		if(this.isSingle) {
			this.sp = Math.max(1, this.sp);
		}
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

		if (slotId >= 36 || slotId <= -1) {
			super.clicked(slotId, dragType, clickType, player);
			return;
		}

		ItemStack stack = this.slots.get(slotId).getItem();
		if (stack.isEmpty() || !stack.is(player.getMainHandItem().getItem())) {
			super.clicked(slotId, dragType, clickType, player);
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		Level world = player.getLevel();

		if(!world.isClientSide()) {
			player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, world.getRandom().nextFloat() * 0.1F + 0.9F);
		}

		if(id == -1) { return true; }

		if(this.isSingle) {
			this.clickMenuSingleButton(player, id);
			return true;
		}

		switch(id) {
		case 0:
			this.sp = Math.min(Math.min(100000, this.cook.getTradeSP()), this.sp + 10);
			break;
		case 1:
			this.sp = Math.min(Math.min(100000, this.cook.getTradeSP()), this.sp + 100);
			break;
		case 2:
			this.sp = Math.min(Math.min(100000, this.cook.getTradeSP()), this.sp + 1000);
			break;
		case 3:
			this.sp = Math.max(0, this.sp - 10);
			break;
		case 4:
			this.sp = Math.max(0, this.sp - 100);
			break;
		case 5:
			this.sp = Math.max(0, this.sp - 1000);
			break;
		case 6:
			this.sendSP(player);
			break;
		case 7:
			this.sendItem(player);
			break;
		case 8:
			this.addButSale(player);
			break;
		}

		return true;
	}

	public void clickMenuSingleButton(Player player, int id) {

		switch(id) {
		case 0:
			this.sp = Math.min(64, this.sp + 1);
			break;
		case 1:
			this.sp = Math.min(64, this.sp + 10);
			break;
		case 2:
			this.sp = Math.min(64, this.sp + 64);
			break;
		case 3:
			this.sp = Math.max(1, this.sp - 1);
			break;
		case 4:
			this.sp = Math.max(1, this.sp - 10);
			break;
		case 5:
			this.sp = Math.max(1, this.sp - 64);
			break;
		case 6:
			this.itemButBuy(player);
			break;
		}
	}

	public void sendSP(Player player) {
		ItemStack stack = player.getMainHandItem();
		if(stack.isEmpty() || !player.getLevel().isClientSide()) { return; }

		CompoundTag tags = this.stack.getOrCreateTag();
		if(!tags.contains("userId") || !tags.getBoolean("isSelect")) { return; }

		Player target = this.playerMap.get(tags.getUUID("userId"));
		if(target == null) { return; }

		ICookingStatus targetCook = ICookingStatus.getState(target);
		if(targetCook == null) { return; }

		PacketHandler.sendToServer(new AddSPtoServerPKT(target.getUUID(), this.sp));
		PacketHandler.sendToServer(new AddSPtoServerPKT(player.getUUID(), -this.sp));
	}

	public void sendItem(Player player) {
		ItemStack stack = this.inventory.getInv().getStackInSlot(0);
		if(stack.isEmpty() || player.getLevel().isClientSide()) { return; }

		CompoundTag tags = this.stack.getOrCreateTag();
		if(!tags.contains("userId") || !tags.getBoolean("isSelect")) { return; }

		Player target = this.playerMap.get(tags.getUUID("userId"));
		if(player == null) { return; }

		Level world = target.getLevel();
		world.addFreshEntity(new ItemEntity(world, target.getX(), target.getY() + 0.5D, target.getZ(), stack.copy()));
		stack.shrink(stack.getCount());
		this.inventory.writeBack();
	}

	public void addButSale(Player player) {
		ItemStack stack = this.inventory.getInv().getStackInSlot(0);
		if(stack.isEmpty() || player.getLevel().isClientSide()) { return; }

		int value = TileNotePC.getGlobalValue(1F, stack);
		stack.shrink(stack.getCount());
		this.cook.addTradeExp(player.getLevel(), value, false);
		this.cook.addTradeSP(value);
		ICookingStatus.sendPKT(player);
		this.inventory.writeBack();
	}

	public void itemButBuy(Player player) {
		CompoundTag tags = this.stack.getOrCreateTag();
		if(!tags.contains("selectId") || tags.getBoolean("isSelect")) { return; }

		int selectId = tags.getInt("selectId");
		int id = selectId % 8;
		int tabId = selectId / 8;

		TradeInfo info = this.tradeList.get(tabId).get(id);
		int value = info.price() * this.sp;
		if(value > this.cook.getTradeSP()) { return; }

		ItemStack copy = info.stack().copy();
		copy.setCount(this.sp);
		Level world = player.getLevel();
		if(world.isClientSide()) { return; }

		world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY() + 0.5D, player.getZ(), copy));
		this.cook.addTradeExp(player.getLevel(), value, true);
		this.cook.addTradeSP(-value);
		ICookingStatus.sendPKT(player);

	}

	public List<List<TradeInfo>> getTradeInfo() {
		List<List<TradeInfo>> infoListList = new ArrayList<>();

		List<TradeInfo> infoList0 = new ArrayList<>();
		infoList0.add(TradeInfo.create(ItemInit.aether_crystal, 1200));
		infoList0.add(TradeInfo.create(ItemInit.alternative_ingot, 500));
		infoList0.add(TradeInfo.create(ItemInit.blank_magic, 50));
		infoList0.add(TradeInfo.create(ItemInit.blank_page, 50));
		infoList0.add(TradeInfo.create(ItemInit.mysterious_page, 200));
		infoList0.add(TradeInfo.create(ItemInit.magic_meal, 100));
		infoList0.add(TradeInfo.create(ItemInit.aether_recovery_book1, 2000));
		infoList0.add(TradeInfo.create(ItemInit.seed_bag, 2000));

		List<TradeInfo> infoList1 = new ArrayList<>();
		infoList1.add(TradeInfo.create(ItemInit.unmeltable_ice, 300));
		infoList1.add(TradeInfo.create(ItemInit.tiny_feather, 300));
		infoList1.add(TradeInfo.create(ItemInit.poison_bottle, 300));
		infoList1.add(TradeInfo.create(ItemInit.electronic_orb, 300));
		infoList1.add(TradeInfo.create(ItemInit.grav_powder, 300));
		infoList1.add(TradeInfo.create(ItemInit.stray_soul, 300));
		infoList1.add(TradeInfo.create(ItemInit.witch_tears, 750));
		infoList1.add(TradeInfo.create(ItemInit.prizmium, 600));

		infoListList.add(infoList0);
		infoListList.add(infoList1);
		return infoListList;
	}
}
