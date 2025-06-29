package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.MFFisher;
import sweetmagic.init.tile.menu.MFFisherMenu;
import sweetmagic.util.ItemHelper;

public class TileMFFisher extends TileSMMagic {

	public int craftTime = 0;
	public int maxCraftTime = 0;
	public int maxMagiaFlux = 10000;
	public static final ItemStack FISHING_ROD = new ItemStack(Items.FISHING_ROD);
	public static final ItemStack MACHETE = new ItemStack(ItemInit.machete);
	public static final ItemStack MILK_PACK = new ItemStack(ItemInit.milk_pack);
	public static final ItemStack AETHER_CRYSTAL = new ItemStack(ItemInit.aether_crystal);
	public static final ItemStack DIVINE_CRYSTAL = new ItemStack(ItemInit.divine_crystal);
	public static final ItemStack ALT_PICK = new ItemStack(ItemInit.alt_pick);
	public static final ItemStack EGG_BAG = new ItemStack(ItemInit.egg_bag);
	protected final StackHandler inputInv = new MagiaHandler(this.getInvSize());

	public TileMFFisher(BlockPos pos, BlockState state) {
		this(TileInit.mfFisher, pos, state);
	}

	public TileMFFisher(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, OUT);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isRSPower()) { return; }

		// 最大クラフト時間の設定
		if (this.maxCraftTime <= 0) {
			this.setMaxCraftTime();
		}

		if (!this.canCraft()) { return; }

		if (this.craftTime++ >= this.maxCraftTime) {
			this.craftFinish(world, pos);
		}

		this.sendPKT();

		if (this.tickTime % 20 == 0) {
			this.tickTime = 0;
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);

		if (this.tickTime % 10 == 0 && !this.isMFEmpty() && this.maxCraftTime >= this.craftTime + 2 && (this.getData() == 3 || this.getData() == 4) ) {

			this.tickTime = 0;

			for (int i = 0; i < 2; i++) {

				float randX = this.getRandFloat();
				float randY = this.getRandFloat(0.25F);
				float randZ = this.getRandFloat();
				float x = pos.getX() + 0.5F + randX;
				float y = pos.getY() + 0.5F + randY;
				float z = pos.getZ() + 0.5F + randZ;
				float xSpeed = -randX * 0.1175F;
				float ySpeed = -(randY * 0.025F - 0.065F);
				float zSpeed = -randZ * 0.1175F;

				this.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}
	}

	public void craftFinish(Level world, BlockPos pos) {

		boolean isCraft = false;
		int useMF = this.getNeedMF();
		int data = this.getData();

		if ((data == 2 || data == 5 || data == 6) && this.getMF() < useMF) {
			isCraft = true;
			useMF = 0;
		}

		else if (this.getMF() >= useMF) {
			isCraft = true;
		}

		if (!isCraft) { return; }

		List<ItemStack> stackList = this.getDropList(world, pos);

		for (ItemStack stack : stackList) {
			ItemStack out = ItemHelper.insertStack(this.getInput(), stack.copy(), true);
			if (!out.isEmpty()) { return; }
		}

		stackList.forEach(s -> ItemHelper.insertStack(this.getInput(), s.copy(), false));
		this.setMF(this.getMF() - useMF);
		this.playSound(this.getBlockPos(), this.getSound(), 0.1F, 1F);
		this.craftTime = 0;
		this.maxCraftTime = 0;
		this.sendPKT();

		if (data == 3 || data == 4) {
			this.renderParicle(world, pos, data);
		}
	}

	public List<ItemStack> getDropList(Level world, BlockPos pos) {

		RandomSource rand = this.getLevel().getRandom();
		List<ItemStack> stackList = new ArrayList<>();

		switch (this.getData()) {
		case 0:

			int addY = world.getBlockState(pos.below()).is(Blocks.WATER) ? -1 : 0;
			Vec3 vec = new Vec3(pos.getX(), pos.getY() + addY, pos.getZ());
			LootContext.Builder loot = (new LootContext.Builder((ServerLevel)this.getLevel())).withParameter(LootContextParams.ORIGIN, vec).withParameter(LootContextParams.TOOL, FISHING_ROD).withRandom(rand).withLuck(1F);
			stackList = world.getServer().getLootTables().get(BuiltInLootTables.FISHING).getRandomItems(loot.create(LootContextParamSets.FISHING));

			if (rand.nextFloat() <= 0.125F) {
				stackList.add(new ItemStack(Items.KELP));
			}

			if (rand.nextFloat() <= 0.125F) {
				stackList.add(new ItemStack(Items.SALMON));
			}

			if (rand.nextFloat() <= 0.25F) {
				stackList.add(new ItemStack(ItemInit.shrimp));
			}

			if (rand.nextFloat() <= 0.35F) {
				stackList.add(new ItemStack(ItemInit.seaweed, rand.nextInt(2) + 1));
			}

			break;
		case 1:

			List<ItemStack> meatList = new ArrayList<>();
			ForgeRegistries.ITEMS.tags().getTag(TagInit.MEAT).forEach(s -> meatList.add(new ItemStack(s)));
			stackList.add(meatList.get(rand.nextInt(meatList.size())));

			if (rand.nextFloat() <= 0.25F) {
				stackList.add(new ItemStack(Items.BEEF));
			}

			if (rand.nextFloat() <= 0.25F) {
				stackList.add(new ItemStack(Items.PORKCHOP));
			}

			if (rand.nextFloat() <= 0.25F) {
				stackList.add(new ItemStack(Items.CHICKEN));
			}

			break;
		case 2:
			stackList.add(new ItemStack(ItemInit.milk_pack, this.rand.nextInt(10) + 6));
			break;
		case 3:
			stackList.add(new ItemStack(AETHER_CRYSTAL.getItem(), this.getNeedMF() / SweetMagicAPI.getMF(AETHER_CRYSTAL)));
			break;
		case 4:
			stackList.add(new ItemStack(DIVINE_CRYSTAL.getItem(), this.getNeedMF() / SweetMagicAPI.getMF(DIVINE_CRYSTAL)));
			break;
		case 5:
			int rate = this.getMF() >= this.getNeedMF() ? 2 : 1;
			stackList.add(new ItemStack(Blocks.COBBLESTONE, (this.rand.nextInt(5) + 2) * rate));
			break;
		case 6:
			stackList.add(new ItemStack(Items.EGG, this.rand.nextInt(10) + 6));
			break;
		}

		return stackList;
	}

	public void renderParicle(Level world, BlockPos pos, int data) {
		if (!(world instanceof ServerLevel sever)) { return; }

		ParticleOptions par = data == 3 ? ParticleInit.AETHER : ParticleInit.DIVINE;

		for (int i = 0; i < 5; i++) {
			float x = (float) pos.getX() + 0.25F + this.rand.nextFloat() * 0.5F;
			float y = (float) pos.getY() + 1F + this.rand.nextFloat() * 0.25F;
			float z = (float) pos.getZ() + 0.25F + this.rand.nextFloat() * 0.5F;
			sever.sendParticles(par, x, y, z, 0, 0F, -0.125F, 0F, 1F);
		}
	}

	public void setMaxCraftTime() {
		int randTime = this.getRandTime();
		this.maxCraftTime = this.rand.nextInt((int) (randTime * 0.5F)) + randTime;
		this.sendPKT();
	}

	public int getRandTime() {
		switch(this.getData()) {
		case 2:  return this.getMF() >= this.getNeedMF() ? 14 : 134;
		case 3:  return 80;
		case 4:  return 120;
		case 5:  return this.getMF() >= this.getNeedMF() ? 20 : 80;
		case 6:  return this.getMF() >= this.getNeedMF() ? 14 : 134;
		default: return 20;
		}
	}

	public int getNeedMF() {
		switch(this.getData()) {
		case 2: return 100;
		case 3:
			int mf = SweetMagicAPI.getMF(AETHER_CRYSTAL);
			int needMF = Math.max(mf, Math.min(this.getMF() / mf, 8) * mf);
			return needMF;
		case 4:
			int mfDiv = SweetMagicAPI.getMF(DIVINE_CRYSTAL);
			int needMFDiv = Math.max(mfDiv, Math.min(this.getMF() / mfDiv, 8) * mfDiv);
			return needMFDiv;
		case 6: return 100;
		default: return 300;
		}
	}

	public boolean canCraft() {
		int data = this.getData();
		return data == 2 || data == 5 || data == 6 || this.getMF() >= this.getNeedMF();
	}

	public SoundEvent getSound() {
		switch(this.getData()) {
		case 0:  return SoundEvents.FISHING_BOBBER_SPLASH;
		case 1:  return SoundEvents.SHEEP_SHEAR;
		case 2:  return SoundEvents.COW_MILK;
		case 3:  return SoundEvents.AMETHYST_BLOCK_BREAK;
		case 4:  return SoundEvents.AMETHYST_BLOCK_BREAK;
		case 5:  return SoundEvents.STONE_BREAK;
		case 6:  return SoundEvents.CHICKEN_EGG;
		default: return SoundEvents.COW_MILK;
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 27;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		switch(this.getData()) {
		case 3: return 40000;
		case 4: return 400000;
		default: return this.maxMagiaFlux;
		}
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		switch(this.getData()) {
		case 3: return 10000;
		case 4: return 50000;
		default: return 5000;
		}
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return Math.min(value, (int) (value * (float) this.craftTime / (float) this.maxCraftTime));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFFisherMenu(windowId, inv, this);
	}

	public int getData() {
		return this.isAir() ? 0 : ((MFFisher) this.getBlock(this.getBlockPos())).getData();
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
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
