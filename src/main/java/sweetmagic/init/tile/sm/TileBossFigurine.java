package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.BossFigurine;
import sweetmagic.init.entity.monster.boss.AncientFairy;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;
import sweetmagic.init.entity.monster.boss.BullFight;
import sweetmagic.init.entity.monster.boss.ElshariaCurious;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.entity.monster.boss.TwilightHora;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.entity.monster.boss.WitchSandryon;

public class TileBossFigurine extends TileAbstractSM {

	private LivingEntity entity = null;
	private double addZ = 0F;

	public TileBossFigurine(BlockPos pos, BlockState state) {
		super(TileInit.bossFigurine, pos, state);
	}

	public TileBossFigurine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// レンダー用のえんちちー取得
	public LivingEntity getRenderEntity() {

		if (this.entity == null) {
			this.entity = this.getEntity();
		}

		return this.entity;
	}

	// えんちちー取得
	public Mob getEntity() {

		Level world = this.level;

		// モブ種類によって設定
		switch (this.getData()) {
		case 1: return new HolyAngel(world);
		case 2: return new IgnisKnight(world);
		case 3: return new WindWitchMaster(world);
		case 4:
			this.addZ = 0.2D;
			return new BullFight(world);
		case 5: return new AncientFairy(world);
		case 6: return new Arlaune(world);
		case 7:
			this.addZ = 0.1D;
			return new SilverLandRoad(world);
		case 8:  return new WhiteButler(world);
		case 9:  return new TwilightHora(world);
		case 10: return new BraveSkeleton(world);
		case 11: return new ElshariaCurious(world);
		case 12: return new WitchSandryon(world);
		case 13: return new BlitzWizardMaster(world);
		default:
			QueenFrost entity = new QueenFrost(world);
			entity.setArmor(3);
			return entity;
		}
	}

	public int getData() {
		return ((BossFigurine) this.getBlock(this.getBlockPos())).getData();
	}

	public double getAddZ() {
		return this.addZ;
	}
}
