package sweetmagic.init.tile.sm;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.monster.boss.AbstractSMBoss;
import sweetmagic.init.entity.monster.boss.AncientFairy;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;
import sweetmagic.init.entity.monster.boss.BullFight;
import sweetmagic.init.entity.monster.boss.ElshariaCurious;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.entity.monster.boss.TwilightHora;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.entity.monster.boss.WitchSandryon;

public class TileSMSpawnerBoss extends TileSMSpawner {

	public TileSMSpawnerBoss(BlockPos pos, BlockState state) {
		super(TileInit.smSpawnerBoss, pos, state);
	}

	public TileSMSpawnerBoss(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public boolean checkTick () {
		return this.tickTime % 20 != 0;
	}

	// プレイヤーが周囲にいるかのチェック
	public boolean checkPlayer (Level world, double range) {
		return !this.getEntityListUp(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), range).isEmpty();
	}

	public void doSpawnEntity (Level world, BlockPos pos, Random rand) {
		if (!(world instanceof ServerLevel server) || !this.checkPlayer(world, this.getRange())) { return; }

		AbstractSMBoss entity = (AbstractSMBoss) this.getEntity(false);
		entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);

		this.setEntityBuff(entity);
		this.startInfo(entity);
		world.addFreshEntity(entity);
		entity.spawnAnim();
		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		this.startInfo(entity);
	}

	// モブのバフを設定
	public void setEntityBuff (Mob entity) {
		int level = this.getMobLevel() - 1;
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 4);
		if (level <= 1) { return; }

		this.addPotion(entity, PotionInit.reflash_effect, 99999, 0);
	}

	// えんちちー取得
	public Mob getEntity (boolean isRender) {

		Level world = this.level;
		AbstractSMBoss entity = null;
		LivingEntity sub = null;
		List<Player> playerList = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), 32D);
		int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size() - 1;
		float addHealth = 1F + summonMobSize * 0.1F;

		switch (this.getMobType()) {
		case 0:
			entity = new BullFight(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.25F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 1:
			entity = new AncientFairy(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 2:
			entity = new Arlaune(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 3:
			entity = new SilverLandRoad(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			((SilverLandRoad) entity).setAlive(true);
			sub = this.addSpawn(isRender);
			break;
		case 4:
			entity = new TwilightHora(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 5:
			entity = new BraveSkeleton(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());

			if (!isRender) {
				sub = ((BraveSkeleton) entity).spawnHorse(this.level, this.getBlockPos().above());
			}
			break;
		case 6:
			entity = new ElshariaCurious(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 7:
			entity = new WitchSandryon(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 8:
			entity = new QueenFrost(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(512D * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 9:
			entity = new HolyAngel(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(512D * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 10:
			entity = new IgnisKnight(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(512D * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 11:
			entity = new WindWitchMaster(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(768D * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		case 12:
			entity = new BlitzWizardMaster(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(768D * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			break;
		}

		if (sub != null) {
			if (sub instanceof AbstractSMBoss boss) {
				boss.setOwnerID(entity);
				entity.setOwnerID(boss);
			}
		}

		return entity;
	}

	public void startInfo(AbstractSMBoss mob) {
		mob.startInfo();
	}

	public AbstractSMBoss addSpawn (boolean isRender) {
		if (isRender) { return null; }

		AbstractSMBoss entity = null;
		Level world = this.level;
		List<Player> playerList = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), 32D);
		int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size();
		float addHealth = 1F + summonMobSize * 0.1F;

		switch (this.getMobType()) {
		case 3:
			entity = new WhiteButler(world);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + playerList.size() * 0.15F) * addHealth);
			entity.setHealth(entity.getMaxHealth());
			((WhiteButler) entity).setAlive(true);
			break;
		}

		BlockPos pos = this.getBlockPos();
		entity.setPos(pos.getX() + 1.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
		this.setEntityBuff(entity);
		entity.startInfo();
		world.addFreshEntity(entity);
		entity.spawnAnim();
		entity.startInfo();
		return entity;
	}

	public void buttonEntity () {
		this.entity = this.getEntity(true);
	}

	// レンダー用のえんちちー取得
	public LivingEntity getRenderEntity () {

		if (this.entity == null) {
			this.entity = this.getEntity(true);
		}

		return this.entity;
	}

	public int maxMobType () {
		return 13;
	}
}
