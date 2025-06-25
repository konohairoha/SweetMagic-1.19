package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
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
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMagiaStorage;
import sweetmagic.util.RenderUtil.RGBColor;

public class MagiaStorage extends BaseFaceBlock implements EntityBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(2D, 2D, 2D, 14D, 16D, 14D);

	public MagiaStorage(String name, int data) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		TileAbstractSM tile = this.getTile(world, pos);
		tile.sendPKT();
		this.openGUI(world, pos, player, tile);
		this.playerSound(world, pos, SoundInit.STORAGE, 0.125F, world.getRandom().nextFloat() * 0.1F + 0.9F);
		return true;
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
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));
		toolTip.add(this.getText("magia_storage",this.format(this.getMaxStackSize())).withStyle(GREEN));
		super.addBlockTip(toolTip);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMagiaStorage(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.magiaStorage);
	}

	public int getMaxStackSize() {
		switch (this.getData()) {
		case 1 : return 1280;
		case 2 : return 5120;
		case 3 : return 25600;
		case 4 : return Integer.MAX_VALUE;
		default: return 256;
		}
	}

	public int getData() {
		return this.data;
	}

	public float getEnchantPower() {
		return this.getData() + 1;
	}

	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {

		List<RGBColor> rgbList = new ArrayList<>();

		switch (this.getData()) {
		case 4:
			rgbList.add(new RGBColor(255, 65, 91));
		case 3:
			rgbList.add(new RGBColor(116, 185, 255));
		case 2:
			rgbList.add(new RGBColor(254, 204, 40));
		case 1:
			rgbList.add(new RGBColor(0, 255, 255));
		case 0:
			rgbList.add(new RGBColor(0, 128, 255));
		}

		ParticleOptions par = ParticleInit.STORAGE;

		for (RGBColor rgb : rgbList) {

			float randX = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			float randY = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			float randZ = (rand.nextFloat() - rand.nextFloat()) * 0.1F;

			float x = pos.getX() + 0.5F + randX;
			float y = pos.getY() + 0.5F + randY;
			float z = pos.getZ() + 0.5F + randZ;
			world.addParticle(par, x, y, z, rgb.red(), rgb.green(), rgb.blue());
		}
	}
}
