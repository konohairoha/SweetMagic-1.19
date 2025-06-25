package sweetmagic.init.item.magic;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;

public class MFWeather extends BaseMagicItem {

	private final int burnTime;
	private final int rainTime;

	public MFWeather(String name, SMElement ele, int burnTime, int rainTime) {
		super(name, SMMagicType.CHARGE, ele, 1, 40, 10, true);
		this.burnTime = burnTime;
		this.rainTime = rainTime;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));
		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		this.changeWeather(world);
		this.playSound(world, player, SoundInit.CHANGETIME, 0.15F, 1F);
		return true;
	}

	public void changeWeather(Level world) {
		if (!world.isClientSide() && world.getServer().getLevel(Level.OVERWORLD).getLevelData() instanceof ServerLevelData worldInfo) {
			worldInfo.setRainTime(this.getRainTime());
			worldInfo.setThunderTime(100000);
			worldInfo.setThundering(true);
			worldInfo.setRaining(this.getRainTime() > 0);
		}
	}

	public int getRainTime() {
		return this.rainTime;
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return this.burnTime;
	}
}
