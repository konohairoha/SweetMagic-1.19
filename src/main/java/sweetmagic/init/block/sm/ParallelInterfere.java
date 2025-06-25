package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileParallelInterfere;
import sweetmagic.init.tile.sm.TileStardustWish;

public class ParallelInterfere extends BaseFaceBlock implements EntityBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 12D, 16D);

	public ParallelInterfere(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	public ItemStack inheritingNBT(ItemStack oldStack, ItemStack newStack) {

		// NBTを持っていたらNBTの移行
		if (oldStack.getOrCreateTag().contains("BlockEntityTag")) {

			CompoundTag tags = oldStack.getTagElement("BlockEntityTag");

			// 移行前のスロットサイズとスロットの取得
			int oldInputSize = 1080;
			tags.putInt("Size", oldInputSize);

			CompoundTag newTags = new CompoundTag();
			newTags.put("BlockEntityTag", tags);
			newStack.setTag(newTags);
		}

		return newStack;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("parallel_interfere", (this.data + 1) * 20).withStyle(GREEN));
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));
		super.addBlockTip(toolTip);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1 : return new TileStardustWish(pos, state);
		default: return new TileParallelInterfere(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch (this.data) {
		case 1: return TileInit.stardustWish;
		default: return TileInit.parallelInterfere;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	public float getEnchantPower() {
		return (this.data + 1) * 15F;
	}
}
