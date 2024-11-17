package sweetmagic.util;

import java.util.List;
import java.util.Optional;

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

	public static Holder<Structure> getHolderForStructure(ServerLevel level, Structure structure) {
		Optional<ResourceKey<Structure>> optional = getStructureRegistry(level).getResourceKey(structure);
		return optional.isPresent() ? getStructureRegistry(level).getHolderOrThrow(optional.get()) : null;
	}
}
