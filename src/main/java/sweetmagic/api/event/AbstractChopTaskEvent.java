package sweetmagic.api.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.util.WorldHelper;

public abstract class AbstractChopTaskEvent {

	public final Level world;
	public final Player player;
	public final boolean isCreative;
	public final BlockPos start;
	public final int blockTick;
	public List<BlockPos> targetblockList = new ArrayList<>(); // 対象リスト
	public Set<BlockPos> posSet = new HashSet<>(); // 対象座標リスト
	public final Direction[] allFace = new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

	public AbstractChopTaskEvent(BlockPos start, Player player, int blockTick) {
		this.world = player.getLevel();
		this.player = player;
		this.isCreative = player.isCreative();
		this.blockTick = blockTick;
		this.start = start;
		this.targetblockList.add(start);
	}

	@SubscribeEvent
	public void chopChop(TickEvent.ServerTickEvent event) {

		// クライアントなら終了
		if (event.side.isClient()) {
			this.finish();
			return;
		}

		BlockPos pos;
		int tick = this.blockTick;
		List<ItemStack> dropList = new ArrayList<>();
		boolean isFirst = this.posSet.isEmpty();

		// 見つかるまで回す
		while(tick > 0) {

			// 空なら終了
			if(this.targetblockList.isEmpty()) {
				this.finish();
				return;
			}

			pos = this.targetblockList.get(0);
			this.targetblockList.remove(0);

			if (isFirst) {

				if (!this.isCreative) {
					dropList.addAll(Block.getDrops(this.world.getBlockState(this.start), (ServerLevel) this.world, pos, this.world.getBlockEntity(pos), this.player, this.player.getMainHandItem()));
				}

				this.world.destroyBlock(pos, false);

				for (Direction face : this.allFace) {
					BlockPos posFace = pos.relative(face);

					// 原木なら
					if (this.checkBlock(this.world, posFace)) {
						this.targetblockList.add(posFace);
						this.posSet.add(posFace);
					}
				}

				isFirst = false;
				continue;
			}

			if (!this.checkBlock(this.world, pos)) { continue; }

			BlockState state = this.world.getBlockState(pos);

			// クリエイティブ以外ならアイテムドロップ
			if (!this.isCreative) {
				dropList.addAll(Block.getDrops(state, (ServerLevel) this.world, pos, this.world.getBlockEntity(pos), this.player, this.player.getMainHandItem()));
			}

			this.world.destroyBlock(pos, false);

			// 4方向確認
			for (Direction face : this.allFace) {

				// 未チェック領域なら追加
				BlockPos posFace = pos.relative(face);
				this.checkPos(posFace);

				if (face != Direction.UP) {
					this.checkPos(posFace.above());
				}
			}

			tick--;
		}

		//リストに入れたアイテムをドロップさせる
		WorldHelper.createLootDrop(dropList, this.world, this.player.xo, this.player.yo, this.player.zo);
	}

	public void checkPos(BlockPos pos) {
		if (!this.posSet.contains(pos) && this.checkBlock(this.world, pos)) {
			this.targetblockList.add(pos);
			this.posSet.add(pos);
		}
	}

	// イベント終了
	public void finish() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public LootContext.Builder getLoot(Level world, BlockPos pos, ItemStack stack) {
		return (new LootContext.Builder((ServerLevel) world)).withRandom(world.random)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, stack).withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos));
	}

	// 原木チェック
	public abstract boolean checkBlock(Level world, BlockPos pos);
}
