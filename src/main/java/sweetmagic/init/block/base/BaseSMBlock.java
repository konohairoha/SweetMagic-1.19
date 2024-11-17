package sweetmagic.init.block.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.util.ItemHelper;

public class BaseSMBlock extends Block implements ISMTip {

	protected final String name;

	public BaseSMBlock(String name, BlockBehaviour.Properties pro) {
		super(pro);
		this.name = name;
	}

	public String getRegistryName () {
		return this.name;
	}

	// 右クリック処理
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		return this.onUse(world, pos, player, hand);
	}

	// ブロックでのアクション
	public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {

		ItemStack stack = player.getItemInHand(hand);
		if (!this.canRightClick(player, stack)) { return InteractionResult.PASS; }

		this.actionBlock(world, pos, player, stack);
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// 右クリックしない
	public boolean canRightClick (Player player, ItemStack stack) {
		return false;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) { }

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		if (!this.isDrop()) { return new ArrayList<>(); }
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}

	// ドロップするかどうか
	protected boolean isDrop () {
		return true;
	}

	// アイテムリストをまとめてドロップ
	public void spawnItemList (Level world, BlockPos pos, List<ItemStack> stackList) {
		if (world.isClientSide) { return; }
		ItemHelper.compactItemListNoStacksize(stackList);
		stackList.forEach(s -> this.spawnItem(world, pos, s));
	}

	public void spawnItem (Level world, BlockPos pos, ItemStack stack) {
		ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
		entity.setNoPickUpDelay();
		entity.setUnlimitedLifetime();
		world.addFreshEntity(entity);
	}

	public void playerSound (Level world, BlockPos pos, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, pos, sound, SoundSource.BLOCKS, vol, pitch);
	}

	// ブロックの取得
	public Block getBlock (Level world, BlockPos pos) {
		return world.getBlockState(pos).getBlock();
	}

	// ブロックえんちちーの取得
	public BlockEntity getTile (Level world, BlockPos pos) {
		return world.getBlockEntity(pos);
	}

	// RS信号で停止するかどうか
	public boolean isRSStop () {
		return false;
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return null;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type1, BlockEntityType<E> type2, BlockEntityTicker<? super E> ticker) {
		return type2 == type1 ? (BlockEntityTicker<A>) ticker : null;
	}

	@Nullable
	protected <T extends BlockEntity> BlockEntityTicker<T> createMailBoxTicker(Level world, BlockEntityType<T> tileType, BlockEntityType<? extends TileAbstractSM> grill) {
		return createTickerHelper(tileType, grill, world.isClientSide() ? TileAbstractSM::clientTick : TileAbstractSM::serverTick);
	}

	public void openGUI (Level world, BlockPos pos, Player player, MenuProvider menu) {
		NetworkHooks.openScreen((ServerPlayer) player, menu, pos);
	}

	public static Properties setState(Material material) {
		return BlockBehaviour.Properties.of(material);
	}

	public static Properties setState(Material material, SoundType sound, float destroyTime, float explosionResist) {
		return setState(material).sound(sound).strength(destroyTime, explosionResist).noOcclusion();
	}

	public static Properties setState(Material material, SoundType sound, float destroyTime, float explosionResist, int lightLevel) {
		return setState(material, sound, destroyTime, explosionResist).lightLevel((l) -> lightLevel);
	}

	public Block getBlock (ItemStack stack) {
		return ((BlockItem) stack.getItem()).getBlock();
	}

	public Block getBlock (LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos).getBlock();
	}

	public void blockSound (Level world, Block block, BlockPos pos, Player player) {
        SoundType sound = this.getSoundType(block.defaultBlockState(), world, pos, player);
        this.playerSound(world, pos, sound.getPlaceSound(), (sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		if (!state.is(newState.getBlock()) && !world.isClientSide) {

			// ブロックえんちちーを取得
			BlockEntity entity = this.getTile(world, pos);
			if (entity != null && entity instanceof TileAbstractSM tile) {
				this.onRemove(world, pos, state, tile);
			}
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	public boolean isKeepTile () {
		return false;
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (!this.isKeepTile()) { return; }
		this.spawnItem(world, pos, tile.getDropStack(new ItemStack(this)));
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return this.getEnchantPower();
	}

	public float getEnchantPower () {
		return 0F;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		this.addBlockTip(toolTip);
	}

	public void addBlockTip (List<Component> toolTip) {
		if (this.getEnchantPower() > 0F) {
			toolTip.add(this.getTipArray(this.getText("enchant_power"), ": ", this.getLabel("" + this.getEnchantPower()).withStyle(WHITE) , GOLD));
		}

		if (this.isRSStop()) {
			toolTip.add(this.getText("rs_stop").withStyle(RED));
		}
	}
}
