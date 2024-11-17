package sweetmagic.init.tile.sm;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.DwarfZombieMaster;
import sweetmagic.init.entity.monster.EnderMage;
import sweetmagic.init.entity.monster.EnderShadow;
import sweetmagic.init.entity.monster.SkullFlame;
import sweetmagic.init.entity.monster.SkullFlameArcher;
import sweetmagic.init.entity.monster.SkullFrost;
import sweetmagic.init.entity.monster.SkullFrostRoyalGuard;
import sweetmagic.init.entity.monster.WindWitch;
import sweetmagic.init.tile.menu.SpawnStoneMenu;

public class TileSpawnStone extends TileAbstractSM {

	private int range = 4;
	private int mobLevel = 1;
	private int mobType = 0;

	public boolean isPlayer = false;
	public boolean isPeace = false;

	public TileSpawnStone(BlockPos pos, BlockState state) {
		super(TileInit.spawnStone, pos, state);
	}

	public TileSpawnStone(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isPeaceful(world)) { return; }

		List<Player> playerList = this.getEntityListHalf(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), this.getRange() * 2);
		if (playerList.isEmpty()) { return; }

		this.spawnMob(world, pos, this.getMobLevel());
	}

	public void spawnMob (Level world, BlockPos pos, int data) {

		int count = 0;
		Random rand = this.rand;
		BlockPos summonPos = new BlockPos(pos.getX() + this.getRand(rand, this.range), pos.getY(), pos.getZ() + this.getRand(rand, this.range));

		while (!world.getBlockState(summonPos).isAir() || world.getBlockState(summonPos.below()).isAir()) {
			summonPos = new BlockPos(pos.getX() + this.getRand(rand, this.range), pos.getY(), pos.getZ() + this.getRand(rand, this.range));
			if (count++ >= 16) { break; }
		}

		int spawnMobType = this.getMobType();

		if (spawnMobType == -1) {
			spawnMobType = rand.nextInt(6);
		}

		int summonSize = 4;

		int playerAllListSize = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), 80D).size() - 1;
		if (playerAllListSize > 0) {
			summonSize *= (1F + playerAllListSize * 0.1F);
		}

		int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size();
		float addHealth = 1F + summonMobSize * 0.1F;

		for (int i = 0; i < summonSize; i++) {

			boolean isZero = i % 4 == 0;

			// 召喚するモブの座標を設定
			BlockPos secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));
			count = 0;

			// 座標がブロックだった場合は再設定
			while (!world.getBlockState(secondPos).isAir() || world.getBlockState(secondPos.below()).isAir()) {
				secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));
				if (count++ >= 16) { break; }
			}

			// モブ召喚を記載
			LivingEntity entity = isZero ? this.setBigMob(world, spawnMobType, data, addHealth) : this.setMob(world, spawnMobType, data, addHealth);
			entity.setPos(secondPos.getX() + 0.5D, secondPos.getY() + 0.5D, secondPos.getZ() + 0.5D);
			world.addFreshEntity(entity);

			( (ISMMob) entity).refreshInfo();
		}

		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		this.playSound(pos, SoundInit.HORAMAGIC, 0.15F, 1F);
	}


	// 雑魚モブの設定
	public LivingEntity setMob (Level world, int spawnMobType, int mobLevel, float addHealth) {

		LivingEntity entity = null;

		switch (spawnMobType) {
		case 1:
			entity = new SkullFlame(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 2:
			entity = new BlazeTempest(world);
			break;
		case 3:
			entity = new EnderMage(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 4:
			entity = new DwarfZombie(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
			break;
		case 5:
			entity = new WindWitch(world);
			this.addPotion(entity, MobEffects.WEAKNESS, 99999, 0);
			break;
		default:
			entity = new SkullFrost(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + mobLevel * 0.5F) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.setMobBuff(entity, mobLevel);
		return entity;
	}

	// 雑魚モブの設定
	public LivingEntity setBigMob (Level world, int spawnMobType, int mobLevel, float addHealth) {

		LivingEntity entity = null;

		switch (spawnMobType) {
		case 1:
			entity = new SkullFlameArcher(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
			break;
		case 2:
			entity = new BlazeTempestTornado(world);
			break;
		case 3:
			entity = new EnderShadow(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			break;
		case 4:
			entity = new DwarfZombieMaster(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_sword));
			break;
		case 6:
			entity = new WindWitch(world);
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 1);
			break;
		default:
			entity = new SkullFrostRoyalGuard(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * (1F + mobLevel * 0.5F) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.setBigMobBuff(entity, mobLevel);
		return entity;
	}

	// モブの名前を取得
	public String getEntityName () {

		String text = "random";

		switch (this.getMobType()) {
		case 0:
			text = "skullflost_royalguard";
			break;
		case 1:
			text = "skullflame_archer";
			break;
		case 2:
			text = "blazetempest_tornado";
			break;
		case 3:
			text = "endershadow";
			break;
		case 4:
			text = "dwarfzombie_master";
			break;
		}

		return Component.translatable("entity.sweetmagic." + text).getString();
	}

	public void setMobBuff(LivingEntity entity, int mobLevel) {
		this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, mobLevel);
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
	}

	public void setBigMobBuff (LivingEntity entity, int mobLevel) {
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
		this.addPotion(entity, PotionInit.aether_armor, 200, 4);
	}

	// ボタンクリック
	public void clickButton (int id) {
		switch (id) {
		case 0:
			this.setRange(Math.min(16, this.getRange() + 1));
			break;
		case 1:
			this.setRange(Math.max(1, this.getRange() - 1));
			break;
		case 2:
			this.setMobLevel(Math.min(10, this.getMobLevel() + 1));
			break;
		case 3:
			this.setMobLevel(Math.max(1, this.getMobLevel() - 1));
			break;
		case 4:
			this.setMobType(Math.min(4, this.getMobType() + 1));
			break;
		case 5:
			this.setMobType(Math.max(-1, this.getMobType() - 1));
			break;
		}

		this.sendPKT();
		this.clickButton();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.getRange());
		tag.putInt("mobLevel", this.getMobLevel());
		tag.putInt("mobType", this.getMobType());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setRange(tag.getInt("range"));
		this.setMobLevel(tag.getInt("mobLevel"));
		this.setMobType(tag.getInt("mobType"));
	}

	public int getRand (Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	// スポーン範囲の取得
	public int getRange () {
		return this.range;
	}

	// スポーン範囲の設定
	public void setRange (int range) {
		this.range = range;
	}

	// モブレベルの取得
	public int getMobLevel () {
		return this.mobLevel;
	}

	// モブレベルの設定
	public void setMobLevel (int level) {
		this.mobLevel = level;
	}

	// モブ種類の取得
	public int getMobType () {
		return this.mobType;
	}

	// モブ種類の設定
	public void setMobType (int mobType) {
		this.mobType = mobType;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new SpawnStoneMenu(windowId, inv, this);
	}
}
