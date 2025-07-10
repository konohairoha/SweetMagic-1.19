package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.item.magic.MFTeleport;
import sweetmagic.init.tile.menu.TransferGateVerticalMenu;

public class TileTransferGateVertical extends TileAbstractSM {

	protected final StackHandler inputInv = new StackHandler(1);

	public TileTransferGateVertical(BlockPos pos, BlockState state) {
		super(TileInit.transferGateVertical, pos, state);
	}

	public TileTransferGateVertical(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if(this.rand.nextFloat() >= 0.67F || this.isAir()) { return; }

		Direction face = this.getFace();
		float x = 0.5F + pos.getX() + this.getRandFloat(1.65F);
		float y = pos.getY();
		float z = 0.5F + pos.getZ() + this.getRandFloat(1.65F);
		float xSpeed = 0F;
		float ySpeed = this.getRandFloat(0.05F);
		float zSpeed = 0F;

		switch (face) {
		case DOWN:
			y += 2F;
			ySpeed = -this.getRandFloat(0.25F) - 0.25F;
			break;
		case UP:
			y -= 0.5F;
			ySpeed = this.getRandFloat(0.25F) + 0.25F;
			break;
		}

		world.addParticle(ParticleInit.DIVINE, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	// 向きの取得
	public Direction getFace() {
		BlockState state = this.getState(this.getBlockPos());
		return !state.hasProperty(BlockStateProperties.FACING) ? Direction.NORTH : state.getValue(BlockStateProperties.FACING);
	}

	public boolean doTereport(LivingEntity entity) {
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof MFTeleport)) { return false; }

		CompoundTag tags = stack.getTag();
		if (tags == null || !tags.contains("pX")) { return false; }

		this.clickButton();
		this.playSound(entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		BlockPos pos = new BlockPos(tags.getInt("pX") + 0.5F, tags.getInt("pY") + 1F, tags.getInt("pZ") + 0.5F);
		ResourceLocation dim = new ResourceLocation(tags.getString("dim"));

		if (this.getLevel() instanceof ServerLevel server) {

			double range = 0.875D;
			double ySpeed = -2D;

			for (int i= -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, entity.blockPosition().above(1), i / 3D, ySpeed, 1D);
			}
		}

		if (entity.getServer() != null && entity instanceof ServerPlayer sp) {
			ServerLevel server = sp.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
			sp.teleportTo(server, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0, 0F);

			double range = 0.875D;
			double ySpeed = 1D;

			for (int i= -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, pos.below(2), i / 3D, ySpeed, 1D);
			}

			this.playSound(server, pos, SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		}

		entity.fallDistance = 0F;

		if (entity instanceof Player player) {
			player.giveExperiencePoints(0);
		}

		return true;
	}

	// メインスロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// メインスロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInputItem().isEmpty();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("handInv", this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("handInv"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new TransferGateVerticalMenu(windowId, inv, this);
	}
}
