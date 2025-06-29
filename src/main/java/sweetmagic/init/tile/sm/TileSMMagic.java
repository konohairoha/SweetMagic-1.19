package sweetmagic.init.tile.sm;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.iblock.ITileMF;
import sweetmagic.init.ParticleInit;

public abstract class TileSMMagic extends TileAbstractSM implements ITileMF {

	public int magiaFlux = 0;						// 所有しているMF
	public boolean isReceive = true;				// 受け取る側かどうか
	public Set<BlockPos> posList = new HashSet<>();	// MFブロックを保存するリスト
	public String POST = "pos";

	public TileSMMagic(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);

		// 一定時間経つと送受信をする
		if (this.getTickTime() % 20 == 0 && !this.posList.isEmpty()) {
			this.sendRecivehandler();
		}
	}

	// クライアント側処理
	@Override
	public void clientTick(Level world, BlockPos p, BlockState state) {
		super.clientTick(world, p, state);
		if (this.getTickTime() % 80 != 0 || this.posList.isEmpty() || this.isMaxMF()) { return; }

		for (BlockPos pos : this.posList) {

			BlockEntity tile = this.getEntity(pos);
			if (!(tile instanceof ITileMF) || !this.checkDistance(pos) || ((ITileMF) tile).isMFEmpty()) { continue; }

			float pX = this.getBlockPos().getX() - pos.getX();
			float pY = this.getBlockPos().getY() - pos.getY();
			float pZ = this.getBlockPos().getZ() - pos.getZ();

			for (int i = 0; i < 4; i++) {

				float randX = this.getRandFloat(0.5F);
				float randY = this.getRandFloat(0.5F);
				float randZ = this.getRandFloat(0.5F);
				float x = pos.getX() + 0.5F + randX;
				float y = pos.getY() + 0.525F + randY;
				float z = pos.getZ() + 0.5F + randZ;
				float xSpeed = pX * 0.1175F;
				float ySpeed = pY * 0.1175F;
				float zSpeed = pZ * 0.1175F;
				world.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}
	}

	// ブロックえんちちーの取得
	public BlockEntity getTile() {
		return this;
	}

	// 座標リストの取得
	public Set<BlockPos> getPosList() {
		return this.posList;
	};

	// 座標リストの追加
	public void addPosList(BlockPos pos) {
		this.posList.add(pos);
	}

	// 座標の取得
	public BlockPos getTilePos() {
		return this.getBlockPos();
	}

	// worldの取得
	public Level getTileWorld() {
		return this.getLevel();
	}

	// 経過時間の取得
	public int getTickTime() {
		return this.tickTime;
	}

	// 経過時間の設定
	public void setTickTime(int tickTime) {
		this.tickTime = tickTime;
	}

	// 受信側かどうかの取得
	public boolean getReceive() {
		return this.isReceive;
	}

	// MFの取得
	public int getMF() {
		return this.magiaFlux;
	}

	// MFの設定
	public void setMF(int mf) {
		this.magiaFlux = mf;
	}

	public String getMFPercent() {
		return this.format(((float) this.getMF() / (float) this.getMaxMF()) * 100F) + "%";
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("magiaFlux", this.magiaFlux);

		if (!this.posList.isEmpty()) {
			this.savePosList(tag, this.getPosList(), this.POST);
		}

		this.saveNBT(tag);
	}

	public void saveNBT(CompoundTag tags) { }

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setMF(tag.getInt("magiaFlux"));
		this.posList = this.loadAllPos(tag, this.POST);
		this.loadNBT(tag);
	}

	public void loadNBT(CompoundTag tags) { }

	public abstract IItemHandler getInput();

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.isMFEmpty();
	}
}
