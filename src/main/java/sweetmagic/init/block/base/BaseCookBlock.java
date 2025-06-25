package sweetmagic.init.block.base;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCookBlock;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.sm.Stove;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAbstractSMCook;

public abstract class BaseCookBlock extends BaseFaceBlock implements EntityBlock, ISMCookBlock {

	public static final IntegerProperty COOK = IntegerProperty.create("cook", 0, 2);

	public BaseCookBlock(String name) {
		super(name, setState(Material.METAL, SoundType.METAL, 1F, 8192F));
		this.registerDefaultState(this.setState().setValue(COOK, 0));
		BlockInfo.create(this, SweetMagicCore.smFoodTab, name);
	}

	public BaseCookBlock(String name, CreativeModeTab tab) {
		super(name, setState(Material.METAL, SoundType.METAL, 1F, 8192F));
		this.registerDefaultState(this.setState().setValue(COOK, 0));
		BlockInfo.create(this, tab, name);
	}

	// ステータスの変更
	public void setState(Level world, BlockPos pos, int data) {
		BlockState state = world.getBlockState(pos);
		world.setBlock(pos, state.setValue(COOK, data), 3);
	}

	// ステータスの取得
	public int getState(BlockState state) {
		return state.getValue(COOK);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (tile instanceof TileAbstractSMCook cook) {
			this.spawnItemList(world, pos, cook.getDropList());
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, COOK);
	}

	// 下のブロックがコンロならtureを返す
	public boolean isUnderStove(Level world, BlockPos pos) {
		return this.getBlock(world, pos.below()) instanceof Stove;
	}

	// 不思議なフォークを持っているかどうか
	public boolean hasFork(Player player) {

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			return porch.hasAcce(leg, ItemInit.mysterious_fork);
		}
		return false;
	}

	// 経験値スポーン
	public void spawnXp(Player player, List<ItemStack> outList, boolean hasFok) {
		if (!hasFok) { return; }

		int xp = 0;
		Level world = player.getLevel();

		for (ItemStack stack : outList) {

			Item item = stack.getItem();
			if (!item.isEdible()) { continue; }

			FoodProperties food = item.getFoodProperties();
			float amount = Math.max(food.getNutrition(), 1F) * Math.max(food.getSaturationModifier(), 0.1F) * stack.getCount() * 0.2F;
			xp += (int) (Math.max(1, amount));
		}

		if(xp <= 0) { return; }

		int cookLevel = ICookingStatus.getState(player).getLevel();
		float rate = 1F + cookLevel * 0.05F;

		ExperienceOrb entity = new ExperienceOrb(world, player.getX(), player.getY(), player.getZ(), (int) (xp * rate));
		world.addFreshEntity(entity);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("cook_block").withStyle(GREEN));
	}
}
