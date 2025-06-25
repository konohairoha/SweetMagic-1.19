package sweetmagic.init.item.magic;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.emagic.SMAcceType;
import sweetmagic.api.emagic.SMDropType;
import sweetmagic.api.iitem.IAcce;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.init.potion.SMEffect;
import sweetmagic.init.tile.inventory.SMInventory.SMPorchInventory;
import sweetmagic.key.SMKeybind;
import sweetmagic.util.ItemHelper;
import sweetmagic.util.SMUtil;

public class SMAcce extends SMItem implements IAcce {

	private final String name;
	private final int data;
	private final int tier;
	private SMAcceType accType;
	private SMDropType dropType;
	private final boolean isDuplication;
	public static final String NOT_ACTIVE = "notActive";
	public static final UUID BLOCK_REACH = UUID.fromString("c85e7079-e9f1-40e8-970e-bf327c23251a");
	private static final UUID ATTACK_SPEED = UUID.fromString("0A87A51E-A43F-4EEF-A770-07C2160D373D");
	private int tickTime = 0;

	public SMAcce(String name, int data, int tier, SMAcceType accType, SMDropType dropType, boolean isDuplication) {
		super(name, new Item.Properties().tab(SweetMagicCore.smMagicTab).stacksTo(1));
		this.name = name;
		this.data = data;
		this.tier = tier;
		this.setAcceType(accType);
		this.setDropType(dropType);
		this.isDuplication = isDuplication;
	}

	/**
	 * 0 =  勇者の腕輪
	 * 1 =  ウィッチ・スクロール
	 * 2 =  灼熱の宝玉
	 * 3 =  人魚の衣
	 * 4 =  血吸の指輪
	 * 5 =  エメラルドピアス
	 * 6 =  フォーチュンリング
	 * 7 =  夜の帳
	 * 8 =  守護のペンダント
	 * 9 =  魔術師のグローブ
	 * 10 = 魔法使いの羽ペン
	 * 11 = アブソープペンダント
	 * 12 = 毒牙
	 * 13 = ペンデュラムネックレス
	 * 14 = イグニスソウル
	 * 15 = フロストチェーン
	 * 16 = ホーリーチャーム
	 * 17 = ウィンドウレリーフ
	 * 18 = エンジェルフリューゲル
	 * 19 = 召喚の手引き書
	 * 20 = 電光のイアリング
	 * 21 = 魔術師のガントレット
	 * 22 = 機敏な羽根
	 * 23 = 不思議なフォーク
	 * 24 = 拡張の指輪
	 * 25 = 妖精の羽根
	 * 26 = 桜のかんざし
	 * 27 = 逢魔時の砂時計
	 * 28 = 戦士の烈火護符
	 * 30 = 魔術師のガントレット
	 * 30 = のびーるバインド
	 */

