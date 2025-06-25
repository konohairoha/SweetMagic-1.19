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
	public final int tier;

	public FieldMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.FIELD, ele, tier, coolTime, useMF, false);
		this.data = data;
		this.tier = tier;
	}

	public FieldMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.FIELD, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
		this.tier = tier;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {

		switch (this.data) {
		case 0:
		case 1:
		case 2:
		case 13:
			toolTip.add(this.getText("magic_square_buff", 60 + this.tier * 30).withStyle(WHITE));
			toolTip.add(this.getText("magic_gravityfield", this.formatPar(10F + this.data * 7.5F)));

			if (this.data == 13) {
				toolTip.add(this.getText("magic_gravityfield2_enemy"));
			}

			else {
				toolTip.add(this.getText(this.name + "_enemy"));
			}

			toolTip.add(this.getText("magic_square").withStyle(WHITE));
			toolTip.add(this.getText("magic_gravityfield_finish", this.getEffectTip("gravity").getString()));

			break;
		case 3:
		case 4:
		case 5:
		case 14:
			toolTip.add(this.getText("magic_square_buff", 60 + this.tier * 30).withStyle(WHITE));
			toolTip.add(this.getText("magic_windfield"));
			toolTip.add(this.getText("magic_square").withStyle(WHITE));
			toolTip.add(this.getText("magic_windfield_regene"));
			toolTip.add(this.getText("magic_windfield_finish", this.getEffectTip("bleeding").getString()));
			break;
		case 6:
		case 7:
		case 8:
		case 15:
			toolTip.add(this.getText("magic_square_buff", 60 + this.tier * 30).withStyle(WHITE));
			int value = this.data - (this.data == 15 ? 11 : 5);
			toolTip.add(this.getText("magic_rainfield", value));
			toolTip.add(this.getText("magic_rainfield_enemy", value));
			toolTip.add(this.getText("magic_square").withStyle(WHITE));
			toolTip.add(this.getText("magic_rainfield_finish", 12.5F * this.tier));
			break;
		case 9:
		case 10:
		case 11:
		case 16:
			toolTip.add(this.getText("magic_square").withStyle(WHITE));
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_future_visionfiled_enemy", String.format("%.1f", 2F + (this.data == 16 ? 3 : this.data - 9) * 1.5F)));
			toolTip.add(this.getText("magic_future_visionfiled_finish", this.tier));
			break;
		case 12:
			toolTip.add(this.getText("magic_square_buff", 60 + this.tier * 30).withStyle(WHITE));
			toolTip.add(this.getText(this.name + "_enemy"));
			toolTip.add(this.getText("magic_square").withStyle(WHITE));
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_finish"));
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
		entity.setMaxLifeTime(600 + level * 20);

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
		case 12:
			entity.setData(4);
			entity.setTier(4);
			break;
		case 13:
			entity.setData(0);
			entity.setTier(4);
			break;
		case 14:
			entity.setData(1);
			entity.setTier(4);
			break;
		case 15:
			entity.setData(2);
			entity.setTier(4);
			break;
		case 16:
			entity.setData(3);
			entity.setTier(4);
			break;
		}

		entity.acceEffect();

		if (!world.isClientSide()) {
			world.addFreshEntity(entity);
		}

		this.playSound(world, player.blockPosition(), SoundInit.FLASH, 0.25F, 1F);
		return true;
	}
}
