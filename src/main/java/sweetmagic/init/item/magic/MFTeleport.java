package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.tile.menu.container.ContainerClero;

public class MFTeleport extends BaseMagicItem {

	public MFTeleport(String name, boolean isShrink) {
		super(name, SMMagicType.CHARGE, SMElement.TIME, 1, 40, isShrink ? 10 : 100, isShrink);
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));
		return toolTip;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains("pX");
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// 選択中のアイテムを取得
		ItemStack stack = wandInfo.getStack();
		IWand wand = wandInfo.getWand();
		ItemStack slotStack= wand.getSlotItem(player, new WandInfo(stack));
		CompoundTag tags = slotStack.getTag();

		// テレポート
		if (tags != null && tags.contains("pX")) {
			this.teleportTo(player, player.blockPosition(), slotStack, tags);
		}

		return true;
	}

	// テレポート
	public void teleportTo(Player player, BlockPos basePos, ItemStack stack, CompoundTag tags) {

		Level world = player.level;
		this.playSound(world, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 0.25F, 1F);

		// NBTから座標取得
		BlockPos pos = new BlockPos(tags.getInt("pX") + 0.5F, tags.getInt("pY") + 1F, tags.getInt("pZ") + 0.5F);
		ResourceLocation dim = new ResourceLocation(tags.getString("dim"));

		// テレポート前のパーティクル表示
		if (world instanceof ServerLevel server) {

			double range = 0.875D;
			double ySpeed = -2.0D;

			for (int i= -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, basePos.above(1), i / 3D, ySpeed, 1D);
			}
		}

		if (player.getServer() != null && player instanceof ServerPlayer sp) {

			// ディメンションテレポート
			ServerLevel server = sp.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
			sp.teleportTo(server, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0, 0F);

			double range = 0.875D;
			double ySpeed = 1.0D;

			// テレポート後のパーティクル表示
			for (int i = -1; i < 5; i++) {
				this.spawnParticleRing(server, ParticleTypes.PORTAL, range, pos.below(2), i / 3D, ySpeed, 1D);
			}

			this.playSound(server, pos, SoundEvents.ENDERMAN_TELEPORT, 0.25F, 1F);
		}

		player.fallDistance = 0.0F;
		player.giveExperiencePoints(0);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);
		CompoundTag tags = stack.getOrCreateTag();

		if (tags != null && player.isShiftKeyDown()) {

			if (!world.isClientSide) {
				NetworkHooks.openScreen((ServerPlayer) player, new ContainerClero(stack), b -> b.writeByte(player.getInventory().selected));
				return InteractionResultHolder.consume(stack);
			}
		}

		if (!world.isClientSide) {
			BlockPos pos = player.blockPosition();
			tags.putInt("pX", pos.getX());
			tags.putInt("pY", pos.getY());
			tags.putInt("pZ", pos.getZ());
			tags.putString("dim", world.dimension().location().toString());
			tags.putString("dim_view", world.dimension().location().getPath().toString());
			player.sendSystemMessage(this.getText("posregi").withStyle(GREEN));
		}

		this.playSound(world, player, SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		return InteractionResultHolder.consume(stack);
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {

		toolTip.add(this.getText("clerodendrum").withStyle(GREEN));
		toolTip.add(this.getText("clero_rename").withStyle(GOLD));
		toolTip.add(this.getTip(" "));

		CompoundTag tag = stack.getTag();

		if (tag != null && tag.contains("pX")) {

			int x = tag.getInt("pX");
			int y = tag.getInt("pY");
			int z = tag.getInt("pZ");
			String dim = tag.getString("dim_view");

			String pos = ": " + x + ", " + y + ", " + z;
			toolTip.add(this.getTipArray( this.getText("regi_pos"), this.getLabel(pos).withStyle(WHITE)).withStyle(GREEN));
			toolTip.add(this.getTipArray( this.getText("regi_dim"), ": ", this.getLabel(dim).withStyle(WHITE)).withStyle(GREEN));
		}
	}
}
