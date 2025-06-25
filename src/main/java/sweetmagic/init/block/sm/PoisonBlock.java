package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.PotionInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class PoisonBlock extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 15.9D, 16D);
	private int tickTime = 0;

	public PoisonBlock(String name) {
		super(name, setState(Material.METAL, SoundType.METAL, 1F, 8192F));
		BlockInfo.create(this, null, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (world.isClientSide() || !(entity instanceof LivingEntity living) || entity instanceof ISMMob) { return; }

		this.tickTime++;
		if(!living.hasEffect(PotionInit.deadly_poison)) {

			if(living.hasEffect(PotionInit.reflash_effect) && this.tickTime % 20 == 0) {
				living.hurt(SMDamage.poisonDamage, 0.75F);
			}

			else {
				PlayerHelper.setPotion(living, PotionInit.deadly_poison, 0, 110);
			}
		}

		else if(this.tickTime % 30 == 0) {
			PlayerHelper.setPotion(living, PotionInit.deadly_poison, 0, 110);
		}

		if(this.tickTime % 60 == 0) {
			this.tickTime = 0;
		}
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "0", this.getEffectTip("deadly_poison")).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "1", this.getEffectTip("reflash_effect")).withStyle(GREEN));
	}
}
