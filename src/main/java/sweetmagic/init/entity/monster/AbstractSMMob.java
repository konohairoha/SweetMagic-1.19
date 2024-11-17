package sweetmagic.init.entity.monster;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDamage;

public abstract class AbstractSMMob extends Monster implements ISMMob {

	protected int defTime = 0;
	protected Random rand = new Random();

	public AbstractSMMob(EntityType<? extends AbstractSMMob> enType, Level world) {
		super(enType, world);
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, double range) {
		return this.getEntityList(enClass, this, filter, range);
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer) {
		return isPlayer ? this.getTargetEntity() : e -> !e.isSpectator() && e.isAlive() && !this.isPlayer(e) && !(e instanceof ISMMob) && !e.hasEffect(PotionInit.resistance_blow);
	}

	public DamageSource getSRC() {
		return SMDamage.getMagicDamage(this, this);
	}

	protected boolean teleport() {
		if (!this.level.isClientSide() && this.isAlive()) {
			double d0 = this.getX() + (this.rand.nextDouble() - 0.5D) * 32D;
			double d1 = this.getY() + (double) (this.rand.nextInt(32) - 16);
			double d2 = this.getZ() + (this.rand.nextDouble() - 0.5D) * 32D;
			return this.teleport(d0, d1, d2);
		}

		return false;
	}

	protected boolean teleport(double x, double y, double z) {

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

		while (pos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(pos).getMaterial().blocksMotion()) {
			pos.move(Direction.DOWN);
		}

		BlockState state = this.level.getBlockState(pos);
		boolean flag = state.getMaterial().blocksMotion();
		boolean flag1 = state.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1) {

			EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
			if (event.isCanceled()) { return false; }

			Vec3 vec3 = this.position();
			boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

			if (flag2) {
				this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
				if (!this.isSilent()) {
					this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1F, 1F);
					this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
				}
			}

			return flag2;
		}

		return false;
	}

	public void tick() {
		super.tick();

		if (this.defTime > 0) {
			this.defTime--;
		}
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		return data;
	}
}
