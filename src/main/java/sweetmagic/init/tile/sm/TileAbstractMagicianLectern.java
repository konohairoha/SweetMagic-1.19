package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.monster.ArchSpider;
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
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.util.SMDamage;

public abstract class TileAbstractMagicianLectern extends TileSMMagic implements ISMTip {

	public int tileTime = 0;
	public int oldCharge = 8;
	public int wave = 1;
	public float chargeSummonSize = 0F;
	public int dethMobCount = 0;
	public int summonMaxCount = 24;
	public int summonCount = 0;
	public int waitTime = 0;
	public int maxWaitTime = 0;
	public int chageTime = 0;
	public boolean deathBoss = false;
	public boolean isHard = false;
	public boolean isButtle = false;
	public SummonType summonType = SummonType.START;
	public ItemStack stack = ItemStack.EMPTY;
	public Monster boss = null;
	public UUID bossId = null;
	protected final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_12)).setDarkenScreen(true);

	public TileAbstractMagicianLectern(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.isPeaceful(world)) { return; }

		if (this.summonType.is(SummonType.CHARGE) || this.summonType.is(SummonType.SUMMON)) {
			this.tileTime++;
			this.sendPKT();
		}

		if (this.tickTime % 3 != 0 || this.summonType.is(SummonType.START) || this.summonType.is(SummonType.END)) { return; }

		if (this.boss == null && this.bossId != null) {
			this.boss = (Monster) ((ServerLevel) this.level).getEntity(this.bossId);
		}

		if ( this.summonType.is(SummonType.SUMMON) || !(this.wave == 4 && !this.summonType.is(SummonType.CHARGE)) ) {
			this.addPlayerBossBar(world, pos);
		}

		// チャージ中以外ならボスゲージを設定
		if (this.summonType.is(SummonType.SUMMON)) {

			this.setBossBar(world, pos);

			// 最大召喚数を満たしてないなら召喚
			if (this.tickTime % this.getSummonInterval() == 0 && ( this.dethMobCount < this.summonMaxCount || this.wave >= 4 ) ) {
				this.onSummonMob(world, pos);
			}

			// 敵モブをすべて倒したら次のウェーブへ以降
			if ( ( this.dethMobCount >= this.summonMaxCount && this.wave <= 3 ) || ( this.wave >= 4 && this.deathBoss ) ) {
				this.setNextWave(world, pos);
			}
		}

		// 待機中なら次へ
		else if (this.summonType.is(SummonType.WAIT)) {

			this.waitTime++;

			if (this.waitTime >= this.maxWaitTime) {
				this.summonType = SummonType.CHARGE;

				if (this.wave >= 4) {
					this.addPlayerBossBar(world, pos);
					this.bossEvent.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);
					this.bossEvent.setColor(BossEvent.BossBarColor.BLUE);
				}
			}

			this.sendPKT();
		}

		// チャージ中なら
		else if (this.summonType.is(SummonType.CHARGE)) {
			this.chargeBossBar(world, pos);
		}
	}

	// Waveの最大召喚数の設定
	public void setMobSize(Level wrold, BlockPos pos) {

		List<ServerPlayer> playerList = this.getPlayer(ServerPlayer.class);

		int baseWave = 16;
		int playerAddWave = 8;

		switch (this.wave) {
		case 1:
			baseWave = 16;
			playerAddWave = 8;
			break;
		case 2:
			baseWave = 20;
			playerAddWave = 16;
			break;
		case 3:
			baseWave = 24;
			playerAddWave = 24;
			break;
		case 4:
			baseWave = 24;
			playerAddWave = 16;
			break;
		}

		this.summonMaxCount = baseWave + playerAddWave * (playerList.size() - 1);
	}

	// ボスケージをプレイヤーに付与
	public void addPlayerBossBar(Level wrold, BlockPos pos) {

		List<ServerPlayer> playerList = this.getPlayer(ServerPlayer.class);
		List<? extends Player> outrangePlayerList = wrold.players().stream().filter(e -> !playerList.contains(e)).toList();
		playerList.forEach(p -> this.bossEvent.addPlayer(p));

		for (Player player : outrangePlayerList) {
			if (!(player instanceof ServerPlayer server)) { return; }
			this.bossEvent.removePlayer(server);
		}

		if (this.wave >= 4) {
			this.bossEvent.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);
			this.bossEvent.setColor(BossEvent.BossBarColor.BLUE);
		}
	}

	// ボスケージをプレイヤーに付与
	public void removeAllPlayerBossBar() {
		this.bossEvent.removeAllPlayers();
	}

	// ボスケージ設定
	public void setBossBar (Level world, BlockPos pos) {

		if (this.wave >= 4) {
			if (this.boss == null) { return; }

			float gage = (float) this.boss.getHealth() / (float) this.boss.getMaxHealth();
			this.bossEvent.setProgress(gage);
			this.bossEvent.setName(this.boss.getDisplayName());
			this.deathBoss = !this.boss.isAlive();

			if (!this.deathBoss) { return; }

			// 召喚したモブを消す
			List<Monster> mobList = this.getMobList();

			for (Monster entity : mobList) {
				entity.setHealth(1F);
				entity.hurt(SMDamage.MAGIC, 999F);
			}

			// 破魔矢のえんちちーアイテムを消す
			List<ItemEntity> entityItemList = this.getEntityList(ItemEntity.class, e -> e.getItem().is(ItemInit.evil_arrow), 128D);
			entityItemList.forEach(e -> e.discard());

			// プレイヤーのインベントリに入った破魔矢のアイテムを消す
			List<Player> playerList = this.getPlayer(Player.class);

			for (Player player : playerList) {
				List<ItemStack> stackList = player.getInventory().items.stream().filter(s -> !s.isEmpty() && s.is(ItemInit.evil_arrow)).toList();
				stackList.forEach(s -> s.shrink(s.getCount()));
			}
		}

		else {

			// 召喚した周囲の死亡した　敵モブ取得
			List<Monster> entityDeadList = this.getEntityList(Monster.class, e -> !e.isAlive() && !e.getPersistentData().getBoolean(this.deadTip()) && e.hasEffect(PotionInit.darkness_fog), 128D);
			this.dethMobCount += entityDeadList.size();

			for (Monster monster : entityDeadList) {
				monster.getPersistentData().putBoolean(this.deadTip(), true);
				this.spawnDeathParticle(world, pos, monster);
			}

			this.bossEvent.setProgress((float) (this.summonMaxCount - this.dethMobCount) / (float) this.summonMaxCount);
			this.bossEvent.setName(this.getWaveName());
			if (this.dethMobCount < (this.summonMaxCount - 3)) { return; }

			List<Monster> entityList = this.getMobList();
			entityList.forEach(e -> this.addPotion(e, MobEffects.GLOWING, 99999, 0));
		}
	}

	//　周囲のプレイヤー取得
	public <T extends Player> List<T> getPlayer(Class<T> enClass) {
		return this.getEntityList(enClass, p -> !p.isSpectator() && p.isAlive(), 64D);
	}

	// 召喚した周囲の敵モブ取得
	public List<Monster> getMobList() {
		return this.getEntityList(Monster.class, e -> e.isAlive() && e.hasEffect(PotionInit.darkness_fog) && !(e instanceof QueenFrost), 128D);
	}

	public abstract String deadTip();

	// ウェーブの表記設定
	public MutableComponent getWaveName() {

		MutableComponent tip = null;

		if (this.wave != 4) {
			tip = this.getTipArray(this.getTip("Wave " + this.wave + ": "), this.getTip(this.dethMobCount + "/" + this.summonMaxCount));
		}

		return tip;
	}

	// ボスケージのチャージ中
	public void chargeBossBar(Level world, BlockPos pos) {

		this.chageTime++;
		float chargeGage = this.chargeSummonSize + (this.summonMaxCount / 80F);
		this.chargeSummonSize = chargeGage;
		this.bossEvent.setProgress(Math.min(chargeGage, this.summonMaxCount) / (float) this.summonMaxCount);
		int time = (80 - this.chageTime) / 10 + 1;

		if (time <= (this.oldCharge - 1)) {
			this.playSound(pos, SoundEvents.COPPER_PLACE, 1F, 1F);
			this.oldCharge = time;
		}

		String name = this.summonType.is(SummonType.CHARGE) ? time + "sec" : "";
		String wave = "Wave " + this.wave;

		if (this.wave == 3) {
			wave = "Final Wave ";
		}

		else if (this.wave == 4) {
			wave = "Extra Wave ";
		}

		this.bossEvent.setName(this.getTipArray(this.getTip(wave + " : "), this.getText("preparing"), this.getTip(" " + name)));
		this.spawnParticle(world, pos);
		if (chargeGage < this.summonMaxCount) { return; }


		if (this.wave >= 4) {
			int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size();
			float addHealth = 1F + summonMobSize * (0.05F * this.getBattleLevel());
			this.summonBoss(world, pos, addHealth);
			this.playSound(pos, SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5), 1F, 0.875F);
		}

		else {
			this.playSound(pos, SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2), 1F, 0.875F);
		}

		this.summonType = SummonType.SUMMON;
		this.tickTime = -1;
		this.sendPKT();
	}

	// モブ召喚
	public void onSummonMob(Level world, BlockPos pos) {
		int value = this.wave == 4 ? 4 : 8;
		List<Monster> mobList = this.getMobList();
		if (mobList.size() >= value) { return; }

		Random rand = this.rand;
		List<ServerPlayer> playerList = this.getPlayer(ServerPlayer.class);
		int count = 4 * playerList.size();
		BlockPos summonPos = new BlockPos(pos.getX() + this.getRand(rand, 16), pos.getY(), pos.getZ() + this.getRand(rand, 16));

		while (!world.getBlockState(summonPos).isAir() && !world.getBlockState(summonPos).is(BlockInit.rune_character)) {
			summonPos = new BlockPos(pos.getX() + this.getRand(rand, 12), pos.getY(), pos.getZ() + this.getRand(rand, 12));
		}

		int summonMobSize = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive(), 80D).size();
		float addHealth = 1F + summonMobSize * (0.05F * this.getBattleLevel());

		for (int i = 0; i < count; i++) {

			// wave3以下なら最大湧き数に満たしたら終了
			if ( this.dethMobCount >= this.summonMaxCount && this.wave <= 3) { return; }

			// モブ召喚を記載
			boolean isZero = i % 4 == 0;
			Monster entity = isZero ? this.setBigMob(world, rand, addHealth) : this.setMob(world, rand, addHealth);
			int setPosCount = 0;

			if (isZero) {

				summonPos = new BlockPos(pos.getX() + this.getRand(rand, 24), pos.getY(), pos.getZ() + this.getRand(rand, 24));

				while (!world.getBlockState(summonPos).isAir() && !world.getBlockState(summonPos).is(BlockInit.rune_character)) {
					summonPos = new BlockPos(pos.getX() + this.getRand(rand, 24), pos.getY(), pos.getZ() + this.getRand(rand, 24));

					if (setPosCount++ >= 16) { break; }
				}
			}

			// 召喚するモブの座標を設定
			BlockPos secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));
			setPosCount = 0;

			// 座標がブロックだった場合は再設定
			while (!world.getBlockState(secondPos).isAir() && !world.getBlockState(secondPos).is(BlockInit.rune_character)) {
				secondPos = new BlockPos(summonPos.getX() + this.getRand(rand, 3), summonPos.getY(), summonPos.getZ() + this.getRand(rand, 3));

				if (setPosCount++ >= 16) { break; }
			}

			entity.setPos(secondPos.getX() + 0.5D, secondPos.getY() + 0.5D, secondPos.getZ() + 0.5D);
			entity.setTarget(playerList.get(rand.nextInt(playerList.size())));
			world.addFreshEntity(entity);

			this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
			entity.refreshDimensions();
			this.summonCount++;
			this.sendPKT();
		}
	}

	protected int getRand(Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	// 召喚間隔
	public int getSummonInterval() {
		switch (this.wave) {
		case 2: return 168;
		case 3: return 144;
		case 4: return 210;
		default: return 192;
		}
	}

	// 雑魚モブの設定
	public Monster setMob(Level world, Random rand, float addHealth) {

		Monster entity = null;

		switch (rand.nextInt(6)) {
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
			entity = new SkullFlame(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			break;
		case 5:
			entity = new DwarfZombie(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue( (this.isHard ? 30D : 20D) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.addPotion(entity, PotionInit.darkness_fog, 99999, 0);

		if (this.isHard) {
			this.addPotion(entity, PotionInit.aether_barrier, 99999, 0);
		}

		return entity;
	}

	// 雑魚モブの設定
	public Monster setBigMob(Level world, Random rand, float addHealth) {

		Monster entity = null;

		switch (rand.nextInt(5)) {
		case 0:
			entity = new SkullFrostRoyalGuard(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
			break;
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
			this.addPotion(entity, MobEffects.MOVEMENT_SLOWDOWN, 99999, 1);
			break;
		case 4:
			entity = new DwarfZombieMaster(world);
			entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_sword));
			this.addPotion(entity, MobEffects.MOVEMENT_SLOWDOWN, 99999, 2);
			break;
		}

		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue( (this.isHard ? 40D : 20D) * addHealth);
		entity.setHealth(entity.getMaxHealth());
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 2);
		this.addPotion(entity, PotionInit.darkness_fog, 99999, 0);
		this.addPotion(entity, PotionInit.leader_flag, 99999, this.wave == 4 ? 1 : 0);

		if (this.isHard) {
			this.addPotion(entity, PotionInit.aether_barrier, 99999, 3);
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 2);
		}

		else {
			this.addPotion(entity, MobEffects.WEAKNESS, 99999, 0);
		}

		return entity;
	}

	// ボス召喚
	public abstract void summonBoss(Level world, BlockPos pos, float addHealth);

	// 次のウェーブを設定
	public void setNextWave(Level world, BlockPos pos) {

		if (this.wave >= 4) {

			this.removeAllPlayerBossBar();
			this.summonType = SummonType.END;
			this.oldCharge = 8;
			BlockPos upPos = pos.above(2);

			if (!world.getBlockState(upPos).is(BlockInit.treasure_chest)) {
				world.setBlock(upPos, BlockInit.treasure_chest.defaultBlockState(), 3);
			}

			TileWoodChest tile = (TileWoodChest) this.getTile(upPos);
			List<Player> playerList = this.getPlayer(Player.class);
			int addDrop = this.isHard ? 2 : 1;
			int size = playerList.size() * addDrop;

			List<ItemStack> stackList = new ArrayList<>();
			stackList.add(new ItemStack(ItemInit.magic_aether_force, (3 + this.rand.nextInt(5)) * size));
			stackList.add(new ItemStack(ItemInit.magic_divine_force, (1 + this.rand.nextInt(3)) * size));

			if (this.isHard) {
				stackList.add(new ItemStack(ItemInit.magic_pure_force, (1 + this.rand.nextInt(4)) * size / 2));
			}

			stackList.add(new ItemStack(ItemInit.divine_crystal, (4 + this.rand.nextInt(7)) * addDrop));
			stackList.add(new ItemStack(ItemInit.pure_crystal, (1 + this.rand.nextInt(3)) * addDrop));
			stackList.add(new ItemStack(ItemInit.magia_bottle, this.rand.nextInt(3) * addDrop));
			stackList.add(new ItemStack(ItemInit.witch_tears, (3 + this.rand.nextInt(4)) * addDrop));

			if (this.isHard) {
				stackList.add(new ItemStack(ItemInit.magia_bottle, (1 + this.rand.nextInt(2)) * addDrop));
				stackList.add(new ItemStack(ItemInit.cosmic_crystal_shard, 1 * addDrop));
				stackList.add(new ItemStack(ItemInit.wish_crystal, 1 * addDrop));
			}

			stackList.add(new ItemStack(BlockInit.sturdust_crystal, 1));
			stackList.forEach(s -> ItemHandlerHelper.insertItemStacked(tile.getInput(), s.copy(), false));
			this.playSound(this.getTilePos(), SoundEvents.PLAYER_LEVELUP, 1F, 1F);

			return;
		}

		this.wave++;
		this.summonCount = 0;
		this.dethMobCount = 0;
		this.chargeSummonSize = 0F;
		this.chageTime = 0;
		this.oldCharge = 8;

		if (this.wave <= 4) {

			// 召喚したモブを消す
			List<Monster> mobList = this.getMobList();
			mobList.forEach(e -> e.discard());
			this.summonType = SummonType.WAIT;

			switch (this.wave) {
			case 2:
				this.waitTime = 0;
				this.maxWaitTime = 20;
				break;
			case 3:
				this.waitTime = 0;
				this.maxWaitTime = 20;
				break;
			case 4:
				this.waitTime = 0;
				this.maxWaitTime = 50;
				this.removeAllPlayerBossBar();
				break;
			}

			this.setMobSize(world, pos);
			this.sendPKT();
		}
	}

	public void spawnParticle(Level world, BlockPos pos) {
		if ( !(world instanceof ServerLevel server) ) { return; }

		float addY = Math.min(2F, this.tileTime * 0.0125F);

		for (int i = 0; i < 2; i++) {

			float randX = this.getRand(this.rand, 4);
			float randY = this.getRand(this.rand, 4);
			float randZ = this.getRand(this.rand, 4);
			float x = pos.getX() + 0.5F + randX;
			float y = pos.getY() + 1.5F + randY + addY;
			float z = pos.getZ() + 0.5F + randZ;
			float xSpeed = -randX * 0.115F;
			float ySpeed = -randY * 0.115F;
			float zSpeed = -randZ * 0.115F;

			server.sendParticles(ParticleInit.NORMAL, x, y, z, 0, xSpeed, ySpeed, zSpeed, 1F);
		}
	}

	public void spawnDeathParticle(Level world, BlockPos pos, Monster entity) {
		if (!(world instanceof ServerLevel server)) { return; }

		float addY = Math.min(2F, this.tileTime * 0.0125F);
		float entityX = (float) entity.getX();
		float entityY = (float) entity.getY();
		float entityZ = (float) entity.getZ();

		for (int i = 0; i < 5; i++) {

			float randX = this.getRandFloat();
			float randY = this.getRandFloat();
			float randZ = this.getRandFloat();
			float x = entityX + 0.5F + randX;
			float y = entityY + 1.5F + randY + addY;
			float z = entityZ + 0.5F + randZ;
			float xSpeed = (pos.getX() - entityX - randX) * 0.115F;
			float ySpeed = (pos.getY() - entityY - randY) * 0.115F;
			float zSpeed = (pos.getZ() - entityZ - randZ) * 0.115F;

			server.sendParticles(ParticleInit.BLOOD, x, y, z, 0, xSpeed, ySpeed, zSpeed, 1F);
		}
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("isButtle", this.isButtle);
		tag.putInt("summonType", this.summonType.getId());
		tag.putInt("tileTime", this.tileTime);
		tag.putInt("wave", this.wave);
		tag.putFloat("chargeSummonSize", this.chargeSummonSize);
		tag.putInt("dethMobCount", this.dethMobCount);
		tag.putInt("summonCount", this.summonCount);
		tag.putInt("summonMaxSize", this.summonMaxCount);
		tag.putInt("waitTime", this.waitTime);
		tag.putInt("maxWaitTime", this.maxWaitTime);
		tag.putInt("chageTime", this.chageTime);
		tag.putBoolean("isHard", this.isHard);

		if (this.boss != null) {
			tag.putUUID("bossId", this.boss.getUUID());
		}
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.isButtle = tag.getBoolean("isButtle");
		this.summonType = SummonType.getType(tag.getInt("summonType"));
		this.tileTime = tag.getInt("tileTime");
		this.wave = tag.getInt("wave");
		this.chargeSummonSize = tag.getFloat("chargeSummonSize");
		this.dethMobCount = tag.getInt("dethMobCount");
		this.summonCount = tag.getInt("summonCount");
		this.summonMaxCount = tag.getInt("summonMaxSize");
		this.waitTime = tag.getInt("waitTime");
		this.maxWaitTime = tag.getInt("maxWaitTime");
		this.chageTime = tag.getInt("chageTime");
		this.isHard = tag.getBoolean("isHard");

		if (tag.hasUUID("bossId")) {
			this.bossId = tag.getUUID("bossId");
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return null;
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}

	public abstract ItemStack getStack();

	public abstract int getBattleLevel();

	public enum SummonType {

		START(0),
		CHARGE(1),
		SUMMON(2),
		WAIT(3),
		END(4);

		private final int id;

		SummonType(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static SummonType getType(int id) {
			switch (id) {
			case 0: return START;
			case 1: return CHARGE;
			case 2: return SUMMON;
			case 3: return WAIT;
			case 4: return END;
			default: return CHARGE;
			}
		}

		public boolean is(SummonType type) {
			return this.equals(type);
		}
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return true;
	}
}
