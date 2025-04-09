package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.item.sm.SMItem;

public class ChargeMagic extends BaseMagicItem {

	public final int data;

	public ChargeMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.CHARGE, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public ChargeMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.CHARGE, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {

		switch(this.data) {
		case 0:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_thunder_common"));
			break;
		case 1:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_thunder_common"));
			break;
		case 6:
		case 8:
			toolTip.add(this.getText("magic_thunderrain"));
			toolTip.add(this.getText("magic_thunder_common"));
			toolTip.add(this.getText("magic_thunderrain_vulnerable"));
			break;
		case 2:
		case 3:
		case 7:
		case 9:
			String range;
			switch (this.getTier()) {
			case 2:
				range = "" + 7;
				break;
			case 3:
				range = "" + 15;
				break;
			case 4:
				range = "" + 25;
				break;
			default:
				range = "" + 2;
				break;
			}
			toolTip.add(this.getText("magic_growth_effect", range));
			break;
		default:
			toolTip.add(this.getText(this.name));
			break;
		}

		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// 杖の取得
		ItemStack stack = wandInfo.getStack();

		switch(this.data) {
		case 0: return this.elecAction(world, player, stack, wandInfo);
		case 1: return this.elecAction(world, player, stack, wandInfo);
		case 2: return this.glowRangeAction(world, player, stack, wandInfo);
		case 3: return this.glowRangeAction(world, player, stack, wandInfo);
		case 6: return this.elecAction(world, player, stack, wandInfo);
		case 7: return this.glowRangeAction(world, player, stack, wandInfo);
		case 8: return this.elecAction(world, player, stack, wandInfo);
		case 9: return this.glowRangeAction(world, player, stack, wandInfo);
		}

		return true;
	}

	public boolean elecAction(Level world, Player player, ItemStack stack, WandInfo wandInfo) {
		if (world.isClientSide) { return true; }

		int data = this.data;

		switch (data) {
		case 6:
			data = 2;
			break;
		case 8:
			data = 3;
			break;
		}

		double rangetRate = 5D;

		switch (data) {
		case 1:
			rangetRate = 7.5D;
			break;
		case 2:
			rangetRate = 10D;
			break;
		case 3:
			rangetRate = 13.5D;
			break;
		}

		double range = rangetRate * ( 1F + this.getExtensionRingCount(player) * 0.25F);

		float addDameRate = 1F;
		int bloodCount = this.getBloodSuckingRing(player);
		if (bloodCount > 0 && player.getHealth() > 1 + bloodCount) {

			addDameRate += bloodCount * 0.2F;

			if (!player.isCreative()) {
				player.setHealth(Math.max(1F, player.getHealth() - bloodCount));
			}
		}

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, player, e -> this.canTargetEffect(e, player), range);
		if (targetList.isEmpty()) { return false; }

		float powerRate = 0F;

		switch (data) {
		case 1:
			powerRate = 3F;
			break;
		case 2:
			powerRate = 6F;
			break;
		case 3:
			powerRate = 9F;
			break;
		}

		// (レベル × 補正値) + (レベル + 追加ダメージ) ÷ (レベル ÷ 2) + 追加ダメージ
		float power = this.getPower(wandInfo) + powerRate;

		double effectRange = 1.5D;

		switch (data) {
		case 1:
			effectRange = 3D;
			break;
		case 2:
			effectRange = 5D;
			break;
		case 3:
			effectRange = 7.5D;
			break;
		}

		for (LivingEntity entity : targetList) {

			ElectricMagicShot magic = new ElectricMagicShot(world, player, wandInfo);
			magic.setAddDamage(magic.getAddDamage());
			magic.setRange(effectRange);
			magic.setData(data);
			magic.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 0, 0);
			magic.setPos(entity.getX(), entity.getY() + 2D, entity.getZ());
			magic.setAddDamage( (magic.getAddDamage() + power) * addDameRate);
			Vec3 vec = entity.getDeltaMovement();
			double y = vec.y - 0.5D;
			magic.setDeltaMovement(new Vec3(vec.x, y, vec.z));
			world.addFreshEntity(magic);
		}

		this.playSound(world, player, SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);

		return true;
	}

	public boolean glowRangeAction(Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		boolean isGlow = false;
		int range = 0;

		switch (this.data) {
		case 2:
			range = 2;
			break;
		case 3:
			range = 7;
			break;
		case 7:
			range = 15;
			break;
		case 9:
			range = 25;
			break;
		}

		range *= (1F + this.getExtensionRingCount(player) * 0.25F);
		Iterable<BlockPos> posList = this.getRangePos(player.blockPosition(), range);
		if (!(world instanceof ServerLevel server)) { return false; }

		RandomSource rand = server.random;

		for (BlockPos pos : posList) {

			BlockState state = world.getBlockState(pos);
			if (state.isAir()) { continue; }

			Block block = state.getBlock();
			if (block instanceof BonemealableBlock crop && !(state.is(BlockTags.DIRT))) {

				boolean isBone = false;

				for (int i = 0; i < 8; i++) {

					BlockState newState = world.getBlockState(pos);
					if (!crop.isValidBonemealTarget(world, pos, newState, true)) { break; }

					crop.performBonemeal(server, rand, pos, newState);
					isGlow = isBone = true;
				}

				if (isBone) {

					for (int i = 0; i < 8; i++) {
						double x = pos.getX() + rand.nextDouble() - 0D;
						double y = pos.getY() + rand.nextDouble() * 0.4D + 0.2D;
						double z = pos.getZ() + rand.nextDouble() - 0D;
						server.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 5, 0F, 0F, 0F, 0.25F);
					}
				}
			}
		}

		if (isGlow) {
			this.playSound(world, player, SoundInit.GROW, 0.1F, 1F);
		}

		return isGlow;
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * SMItem.SPEED, 1F);
	}
}
