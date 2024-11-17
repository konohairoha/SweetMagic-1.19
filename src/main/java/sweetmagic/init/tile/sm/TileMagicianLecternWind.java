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
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.entity.monster.WitchCrystal;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;

public class TileMagicianLecternWind extends TileAbstractMagicianLectern {

	private static final ItemStack ACCE = new ItemStack(ItemInit.wind_relief);

	public TileMagicianLecternWind(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileMagicianLecternWind(BlockPos pos, BlockState state) {
		super(TileInit.magicianLecternWind, pos, state);
	}

	// ボス召喚
	public void summonBoss (Level world, BlockPos pos, float addHealth) {

		int rate = this.isHard ? 5 : 3;

		Monster entity = new WindWitchMaster(world);
		entity.setPos(pos.getX() + 2.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * addHealth);
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 4);

		if (this.isHard) {
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 4);
			this.addPotion(entity, PotionInit.reflash_effect, 99999, 0);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(900D * addHealth);
		}

		List<Player> playerList = this.getPlayer(Player.class);
		WindWitchMaster witch = (WindWitchMaster) entity;
		witch.clearInfo();
		witch.setLectern(true);
		witch.setSpawnPos(pos.above());
		entity.setHealth(entity.getMaxHealth());
		world.addFreshEntity(witch);

		for (int i = 0; i < playerList.size() * rate; i++) {

			WitchCrystal crystal = new WitchCrystal(world);

			int setPosCount = 0;
			BlockPos targetPos = new BlockPos(pos.getX() + this.getRand(this.rand, 16), pos.getY(), pos.getZ() + this.getRand(this.rand, 16));

			// 座標がブロックだった場合は再設定
			while (!world.getBlockState(targetPos).isAir() && !world.getBlockState(targetPos).is(BlockInit.rune_character)) {
				targetPos = new BlockPos(targetPos.getX() + this.getRand(this.rand, 3), targetPos.getY(), targetPos.getZ() + this.getRand(this.rand, 3));

				if (setPosCount++ >= 16) { break; }
			}

			if (this.isHard) {
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60D);
				entity.setHealth(entity.getMaxHealth());
			}

			crystal.setPos(targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D);
			crystal.setOwnerID(witch);
			world.addFreshEntity(crystal);
		}

		witch.setArmor(playerList.size() * rate);
		witch.setAetherBattier();
		this.boss = witch;
		this.sendPKT();
	}

	public ItemStack getStack () {
		return ACCE;
	}

	public String deadTip () {
		return "smDeadFire";
	}

	public int getBattleLevel () {
		return 4;
	}
}
