package sweetmagic.api.iitem;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import sweetmagic.util.PlayerHelper;

public interface ISMUtil {

	// ポーション付与
	default void addPotion (LivingEntity target, MobEffect effect, int level, int time) {
		PlayerHelper.setPotion(target, effect, level, time);
	}

	// ポーション付与
	default void setPotion (LivingEntity target, MobEffect effect, int level, int time, boolean flag) {
		PlayerHelper.setPotion(target, effect, level, time, flag);
	}

	default void playSound(Level world, LivingEntity entity, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, entity.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// 音を流す
	default void playSound (Level level, BlockPos pos, SoundEvent sound, float vol, float pit) {
		level.playSound(null, pos, sound, SoundSource.PLAYERS, vol, pit);
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity, range));
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> filter, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity, range)).stream().filter(filter).toList();
	}

	// 範囲の取得
	default AABB getAABB (BlockPos pos, double range) {
		return this.getAABB(pos, range, range, range);
	}

	// 範囲の取得
	default AABB getAABB (BlockPos pos, double x, double  y, double  z) {
		return new AABB(pos.offset(-x, -y, -z), pos.offset(x, y, z));
	}

	// 範囲の取得
	default AABB getAABB (Entity entity, double range) {
		return entity.getBoundingBox().inflate(range, range, range);
	}

	// 範囲の取得
	default AABB getAABB (Entity entity, double x, double  y, double  z) {
		return entity.getBoundingBox().inflate(x, y, z);
	}

	// 周囲の座標取得
	default Iterable<BlockPos> getRangePos (BlockPos pos, int range) {
		return BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 乱数取得
	default double getRand(RandomSource rand, double scale) {
		return (rand.nextDouble() - rand.nextDouble()) * scale;
	}
}
