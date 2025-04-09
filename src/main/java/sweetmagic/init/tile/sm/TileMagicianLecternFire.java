package sweetmagic.init.tile.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.entity.monster.boss.IgnisKnight;

public class TileMagicianLecternFire extends TileAbstractMagicianLectern {

	private static final ItemStack ACCE = new ItemStack(ItemInit.ignis_soul);

	public TileMagicianLecternFire(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileMagicianLecternFire(BlockPos pos, BlockState state) {
		super(TileInit.magicianLecternFire, pos, state);
	}

	// ボス召喚
	public void summonBoss (Level world, BlockPos pos, float addHealth) {

		int rate = this.isHard ? 8 : 4;

		Monster entity = new IgnisKnight(world);
		entity.setPos(pos.getX() + 2.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 4);
		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * addHealth);

		if (this.isHard) {
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 3);
			this.addPotion(entity, PotionInit.reflash_effect, 99999, 0);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(800D * addHealth);
		}

		List<Player> playerList = this.getPlayer(Player.class);
		IgnisKnight ignis = (IgnisKnight) entity;
		ignis.clearInfo();
		ignis.setArmor(playerList.size() * rate);
		ignis.setLectern(true);
		ignis.setSpawnPos(pos.above());
		entity.setHealth(entity.getMaxHealth());
		world.addFreshEntity(ignis);

		this.boss = ignis;
		this.sendPKT();
	}

	public ItemStack getStack () {
		return ACCE;
	}

	public String deadTip () {
		return "smDeadFire";
	}

	public int getBattleLevel () {
		return 3;
	}

	public int getMaxMF() {
		return 0;
	}
}
