package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.fluid.BucketWrapper;
import sweetmagic.util.WorldHelper;

public class SMBucket extends SMItem {

	public final int data;

	public SMBucket(String name, int data) {
		super(name, new Item.Properties().tab(SweetMagicCore.smMagicTab).stacksTo(1));
		this.data = data;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return this.shrinkWater(stack.copy(), 1000);
	}

	public ItemStack shrinkWater (ItemStack stack, int shrink) {
		FluidStack fluid = this.getFluidStack(stack);
		if (fluid.isEmpty()) { new ItemStack(ItemInit.alt_bucket); }

		fluid.shrink(shrink);
		this.saveFluid(stack, fluid);

		if (fluid.isEmpty() || fluid.getAmount() <= 0) {
			stack = new ItemStack(ItemInit.alt_bucket);
		}

		return stack;
	}

	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult result = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
		InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, stack, result);
		if (ret != null) { return ret; }

		BlockPos pos = result.getBlockPos();
		Direction face = result.getDirection();
		BlockPos pos1 = pos.relative(face);
		if (!world.mayInteract(player, pos) || !player.mayUseItemAt(pos1, face, stack)) { return InteractionResultHolder.fail(stack); }

		// 空バケツなら
		if (this.getFluid() == Fluids.EMPTY) {

			BlockState state1 = world.getBlockState(pos);
			if (state1.getBlock() instanceof BucketPickup pickup) {

				ItemStack stack1 = pickup.pickupBlock(world, pos, state1);

				if (stack1.is(Items.WATER_BUCKET)) {
					stack1 = new ItemStack(ItemInit.alt_bucket_water);
				}

				else if (stack1.is(Items.LAVA_BUCKET)) {
					stack1 = new ItemStack(ItemInit.alt_bucket_lava);
				}

				else {
					return InteractionResultHolder.pass(stack);
				}

				if (!stack1.isEmpty()) {
					pickup.getPickupSound(state1).ifPresent((s) -> player.playSound(s, 1F, 1F));

					if (stack1.getItem() instanceof SMBucket bucket) {
						bucket.rangeDrainFluid(world, pos, stack1);
					}

					return InteractionResultHolder.sidedSuccess(stack1, world.isClientSide());
				}
			}

			return InteractionResultHolder.fail(stack);
		}

		// 溶岩、水バケツ
		BlockState state = world.getBlockState(pos);
		if (state.isAir()) { return InteractionResultHolder.pass(stack); }

		if (!this.checkFluid(world, pos, state)) {
			if (this.emptyDrain(player, world, pos, result, stack)) {
				stack = player.isCreative() ? stack : this.fillStack(stack);
				return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
			}

			else {
				FluidState fState = world.getFluidState(pos1);
				if (!fState.isEmpty() && !fState.is(this.getFluid())) {
					world.setBlock(pos1, this.getFluid().defaultFluidState().createLegacyBlock(), 3);
					stack = this.shrinkWater(stack, 1000);
					this.playEmptySound(player, world, pos1);
				}
			}
		}

		else {

			if (state.getBlock() instanceof BucketPickup pickup) {
				pickup.getPickupSound(state).ifPresent((s) -> player.playSound(s, 1F, 1F));
			}

			this.rangeDrainFluid(world, pos, stack);
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
		}

		return InteractionResultHolder.fail(stack);
	}

	public boolean emptyDrain(Player player, Level world, BlockPos pos, BlockHitResult result, ItemStack stack) {
		if (!(this.getFluid() instanceof FlowingFluid)) { return false; }

		FluidState fluid = world.getFluidState(pos);
		if (fluid.is(Fluids.WATER) || fluid.is(Fluids.LAVA)) { return false; }

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Material material = state.getMaterial();
		boolean flag = state.canBeReplaced(this.getFluid());
		boolean flag1 = state.isAir() || flag || block instanceof LiquidBlockContainer liq && liq.canPlaceLiquid(world, pos, state, this.getFluid());

		if (!flag1) {
			return result != null && this.emptyDrain(player, world, result.getBlockPos().relative(result.getDirection()), (BlockHitResult) null, stack);
		}

		else if (block instanceof LiquidBlockContainer liq && liq.canPlaceLiquid(world, pos, state, getFluid())) {
			liq.placeLiquid(world, pos, state, ((FlowingFluid) this.getFluid()).getSource(false));
			this.playEmptySound(player, world, pos);
			return true;
		}

		if (!world.isClientSide() && flag && !material.isLiquid()) {
			world.destroyBlock(pos, true);
		}

		if (!world.setBlock(pos, this.getFluid().defaultFluidState().createLegacyBlock(), 11) && !state.getFluidState().isSource()) {
			return false;
		}

		this.playEmptySound(player, world, pos);
		return true;
	}

	protected void playEmptySound(Player player, LevelAccessor world, BlockPos pos) {

		SoundEvent sound = this.getFluid().getFluidType().getSound(player, world, pos, SoundActions.BUCKET_EMPTY);

		if (sound == null) {
			sound = this.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		}

		world.playSound(player, pos, sound, SoundSource.BLOCKS, 1F, 1F);
	}

	// バケツの液体消費
	public ItemStack fillStack(ItemStack stack) {

		FluidStack fluid = this.getFluidStack(stack);
		fluid.shrink(1000);

		if (fluid.getAmount() <= 0) {
			stack = new ItemStack(ItemInit.alt_bucket);
			CompoundTag tags = ((SMBucket) stack.getItem()).getTag(stack);
			CompoundTag fluidNBT = new CompoundTag();
			fluid = new FluidStack(Fluids.EMPTY, 0);
			fluid.writeToNBT(fluidNBT);
			tags.put("fluid", fluidNBT);
		}

		this.saveFluid(stack, fluid);
		return stack;
	}

	public ItemStack drainStack(ItemStack stack, Fluid flu, int amount) {

		FluidStack fluid = this.getFluidStack(stack);

		if (fluid.isEmpty() || fluid.getAmount() <= 0) {
			fluid = new FluidStack(fluid, amount);
		}

		else {
			fluid.grow(amount);
		}

		this.saveFluid(stack, fluid);
		return stack;
	}

	// 周囲の液体回収
	public void rangeDrainFluid(Level world, BlockPos pos, ItemStack stack) {

		// 範囲の座標取得
		int fluidAmount = 0;
		int range = 5;
		Iterable<BlockPos> pList = WorldHelper.getRangePos(pos, range);

		for (BlockPos p : pList) {
			FluidState fState = world.getFluidState(p);
			if (!fState.is(this.geteFluidTag())) { continue; }

			BlockState state = world.getBlockState(p);
			if (fState.getType().isSame(this.getFluid()) && state.hasProperty(BlockStateProperties.LEVEL) && state.getValue(BlockStateProperties.LEVEL) == 0) {
				fluidAmount++;
			}

			if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
				world.setBlock(p, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
			}

			else {
				world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
			}
		}

		FluidStack fluid = this.getFluidStack(stack);

		if (fluid.isEmpty()) {
			fluid = new FluidStack(this.getFluid(), 1000 * Math.max(fluidAmount, 1));
		}

		else {
			fluid.setAmount(fluid.getAmount() + 1000 * fluidAmount);
		}

		this.saveFluid(stack, fluid);
	}

	private boolean checkFluid(Level world, BlockPos pos, BlockState state) {
		FluidState fluid = world.getFluidState(pos);
		return fluid != null && fluid.getType().isSame(this.getFluid());
	}

	public FluidStack getFluidStack(ItemStack stack) {
		return FluidStack.loadFluidStackFromNBT(this.getTag(stack).getCompound("fluid"));
	}

	public void saveFluid(ItemStack stack, FluidStack fluid) {
		CompoundTag tags = this.getTag(stack);
		CompoundTag fluidNBT = new CompoundTag();
		fluid.writeToNBT(fluidNBT);
		tags.put("fluid", fluidNBT);
	}

	public TagKey<Fluid> geteFluidTag() {
		switch (this.data) {
		case 1:  return FluidTags.WATER;
		case 2:  return FluidTags.LAVA;
		default: return null;
		}
	}

	public Fluid getFluid() {
		switch (this.data) {
		case 1:  return Fluids.WATER;
		case 2:  return Fluids.LAVA;
		default: return Fluids.EMPTY;
		}
	}

	public CompoundTag getTag(ItemStack stack) {
		CompoundTag tags = stack.getOrCreateTag();

		if (!tags.contains("fluid")) {
			CompoundTag fluidNBT = new CompoundTag();
			FluidStack fluid = new FluidStack(this.getFluid(), 0);
			fluid.writeToNBT(fluidNBT);
			tags.put("fluid", fluidNBT);
		}

		return tags;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tags) {
		return new BucketWrapper(stack, this);
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		FluidStack fluid = this.getFluidStack(stack);
		if (fluid.isEmpty()) { return; }

		toolTip.add(this.getTipArray(fluid.getDisplayName().getString(), ": ", this.getLabel(String.format("%,.1f", fluid.getAmount() * 0.001F) + "B", GREEN)));
	}
}