	// ツールチップ
	@Override
	public void magicToolTip(List<MutableComponent> toolTip, ItemStack stack) {

		int count = this.getStackCount(new AcceInfo(stack));
		String name = this.name;

		switch (this.data) {
		case 0:
			toolTip.add(this.getText(name, count * 2));
			toolTip.add(this.getText(name + "_buff"));
			break;
		case 1:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_quick"));
			break;
		case 3:
			toolTip.add(this.getText(name, String.format("%.0f%%", 50F * count)));
			break;
		case 4:
			toolTip.add(this.getText(name, 0.5F * count));
			toolTip.add(this.getText(name + "_dame", String.format("%.0f%%", 20F * count)));
			toolTip.add(this.getText(name + "_kill", String.format("%.0f%%", 10F * count)));
			toolTip.add(this.getText(name + "_dark"));
			break;
		case 5:
			toolTip.add(this.getText(name, String.format("%.0f%%", 10F * count)));
			toolTip.add(this.getText(name + "_damage", String.format("%.0f%%", 10F * count)));
			break;
		case 6:
			toolTip.add(this.getText(name, this.getEnchaTip(count + 1).getString()));
			break;
		case 7:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_damage_suffe", String.format("%.0f%%", 10F * count)));
			toolTip.add(this.getText(name + "_damage_inflicte", String.format("%.0f%%", 10F * count)));
			break;
		case 8:
			toolTip.add(this.getText(name, String.format("%.0f%%", 50F + (count - 1) * 12.5F), this.getEnchaTip(count).getString()));
			toolTip.add(this.getText(name + "_cooltime", String.format("%.1f", 600F - 37.5F * (count - 1))));
			break;
		case 9:
			toolTip.add(this.getText(name, String.format("%.2f%%", 6.25F * count)));
			toolTip.add(this.getText(name + "_exp", String.format("%.1f%%", 12.5F * count)));
			toolTip.add(this.getText(name + "_magic"));
			break;
		case 10:
			toolTip.add(this.getText(name, this.getEffectTip("aether_armor").getString()));
			toolTip.add(this.getText(name + "_charge"));
			toolTip.add(this.getText(name + "_boss"));
			break;
		case 11:
			Component key = KeyPressEvent.getKeyName(SMKeybind.POUCH);
			toolTip.add(this.getText(name));
			toolTip.add(this.getTipArray( "[", key.copy(), this.getText("key_no"), "]", this.getText(name + "_off")));
			break;
		case 12:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_counter"));
			toolTip.add(this.getText(name + "_cool"));
			break;
		case 13:
			toolTip.add(this.getText(name, String.format("%.0f%%", 25F * count)));
			toolTip.add(this.getText(name + "_debuff", String.format("%.1f%%", 12.5F * count)));
			break;
		case 14:
		case 15:
		case 17:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_up"));
			break;
		case 16:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_attack"));
			toolTip.add(this.getText(name + "_add"));
			break;
		case 18:
			toolTip.add(this.getText(name));
			String heal = this.getEffectTip("regeneration").getString();
			String dame = this.getMCTip("strength").getString() + this.getEnchaTip(4).getString();
			String mf = this.getEffectTip("mfcostdown").getString() + this.getEnchaTip(3).getString();

			toolTip.add(this.getText(name + "_buff", heal, dame, mf));
			toolTip.add(this.getText(name + "_heal"));
			break;
		case 19:
			toolTip.add(this.getText(name, String.format("%.0f%%", 20F * count)));
			break;
		case 20:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText("varrier_pendant_cooltime", String.format("%.1f", 600F - 37.5F * (count - 1))));
			break;
		case 21:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_ele"));
			break;
		case 22:
			toolTip.add(this.getText(name, String.format("%.0f%%", 30F * count)));
			break;
		case 23:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_debuff"));
			toolTip.add(this.getText(name + "_xp"));
			toolTip.add(this.getText(name + "_chance"));
			break;
		case 24:
			toolTip.add(this.getText(name, String.format("%.1f%%", 12.5F * count)));
			toolTip.add(this.getText(name + "_single", count));
			break;
		case 25:
			toolTip.add(this.getText(name));

			Component key2 = KeyPressEvent.getKeyName(SMKeybind.NEXT);
			toolTip.add(this.getTipArray( "[", key2.copy(), this.getText("key_no"), "]", this.getText("gravity_pendant_off")));
			break;
		case 26:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_critical1"));
			toolTip.add(this.getText(name + "_critical2"));
			break;
		case 27:
			toolTip.add(this.getText("wizard_brooch"));
			toolTip.add(this.getText(name));
			break;
		case 28:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_twi"));
			toolTip.add(this.getText(name + "_summon", String.format("%.1f%%", 25F)));
			break;
		case 29:
			toolTip.add(this.getText(name, this.getMCTip("strength").getString()));
			toolTip.add(this.getText(name + "_attack", this.getEffectTip("flame").getString()));
			break;
		case 30:
			toolTip.add(this.getText(name, 2 * count));
			break;
		case 31:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_0"));
			toolTip.add(this.getText(name + "_1"));
			break;
		case 32:
			toolTip.add(this.getText(name));
			toolTip.add(this.getText(name + "_attack"));
			break;
		case 33:
			toolTip.add(this.getText(name, String.format("%.1f%%", count * 5F)));
			toolTip.add(this.getText(name + "_armor", String.format("%.1f%%", count * 10F)));
			break;
		default: toolTip.add(this.getText(name));
			break;
		}
	}

	@Override
	public void debuffRemovalTip(List<MutableComponent> toolTip) {
		switch (this.data) {
		case 2:
			toolTip.add(this.getEffectTip("flame"));
			break;
		case 3:
			toolTip.add(this.getEffectTip("bubble"));
			break;
		case 6:
			toolTip.add(this.getMCTip("unluck"));
			break;
		case 11:
			toolTip.add(this.getEffectTip("gravity"));
			break;
		case 22:
			toolTip.add(this.getMCTip("slowness"));
			break;
		}
	}

	@Override
	public void dropMobTip(List<MutableComponent> toolTip) {
		switch (this.data) {
		case 10:
			toolTip.add(this.getEntityTip("witch_sandryon"));
			break;
		case 12:
			toolTip.add(this.getEntityTip("silver_landroad"));
			break;
		case 14:
			toolTip.add(this.getEntityTip("ignisknight"));
			break;
		case 15:
			toolTip.add(this.getEntityTip("queenfrost"));
			break;
		case 16:
			toolTip.add(this.getEntityTip("holyangel"));
			break;
		case 18:
			toolTip.add(this.getEntityTip("elsharia_curious"));
			break;
		case 21:
			toolTip.add(this.getEntityTip("blitz_wizard_master"));
			break;
		case 26:
			toolTip.add(this.getEntityTip("ancientfairy"));
			break;
		case 27:
			toolTip.add(this.getEntityTip("arlaune"));
			break;
		case 28:
			toolTip.add(this.getEntityTip("twilight_hora"));
			break;
		case 29:
			toolTip.add(this.getEntityTip("brave_skeleton"));
			break;
		case 31:
			toolTip.add(this.getEntityTip("stella_wizard_master"));
			break;
		case 32:
			toolTip.add(this.getEntityTip("demons_belial"));
			break;
		}
	}

	// 最大スタック数の取得
	@Override
	public int getMaxStackCount() {
		switch (this.data) {
		case 0: return 5;
		case 3: return 6;
		case 4: return 5;
		case 5: return 10;
		case 6: return 4;
		case 7: return 5;
		case 8: return 5;
		case 9: return 8;
		case 13: return 4;
		case 19: return 5;
		case 20: return 4;
		case 22: return 5;
		case 24: return 8;
		case 30: return 5;
		case 33: return 5;
		default: return 1;
		}
	}

	// 効果が発動できるか
	public boolean canUseEffect(Level world, Player player, AcceInfo info) {

		boolean flag = true;
		this.tickTime++;
		ItemStack stack = info.getStack();
		boolean hasCoolTime = !player.getCooldowns().isOnCooldown(info.getItem());

		switch (this.data) {
		// 血吸の指輪
		case 4:
			flag = player.getHealth() > 1F;
			break;
		// 守護のペンダント
		case 8:
			flag = player.getHealth() <= (player.getMaxHealth() * 0.3F);
			break;
		// アブソープペンダント
		case 11:
			flag = !stack.getOrCreateTag().getBoolean(SMAcce.NOT_ACTIVE);
			break;
		// 電光のイアリング
		case 20:
			flag = player.tickCount % 30 != 0;
		// 不思議なフォーク
		case 23:
			flag = this.tickTime % 10 == 0;
		// アブソープペンダント
		case 25:
			flag = !stack.getOrCreateTag().getBoolean(SMAcce.NOT_ACTIVE);
			break;
		default:
			break;
		}

		if (this.tickTime % 20 == 0) {
			this.tickTime = 0;
		}

		return hasCoolTime && flag;
	}

	// 常に発動したいならここで
	public void onUpdate(Level world, Player player, AcceInfo info) {

		ItemStack stack = info.getStack();

		switch (this.data) {
		case 0:
			// 戦士の指輪
			this.checkDebuf(player, MobEffects.HUNGER, MobEffects.WEAKNESS);
		case 2:
			// 灼熱の宝玉
			this.scorchEffect(world, player, stack);
			break;
		case 22:
			// 機敏な羽根
//			this.addPotion(player, PotionInit.prompt_feather, 0, 201);
			break;
		case 23:
			// 不思議なフォーク
			this.forkEffext(world, player, stack);
			break;
		default:
			break;
		}
	}

	// 重複系で常に発動したいならここで
	public void onMultiUpdate(Level world, Player player, AcceInfo info, PorchInfo pInfo) {
		ItemStack leg = pInfo.getStack();
		IPorch porch = pInfo.getPorch();
		int count = porch.acceCount(leg, this, this.getMaxStackCount());

		switch (this.data) {
		case 6:
			// フォーチュンリング
			this.addPotion(player, MobEffects.LUCK, count, 201);
			this.checkDebuf(player, MobEffects.UNLUCK);
			break;
		case 7:
			// 夜の帳
			this.checkDebuf(player, MobEffects.BLINDNESS);
			break;
		case 8:
			this.varrierEffect(world, player, info, count);
			break;
		case 20:
			this.electricEffect(world, player, info, count);
			break;
		case 11:
			// アブソープペンダント
			this.gravityEffext(world, player);
			break;
		}
	}

	public Multimap<Attribute, AttributeModifier> getAttributeMap(Level world, Player player, AcceInfo info, PorchInfo pInfo) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		ItemStack leg = pInfo.getStack();
		IPorch porch = pInfo.getPorch();
		int count = porch.acceCount(leg, this, this.getMaxStackCount());

		switch(this.data) {
		case 3:
			// 人魚の衣
			map.putAll(this.mermaidEffect(world, player, info, count));
			break;
		case 22:
			// 機敏な羽根
			map.putAll(this.featherEffect(world, player, info, count));
			break;
		case 30:
			// のびーるバイド
			map.putAll(this.bandEffect(world, player, info, count));
			break;
		}

		return map.build();
	}

	// 灼熱の宝玉
	public boolean scorchEffect(Level world, Player player, ItemStack stack) {

		this.addPotion(player, MobEffects.FIRE_RESISTANCE, 0, 201);
		if (player.isOnFire()) {
			player.clearFire();
		}

		// やけど状態ならやけどを除去
		this.checkDebuf(player, PotionInit.flame);
		return true;
	}

	// アブソープペンダント
	public boolean gravityEffext(Level world, Player player) {

		// 重力状態を除去
		this.checkDebuf(player, PotionInit.gravity);
		List<ItemEntity> entityList = this.getEntityList(ItemEntity.class, player, 8D);
		NonNullList<ItemStack> pInv = player.getInventory().items;
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();

		for (ItemEntity entity : entityList) {

			ItemStack st = entity.getItem();
			if (ItemHelper.simulateFit(pInv, st) >= st.getCount()) { continue; }

			double dX = x - entity.getX();
			double dY = y - entity.getY();
			double dZ = z - entity.getZ();
			double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
			double vel = 1.0 - dist / 15.0;

			if (vel > 0D) {
				vel *= vel;
				entity.setDeltaMovement(entity.getDeltaMovement().add(dX / dist * vel * 0.1, dY / dist * vel * 0.2, dZ / dist * vel * 0.1));
			}

			if (world instanceof ServerLevel server) {
				float randX = this.getRandFloat(1.5F);
				float randY = this.getRandFloat(1.5F);
				float randZ = this.getRandFloat(1.5F);
				Vec3 vec = entity.getDeltaMovement();
				float f1 = (float) entity.xo - 0F + randX + (float) vec.x * 1.5F;
				float f2 = (float) (entity.yo + randY) + (float) vec.y * 1.5F;
				float f3 = (float) entity.zo - 0F + randZ + (float) vec.z * 1.5F;
				float xSpeed = -randX * 0.125F;
				float ySpeed = -randY * 0.125F;
				float zSpeed = -randZ * 0.125F;
				server.sendParticles(ParticleInit.NORMAL, f1, f2, f3, 0, xSpeed, ySpeed, zSpeed, 1F);
			}
		}

		int sumXP = 0;
		List<ExperienceOrb> expList = this.getEntityList(ExperienceOrb.class, player, e -> !e.getPersistentData().getBoolean("isSum"), 8D);

		for (ExperienceOrb entity : expList) {
			sumXP += entity.getValue();
			entity.discard();
		}

		if (sumXP > 0 && !world.isClientSide()) {
			ExperienceOrb entity = new ExperienceOrb(world, x, y, z, sumXP);
			CompoundTag tag = entity.getPersistentData();
			tag.putBoolean("isSum", true);
			entity.addAdditionalSaveData(tag);
			world.addFreshEntity(entity);
		}

		return true;
	}

	// 不思議なフォーク
	public boolean forkEffext(Level world, Player player, ItemStack stack) {

		int foodLevel = player.getFoodData().getFoodLevel();

		if (foodLevel >= 20) {
			this.addPotion(player, MobEffects.DAMAGE_RESISTANCE, 1, 211);
		}

		else if (6 >= foodLevel) {
			this.addPotion(player, MobEffects.WEAKNESS, 1, 211);
		}

		return true;
	}

	// 守護のペンダント
	public boolean varrierEffect(Level world, Player player, AcceInfo info, int count) {

		int value = (count - 1);

		// 守護のペンダント
		player.heal(player.getMaxHealth() * (0.5F + value * 0.125F));
		this.addPotion(player, MobEffects.ABSORPTION, value, 1200);
		this.addPotion(player, MobEffects.DAMAGE_RESISTANCE, 4, 600);
		player.getCooldowns().addCooldown(info.getStack().getItem(), 12000 - value * 1000);
		this.playSound(player, SoundInit.HEAL, 0.0625F, 1.15F);
		return true;
	}

	// 電光のイアリング
	public boolean electricEffect(Level world, Player player, AcceInfo info, int count) {
		List<Mob> entityList = this.getEntityList(Mob.class, player, e -> e instanceof Enemy, 7.5D);
		if (entityList.isEmpty()) { return false; }

		entityList.forEach(e -> SMUtil.tameAIDonmov(e, 60));
		int value = (count - 1);
		player.getCooldowns().addCooldown(info.getStack().getItem(), 12000 - value * 1000);
		this.playSound(player, SoundInit.ELECTRIC, 0.0625F, 1.15F);
		return true;
	}

	// 人魚の衣
	public Multimap<Attribute, AttributeModifier> mermaidEffect(Level world, Player player, AcceInfo info, int count) {

		if (player.isInWater()) {
			player.setAirSupply(player.getMaxAirSupply());
			player.fallDistance = 0F;
		}

		this.checkDebuf(player, PotionInit.bubble);
		double range = 0.5D * count;
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(ATTACK_SPEED, "Attack Speed", range, AttributeModifier.Operation.ADDITION));
		return map.build();
	}

	// 機敏な羽根
	public Multimap<Attribute, AttributeModifier> featherEffect(Level world, Player player, AcceInfo info, int count) {
		this.checkDebuf(player, MobEffects.MOVEMENT_SLOWDOWN);
		double range = 0.03D * count;
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SMEffect.SPEED_UUID, "Move Speed", range, AttributeModifier.Operation.ADDITION));
		return map.build();
	}

	// のびーるバンド
	public Multimap<Attribute, AttributeModifier> bandEffect(Level world, Player player, AcceInfo info, int count) {
		double range = 2D * count;
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(BLOCK_REACH, "Block Reach", range, AttributeModifier.Operation.ADDITION));
		return map.build();
	}

	// デバフチェック
	public void checkDebuf(Player player, MobEffect... potionArray) {
		for (MobEffect potion : potionArray) {
			if (player.hasEffect(potion)) {
				player.removeEffect(potion);
			}
		}
	}

	// 装飾品タイプの取得
	@Override
	public SMAcceType getAcceType() {
		return this.accType;
	}

	// 装飾品タイプの設定
	@Override
	public void setAcceType(SMAcceType type) {
		this.accType = type;
	}

	// ドロップタイプの取得
	@Override
	public SMDropType getDropType() {
		return this.dropType;
	}

	// ドロップタイプの設定
	@Override
	public void setDropType(SMDropType type) {
		this.dropType = type;
	}

	// tierの取得
	@Override
	public int getTier() {
		return this.tier;
	}

	// 重複できるか
	@Override
	public boolean isDuplication() {
		return this.isDuplication;
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// 腰の装飾品を取得
		ItemStack stack = player.getItemInHand(hand);
		ItemStack armor = player.getItemBySlot(EquipmentSlot.LEGS);
		if ( !(armor.getItem() instanceof IPorch porch) ) { return InteractionResultHolder.consume(stack); }

		SMPorchInventory inv = new SMPorchInventory(new PorchInfo(armor));

		for (int i = 0; i < inv.getSlots(); i++) {

			ItemStack acce = inv.getStackInSlot(i);
			if (!acce.isEmpty()) { continue; }

			inv.insertItem(i, stack.copy(), false);
			stack.shrink(1);
			this.playSound(world, player, SoundInit.ROBE, 0.1F, 1F);
			break;
		}

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public boolean isSwitch() {
		return this.data == 11;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		return this.isDuplication() && this.getStackCount(new AcceInfo(stack)) >= this.getMaxStackCount();
	}
}
