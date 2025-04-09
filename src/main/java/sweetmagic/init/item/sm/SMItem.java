package sweetmagic.init.item.sm;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;

public class SMItem extends Item implements ISMTip {

	public final String name;
	public static float SPEED = 6F;
	public final Random rand = new Random();

	public SMItem(String name) {
		super(setItem(SweetMagicCore.smTab));
		this.name = name;
		ItemInit.itemMap.put(this, this.name);
	}

	public SMItem(String name, CreativeModeTab tab) {
		super(setItem(tab));
		this.name = name;
		ItemInit.itemMap.put(this, this.name);
	}

	public SMItem(String name, Properties pro) {
		super(pro);
		this.name = name;
		ItemInit.itemMap.put(this, this.name);
	}

	public SMItem(String name, boolean flag) {
		super(new Item.Properties());
		this.name = name;
		ItemInit.itemMap.put(this, this.name);
	}

	public String getRegistryName() {
		return this.name;
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity.blockPosition(), range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> flag, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity.blockPosition(), range)).stream().filter(flag).toList();
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double range) {
		return this.getAABB(pos, range, range, range);
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double x, double y, double z) {
		return new AABB(pos.offset(-x, -y, -z), pos.offset(x, y, z));
	}

	public static Properties setItem(CreativeModeTab tab) {
		return new Item.Properties().tab(tab);
	}

	public static Properties setItem(int value, CreativeModeTab tab) {
		return setItem(tab).durability(value);
	}

	// アイテムドロップ
	public void spawnItem(Level world, BlockPos pos, ItemStack stack) {
		ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, stack);
		entity.setNoPickUpDelay();
		entity.setUnlimitedLifetime();
		world.addFreshEntity(entity);
	}

	public void playSound(Entity entity, SoundEvent sound, float vol, float pitch) {
		entity.level.playSound(null, entity, sound, SoundSource.PLAYERS, vol, pitch);
	}

	// パーティクルスポーンリング
	protected void spawnParticleRing(Level world, ParticleOptions particle, double range, double x, double y, double z, double addY, double ySpeed, double moveValue) {
		if (!(world instanceof ServerLevel server)) { return; }

		y += addY;
		RandomSource rand = world.random;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 1D) {

			double yS = ySpeed;

			if (ySpeed != 0D) {
				yS += rand.nextFloat() * 0.025F;
			}

			server.sendParticles(particle, x + Math.cos(degree), y, z + Math.sin(degree), 0, 0D, yS, 0D, moveValue);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * SPEED, 1);
	}

	public float getRandFloat() {
		return this.rand.nextFloat() - this.rand.nextFloat();
	}

	public float getRandFloat(float rate) {
		return this.getRandFloat() * rate;
	}

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		this.addTip(stack, toolTip);
	}

	public void addTip(ItemStack stack, List<Component> toolTip) { }
}
