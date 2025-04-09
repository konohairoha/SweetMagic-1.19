package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.block.sm.ParallelInterfere;
import sweetmagic.init.tile.inventory.SMInventory.SMBookInventory;
import sweetmagic.init.tile.inventory.SMInventory.SMPorchInventory;
import sweetmagic.init.tile.inventory.SMInventory.SMWandInventory;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;
import sweetmagic.recipe.pedal.PedalRecipe;
import sweetmagic.util.RenderUtil.RGBColor;

public class TilePedalCreate extends TileSMMagic implements ISMTip {

	public int maxMagiaFlux = 20000;				// 最大MF量を設定
	private static final int MAX_CRAFT_TIME = 10;
	public int maxCrafttime = MAX_CRAFT_TIME;
	public int craftTime = 0;
	public int nowTick = 0;
	public int amount = 0;
	public boolean isCraft = false;
	public boolean isHaveBlock = false;
	public boolean quickCraft = false;
	public List<ItemStack> craftList = new ArrayList<>();	// クラフト素材
	public List<ItemStack> resultList = new ArrayList<>();	// クラフト後素材

	private static final List<RGBColor> colorList = Arrays.<RGBColor> asList(
		new RGBColor(255, 138, 147), new RGBColor(255, 196, 138), new RGBColor(255, 255, 138), new RGBColor(255, 138, 147),
		new RGBColor(147, 255, 138), new RGBColor(138, 183, 255), new RGBColor(255, 138, 238)
	);

	public TilePedalCreate(BlockPos pos, BlockState state) {
		super(TileInit.pedal, pos, state);
	}

	public TilePedalCreate(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.isCraft) {
			this.nowTick++;
			this.sendPKT();
		}

		if (this.tickTime % 20 != 0 || !this.isCraft) { return; }

		// クラフト時間が満たしたら
		if (++this.craftTime >= this.maxCrafttime) {

			for (ItemStack stack : this.resultList) {

				ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, stack.copy());
				world.addFreshEntity(entity);
				entity.xo = 0F;
				entity.zo = 0F;
				entity.setExtendedLifetime();
			}

