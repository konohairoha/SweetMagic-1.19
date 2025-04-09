package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.crop.MagiaFlower;
import sweetmagic.init.tile.menu.AetherPlanterMenu;

public class TileAetherPlanter extends TileSMMagic {

	public boolean isMaxGlow = false;
	public int maxMagiaFlux = 20000;
	public final StackHandler inputInv = new StackHandler(this.getInvSize());
	public ItemStack stack = ItemStack.EMPTY;

	public TileAetherPlanter(BlockPos pos, BlockState state) {
		this(TileInit.aetherPlanter, pos, state);
	}

	public TileAetherPlanter(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, OUT);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0 || this.isRSPower()) { return; }


		if (this.isMaxGlow && world instanceof ServerLevel sever) {
			this.getCropStack(sever, pos);
		}

		else if (this.rand.nextFloat() <= 0.1F && this.getMF() >= this.getUserMF() && world instanceof ServerLevel sever) {
			this.glowPlant(sever, pos);
		}

		if (this.tickTime % 40 == 0) {
			this.tickTime = 0;
			Block block = this.getBlock(pos.above());
			this.stack = block != Blocks.AIR ? new ItemStack(block) : ItemStack.EMPTY;
			this.sendPKT();
		}
	}

	public void glowPlant(ServerLevel world, BlockPos pos) {

		BlockPos upPos = pos.above();
		BlockState upState = world.getBlockState(upPos);
		Block upBlock = upState.getBlock();
		BlockPos upPos2 = pos.above(2);
		BlockState upState2 = world.getBlockState(upPos2);
		Block upBlock2 = upState2.getBlock();

		boolean isGlow = this.glowPlant(world, upPos, upState, upBlock);

		if (!isGlow) {
			isGlow = this.glowPlant(world, upPos2, upState2, upBlock2);
		}

		else {
			this.glowPlant(world, upPos2, upState2, upBlock2);
		}

		if (isGlow) {
			this.setMF(this.getMF() - this.getUserMF());

			for (int i = 0; i < 4; i++) {
				double x = upPos.getX() + this.rand.nextDouble();
				double y = upPos.getY() + this.rand.nextDouble() * 0.4D + 0.2D;
				double z = upPos.getZ() + this.rand.nextDouble();
				world.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 5, 0F, 0F, 0F, 0.25F);
			}
		}

		this.sendPKT();
	}

	public boolean glowPlant(ServerLevel world, BlockPos upPos, BlockState upState, Block upBlock) {

		boolean isGlow = false;

		if (upBlock instanceof MushroomBlock) {
			this.isMaxGlow = isGlow = true;
		}

		else if (upBlock instanceof BambooBlock bam) {
			this.isMaxGlow = isGlow = true;
		}

		else if (upBlock instanceof BonemealableBlock crop) {

			if (crop.isValidBonemealTarget(world, upPos, upState, true)) {
				crop.performBonemeal(world, world.random, upPos, upState);
				isGlow = true;
			}

			if (!crop.isValidBonemealTarget(world, upPos, upState, true)) {
				this.isMaxGlow = true;
			}
		}

		else if (upBlock instanceof MagiaFlower flower) {

			if (flower.isMaxAge(upState)) {
				this.isMaxGlow = true;
			}

			else if (!flower.canGlow(world, world.getDayTime() % 24000 < 12000)) {
				return isGlow;
			}

			else {
				flower.glowUp(world, upState, upPos);
				isGlow = true;

				if (flower.isMaxAge(upState)) {
					this.isMaxGlow = true;
				}
			}
		}

		return isGlow;
	}

	public void getCropStack(ServerLevel world, BlockPos pos) {

		BlockPos upPos = pos.above();
		BlockState upState = world.getBlockState(upPos);
		Block upBlock = upState.getBlock();
		BlockPos upPos2 = pos.above(2);
		BlockState upState2 = world.getBlockState(upPos2);
		Block upBlock2 = upState2.getBlock();
		List<ItemStack> stackList = new ArrayList<>();

		if (upBlock instanceof MushroomBlock) {
			stackList.add(new ItemStack(upBlock, this.rand.nextInt(2) + 1));
			BlockState newState = upBlock2.defaultBlockState();
			world.setBlock(upPos2, newState, 3);
		}

		else if (upBlock instanceof BambooBlock) {
			stackList.addAll(Block.getDrops(upState, world, upPos, world.getBlockEntity(upPos)));
		}

		else if (upBlock instanceof StemBlock crop && !crop.isValidBonemealTarget(world, upPos, upState, true)) {
			stackList.addAll(Block.getDrops(upState, world, upPos, world.getBlockEntity(upPos)));
			stackList.add(new ItemStack(crop.getFruit()));
			BlockState newState = upBlock.defaultBlockState();
			world.setBlock(upPos, newState, 3);
		}

		else if (upBlock2 instanceof BonemealableBlock crop && !crop.isValidBonemealTarget(world, upPos2, upState2, true)) {
			stackList.addAll(Block.getDrops(upState2, world, upPos2, world.getBlockEntity(upPos2)));
			BlockState newState = upBlock2.defaultBlockState();
			world.setBlock(upPos2, newState, 3);
		}

		if (upBlock instanceof BonemealableBlock crop && !crop.isValidBonemealTarget(world, upPos, upState, true)) {
			stackList.addAll(Block.getDrops(upState, world, upPos, world.getBlockEntity(upPos)));
			BlockState newState = upBlock.defaultBlockState();
			world.setBlock(upPos, newState, 3);
		}

		else if (upBlock instanceof MagiaFlower crop) {
			stackList.addAll(Block.getDrops(upState, world, upPos, world.getBlockEntity(upPos)));
			BlockState newState = upBlock.defaultBlockState();
			world.setBlock(upPos, newState, 3);
		}

		if (!stackList.isEmpty()) {

			List<ItemStack> dropList = new ArrayList<>();
			for (ItemStack stack : stackList) {

				ItemStack outStack = ItemHandlerHelper.insertItemStacked(this.getInput(), stack.copy(), false);

				if (!outStack.isEmpty()) {
					dropList.add(outStack);
				}
			}

			if (!dropList.isEmpty()) {
				stackList = dropList;
				return;
			}
		}

		this.isMaxGlow = false;
		this.playSound(upPos, SoundEvents.CROP_BREAK, 1F, 0.9F + this.getRandFloat(0.15F));
		ParticleOptions par = new BlockParticleOption(ParticleTypes.BLOCK, upState);

		for (int i = 0; i < 8; i++) {
			world.sendParticles(par, upPos.getX() + 0.5D, upPos.getY() + 0.25D, upPos.getZ() + 0.5D, 0, this.getRandFloat(0.25F), 0, this.getRandFloat(0.25F), 1D);
		}

		this.sendPKT();
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 27;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 5000;
	}

	public int getUserMF() {
		return 50;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	public boolean isInvEmpty() {
		for (int i = 0; i < this.getInvSize(); i ++) {
			if (!this.getInputItem(i).isEmpty()) { return false; }
		}
		return this.isMFEmpty();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.putBoolean("isMaxGlow", this.isMaxGlow);
		tag.put("wandStack", this.stack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.isMaxGlow = tag.getBoolean("isMaxGlow");
		this.stack = ItemStack.of(tag.getCompound("wandStack"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherPlanterMenu(windowId, inv, this);
	}
}
