package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;

public class TileTransferGate extends TileAbstractSM {

	public TileTransferGate(BlockPos pos, BlockState state) {
		super(TileInit.transferGate, pos, state);
	}

	public TileTransferGate(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if(this.rand.nextFloat() >= 0.67F || this.isAir()) { return; }

		Direction face = this.getFace();
		boolean isY = face == Direction.NORTH || face == Direction.SOUTH;

		float pX = pos.getX();
		float pY = pos.getY();
		float pZ = pos.getZ();

		float randX = this.getRandFloat(1.65F);
		float randY = this.getRandFloat(1.65F);
		float randZ = this.getRandFloat(1.65F);

		float x = 0;
		float y = pY + 0.5F + randY;
		float z = 0F;
		float xSpeed = 0F;
		float ySpeed = this.getRandFloat(0.05F);
		float zSpeed = 0F;

		switch (face) {
		case NORTH:
			x = pX + 0.5F + (isY ? randX : 0F);
			z = pZ + 0.25F;
			zSpeed = 0.35F + this.rand.nextFloat() * 0.35F;
			break;
		case SOUTH:
			x = pX + 0.5F + randX;
			z = pZ + 0.75F;
			zSpeed -= 0.35F + this.rand.nextFloat() * 0.35F;
			break;
		case WEST:
			x = pX + 0.25F;
			z = pZ + 0.5F + randZ;
			xSpeed = 0.35F + this.getRandFloat(0.35F);
			break;
		case EAST:
			x = pX + 0.75F;
			z = pZ + 0.5F + randZ;
			xSpeed -= 0.35F + this.getRandFloat(0.35F);
			break;
		}

		world.addParticle(ParticleInit.DIVINE.get(), x, y, z, xSpeed, ySpeed, zSpeed);
	}
}
