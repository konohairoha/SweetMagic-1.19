package sweetmagic.init.tile.sm;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.AABB;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.SpawnCrystal;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.ElectricCube;
import sweetmagic.init.entity.monster.EnderMage;
import sweetmagic.init.entity.monster.SkullFlame;
import sweetmagic.init.entity.monster.SkullFrost;
import sweetmagic.init.tile.menu.SpawnCrystalMenu;

public class TileSpawnCrystal extends TileAbstractSM {

	public int range = 7;

	public TileSpawnCrystal(BlockPos pos, BlockState state) {
		super(TileInit.spawn_crystal, pos, state);
	}

	public TileSpawnCrystal(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isPeaceful(world)) { return; }

		List<Player> playerList = this.getEntityListUp(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), this.range);
		if (playerList.isEmpty()) { return; }

		SpawnCrystal crystal = (SpawnCrystal) this.getBlock(pos);
		int data = crystal.getData();
		this.spawnMob(world, pos, data);
	}

	public void spawnMob(Level world, BlockPos pos, int data) {

		int count = 0;
		Random rand = this.rand;
		BlockPos summonPos = new BlockPos(pos.getX() + this.getRand(rand, this.range), pos.getY(), pos.getZ() + this.getRand(rand, this.range));

		while (!world.isEmptyBlock(summonPos) || world.isEmptyBlock(summonPos.below())) {
			summonPos = new BlockPos(pos.getX() + this.getRand(rand, this.range), pos.getY(), pos.getZ() + this.getRand(rand, this.range));
			if (count++ >= 16) { break; }
		}

		int spawnMobType = rand.nextInt(7);
		int summonSize = this.getSummonSize(data);

		int playerAllListSize = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), 80D).size() - 1;
		if (playerAllListSize > 0) {
			summonSize *= (1F + playerAllListSize * 0.5F);
		}

		int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size();
		float addHealth = 1F + summonMobSize * 0.05F;

		for (int i = 0; i < summonSize; i++) {

			// 召喚するモブの座標を設定
			boolean isZero = i % this.getSummonSize(data) == 0;
			BlockPos secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));
			count = 0;

			// 座標がブロックだった場合は再設定
			while (!world.isEmptyBlock(secondPos) || world.isEmptyBlock(summonPos.below())) {
				secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));
				if (count++ >= 16) { break; }
			}

			// モブ召喚を記載
			LivingEntity entity = isZero ? this.setBigMob(world, spawnMobType, data, addHealth) : this.setMob(world, spawnMobType, data, addHealth);
			entity.setPos(secondPos.getX() + 0.5D, secondPos.getY() + 0.5D, secondPos.getZ() + 0.5D);
			world.addFreshEntity(entity);

			((ISMMob) entity).refreshInfo();
		}

		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		this.playSound(pos, SoundInit.HORAMAGIC, 0.15F, 1F);
	}

	// 雑魚モブの設定
	public LivingEntity setMob(Level world, int spawnMobType, int data, float addHealth) {

		LivingEntity entity = null;

		switch (spawnMobType) {
		case 0:
			entity = new BlazeTempest(world);
			break;
		case 1:
			entity = new EnderMage(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 2:
			entity = new SkullFrost(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 3:
			entity = new ArchSpider(world);
			break;
		case 4:
			entity = new ElectricCube(world);
			((ElectricCube) entity).setSize(4);
			((ElectricCube) entity).setFixed();
			break;
		case 5:
			entity = new SkullFlame(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 6:
			entity = new DwarfZombie(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMobHealth(data) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.setMobBuff(entity, data);
		return entity;
	}

	// 雑魚モブの設定
	public LivingEntity setBigMob(Level world, int spawnMobType, int data, float addHealth) {

		LivingEntity entity = null;

		switch (spawnMobType) {
		case 0:
			entity = new BlazeTempest(world);
			break;
		case 1:
			entity = new EnderMage(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 2:
			entity = new SkullFrost(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			break;
		case 3:
			entity = new ArchSpider(world);
			break;
		case 4:
			entity = new ElectricCube(world);
			((ElectricCube) entity).setSize(8);
			((ElectricCube) entity).setFixed();
			this.addPotion(entity, PotionInit.leader_flag, 99999, 0);
			break;
		case 5:
			entity = new SkullFlame(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 6:
			entity = new DwarfZombie(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getBigMobHealth(data) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.setBigMobBuff(entity, data);
		return entity;
	}

	public int getRand(Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	public int getSummonSize(int data) {
		switch (data) {
		case 1: return 6;
		case 2: return 8;
		case 3: return 9;
		case 4: return 10;
		case 5: return 12;
		default: return 4;
		}
	}

	public double getMobHealth(int data) {
		switch (data) {
		case 1: return 50D;
		case 2: return 70D;
		case 3: return 100D;
		case 4: return 120D;
		case 5: return 160D;
		default: return 30D;
		}
	}

	public void setMobBuff(LivingEntity entity, int data) {
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
		if (data >= 1) {
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, data);
			this.addPotion(entity, PotionInit.aether_barrier, 4800, data);
		}
	}

	public void setBigMobBuff(LivingEntity entity, int data) {
		this.addPotion(entity, PotionInit.leader_flag, 99999, 0);
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
		this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 1 + (data * 2) );
	}

	public double getBigMobHealth(int data) {
		switch (data) {
		case 1: return 120D;
		case 2: return 150D;
		case 3: return 170D;
		case 4: return 200D;
		case 5: return 240D;
		default: return 70D;
		}
	}

	// 範囲の取得
	public AABB getAABB(double x, double y, double z) {
		BlockPos pos = this.getBlockPos();
		return new AABB(pos.offset(-x, 0, -z), pos.offset(x, y, z));
	}

	// ボタンクリック
	public void clickButton(int id) {
		switch (id) {
		case 0:
			this.setRange(Math.min(16, this.getRange() + 1));
			break;
		case 1:
			this.setRange(Math.max(1, this.getRange() - 1));
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
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("range")) {
			this.setRange(tag.getInt("range"));
		}
	}

	// スポーン範囲の取得
	public int getRange() {
		return this.range;
	}

	// スポーン範囲の設定
	public void setRange(int range) {
		this.range = range;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new SpawnCrystalMenu(windowId, inv, this);
	}
}
