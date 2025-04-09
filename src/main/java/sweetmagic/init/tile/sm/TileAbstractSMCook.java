package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.iitem.IFood;
import sweetmagic.api.iitem.info.FoodInfo;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.capability.ICookingStatus;

public abstract class TileAbstractSMCook extends TileAbstractSM {

	public boolean hasFork = false;
	private int foodAmount = 0;
	public Player player = null;
	private ItemStack foodStack = ItemStack.EMPTY;

	public TileAbstractSMCook(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// 最大料理時間の取得
	public abstract int getMaxCookTime();

	// 料理時間の取得
	public abstract int getCookTime();

	// 料理時間の設定
	public abstract void setCookTime(int ｃookTime);

	// 料理中か
	public abstract boolean isCook();

	// レシピチェック
	public abstract boolean checkRecipe();

	// 作成開始
	public abstract void craftStart();

	// クラフトの完成
	public abstract void craftFinish();

	// 初期化
	public abstract void clearInfo();

	public abstract List<ItemStack> getDropList();

	// 料理時間の経過
	public void addCookTime() {
		this.setCookTime(this.getCookTime() + 1);
	}

	// 料理時間を超えているか
	public boolean isFinishCook() {
		return this.getCookTime() >= this.getMaxCookTime();
	}

	public void setState(int data) {
		if (this.getBlock(this.getBlockPos()) instanceof BaseCookBlock block) {
			block.setState(this.level, this.getBlockPos(), data);
		}
	}

	// 料理の状態を取得
	public int getCookData() {
		return this.getState(this.getBlockPos()).getValue(BaseCookBlock.COOK);
	}

	public List<ItemStack> setCookQuality(Player player, List<ItemStack> stackList, int count) {

		float chance = this.getBaseChance();
		List<ItemStack> newStackList = new ArrayList<>();
		Map<Item, Integer> qualityMap = new HashMap<>();
		FoodInfo info = null;

		for (ItemStack stack : stackList) {

			Item item = stack.getItem();
			if (!(item instanceof IFood food)) {
				newStackList.add(stack);
				continue;
			}

			if (info == null) {
				info = new FoodInfo(stack);
			}

			if (qualityMap.get(item) != null) {
				food.setQualityValue(stack, qualityMap.get(item));
			}

			else {
				int level = food.setQuality(this.getBlockPos(), player, stack, chance);
				food.setQualityValue(stack, level);
				qualityMap.put(item, level);
			}

			newStackList.add(stack);
		}

		ItemStack food = newStackList.get(0);

		if (!food.isEmpty() && food.getItem() instanceof IFood) {
			this.foodStack = food;
			this.foodAmount = count;
		}

		return newStackList;
	}

	public void getExpValue() {
		if (this.foodStack.isEmpty() || !(this.foodStack.getItem() instanceof IFood) || this.player == null) { return; }
		IFood.getExpValue(this.player, new FoodInfo(this.foodStack), this.foodAmount);
		ICookingStatus.sendPKT(this.player);
	}

	public abstract List<ItemStack> getCraftList();

	public float getBaseChance() {
		float chance = 0F;
		List<ItemStack> craftList = this.getCraftList();
		if (craftList.isEmpty()) { return chance; }

		for (ItemStack stack : craftList) {
			if (stack.getItem() instanceof IFood food) {
				chance += food.getQualityValue(stack) * 0.05F;
			}
		}

		return chance;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("hasFork", this.hasFork);
		tag.putInt("foodAmount", this.foodAmount);
		tag.put("foodStack", this.foodStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.hasFork = tag.getBoolean("hasFork");
		this.foodAmount = tag.getInt("foodAmount");
		this.foodStack = ItemStack.of(tag.getCompound("foodStack"));
	}
}
