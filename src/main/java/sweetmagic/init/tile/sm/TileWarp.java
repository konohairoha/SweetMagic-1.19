package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.item.magic.MFTeleport;
import sweetmagic.init.tile.menu.WarpMenu;

public class TileWarp extends TileAbstractSM {

	protected final StackHandler inputInv = new StackHandler(this.getInvSize()) {
		public int getSlotLimit(int slot) { return 1; }
	};

	public TileWarp(BlockPos pos, BlockState state) {
		super(TileInit.warpBlock, pos, state);
	}

	public void doTereport(Player player, int id) {
		ItemStack stack = this.getInputItem(id);
		if (stack.isEmpty() || !(stack.getItem() instanceof MFTeleport)) { return; }

		CompoundTag tags = stack.getTag();
		if (tags == null || !tags.contains("pX")) { return; }

		this.clickButton();
		this.playSound(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);

		BlockPos pos = new BlockPos(tags.getInt("pX") + 0.5F, tags.getInt("pY") + 1F, tags.getInt("pZ") + 0.5F);
		ResourceLocation dim = new ResourceLocation(tags.getString("dim"));

		if (this.getLevel() instanceof ServerLevel server) {

			double range = 0.875D;
			double ySpeed = -2D;

			for (int i= -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, player.blockPosition().above(1), i / 3D, ySpeed, 1D);
			}
		}

		if (player.getServer() != null && player instanceof ServerPlayer sp) {
			ServerLevel server = sp.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
			sp.teleportTo(server, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0, 0F);

			double range = 0.875D;
			double ySpeed = 1D;

			for (int i= -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, pos.below(2), i / 3D, ySpeed, 1D);
			}

			this.playSound(server, pos, SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		}

		player.fallDistance = 0F;
		player.giveExperiencePoints(0);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 4;
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInputList().isEmpty();
	}

	public List<ItemStack> getInputList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setInv(this.inputInv, tag, "inputInv");
	}

	public void setInv(StackHandler inv, CompoundTag tags, String name) {
		CompoundTag tag = tags.getCompound(name);
		if (tag == null) { return; }
		inv.deserializeNBT(tag);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new WarpMenu(windowId, inv, this);
	}
}
