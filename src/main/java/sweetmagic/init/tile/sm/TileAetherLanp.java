package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherLanternMenu;

public class TileAetherLanp extends TileSMMagic {

	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 16;		// 最大範囲
	public int maxMagiaFlux = 20000;				// 最大MF量を設定
	public int range = 12;
	public int mfInsert = 0;

	public TileAetherLanp(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileAetherLanp(BlockPos pos, BlockState state) {
		super(TileInit.aetherLanp, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.tickTime % 20 == 0 && !this.isRSPower()) {
			this.tickTime = 0;
			this.roundMFRecive();
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);

		if (this.tickTime % 40 == 0 && !this.isMaxMF()) {
			this.tickTime = 0;
			this.roundMFEffect();
		}
	}

	// 周囲のMF回収
	public void roundMFRecive() {

		// 範囲の座標取得
		int sumMF = 0;
		Iterable<BlockPos> posList = this.getRangePosUnder(this.getBlockPos(), this.getRange());

		// リスト分まわす
		for (BlockPos pos : posList) {
			if (!(this.getTile(pos) instanceof TileSMMagic tile) || tile.getReceive()) { continue; }

			// MFブロックからMFを入れるときの処理
			int oldMF = this.getMF();
			this.insertMF(this , tile, this.getTickTime());

			// MFを貯めれなくなったら終了
			int newMF = this.getMF();
			sumMF += newMF - oldMF;
			if (this.isMaxMF()) { break; }
		}

		this.mfInsert = sumMF > 0 ? sumMF : 0;
		this.sendPKT();
	}

	// 周囲のMF回収
	public void roundMFEffect() {

		BlockPos pos = this.getBlockPos();
		Iterable<BlockPos> posArray = this.getRangePosUnder(this.getBlockPos(), this.getRange());

		// リスト分まわす
		for (BlockPos p : posArray) {
			if (!(this.getTile(p) instanceof TileSMMagic tile) || tile.getReceive() || tile.isMFEmpty()) { continue; }

			float pX = pos.getX() - p.getX();
			float pY = pos.getY() - p.getY();
			float pZ = pos.getZ() - p.getZ();

			for (int i = 0; i < 2; i++) {

				float randX = this.getRandFloat(0.5F);
				float randY = this.getRandFloat(0.5F);
				float randZ = this.getRandFloat(0.5F);
				float x = p.getX() + 0.5F + randX;
				float y = p.getY() + 0.525F + randY;
				float z = p.getZ() + 0.5F + randZ;
				float xSpeed = pX * 0.1175F;
				float ySpeed = pY * 0.1175F;
				float zSpeed = pZ * 0.1175F;

				this.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}
	}

	public void addRange(int id) {

		int addValue = 0;

		switch (id) {
		case 0:
			addValue = 1;
			break;
		case 1:
			addValue = 10;
			break;
		case 2:
			addValue = -1;
			break;
		case 3:
			addValue = -10;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	public int getRange() {
		return this.range;
	}

	// 受信するMF量の取得
	public int getReceiveMF() {
		return 5000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.range);
		tag.putInt("mfInsert", this.mfInsert);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("range")) {
			this.range = tag.getInt("range");
		}
		this.mfInsert = tag.getInt("mfInsert");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherLanternMenu(windowId, inv, this);
	}
}
