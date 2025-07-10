package sweetmagic.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

public class WorldHelper {

	public static boolean isPeace(LevelAccessor world) {
		return world.getDifficulty() == Difficulty.PEACEFUL;
	}

	public static void createLootDrop(List<ItemStack> drops, Level world, double x, double y, double z) {
		if (drops.isEmpty()) { return; }
		ItemHelper.compactItemListNoStacksize(drops);
		drops.forEach(d -> world.addFreshEntity(new ItemEntity(world, x, y, z, d)));
	}

	public static Structure getStructureKey(ServerLevel world, ResourceLocation key) {
		return getStructureRegistry(world).get(key);
	}

	private static Registry<Structure> getStructureRegistry(ServerLevel world) {
		return world.registryAccess().ownedRegistryOrThrow(Registry.STRUCTURE_REGISTRY);
	}

	public static Holder<Structure> getStructure(ServerLevel world, Structure struc) {
		Registry<Structure> strReg = WorldHelper.getStructureRegistry(world);
		Optional<ResourceKey<Structure>> str = strReg.getResourceKey(struc);
		return str.isPresent() ? strReg.getHolderOrThrow(str.get()) : null;
	}

	public static Iterable<BlockPos> getRangePos(BlockPos pos, double area) {
		return WorldHelper.getRangePos(pos, -area, -area, -area, area, area, area);
	}

	public static Iterable<BlockPos> getRangePos(BlockPos pos, double xA, double yA, double zA, double xB, double yB, double zB) {
		return BlockPos.betweenClosed(pos.offset(xA, yA, zA), pos.offset(xB, yB, zB));
	}

	public static <T extends Entity> List<T> getEntityList(Entity entity, Class<T> enClass, AABB aabb) {
		return entity.getLevel().getEntitiesOfClass(enClass, aabb);
	}

	public static <T extends Entity> List<T> getEntityList(Level world, Class<T> enClass, AABB aabb) {
		return world.getEntitiesOfClass(enClass, aabb);
	}

	public static <T extends Entity> List<T> getEntityList(Entity entity, Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return entity.getLevel().getEntitiesOfClass(enClass, aabb).stream().filter(filter).toList();
	}

	public static <T extends Entity> List<T> getEntityList(Level world, Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return world.getEntitiesOfClass(enClass, aabb).stream().filter(filter).toList();
	}
}
