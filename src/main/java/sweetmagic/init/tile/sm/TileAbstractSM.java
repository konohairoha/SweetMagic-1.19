package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.capability.ICapabilityResolver;
import sweetmagic.init.capability.SidItemHandler;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.init.tile.slot.WrappedItemHandler;
import sweetmagic.init.tile.slot.WrappedItemHandler.WriteMode;

public abstract class TileAbstractSM extends BlockEntity implements MenuProvider {

	protected ICapabilityResolver<IItemHandler> resolver = null;
	protected static final WriteMode IN = WrappedItemHandler.WriteMode.IN;
	protected static final WriteMode IN_OUT = WrappedItemHandler.WriteMode.IN_OUT;
	protected static final WriteMode OUT = WrappedItemHandler.WriteMode.OUT;

	public int tickTime = 0;
	protected Random rand = new Random();

	public TileAbstractSM(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tags = pkt.getTag();
		this.load(tags);
	}

	@Override
    public void handleUpdateTag(CompoundTag tags) {
        this.deserializeNBT(tags);
    }

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("tickTime", this.tickTime);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.tickTime = tag.getInt("tickTime");
	}

	// ブロックステータスの取得
	public BlockState getState (BlockPos pos) {
		return this.getLevel().getBlockState(pos);
	}

	// ブロックの取得
	public Block getBlock (BlockPos pos) {
		return this.getState(pos).getBlock();
	}

	// ブロックえんちちーの取得
	public BlockEntity getTile (BlockPos pos) {
		return this.getLevel().getBlockEntity(pos);
	}

	// 向きの取得
	public Direction getFace () {
		BlockState state = this.getState(this.getBlockPos());
		return !state.hasProperty(BaseFaceBlock.FACING) ? Direction.NORTH : this.getState(this.getBlockPos()).getValue(BaseFaceBlock.FACING);
	}

	// 角度取得
	public float getRot () {
		switch (this.getFace()) {
		case NORTH: return 0F;
		case SOUTH: return 180F;
		case WEST: return 90F;
		case EAST: return 270F;
		default: return 0F;
		}
	}

	// 音を流す
	public void playSound (BlockPos pos, SoundEvent sound, float vol, float pit) {
		this.getLevel().playSound(null, pos, sound, SoundSource.BLOCKS, vol, pit);
	}

	// 音を流す
	public void playSound (Level world, BlockPos pos, SoundEvent sound, float vol, float pit) {
		world.playSound(null, pos, sound, SoundSource.BLOCKS, vol, pit);
	}

	// ゲーム時間の取得
	public long getTime () {
		return this.getLevel().getGameTime();
	}

	// サーバーかどうか
	public boolean isSever () {
		return !this.getLevel().isClientSide;
	}

	// ブロックえんちちーの取得
	public BlockEntity getEntity(BlockPos pos) {
		return this.getLevel().getBlockEntity(pos);
	}

	public static void serverTick(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		tile.serverTick(world, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		this.tickTime++;
	}

	public static void clientTick(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		tile.clientTick(world, pos, state);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		this.tickTime++;

		if (this.tickTime % 20 == 0 && this.isRSStop() && this.isRSPower()) {
			this.addParticlesAroundSelf(world, this.rand, pos, DustParticleOptions.REDSTONE);
		}
	}

	// パーティクルスポーンリング
	protected void spawnParticleRing(ServerLevel server, ParticleOptions particle, double range, BlockPos pos, double addY, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.05D) {
			double rate = range;
			server.sendParticles(particle, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, -Math.cos(degree) * 0.25D, ySpeed, -Math.sin(degree) * 0.25D, moveValue);
		}
	}

	// パーティクルスポーンリング
	protected void spawnParticleRing(Level world, ParticleOptions particle, double range, BlockPos pos, double addY, double ySpeed, float chance) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.05D) {
			if (chance < this.rand.nextFloat()) { continue; }
			double rate = range;
			world.addParticle(particle, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, Math.cos(degree) * 0.25D * this.getRandFloat(), this.rand.nextDouble() * 0.1D, Math.sin(degree) * 0.1D * this.getRandFloat());
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (Level world, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		world.addParticle(particle, x, y, z, face.get3DDataValue() * way, range, angle + way * 1 * SMItem.SPEED);
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (Level world, ParticleOptions particle, BlockPos pos, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 0;
		world.addParticle(particle, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, face.get3DDataValue() * way, range, angle + way * 1 * SMItem.SPEED);
	}

	protected void addParticlesAroundSelf(Level level, Random rand, BlockPos pos, ParticleOptions par) {
		for (int i = 0; i < 4; ++i) {
			double d0 = rand.nextDouble() * 0.02D;
			double d1 = rand.nextDouble() * 0.02D;
			double d2 = rand.nextDouble() * 0.02D;
			level.addParticle(par, this.getRandomX(pos, rand, 0.5D), this.getRandomY(pos, rand), this.getRandomZ(pos, rand, 0.5D), d0, d1, d2);
		}
	}

	public double getRandomX(BlockPos pos, Random rand, double scale) {
		return pos.getX() + ((2D * rand.nextDouble() - 1D) * scale) + 0.5D;
	}

	public double getRandomY(BlockPos pos, Random rand) {
		return pos.getY() + rand.nextDouble() * 0.5D + 1D;
	}

	public double getRandomZ(BlockPos pos, Random rand, double scale) {
		return pos.getZ() + ((2D * rand.nextDouble() - 1D) * scale) + 0.5D;
	}

	public float getRandFloat () {
		return this.rand.nextFloat() - this.rand.nextFloat();
	}

	public float getRandFloat (float rate) {
		return this.getRandFloat() * rate;
	}

	// List<ItemStack>をnbt保存
	public CompoundTag saveStackList (CompoundTag nbt, List<ItemStack> stackList, String name) {

		// NULLチェックとListの個数を確認
		if (stackList != null && !stackList.isEmpty()) {

			// リストの分だけ回してNBTに保存
			ListTag tagsList = new ListTag();
			for (ItemStack stack : stackList) {

                // nbtリストにnbtを入れる
				if (stack.isEmpty()) { continue; }
                tagsList.add(stack.save(new CompoundTag()));
			}

			// NBTに保存
			nbt.put(name, tagsList);
		}

		return nbt;
	}

	// nbtを呼び出してList<BlockPos>に突っ込む
	public List<ItemStack> loadAllStack(CompoundTag nbt, String name) {
		List<ItemStack> stackList = new ArrayList<>();
		nbt.getList(name, 10).forEach(t -> stackList.add(ItemStack.of(this.getTag(t))));
		return stackList;
	}

	// List<ItemStack>をnbt保存
	public CompoundTag saveStackListList (CompoundTag nbt, List<List<ItemStack>> stackListList, String name) {

		// NULLチェックとListの個数を確認
		if (stackListList != null && !stackListList.isEmpty()) {

			ListTag tagsListList = new ListTag();

			for (List<ItemStack> stackList : stackListList) {

				// リストの分だけ回してNBTに保存
				ListTag tagsList = new ListTag();
				for (ItemStack stack : stackList) {

	                // nbtリストにnbtを入れる
					if (stack.isEmpty()) { continue; }
	                tagsList.add(stack.save(new CompoundTag()));
				}

				// NBTに保存
				tagsListList.add(tagsList);
			}

			// NBTに保存
			nbt.put(name, tagsListList);
		}

		return nbt;
	}

	// nbtを呼び出してList<BlockPos>に突っ込む
	public List<List<ItemStack>> loadAllStackList(CompoundTag nbt, String name) {

		// nbtを受け取りnbtリストを作成
		ListTag tagsListList = nbt.getList(name, 9);
		List<List<ItemStack>> stackListList = new ArrayList<>();

		// nbtリスト分だけ回して座標リストに入れる
		for (int i = 0; i < tagsListList.size(); ++i) {
			List<ItemStack> stackList = new ArrayList<>();
			tagsListList.getList(i).forEach(t -> stackList.add(ItemStack.of(this.getTag(t))));
			stackListList.add(stackList);
		}
		return stackListList;
	}

	// List<Float>をnbt保存
	public CompoundTag saveFloatList (CompoundTag nbt, List<Float> floatList, String name) {

		// NULLチェックとListの個数を確認
		if (floatList != null && !floatList.isEmpty()) {

			// リストの分だけ回してNBTに保存
			ListTag tagsList = new ListTag();
			for (Float f : floatList) {

                // nbtリストにnbtを入れる
				CompoundTag tags = new CompoundTag();
				tags.putFloat("floatList", f);
                tagsList.add(tags);
			}

			// NBTに保存
			nbt.put(name, tagsList);
		}

		return nbt;
	}

	// nbtを呼び出してList<Float>に突っ込む
	public List<Float> loadAllFloat(CompoundTag nbt, String name) {
		List<Float> floatList = new ArrayList<>();
		nbt.getList(name, 10).forEach(t -> floatList.add(this.getTag(t).getFloat("floatList")));
		return floatList;
	}

	public void sendPKT() {

		Level world = this.level;
		if (world == null) { return; }

		BlockPos pos = this.getBlockPos();
		BlockState state = world.getBlockState(pos);

		if (world.hasChunkAt(pos)) {
			world.getChunkAt(pos).setUnsaved(true);
		}

		world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
	}

	// インベントリサイズの取得
	public int getInvSize () {
		return 0;
	}

	public CompoundTag getTag (Tag tag) {
		return (CompoundTag) tag;
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("");
	}

	public void addStackList (List<ItemStack> stackList, ItemStack stack) {
		if (!stack.isEmpty()) {
			stackList.add(stack);
		}
	}

	public Iterable<BlockPos> getRangePos (BlockPos pos, int range) {
		return BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	public Iterable<BlockPos> getRangePosUnder (int range) {
		return BlockPos.betweenClosed(this.getBlockPos().offset(-range, 0, -range), this.getBlockPos().offset(range, range, range));
	}

	public IItemHandler getItemHandler(BlockEntity tile, Direction face) {
		Optional<IItemHandler> cap = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, face).resolve();
		if (cap.isPresent()) {
			return cap.get();
		}

		else if (tile instanceof WorldlyContainer con) {
			return new SidedInvWrapper(con, face);
		}

		else if (tile instanceof Container con) {
			return new InvWrapper(con);
		}

		return null;
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> flag, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range)).stream().filter(flag).toList();
	}

	public <T extends Entity> List<T> getEntityListHalf(Class<T> enClass, Predicate<T> flag, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABBHalf(range)).stream().filter(flag).toList();
	}

	// 範囲の取得
	public AABB getAABB (double range) {
		return this.getAABB(range, range, range);
	}

	// 範囲の取得
	public AABB getAABBHalf (double range) {
		return this.getAABB(range, range * 0.5D, range);
	}

	// 範囲の取得
	public AABB getAABB (double x, double  y, double  z) {
		BlockPos pos = this.getBlockPos();
		return new AABB(pos.offset(-x, -y, -z), pos.offset(x, y, z));
	}

	public void clickButton () {
		this.playSound(this.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, 0.15F, this.rand.nextFloat() * 0.1F + 0.9F);
	}

	public boolean isAir () {
		return this.getState(this.getBlockPos()).isAir();
	}

	public boolean isRSPower () {
		return this.getLevel().getBestNeighborSignal(this.getBlockPos()) > 0;
	}

	public boolean isRSStop () {
		return false;
	}

	public boolean isPeaceful (Level world) {
		return world.getDifficulty() == Difficulty.PEACEFUL;
	}

	public void addPotion (LivingEntity entity, MobEffect potion, int time, int level) {
		entity.addEffect(new MobEffectInstance(potion, time, level, true, false));
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return true;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return null;
	}

	public class StackHandler extends ItemStackHandler {

		public boolean isUpdate = false;

		public StackHandler(int size) {
			super(size);
		}

		public StackHandler(int size, boolean isUpdate) {
			super(size);
			this.isUpdate = true;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			setChanged();

			if (this.isUpdate) {
				sendInfo();
			}
		}

		public NonNullList<ItemStack> getContents() {
			return this.stacks;
		}
	}

	protected void sendInfo() {

		Level world = this.getLevel();
		if (world == null) { return; }

		BlockPos pos = this.getBlockPos();
		BlockState state = world.getBlockState(pos);

		if (world.hasChunkAt(pos)) {
			world.getChunkAt(pos).setUnsaved(true);
		}

		world.sendBlockUpdated(this.getBlockPos(), state, state, Block.UPDATE_CLIENTS);
	}

	public ItemStack getDropStack(ItemStack stack) {
		if (!this.isInfoEmpty()) {
			stack.addTagElement("BlockEntityTag", this.saveWithoutMetadata());
		}

		return stack;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		if (this.resolver != null) {
			this.resolver.invalidateAll();
		}
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER && this.resolver != null) {
			return this.resolver.getCapabilityUnchecked(cap, side);
		}
		return super.getCapability(cap, side);
	}

	public NonNullLazy<IItemHandler> getHandler (IItemHandlerModifiable handler, WriteMode mode) {
		return () -> new WrappedItemHandler(handler, mode);
	}

	protected class SingleHandlerProvider extends SidItemHandler {

		private final ICapabilityResolver<IItemHandler> inRes;

		protected SingleHandlerProvider(NonNullLazy<IItemHandler> compose) {
			this.inRes = this.getBasicResolver(compose);
		}

		protected SingleHandlerProvider(IItemHandlerModifiable compose, WriteMode mode) {
			this.inRes = this.getBasicResolver(this.getHandler(compose, mode));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction face) {
			return this.inRes;
		}

		@Override
		public void invalidateAll() {
			this.inRes.invalidateAll();
		}
	}

	protected class InOutHandlerProvider extends SidItemHandler {

		private final ICapabilityResolver<IItemHandler> inRes;
		private final ICapabilityResolver<IItemHandler> outRes;

		protected InOutHandlerProvider(IItemHandlerModifiable in, IItemHandlerModifiable out) {
			this.inRes = this.getBasicResolver(this.getHandler(in, WrappedItemHandler.WriteMode.IN));
			this.outRes = this.getBasicResolver(this.getHandler(out, WrappedItemHandler.WriteMode.OUT));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction face) {

			if (face == null) {
				return this.inRes;
			}

			else if (face == Direction.DOWN) {
				return this.outRes;
			}

			return this.inRes;
		}

		@Override
		public void invalidateAll() {
			this.inRes.invalidateAll();
			this.outRes.invalidateAll();
		}
	}
}
