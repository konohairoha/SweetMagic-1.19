package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.item.sm.SMItem;

public class MagicianWand extends SMItem {

	public MagicianWand(String name) {
		super(name);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext con) {

		Player player = con.getPlayer();
		Level world = con.getLevel();
		BlockPos pos = con.getClickedPos();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Direction face = con.getClickedFace();
		world.setBlock(pos, block.rotate(state, Rotation.CLOCKWISE_90), 3);

		if (world.isClientSide()) {
			for (int i = 0; i < 6; i++) {
				this.spawnParticleCycle(world, ParticleInit.CYCLE_GRAY_ORB, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, 0.85D, face.toYRot() + i * 10, false);
			}
		}

		SoundType sound = block.getSoundType(state, world, pos, player);
		world.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);

		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(Level world, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		world.addParticle(par, x, y, z, face.get3DDataValue() * way, range, angle + way * 1 * SMItem.SPEED);
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
