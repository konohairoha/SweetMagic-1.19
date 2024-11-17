package sweetmagic.init.tile.sm;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.CreeperCalamity;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.EnderMage;
import sweetmagic.init.entity.monster.SkullFlame;
import sweetmagic.init.entity.monster.SkullFrost;
import sweetmagic.init.tile.menu.SMSpawnerMenu;

public class TileSMSpawner extends TileAbstractSM {

	private int range = 4;
	private int mobLevel = 1;
	private int mobType = 0;
	protected LivingEntity entity = null;
	private int spawnTick = 0;
	private int maxSpawnTick = 0;

	public boolean isPlayer = false;
	public boolean isPeace = false;

	private static final int MAX_SPAWNTICK = 15;
	private static final int RAND_SPAWNTICK = 8;
	private static final int MAX_SPAWNCOUNT = 5;

	public TileSMSpawner(BlockPos pos, BlockState state) {
		super(TileInit.smSpawner, pos, state);
	}

	public TileSMSpawner(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isPeaceful(world)) { return; }

		// 最大時間が設定されていないなら初期化
		if (this.maxSpawnTick == 0) {
			this.maxSpawnTick = this.rand.nextInt(RAND_SPAWNTICK) + MAX_SPAWNTICK;
		}

		// 最大時間を満たしていないなら終了
		if (!this.checkTick()) { return; }

		// モブスポーン
		this.spawnEntity(world, pos);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		// ピースフルなら終了
		this.isPeace = this.isPeaceful(world);
		if (this.isPeace) { return; }

		this.isPlayer = this.checkPlayer(world, 16D);
	}

	public boolean checkTick () {
		return this.spawnTick++ >= this.maxSpawnTick;
	}

	// モブスポーン
	public void spawnEntity (Level world, BlockPos pos) {

		// 情報の初期化
		this.clearInfo();

		if (this.getMobType() == -1 && this.checkPlayer(world, 48D)) {
			this.setMobType(this.rand.nextInt(this.maxMobType()));
		}

		this.doSpawnEntity(world, pos, this.rand);
	}

	public void doSpawnEntity (Level world, BlockPos pos, Random rand) {
		if (!(world instanceof ServerLevel server) || !this.checkPlayer(world, 16D)) { return; }

		// ブロックごとのスポーンしたモブを取得
		String uniqueName = this.getUniqueTagName();
		int entityListSize = this.getEntityList(LivingEntity.class, e -> e.isAlive() && e.getPersistentData().getBoolean(uniqueName), this.getRange() * 4).size();
		int canSpawnCount = MAX_SPAWNCOUNT - entityListSize;

		// 最大スポーン可能数からランダムにスポーン可能数を設定
		canSpawnCount = canSpawnCount == 0 ? 0 : Math.min(canSpawnCount, rand.nextInt(canSpawnCount) + 1);

		for (int i = 0; i < canSpawnCount; i++) {

			// スポーンする座標を設定
			int count = 0;
			BlockPos spawnPos = pos.offset(this.getRand(rand, this.getRange()), 0.5D, this.getRand(rand, this.getRange()));

			// 空気ブロックの場所になるまで座標を探し続ける
			while (true) {

				if (this.getState(spawnPos).isAir() && this.getState(spawnPos.above()).isAir()) { break; }
				spawnPos = pos.offset(this.getRand(rand, this.getRange()), 0.5D, this.getRand(rand, this.getRange()));

				if (count++ > 16) { break; }
			}

			Mob entity = this.getEntity();
			entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D);
			entity.getPersistentData().putBoolean(uniqueName, true);
			this.setEntityBuff(entity);
			world.addFreshEntity(entity);
			entity.spawnAnim();
		}

		if (canSpawnCount <= 0) { return; }

		for (int i = 0; i < 16; i++) {

			float x = pos.getX() + 0.5F + (rand.nextFloat() * 0.5F + 1F) * (rand.nextBoolean() ? 1F : -1F);
			float y = pos.getY() + rand.nextFloat() * 0.5F + 0.25F;
			float z = pos.getZ() + 0.5F + (rand.nextFloat() * 0.5F + 1F) * (rand.nextBoolean() ? 1F : -1F);
			float aX = this.getRand(rand) * 0.1F;
			float aY = rand.nextFloat() * 0.15F;
			float aZ = this.getRand(rand) * 0.1F;

			server.sendParticles(ParticleInit.NORMAL.get(), x, y, z, 0, aX, aY, aZ, 1F);
		}
	}

	// プレイヤーが周囲にいるかのチェック
	public boolean checkPlayer (Level world, double range) {
		return !this.getEntityListHalf(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), range).isEmpty();
	}

	public int getRand (Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	public float getRand (Random rand) {
		return rand.nextFloat() - rand.nextFloat();
	}

	// 情報の初期化
	public void clearInfo () {
		this.maxSpawnTick = this.rand.nextInt(RAND_SPAWNTICK) + MAX_SPAWNTICK;
		this.tickTime = 0;
		this.spawnTick = 0;
		this.sendPKT();
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
			this.setMobType(Math.min(this.maxMobType() - 1, this.getMobType() + 1));
			break;
		case 5:
			this.setMobType(Math.max(-1, this.getMobType() - 1));
			break;
		}

		this.sendPKT();
		this.clickButton();

		if (id >= 4) {
			this.buttonEntity();
		}
	}

	public void buttonEntity () {
		this.entity = this.getEntity();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.getRange());
		tag.putInt("mobLevel", this.getMobLevel());
		tag.putInt("mobType", this.getMobType());
		tag.putInt("spawnTick", this.spawnTick);
		tag.putInt("maxSpawnTick", this.maxSpawnTick);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setRange(tag.getInt("range"));
		this.setMobLevel(tag.getInt("mobLevel"));
		this.setMobType(tag.getInt("mobType"));
		this.spawnTick = tag.getInt("spawnTick");
		this.maxSpawnTick = tag.getInt("maxSpawnTick");
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

	// レンダー用のえんちちー取得
	public LivingEntity getRenderEntity () {

		if (this.entity == null) {
			this.entity = this.getEntity();
		}

		return this.entity;
	}

	// えんちちー取得
	public Mob getEntity () {

		Level world = this.level;
		Mob entity = null;

		// モブ種類によって設定
		switch (this.getMobType()) {
		case 0:
			entity = new SkullFrost(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 1:
			entity = new EnderMage(world);
			break;
		case 2:
			entity = new BlazeTempest(world);
			break;
		case 3:
			entity = new ArchSpider(world);
			break;
		case 4:
			entity = new CreeperCalamity(world);
			break;
		case 5:
			entity = new SkullFlame(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 6:
			entity = new DwarfZombie(world);
			break;
		}
		return entity;
	}

	public int maxMobType () {
		return 7;
	}

	// モブの名前を取得
	public String getEntityName () {

		if (this.getMobType() == -1) {
			return "random";
		}

		return this.getRenderEntity().getName().getString();
	}

	// モブのバフを設定
	public void setEntityBuff (Mob entity) {

		int level = this.getMobLevel() - 1;
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
		if (level <= 1) { return; }

		float rate = 1F * level * 0.5F;
		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * rate);
		entity.setHealth(entity.getMaxHealth());
		this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, level / 2);
		this.addPotion(entity, PotionInit.aether_barrier, 99999, level / 2);
	}

	// ユニーク名を取得
	public String getUniqueTagName () {
		BlockPos pos = this.getBlockPos();
		return pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new SMSpawnerMenu(windowId, inv, this);
	}
}