			this.craftFinish();
			this.clearInfo();
		}

		else {
			this.sendPKT();
			this.playSound(pos, SoundInit.MAGIC_CRAFT, 0.0625F, 1F);
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		this.isHaveBlock = this.getState(pos.below()).is(TagInit.AC_BLOCK);
		if (!this.isCraft) { return; }

		this.spawnParticle(world, pos);
	}

	// ブロック一致確認
	public boolean checkBlock () {
		return this.getState(this.getBlockPos().below()).is(TagInit.AC_BLOCK);
	}

	// クラフト可能か
	public MutableComponent checkCanCraft (List<ItemStack> stackList) {

		// 必要なブロックがない場合
		if (!this.checkBlock()) {
			return this.getTipArray(this.getText("pedastal_noblock"), ":", this.getNeedBlock().getName().withStyle(RED));
		}

		// レシピを取得して見つからなければ終了
		Optional<PedalRecipe> recipeOp = PedalRecipe.getRecipe(this.level, stackList);
		if (recipeOp.isEmpty()) {
			return this.getText("pedastal_norecipe");
		}

		// 要求MF未満なら終了
		PedalRecipe recipe = recipeOp.get();
		int needMF = recipe.getMFList().get(0);
		if (this.getMF() < needMF) {
			return this.getTipArray(this.getText("pedastal_nomf"), ":" + String.format("%,d", needMF));
		}

		// クラフト要求アイテムの消費とクラフト後のアイテム取得
		RecipeUtil recipeUtil = RecipeHelper.recipeSingleCraft(stackList, recipe);
		this.craftList = recipeUtil.getInputList();
		this.setCraftList(recipeUtil, needMF);

		return null;
	}

	// クラフト可能か
	public MutableComponent checkCanAllCraft (List<ItemStack> stackList) {

		// 必要なブロックがない場合
		if (!this.checkBlock()) {
			return this.getTipArray(this.getText("pedastal_noblock"), ":", this.getNeedBlock().getName().withStyle(RED));
		}

		for (int i = 0; i < 8; i++) {

			// レシピを取得して見つからなければ終了
			Optional<PedalRecipe> recipeOp = PedalRecipe.getRecipe(this.level, stackList);
			if (recipeOp.isEmpty()) {
				return i > 0 ? null : this.getText("pedastal_norecipe");
			}

			// 要求MF未満なら終了
			PedalRecipe recipe = recipeOp.get();
			int needMF = recipe.getMFList().get(0);
			if (this.getMF() < needMF) {
				return i > 0 ? null : this.getTipArray(this.getText("pedastal_nomf"), ":" + String.format("%,d", needMF));
			}

			// クラフト要求アイテムの消費とクラフト後のアイテム取得
			RecipeUtil recipeUtil = RecipeHelper.recipeSingleCraft(stackList, recipe);
			this.craftList.addAll(recipeUtil.getInputList());
			this.setCraftList(recipeUtil, needMF);
		}

		return null;
	}

	public void setCraftList (RecipeUtil recipeUtil, int needMF) {

		this.setMF(this.getMF() - needMF);
		List<ItemStack> resultList = new ArrayList<>();
//		recipeUtil.getResultList().forEach(s -> this.resultList.add(s.copy()));
		recipeUtil.getResultList().forEach(s -> resultList.add(s.copy()));
		ItemStack inputStack = this.craftList.get(0);
		CompoundTag tags = inputStack.getTag();

		if (tags != null && inputStack.getItem() instanceof IWand) {

			// 元の杖の魔法リスト
			List<ItemStack> invStackList = new ArrayList<>();
			ItemStack outStack = resultList.get(0);
			Item outItem = outStack.getItem();

			// スロットの数を増やす
			if (outItem instanceof IWand wand) {
				tags.putInt(IWand.SLOTCOUNT, wand.getSlot());
				invStackList = wand.getMagicList(inputStack);
			}

			// 出力アイテムにNBTを設定
			outStack.setTag(tags);

			if (outItem instanceof IWand wand) {

				// インベントリを取得してスロット数を設定
				SMWandInventory inv = new SMWandInventory(new WandInfo(outStack));
				inv.inv = new ItemStackHandler(wand.getSlot());

				// インベントリに元の杖の魔法リストのアイテムを突っ込む
				for (int i = 0; i < invStackList.size(); i++) {
					inv.insertItem(i, invStackList.get(i), false);
				}

				// NBTの保存
				inv.writeBack();
			}
		}

		else if (tags != null && inputStack.getItem() instanceof IMagicBook book) {

			// 元の杖の魔法リスト
			List<ItemStack> invStackList = new ArrayList<>();
			ItemStack outStack = resultList.get(0);
			Item outItem = outStack.getItem();

			// スロットの数を増やす
			if (outItem instanceof IMagicBook wand) {
				tags.putInt(IMagicBook.SLOTCOUNT, wand.getSlotSize());
				invStackList = wand.getInvList(new BookInfo(inputStack));
			}

			// 出力アイテムにNBTを設定
			outStack.setTag(tags);

			if (outItem instanceof IMagicBook wand) {

				// インベントリを取得してスロット数を設定
				SMBookInventory inv = new SMBookInventory(new BookInfo(outStack));
				inv.inv = new ItemStackHandler(wand.getSlotSize());

				// インベントリに元の杖の魔法リストのアイテムを突っ込む
				for (int i = 0; i < invStackList.size(); i++) {
					inv.insertItem(i, invStackList.get(i), false);
				}

				// NBTの保存
				inv.writeBack();
			}

			if (resultList.size() > 2) {

				ItemStack oldBook = resultList.get(1);

				if (!oldBook.isEmpty()) {
					oldBook.setTag(new CompoundTag());
				}
			}
		}

		else if (tags != null && inputStack.getItem() instanceof IPorch book) {

			// 元の杖の魔法リスト
			List<ItemStack> invStackList = new ArrayList<>();
			ItemStack outStack = resultList.get(0);
			Item outItem = outStack.getItem();

			// スロットの数を増やす
			if (outItem instanceof IPorch wand) {
				tags.putInt(IMagicBook.SLOTCOUNT, wand.getSlotSize());
				invStackList = wand.getStackList(inputStack);
			}

			// 出力アイテムにNBTを設定
			outStack.setTag(tags);

			if (outItem instanceof IPorch wand) {

				// インベントリを取得してスロット数を設定
				SMPorchInventory inv = new SMPorchInventory(new PorchInfo(outStack));
				inv.inv = new ItemStackHandler(wand.getSlotSize());

				// インベントリに元の杖の魔法リストのアイテムを突っ込む
				for (int i = 0; i < invStackList.size(); i++) {
					inv.insertItem(i, invStackList.get(i), false);
				}

				// NBTの保存
				inv.writeBack();
			}
		}

		else if (tags != null && inputStack.getItem() instanceof BlockItem bItem) {

			// MFテーブル系の中身保持
			if (bItem.getBlock() instanceof BaseMFBlock mfBlock && mfBlock.keepTileInfo()) {
				mfBlock.inheritingNBT(inputStack, resultList.get(0));
			}

			// MFテーブル系の中身保持
			else if (bItem.getBlock() instanceof ParallelInterfere parallel) {
				parallel.inheritingNBT(inputStack, resultList.get(0));
			}

			else {
				ItemStack outStack = resultList.get(0);
				outStack.setTag(tags);
			}
		}

		// 出力アイテムにNBTを設定
		else if (tags != null) {
			ItemStack outStack = resultList.get(0);
			outStack.setTag(tags);
		}

		this.resultList.addAll(resultList);
	}

	// 作成開始
	public void craftStart () {
		this.isCraft = true;
		this.nowTick = 0;
		this.tickTime = 0;
		this.maxCrafttime = this.quickCraft ? 5 : 10;
		this.sendPKT();
		this.playSound(this.getBlockPos(), SoundInit.MAGIC_CRAFT, 0.1F, 1F);
	}

	// クラフトの完成
	public void craftFinish () {
		this.isCraft = false;
		this.playSound(this.getBlockPos(), SoundEvents.PLAYER_LEVELUP, 0.5F, 1F);
		this.level.levelEvent(2003, this.getBlockPos().above(2), 0);
		this.sendPKT();
	}

	// 初期化
	public void clearInfo () {
		this.amount = 0;
		this.craftTime = 0;
		this.nowTick = 0;
		this.maxCrafttime = MAX_CRAFT_TIME;
		this.isCraft = false;
		this.quickCraft = false;
		this.craftList.clear();
		this.resultList.clear();
		this.sendPKT();
	}

	// パーティクルスポーン
	public void spawnParticle(Level world, BlockPos pos) {

		float posX = pos.getX() + 0.5F;
		float posY = pos.getY() + 0.5F;
		float posZ = pos.getZ() + 0.5F;
		float rate = this.quickCraft ? 2F : 1F;

		for (int k = 0; k <= 4; k++) {
			float f1 = (float) posX - 0.5F + this.rand.nextFloat();
			float f2 = (float) (posY + 0.85F + this.rand.nextFloat() * 0.75F) + this.nowTick * 0.00375F * rate;
			float f3 = (float) posZ - 0.5F + this.rand.nextFloat();

			world.addParticle(ParticleInit.TWILIGHTLIGHT, true, f1, f2, f3, 0, 0, 0);

			float f4 = (float) posX - 0.5F + this.rand.nextFloat();
			float f5 = (float) (posY + 0.35F + this.rand.nextFloat() * 0.75F) + this.nowTick * 0.003875F * rate;
			float f6 = (float) posZ - 0.5F + this.rand.nextFloat();

			RGBColor color = colorList.get(this.rand.nextInt(colorList.size()));
			world.addParticle(ParticleInit.LAY, true, f4, f5, f6, color.red(), color.green(), color.blue());
		}
	}

	public void spawnParticleRing(Level world, double x, double y, double z, double vecX, double vecY, double vecZ, double step) {

		double spped = 0.1D;

		for (double degree = 0D; degree < 2D * Math.PI; degree += step) {
			world.addParticle(ParticleInit.NORMAL, true, x + Math.cos(degree), y, z + Math.sin(degree), -Math.cos(degree) * spped, vecY, -Math.sin(degree) * spped);
		}
	}

	// ドロップリストを取得
	public List<ItemStack> getDropList() {
		return this.craftList;
	}

	// 最大料理時間の取得
	public int getMaxCookTime() {
		return this.maxCrafttime;
	}

	// 料理時間の取得
	public int getCookTime() {
		return this.craftTime;
	}

	public Block getNeedBlock () {
		return BlockInit.aethercrystal_block;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("quickCraft", this.quickCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCrafttime", this.maxCrafttime);
		tag.putInt("nowTick", this.nowTick);
		tag.putInt("amount", this.amount);
		this.saveStackList(tag, this.craftList, "craftList");
		this.saveStackList(tag, this.resultList, "resultList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.isCraft = tag.getBoolean("isCraft");
		this.quickCraft = tag.getBoolean("quickCraft");
		this.craftTime = tag.getInt("craftTime");
		this.maxCrafttime = tag.getInt("maxCrafttime");
		this.nowTick = tag.getInt("nowTick");
		this.amount = tag.getInt("amount");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.resultList = this.loadAllStack(tag, "resultList");
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}
}
