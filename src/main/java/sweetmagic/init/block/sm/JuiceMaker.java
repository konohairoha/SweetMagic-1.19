package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCookBlock;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.init.tile.sm.TileJuiceMaker;
import sweetmagic.util.FaceAABB;

public class JuiceMaker extends BaseFaceBlock implements EntityBlock, ISMCookBlock {

	private final int data;
	private static final VoxelShape[] AABB = FaceAABB.create(2D, 0D, 1D, 13D, 11D, 15D);
	private static final VoxelShape[] CAFFEE = FaceAABB.create(4D, 0D, 3D, 12D, 14.5D, 15D);

	public JuiceMaker(String name) {
		super(name, setState(Material.METAL, SoundType.METAL, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smFoodTab, name);
		this.data = 0;
	}

	public JuiceMaker(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 1:  return FaceAABB.getAABB(CAFFEE, state);
		default: return FaceAABB.getAABB(AABB, state);
		}
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		TileJuiceMaker tile = (TileJuiceMaker) this.getTile(world, pos);

		if (stack.is(Items.WATER_BUCKET)) {

			if (!tile.canInsertWater(1000)) { return false; }

			stack.shrink(1);
			this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(new ItemStack(Items.BUCKET)));
			tile.setAmount(tile.getFluidValue() + 1000);
			tile.sendPKT();
			this.playerSound(world, pos, SoundEvents.BUCKET_FILL, 1F, 1F);
			return true;
		}

		else if (stack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bk) {
			FluidStack fluid = bk.getFluidStack(stack);
			int insertWaterValue = Math.min(1000, fluid.getAmount());
			fluid.shrink(insertWaterValue);
			bk.saveFluid(stack, fluid);
			tile.setAmount(tile.getFluidValue() + insertWaterValue);
			tile.sendPKT();
			this.playerSound(world, pos, SoundEvents.BUCKET_FILL, 1F, 1F);

			if (fluid.isEmpty() || fluid.getAmount() <= 0) {
				stack.shrink(1);
				this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(new ItemStack(ItemInit.alt_bucket)));
			}
			return true;
		}

		tile.player = player;
		this.openGUI(world, pos, player, tile);
		return true;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileJuiceMaker(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.juicemaker);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		if (this.data != 0) {
			toolTip.add(this.getText("juice_maker_use").withStyle(GOLD));
		}
	}
}
