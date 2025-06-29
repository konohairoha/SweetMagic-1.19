package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherLamplightMenu;

public class TileAetherLamplight extends TileSMMagic {

	public int order = 0;
	public int selectId = -1;
	public int range = 12;
	public int maxMagiaFlux = 70000;			// 最大MF量を設定
	private static final int MIN_RANGE = 1;		// 最小範囲
	private static final int MAX_RANGE = 64;	// 最大範囲
	public boolean isRangeView = false;
	public BlockOrder blockOrder = new BlockOrder();

	public TileAetherLamplight(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileAetherLamplight(BlockPos pos, BlockState state) {
		super(TileInit.aetheLamplight, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.tickTime % 20 == 0 && !this.isRSPower()) {
			if (this.tickTime % 40 == 0) {
				this.tickTime = 0;
			}
			this.roundMFTransmit();
		}
	}

	// 周囲にMF送信
	public void roundMFTransmit() {

		// 範囲の座標取得
		Iterable<BlockPos> posList = this.getRangePos(this.getBlockPos(), this.range);
		Map<BlockPos, BlockTile> posBlockMap = new HashMap<>();

		// リスト分まわす
		for (BlockPos pos : posList) {
			if (!(this.getTile(pos) instanceof TileSMMagic tile) || !tile.getReceive() || tile.isMaxMF()) { continue; }
			if (tile instanceof TileAetherLamplight || tile instanceof TileAetherLanp || tile instanceof TileMFTank) { continue; }

			posBlockMap.put(pos, new BlockTile(this.getBlock(pos), tile));
		}

		BlockOrder blockOrder = this.blockOrder;
		List<Block> orderBlock = new ArrayList<>();

		// 設定された優先度がある場合
		if (!blockOrder.getBlockList().isEmpty()) {

			Map<Integer, List<Block>> orderBlockMap = blockOrder.getOrderBlockMap();	// 優先度情報の取得
			List<Integer> orderNumerList = this.getOrderNumber();						// 優先度順の取得

			for (int order : orderNumerList) {

				// 優先度のごとのブロックを取得
				List<Block> orderBlockList = orderBlockMap.get(order);

				// 優先度が-1以下なら次へ
				if (order < 0) {
					orderBlock.addAll(orderBlockList);
					continue;
				}

				for (Entry<BlockPos, BlockTile> map : posBlockMap.entrySet()) {
					BlockTile bTile = map.getValue();
					if (orderBlockList.contains(bTile.block())) { continue; }

					// MFブロックからMFを入れるときの処理、MFを貯めれなくなったら終了
					this.insertMF(bTile.tile(), this, this.getTickTime());
					orderBlock.add(bTile.block());
					if (this.isMFEmpty()) { break; }
				}
			}

			// MFが空でないなら未登録のブロックへのMF配布
			if (!this.isMFEmpty()) {
				for (Entry<BlockPos, BlockTile> map : posBlockMap.entrySet()) {
					BlockTile bTile = map.getValue();
					if (orderBlock.contains(bTile.block())) { continue; }

					// MFブロックからMFを入れるときの処理、MFを貯めれなくなったら終了
					this.insertMF(bTile.tile(), this, this.getTickTime());
					if (this.isMFEmpty()) { break; }
				}
			}
		}

		// 優先度未登録時のMF配布
		else {
			for (Entry<BlockPos, BlockTile> map : posBlockMap.entrySet()) {

				// MFブロックからMFを入れるときの処理、MFを貯めれなくなったら終了
				this.insertMF(map.getValue().tile(), this, this.getTickTime());
				if (this.isMFEmpty()) { break; }
			}
		}

		this.sendPKT();
	}

	public void addRange(int id) {

		int addValue = 0;

		switch (id) {
		case 0:
			addValue = 1;
			break;
		case 1:
			addValue = 10;
			break;
		case 2:
			addValue = -1;
			break;
		case 3:
			addValue = -10;
			break;
		case 6:
			this.isRangeView = !this.isRangeView;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	public void addOrder(int id) {

		int addValue = 0;

		switch (id) {
		case 4:
			addValue = 1;
			break;
		case 5:
			addValue = -1;
			break;
		}

		this.order = Math.min(9, Math.max(-1, this.order + addValue));
		this.clickButton();
		this.sendPKT();
	}

	public void setOrder() {
		if(this.selectId == -1) { return; }
		this.blockOrder.registerMap(this.order, this.getRangeBlockSet().stream().toList().get(this.selectId));
		this.clickButton();
		this.sendPKT();
	}

	public void registerBlock() {
		if (this.selectId == -1) { return; }

		List<Block> blockList = this.getRangeBlockList();
		if(blockList.isEmpty() || this.selectId >= blockList.size()) { return; }

		this.blockOrder.registerMap(this.order, blockList.get(this.selectId));
	}

	public List<Block> getRangeBlockList() {

		List<Block> blockList = new ArrayList<>();
		Iterable<BlockPos> posList = this.getRangePos(this.getBlockPos(), this.range);

		// リスト分まわす
		for (BlockPos pos : posList) {
			if (!(this.getTile(pos) instanceof TileSMMagic tile) || !tile.getReceive()) { continue; }
			if (tile instanceof TileAetherLamplight || tile instanceof TileAetherLanp || tile instanceof TileMFTank) { continue; }

			blockList.add(this.getBlock(pos));
		}

		return blockList;
	}

	public Set<Block> getRangeBlockSet() {

		Set<Block> blockList = new LinkedHashSet<>();
		Set<Block> orderBlockSet = new LinkedHashSet<>();
		List<Integer> orderNumerList = this.getOrderNumber();
		Iterable<BlockPos> posList = this.getRangePos(this.getBlockPos(), this.range);

		// リスト分まわす
		for (BlockPos pos : posList) {
			if (!(this.getTile(pos) instanceof TileSMMagic tile) || !tile.getReceive()) { continue; }
			if (tile instanceof TileAetherLamplight || tile instanceof TileAetherLanp || tile instanceof TileMFTank) { continue; }

			blockList.add(this.getBlock(pos));
		}

		if (!orderNumerList.isEmpty()) {
			for (Integer order : orderNumerList) {
				List<Block> orderBlockList = this.blockOrder.orderBlockMap.get(order);
				if (orderBlockList == null || orderBlockList.isEmpty()) { break; }

				for (Block block : blockList) {
					if(!orderBlockList.contains(block)) { continue; }

					orderBlockSet.add(block);
				}
			}
		}

		blockList.forEach(b -> orderBlockSet.add(b));
		return orderBlockSet;
	}

	// 受信するMF量の取得
	public int getReceiveMF() {
		return 70000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getUserMF() {
		return 5000;
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.range);
		tag.putInt("order", this.order);
		tag.putInt("selectId", this.selectId);
		tag.putBoolean("isRangeView", this.isRangeView);
		this.saveBLockOrder(tag, this.blockOrder, "blockOrder");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.range = tag.getInt("range");
		this.order = tag.getInt("order");
		this.selectId = tag.getInt("selectId");
		this.isRangeView = tag.getBoolean("isRangeView");
		this.blockOrder = this.loadBlockOrder(tag, "blockOrder");
	}

	// BlockOrderをnbt保存
	public CompoundTag saveBLockOrder(CompoundTag nbt, BlockOrder blockOrder, String name) {

		// NULLチェックとListの個数を確認
		if (!blockOrder.getBlockList().isEmpty()) {

			Map<Integer, List<Block>> orderMap = blockOrder.getOrderBlockMap();

			// リストの分だけ回してNBTに保存
			ListTag tagsList = new ListTag();
			for (Entry<Integer, List<Block>> map : orderMap.entrySet()) {

				// nbtリストにnbtを入れる
				CompoundTag tags = new CompoundTag();
				tags.putInt("order", map.getKey());

				ListTag tagsList2 = new ListTag();

				for (Block block : map.getValue()) {
					CompoundTag tags2 = new CompoundTag();
					tags2.putString("blockId", ForgeRegistries.BLOCKS.getKey(block).toString());
					tagsList2.add(tags2);
				}

				tags.put("blockList", tagsList2);
				tagsList.add(tags);
			}

			// NBTに保存
			nbt.put(name, tagsList);
		}

		return nbt;
	}

	// nbtを呼び出してBlockOrderを取得
	public BlockOrder loadBlockOrder(CompoundTag nbt, String name) {
		BlockOrder blockOrder = new BlockOrder();

		for (Tag tag : nbt.getList(name, 10)) {
			CompoundTag tags = (CompoundTag) tag;
			int order = tags.getInt("order");

			for (Tag tag2 : tags.getList("blockList", 10)) {
				CompoundTag tags2 = (CompoundTag) tag2;
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tags2.getString("blockId")));
				blockOrder.registerMap(order, block);
			}
		}
		return blockOrder;
	}

	public List<Integer> getOrderNumber() {

		List<Integer> orderList = new ArrayList<>();
		Map<Integer, List<Block>> orderBlockMap = this.blockOrder.getOrderBlockMap();


		if (!orderBlockMap.isEmpty()) {
			orderList.addAll(orderBlockMap.keySet());
			orderList = orderList.stream().sorted((s1, s2) -> this.sortOrder(s1, s2)).toList();
		}

		else {
			orderList.add(0);
		}

		return orderList;
	}


	// 優先度ソート
	public int sortOrder (int int1, int int2) {
		if (int1 > int2) { return -1; }
		if (int1 < int2) { return 1; }
		return 0;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherLamplightMenu(windowId, inv, this);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.blockOrder.getBlockList().isEmpty() && this.isMFEmpty();
	}

	public record BlockTile(Block block, TileSMMagic tile) { }

	public class BlockOrder {

		private List<Block> blockList = new ArrayList<>();
		private Map<Integer, List<Block>> orderBlockMap = new HashMap<>();

		public void registerMap(int order, Block block) {
			if (!this.orderBlockMap.isEmpty() && this.orderBlockMap.get(order) != null && this.orderBlockMap.get(order).contains(block)) { return; }

			for (Integer orderNum : this.orderBlockMap.keySet()) {
				List<Block> orderNumList = this.orderBlockMap.get(orderNum);

				if (orderNumList.contains(block)) {
					orderNumList.removeIf(b -> b == block);
					break;
				}
			}

			List<Block> blockList = this.orderBlockMap.get(order);

			if (blockList == null || blockList.isEmpty()) {
				blockList = new ArrayList<>();
				blockList.add(block);
				this.orderBlockMap.put(order, blockList);
			}

			else {
				blockList.add(block);
			}

			if (!this.blockList.contains(block)) {
				this.blockList.add(block);
			}
		}

		public List<Block> getBlockList() {
			return this.blockList;
		}

		public Map<Integer, List<Block>> getOrderBlockMap() {
			return this.orderBlockMap;
		}

		public int getBlockOrder(Block block) {
			if (!this.getBlockList().contains(block)) { return 0; }

			for (Entry<Integer, List<Block>> map : this.getOrderBlockMap().entrySet()) {
				if (!map.getValue().contains(block)) { continue; }
				return map.getKey();
			}

			return 0;
		}
	}
}
