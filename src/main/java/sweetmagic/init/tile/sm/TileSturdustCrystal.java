package sweetmagic.init.tile.sm;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;

public class TileSturdustCrystal extends TileAbstractSM {

	public boolean isRender = false;

	public TileSturdustCrystal(BlockPos pos, BlockState state) {
		super(TileInit.sturdust_crystal, pos, state);
	}

	public TileSturdustCrystal(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		Random rand = this.rand;
		float posX = pos.getX();
		float posY = pos.getY();
		float posZ = pos.getZ();

		for (int i = 0; i < 6; i++) {
			float f1 = posX + rand.nextFloat();
			float f2 = posY + 0.3F + rand.nextFloat() * 0.5F;
			float f3 = posZ + rand.nextFloat();
			float x = this.getRandFloat() * 0.075F;
			float y = rand.nextFloat() * 0.08F;
			float z = this.getRandFloat() * 0.075F;
			world.addParticle(ParticleInit.NORMAL.get(), f1, f2, f3, x, y, z);
		}
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("isRender", this.isRender);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.isRender = tag.getBoolean("isRender");
	}
}
