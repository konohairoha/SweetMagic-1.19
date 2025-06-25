package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.init.StructureInit;
import sweetmagic.init.item.magic.SMMagicItem;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerCompas;

public class DungeonCompas extends SMMagicItem {

	public static final String ACTIVE = "Active";

	public DungeonCompas(String name) {
		super(name);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerCompas(stack));
		}

		return InteractionResultHolder.consume(stack);
	}

	public float getRotCompas(Entity entity, BlockPos pos) {
		double d0 = getAngle(entity, pos);
		double d1 = getRot(entity);
		double d2 = 0.5D - (d1 - 0.25D - d0);
		return Mth.positiveModulo((float) d2, 1F);
	}

	private double getAngle(Entity entity, BlockPos pos) {
		Vec3 vec3 = Vec3.atCenterOf(pos);
		return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX()) / (double) ((float) Math.PI * 2F);
	}

	private double getRot(Entity entity) {
		return Mth.positiveModulo((double) (entity.getVisualRotationYInDegrees() / 360F), 1D);
	}

	public String getDungeonName(ItemStack stack) {
		CompoundTag tags = stack.getOrCreateTag();
		if (!tags.getBoolean("foundStructure") || !tags.contains("selectId")) { return "select_dungen"; }
		return StructureInit.strucMap.get(tags.getInt("selectId")).name();
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GOLD));
	}
}
