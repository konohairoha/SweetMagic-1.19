package sweetmagic.util;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public class WorldHelper {

	public static void createLootDrop(List<ItemStack> drops, Level world, double x, double y, double z) {
		if (drops.isEmpty()) { return; }
		ItemHelper.compactItemListNoStacksize(drops);
		drops.forEach(d -> world.addFreshEntity(new ItemEntity(world, x, y, z, d)));
	}

	public static Structure getStructureForKey(ServerLevel world, ResourceLocation key) {
		return getStructureRegistry(world).get(key);
	}

	private static Registry<Structure> getStructureRegistry(ServerLevel world) {
		return world.registryAccess().ownedRegistryOrThrow(Registry.STRUCTURE_REGISTRY);
	}

	public static Holder<Structure> getHolderForStructure(ServerLevel world, Structure struc) {
		Optional<ResourceKey<Structure>> str = getStructureRegistry(world).getResourceKey(struc);
		return str.isPresent() ? getStructureRegistry(world).getHolderOrThrow(str.get()) : null;
	}

	public static Iterable<BlockPos> getRangePos(BlockPos pos, double area) {
		return WorldHelper.getRangePos(pos, area, area, area, area, area, area);
	}

	public static Iterable<BlockPos> getRangePos(BlockPos pos, double xA, double yA, double zA, double xB, double yB, double zB) {
		return BlockPos.betweenClosed(pos.offset(-xA, -yA, -zA), pos.offset(xB, yB, zB));
	}
}
