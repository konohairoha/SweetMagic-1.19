package sweetmagic.init.block.base;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ITileMF;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileSMMagic;

public abstract class BaseMFBlock extends BaseFaceBlock implements EntityBlock {

	public BaseMFBlock(String name) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F));
		this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	public BaseMFBlock(String name, boolean flag) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	public BaseMFBlock(String name, BlockBehaviour.Properties props) {
		super(name, props);
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// 右クリックしない
	public boolean canRightClick(Level world, BlockPos pos, Player player, ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (player == null) { return InteractionResult.PASS; }

		ItemStack stack = player.getItemInHand(hand);
		if (!this.canRightClick(world, pos, player, stack)) { return InteractionResult.PASS; }

		// 何も持ってなかったら終了
		if (stack.isEmpty()) {
			this.actionBlock(world, pos, player, stack);
			return InteractionResult.sidedSuccess(world.isClientSide);
		}

		// NBTを取得
		CompoundTag tags = stack.getTag();

		if (tags == null || !tags.contains("X")) {

			if (stack.is(ItemInit.mf_stuff)) {
				return this.setBlockPos(world, tags, stack, player, pos);
			}

			this.actionBlock(world, pos, player, stack);
			return InteractionResult.sidedSuccess(world.isClientSide);
		}

		BlockEntity tile = world.getBlockEntity(pos);
		if (tile == null || !(tile instanceof ITileMF mfBlock)) { return InteractionResult.PASS; }

		// 受け取り側かどうか
		if (mfBlock.getReceive()) {

			// NBTがnull以外なら
			BlockPos tilePos = new BlockPos(tags.getInt("X"), tags.getInt("Y"), tags.getInt("Z"));
			if (tilePos.getX() == pos.getX() && tilePos.getY() == pos.getY() && tilePos.getZ() == pos.getZ()) { return InteractionResult.PASS; }

			// 送信側のブロックえんちちーを取得
			if (world.getBlockEntity(tilePos) instanceof ITileMF sendMFBlock && sendMFBlock.getPosList().contains(pos)) {
				return InteractionResult.PASS;
			}

			mfBlock.addPosList(tilePos);
			mfBlock.sentClient();
			tags.remove("tick");
			tags.remove("X");
			tags.remove("Y");
			tags.remove("Z");

			if (!world.isClientSide) {
				player.sendSystemMessage(this.getText("posregi").withStyle(GREEN));
			}

			else {
				player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
				this.spawnParticl(world, pos, world.random);
			}
		}

		// 送り側なら座標を登録
		else {
			this.setBlockPos(world, tags, stack, player, pos);
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// 座標を登録
	public InteractionResult setBlockPos(Level world, CompoundTag tags, ItemStack stack, Player player, BlockPos pos) {

		// NBTが保存したなかったら初期化
		if (tags == null) {
			stack.setTag(new CompoundTag());
			tags = stack.getTag();
		}

		// 座標をアイテムに登録
		tags.putInt("tick", player.tickCount % 30);
		tags.putInt("X", pos.getX());
		tags.putInt("Y", pos.getY());
		tags.putInt("Z", pos.getZ());

		if (!world.isClientSide) {
			player.sendSystemMessage(this.getText("posset").withStyle(GOLD));
		}

		else {
			player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
			this.spawnParticl(world, pos, world.random);
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// パーティクルスポーン
	public void spawnParticl(Level world, BlockPos pos, RandomSource rand) {

		for (int i = 0; i < 16; i++) {

			float f1 = pos.getX() + 0.5F;
			float f2 = pos.getY() + 0.25F + rand.nextFloat() * 0.5F;
			float f3 = pos.getZ() + 0.5F;
			float x = (rand.nextFloat() - rand.nextFloat()) * 0.15F;
			float z = (rand.nextFloat() - rand.nextFloat()) * 0.15F;
			world.addParticle(ParticleInit.NORMAL, f1, f2, f3, x, 0, z);
		}
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// ドロップアイテムにNBTを登録してアイテムドロップ
	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM base) {
		if (base instanceof TileSMMagic tile) {
			ItemStack stack = this.setTagStack(tile, new ItemStack(this));
			this.spawnItem(world, pos, stack);
		}
	}

	// ドロップアイテムにNBT付与
	public ItemStack setTagStack(TileSMMagic tile, ItemStack stack) {
		if (tile.isInfoEmpty()) { return stack; }
		CompoundTag tileTags = tile.saveWithoutMetadata();
		if (tileTags.contains(tile.POST)) { tileTags.remove(tile.POST); }
		stack.addTagElement("BlockEntityTag", tileTags);
		stack.getTag().putInt("mf", tile.getMF());
		return stack;
	}

	// tierの取得
	public int getTier() {
		return 1;
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 10000;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		super.appendHoverText(stack, get, toolTip, flag);

		CompoundTag tags = stack.getTag();
		this.addTip(toolTip, stack, tags);
		toolTip.add(this.getTip(""));
		toolTip.add(this.tierTip(this.getTier()));
		toolTip.add(this.getTipArray(this.getText("maxmf"), ": ", getTip(String.format("%,d", this.getMaxMF())).withStyle(WHITE), GREEN));

		int mf = tags != null ? tags.getInt("mf") : 0;
		toolTip.add(this.getTipArray(String.format("%,d", mf), getTip("MF").withStyle(GREEN), WHITE));
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
	}

	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int par1, int par2) {
		super.triggerEvent(state, level, pos, par1, par2);
		BlockEntity tile = level.getBlockEntity(pos);
		return tile == null ? false : tile.triggerEvent(par1, par2);
	}

	public float getEnchantPower() {
		switch (this.getTier()) {
		case 2: return 2.5F;
		case 3: return 5F;
		default: return 1F;
		}
	}

	public boolean keepTileInfo() {
		return false;
	}

	public ItemStack inheritingNBT(ItemStack oldStack, ItemStack newStack) {
		return newStack;
	}
}
