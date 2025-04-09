package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherRepairMenu;

public class TileAetherRepair extends TileSMMagic {

	public int maxMagiaFlux = 100000;
	private static final float[] xPosArray = { 0F, 0.125F, 0F, -0.125F };
	private static final float[] zPosArray = { -0.125F, 0F, 0.125F, 0F };
	protected final StackHandler inputInv = new StackHandler(this.getInvSize(), true);

	public TileAetherRepair(BlockPos pos, BlockState state) {
		super(TileInit.aetherRepair, pos, state);
	}

	public TileAetherRepair(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0 || this.isMFEmpty()) { return; }

		// 耐久値回復
		this.repairTool();
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 20 != 0 || this.isMFEmpty()) { return; }

		for (int i = 0; i < this.getInvSize(); i++) {

			ItemStack stack = this.getInputItem(i);
			if (!stack.isEmpty() && stack.getDamageValue() > 0) {
				this.spawnParticl(pos, xPosArray[i], zPosArray[i]);
			}
		}
	}

	// 耐久値回復
	public void repairTool() {

		for (int i = 0; i < this.getInvSize(); i++) {

			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty() || stack.getDamageValue() <= 0) { continue; }

			int damage = stack.getDamageValue();
			int maxDamage = stack.getMaxDamage();

			// 耐久値1当たりの消費MFを取得
			int useMF = Math.max(1, Math.min(1000, (int) ( maxDamage * maxDamage * 0.0001F )));

			// 回復する耐久値量に合わせて消費MFを乗算
			int healValue = Math.min(10, damage);
			useMF *= healValue;

			// MFが足りなければ次へ
			if (this.getMF() < useMF) { continue; }

			stack.setDamageValue(damage - healValue);
			this.setMF(this.getMF() - useMF);
			this.sendPKT();
		}
	}

	public void spawnParticl(BlockPos pos, float pX, float pZ) {

		for (int i = 0; i < 2; i++) {

			float randX = this.getRandFloat(0.1F);
			float randY = this.getRandFloat(0.1F);
			float randZ = this.getRandFloat(0.1F);

			float x = pos.getX() + 0.5F + randX;
			float y = pos.getY() + 0.525F + randY;
			float z = pos.getZ() + 0.5F + randZ;
			float xSpeed = pX * 0.25F;
			float ySpeed = 0.1F * 0.4F;
			float zSpeed = pZ * 0.25F;

			this.level.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 4;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 消費MF量の取得
	public int getShrinkMF() {
		return 10000;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherRepairMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
