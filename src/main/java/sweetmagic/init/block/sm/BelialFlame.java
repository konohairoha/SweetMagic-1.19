package sweetmagic.init.block.sm;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.PotionInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.util.PlayerHelper;

public class BelialFlame extends BaseModelBlock {

	protected static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 1D, 16D);

	public BelialFlame(String name) {
		super(name, setState(Material.FIRE, SoundType.WOOL, 0F, 8192F, 15).instabreak().randomTicks());
		BlockInfo.create(this, null, name);
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if(world.getBlockState(pos.above()).is(BlockInit.magiclight)) { return; }
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (!world.isClientSide()) {
			world.levelEvent((Player) null, 1009, pos, 0);
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext con) {
		return Shapes.empty();
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if(!(entity instanceof LivingEntity living) || living instanceof ISMMob) { return; }

		if(living.hasEffect(PotionInit.belial_flame)) {
			if(living.getEffect(PotionInit.belial_flame).getDuration() % 20 == 0) {
				PlayerHelper.setPotion(living, PotionInit.belial_flame, 0, 220);
			}
		}

		else {
			PlayerHelper.setPotion(living, PotionInit.belial_flame, 0, 200);
		}
	}
}
