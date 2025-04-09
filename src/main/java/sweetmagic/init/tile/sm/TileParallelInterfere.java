package sweetmagic.init.tile.sm;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.ParallelInterfereMenu;

public class TileParallelInterfere extends TileWoodChest {

	public int time;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	public float rot;
	public float oRot;
	public float tRot;

	public TileParallelInterfere(BlockPos pos, BlockState state) {
		this(TileInit.parallelInterfere, pos, state);
	}

	public TileParallelInterfere(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	public final StackHandler inputInv = new StackHandler(this.getInvSize());

	public StackHandler getInputInv() {
		return this.inputInv;
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);

		this.oOpen = this.open;
		this.oRot = this.rot;
		Player player = world.getNearestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 3.0D, false);

		if (player != null) {
			double d0 = player.getX() - ((double) pos.getX() + 0.5D);
			double d1 = player.getZ() - ((double) pos.getZ() + 0.5D);
			this.tRot = (float) Mth.atan2(d1, d0);
			this.open += 0.1F;

			if (this.open < 0.5F || this.rand.nextInt(40) == 0) {
				float f1 = this.flipT;

				do {
					this.flipT += (float) (this.rand.nextInt(4) - this.rand.nextInt(4));
				} while (f1 == this.flipT);
			}

			if (this.tickTime % 10 == 0) {
				this.spawnParticle(pos, this.rand);
			}
		}

		else {
			this.tRot += 0.02F;
			this.open -= 0.1F;
		}

		while (this.rot >= (float) Math.PI) { this.rot -= ((float) Math.PI * 2F); }
		while (this.rot < -(float) Math.PI) { this.rot += ((float) Math.PI * 2F); }
		while (this.tRot >= (float) Math.PI) { this.tRot -= ((float) Math.PI * 2F); }
		while (this.tRot < -(float) Math.PI) { this.tRot += ((float) Math.PI * 2F); }

		float f2;
		for (f2 = this.tRot - this.rot; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) { }
		while (f2 < -(float) Math.PI) { f2 += ((float) Math.PI * 2F); }

		this.rot += f2 * 0.4F;
		this.open = Mth.clamp(this.open, 0.0F, 1.0F);
		++this.time;
		this.oFlip = this.flip;
		float f = (this.flipT - this.flip) * 0.4F;
		f = Mth.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}

	public void spawnParticle(BlockPos pos, Random rand) {
		float randX = this.getRandFloat(0.5F);
		float randZ = this.getRandFloat(0.5F);
		float x = pos.getX() + 0.5F + randX;
		float y = pos.getY() + 1F;
		float z = pos.getZ() + 0.5F + randZ;
		float xSpeed = this.getRandFloat(0.05F);
		float ySpeed = 0.025F + rand.nextFloat() * 0.05F;
		float zSpeed = this.getRandFloat(0.05F);
		this.level.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 540;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ParallelInterfereMenu(windowId, inv, this);
	}

	public int getData() {
		return 1024;
	}
}
