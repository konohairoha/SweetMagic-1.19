package sweetmagic.init.item.magic;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.key.SMKeybind;
import sweetmagic.util.SchematicExport;

public class StartLightWand extends SMItem implements IMFTool {

	public int maxMF;
	public final int data;

	public StartLightWand(String name, int data) {
		super(name, setItem(SweetMagicCore.smMagicTab));
		this.setMaxMF(data == 0 ? 50000 : 0);
		this.data = data;
	}

	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> build = ImmutableMultimap.builder();
			build.putAll(map);
			build.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SMAcce.BLOCK_REACH, "Block Reach", 32D, AttributeModifier.Operation.ADDITION));
			map = build.build();
		}
		return map;
	}

	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext con) {

		Player player = con.getPlayer();
		CompoundTag tag = stack.getOrCreateTag();
		Level world = con.getLevel();
		BlockPos pos = this.getPos(con.getClickedPos(), con.getClickLocation(), con.getClickedFace());

		if (!player.isShiftKeyDown()) {
			tag.putInt("startX", pos.getX());
			tag.putInt("startY", pos.getY());
			tag.putInt("startZ", pos.getZ());

			if (world.isClientSide) {
				player.sendSystemMessage(this.getText("posregi_start").withStyle(GREEN));
			}
		}

		else {
			tag.putInt("endX", pos.getX());
			tag.putInt("endY", pos.getY());
			tag.putInt("endZ", pos.getZ());

			if (world.isClientSide) {
				player.sendSystemMessage(this.getText("posregi_end").withStyle(GREEN));
			}
		}

		if (world.isClientSide) {
			player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	public void registerBlock(Level world, BlockState state, BlockPos pos, ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		Block block = state.getBlock();
		tag.putString("blockId", ForgeRegistries.BLOCKS.getKey(block).toString());
		tag.put("state", NbtUtils.writeBlockState(state));
		VoxelShape voxel = block.defaultBlockState().getCollisionShape(world, pos);
		tag.putBoolean("isFull", voxel.equals(Shapes.block()));
	}

	public void setBlock(Level world, Player player, ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags == null || (!tags.contains("startX") && !tags.contains("endX")) || !tags.contains("blockId") || !tags.contains("state") || player.hasEffect(PotionInit.non_destructive)) { return; }

		// 採掘範囲の取得
		BlockPos startPos = tags.contains("startX") ? new BlockPos(tags.getInt("startX"), tags.getInt("startY"), tags.getInt("startZ")) : new BlockPos(tags.getInt("endX"), tags.getInt("endY"), tags.getInt("endZ"));
		BlockPos endPos = tags.contains("endX") ? new BlockPos(tags.getInt("endX"), tags.getInt("endY"), tags.getInt("endZ")) : new BlockPos(tags.getInt("startX"), tags.getInt("startY"), tags.getInt("startZ"));
		Iterable<BlockPos> posList = BlockPos.betweenClosed(startPos, endPos);

		// 採掘するブロックと置換、クリエイティブ
		BlockState staet = NbtUtils.readBlockState(tags.getCompound("state"));
		Block block = this.getBlock(tags);
		Item blockItem = new ItemStack(block).getItem();
		boolean isExchange = tags.getBoolean("isExchange");
		boolean isCreative = player.isCreative();
		boolean isSetBlock = false;

		// 消費MFと設置個数の取得
		int mf = this.getMF(stack);
		int costDown = Math.min(10, this.getEnchaLevel(stack, EnchantInit.mfCostDown));
		float useMF = 0;
		float useMFRate = 1F - costDown * 0.05F;
		int count = 0;
		int maxBlockCount = 0;

		List<ItemStack> stackList = player.getInventory().items.stream().filter(s -> s.is(blockItem)).toList();
		List<ItemStack> dropList = new ArrayList<>();

		for (ItemStack item : stackList) {
			maxBlockCount += item.getCount();
		}

		if(this.data == 1) {
			if(SchematicExport.saveSchematic(SchematicExport.SCHEMATICS, staet.getBlock().getName().getString(), false, world, startPos, endPos)) {
				player.sendSystemMessage(this.getText("register_struc").withStyle(GREEN));
			}

			return;
		}

		for (BlockPos pos : posList) {
			BlockState targetState = world.getBlockState(pos);
			Block targetBlock = targetState.getBlock();
			if (targetBlock == block || targetState.hasBlockEntity()) { continue; }

			if (!isExchange && !targetState.isAir()) { continue; }

			if (isExchange && !targetState.isAir() && world instanceof ServerLevel server) {
				dropList.addAll(Block.getDrops(targetState, server, pos, world.getBlockEntity(pos), player, stack));
			}

			world.setBlock(pos, staet, 3);
			useMF += 1 * useMFRate;
			isSetBlock = true;

			if (!isCreative && (useMF >= mf || count++ >= maxBlockCount)) { break; }
		}

		if (isSetBlock) {
			SoundType sound = block.getSoundType(staet, world, player.blockPosition(), player);
			this.playSound(world, player, sound.getPlaceSound(), (sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);
		}

		if (!isCreative) {
			this.setMF(stack, mf - (int) useMF);

			for (ItemStack item : stackList) {
				int shrinkCount = Math.min(Math.min(item.getCount(), maxBlockCount), count);
				item.shrink(shrinkCount);
				maxBlockCount -= shrinkCount;
				count -= shrinkCount;
				if (maxBlockCount <= 0 || count <= 0) { break; }
			}

			if (!dropList.isEmpty() && !world.isClientSide) {
				BlockPos p = player.blockPosition();
				dropList.forEach(s -> world.addFreshEntity(new ItemEntity(world, p.getX(), p.getY(), p.getZ(), s)));
			}
		}
	}

	public void resetPos(Player player, ItemStack stack) {
		CompoundTag tags = stack.getOrCreateTag();
		tags.remove("startX");
		tags.remove("startY");
		tags.remove("startZ");
		tags.remove("endX");
		tags.remove("endY");
		tags.remove("endZ");
		player.sendSystemMessage(this.getText("posremo").withStyle(RED));
		this.playSound(player.getLevel(), player, SoundEvents.UI_BUTTON_CLICK, 0.25F, player.getRandom().nextFloat() * 0.1F + 1.2F);
	}

	public void changeExchange(Player player, ItemStack stack) {
		CompoundTag tags = stack.getOrCreateTag();
		tags.putBoolean("isExchange", !tags.getBoolean("isExchange"));
		player.sendSystemMessage(this.getText(tags.getBoolean("isExchange") ? "exchange_mode" : "set_mode").withStyle(GREEN));
		this.playSound(player.getLevel(), player, SoundEvents.UI_BUTTON_CLICK, 0.25F, player.getRandom().nextFloat() * 0.1F + 1.2F);
	}

	public BlockPos getPos(BlockPos pos, Vec3 vec, Direction face) {

		boolean isOver = false;
		double x = Math.abs((int) vec.x - vec.x);
		double y = Math.abs((int) vec.y - vec.y);
		double z = Math.abs((int) vec.z - vec.z);
		double size = 0.15D;

		switch (face) {
		case UP:
		case DOWN:
			isOver = (x > 1D - size || x < size) || (z > 1D - size || z < size);
			break;
		case NORTH:
		case SOUTH:
			isOver = (x > 1D - size || x < size) || (y > 1D - size || y < size);
			break;
		case WEST:
		case EAST:
			isOver = (y > 1D - size || y < size) || (z > 1D - size || z < size);
			break;
		}

		return isOver ? pos.relative(face) : pos;
	}

	public Block getBlock(CompoundTag tags) {
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tags.getString("blockId")));
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {

		toolTip.add(this.getText(this.name).withStyle(GOLD));

		MutableComponent keyNext = KeyPressEvent.getKeyName(SMKeybind.NEXT);
		MutableComponent keyBack = KeyPressEvent.getKeyName(SMKeybind.BACK);
		toolTip.add(this.getTipArray(keyNext.copy(), this.getText("key"), this.getText("startlight_wand_key1").withStyle(WHITE)).withStyle(RED));
		toolTip.add(this.getTipArray(keyBack.copy(), this.getText("key"), this.getText("startlight_wand_key2").withStyle(WHITE)).withStyle(RED));
		toolTip.add(this.empty());

		CompoundTag tag = stack.getOrCreateTag();

		toolTip.add(this.getTipArray(this.getLabel("MF: "), this.getLabel(this.format(this.getMF(stack)), WHITE)).withStyle(GREEN));

		String startPos = tag.contains("startX") ? tag.getInt("startX") + ", " + tag.getInt("startY") + ", " + tag.getInt("startZ") : this.getText("unregistered").getString();
		toolTip.add(this.getTipArray(this.getText("start_pos"), this.getLabel(startPos, WHITE)).withStyle(GREEN));

		String endPos = tag.contains("endX") ? tag.getInt("endX") + ", " + tag.getInt("endY") + ", " + tag.getInt("endZ") : this.getText("unregistered").getString();
		toolTip.add(this.getTipArray(this.getText("end_pos"), this.getLabel(endPos, WHITE)).withStyle(GREEN));

		String block = tag.contains("blockId") ? this.getBlock(tag).getName().getString() : this.getText("unregistered").getString();
		toolTip.add(this.getTipArray(this.getText("set_block"), this.getLabel(block, WHITE)).withStyle(GREEN));

		toolTip.add(this.getTipArray(this.getText("star_mode"), this.getText(tag.getBoolean("isExchange") ? "exchange_mode" : "set_mode").withStyle(WHITE)).withStyle(GREEN));
	}

	@Override
	public int getMaxMF(ItemStack stack) {
		int addMaxMF = (this.getEnchantLevel(EnchantInit.maxMFUP, stack) * 10) * (this.maxMF / 100);
		return this.maxMF + addMaxMF;
	}

	@Override
	public void setMaxMF(int maxMF) {
		this.maxMF = maxMF;
	}

	// エンチャレベル取得
	public int getEnchantLevel(Enchantment enchant, ItemStack stack) {
		return Math.min(EnchantmentHelper.getItemEnchantmentLevel(enchant, stack), 10);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return this.getMF(stack) >= this.getMaxMF(stack) ? 0X30FF89 : 0X00C3FF;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return this.getMF(stack) != 0;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return Math.min(13, Math.round(13F * (float) this.getMF(stack) / (float) this.getMaxMF(stack)));
	}
}
