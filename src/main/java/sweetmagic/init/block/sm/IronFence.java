package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class IronFence extends IronBarsBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 24D, 16D);

	public IronFence(String name, int data) {
		super(BaseSMBlock.setState(getMaterial(data)).sound(getSound(data)).strength(0.5F, 8192F));
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if(this.data != 1) { return super.use(state, world, pos, player, hand, result); }

		if (world.isClientSide) {
			return player.getItemInHand(hand).is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		return LeadItem.bindPlayerMobs(player, world, pos);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {

		if (this.data == 0 || (col instanceof EntityCollisionContext con && con.getEntity() instanceof Player)) {
			return super.getCollisionShape(state, world, pos, col);
		}

		return AABB;
	}

	public static Material getMaterial(int data) {
		switch (data) {
		case 1:  return Material.WOOD;
		case 2:  return Material.STONE;
		default: return Material.METAL;
		}
	}

	public static SoundType getSound(int data) {
		switch (data) {
		case 1:  return SoundType.WOOD;
		case 2:  return SoundType.STONE;
		default: return SoundType.METAL;
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
