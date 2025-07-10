package sweetmagic.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.fml.loading.FMLPaths;

public class SchematicExport {

	public static final Path SCHEMATICS = FMLPaths.GAMEDIR.get().resolve("schematics");

	public static boolean saveSchematic(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second) {
		BoundingBox bb = BoundingBox.fromCorners(first, second);
		BlockPos origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
		BlockPos bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

		StructureTemplate structure = new StructureTemplate();
		structure.fillFromWorld(level, origin, bounds, true, Blocks.STRUCTURE_VOID);
		CompoundTag data = structure.save(new CompoundTag());
		if (!fileName.endsWith(".nbt")) { fileName += ".nbt"; }
		Path file = dir.resolve(fileName).toAbsolutePath();

		try {
			Files.createDirectories(dir);
			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
				NbtIo.writeCompressed(data, out);
			}
		}

		catch (IOException e) { return false; }

		return true;
	}
}
