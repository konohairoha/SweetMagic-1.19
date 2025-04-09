package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;

public class MFTime extends BaseMagicItem {

	private final int time;

	public MFTime(String name, int time) {
		super(name, SMMagicType.CHARGE, SMElement.TIME, 1, 40, 10, true);
		this.time = time;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));
		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		if (!world.isClientSide) {
			ServerLevel sever = world.getServer().getLevel(Level.OVERWORLD);
			int dayTime = 24000;
			long day = (sever.getDayTime() / dayTime) + 1;
			sever.setDayTime(this.time + (day * dayTime));
		}

		this.playSound(world, player, SoundInit.CHANGETIME, 0.15F, 1F);
		return true;
	}

	public int getTime() {
		return this.time;
	}
}
