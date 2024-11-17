package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.MagicSquareMagic;

public class FieldMagic extends BaseMagicItem {

	public final int data;

	public FieldMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.FIELD, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public FieldMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.FIELD, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip (List<MutableComponent> toolTip) {
		toolTip.add(this.getText("magic_square"));

		switch (this.data) {
		case 0:
		case 1:
		case 2:
			toolTip.add(this.getText("magic_gravityfield", String.format("%.1f%%", 10F + (this.data) * 7.5F )));
			toolTip.add(this.getText(this.name + "_enemy"));
			break;
		case 3:
		case 4:
		case 5:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_windfield_regene"));
			break;
		case 6:
		case 7:
		case 8:
			String value = "" + (this.data - 5);
			toolTip.add(this.getText("magic_rainfield", value));
			toolTip.add(this.getText("magic_rainfield_enemy", value));
			break;
		case 9:
		case 10:
		case 11:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_future_visionfiled_enemy", String.format("%.1f", 2F + (this.data - 9) * 1.5F)));
			break;
		default:
			toolTip.add(this.getText(this.name));
			break;
		}

		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		BlockPos pos = player.blockPosition();
		MagicSquareMagic entity = new MagicSquareMagic(world, player, wandInfo);
		entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1F, 0);
		entity.shoot(0D, 0D, 0D, 0F, 0F);
		entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		entity.setHitDead(false);
		entity.setNotDamage(true);
		entity.setData(this.data);

		int level = wandInfo.getLevel();
		entity.setRange(Math.min(3D + level * 0.5D, 12D));
		entity.setMaxLifeTime((int) this.getHealValue(player, this.effectTime(wandInfo)));

		switch (this.data) {
		case 0:
			entity.setData(0);
			entity.setTier(1);
			break;
		case 1:
			entity.setData(0);
			entity.setTier(2);
			break;
		case 2:
			entity.setData(0);
			entity.setTier(3);
			break;
		case 3:
			entity.setData(1);
			entity.setTier(1);
			break;
		case 4:
			entity.setData(1);
			entity.setTier(2);
			break;
		case 5:
			entity.setData(1);
			entity.setTier(3);
			break;
		case 6:
			entity.setData(2);
			entity.setTier(1);
			break;
		case 7:
			entity.setData(2);
			entity.setTier(2);
			break;
		case 8:
			entity.setData(2);
			entity.setTier(3);
			break;
		case 9:
			entity.setData(3);
			entity.setTier(1);
			break;
		case 10:
			entity.setData(3);
			entity.setTier(2);
			break;
		case 11:
			entity.setData(3);
			entity.setTier(3);
			break;
		}

		entity.acceEffect();

		if (!world.isClientSide) {
			world.addFreshEntity(entity);
		}

		this.playSound(world, player.blockPosition(), SoundInit.FLASH, 0.25F, 1F);
		return true;
	}
}
