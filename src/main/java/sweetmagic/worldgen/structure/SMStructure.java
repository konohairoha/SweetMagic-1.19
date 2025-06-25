package sweetmagic.worldgen.structure;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import sweetmagic.init.StructureInit;

public class SMStructure extends Structure {

	public static final Codec<SMStructure> CODEC = RecordCodecBuilder.<SMStructure> mapCodec((par1) -> {
		return par1.group(settingsCodec(par1),
			StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((par2) -> {
				return par2.startPool;
			}), ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((par3) -> {
				return par3.jigsawName;
			}), Codec.intRange(0, 512).fieldOf("size").forGetter((par4) -> {
				return par4.maxDepth;
			}), HeightProvider.CODEC.fieldOf("start_height").forGetter((par5) -> {
				return par5.startHeight;
			}), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((par6) -> {
				return par6.projectStartToHeightmap;
			}), Codec.intRange(1, 4096).fieldOf("max_distance_from_center").forGetter((par7) -> {
				return par7.maxDistanceFromCenter;
			})).apply(par1, SMStructure::new);
		}).flatXmap(setRange(), setRange()).codec();

	private static Function<SMStructure, DataResult<SMStructure>> setRange() {
		return (par1) -> {
			int b0;
			switch (par1.terrainAdaptation()) {
			case NONE:
				b0 = 0;
				break;
			case BURY:
			case BEARD_THIN:
			case BEARD_BOX:
				b0 = 256;
				break;
			default:
				throw new IncompatibleClassChangeError();
			}

			int i = b0;
			return par1.maxDistanceFromCenter + i > 8096 ? DataResult.error("Structure size including terrain adaptation must not exceed 1024") : DataResult.success(par1);
		};
	}

	private final Holder<StructureTemplatePool> startPool;
	private final Optional<ResourceLocation> jigsawName;
	private final int maxDepth;
	private final HeightProvider startHeight;
	private final Optional<Heightmap.Types> projectStartToHeightmap;
	private final int maxDistanceFromCenter;

	public SMStructure(Structure.StructureSettings set, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> jigsawName, int maxDepth, HeightProvider startHeight, Optional<Heightmap.Types> heightmap, int maxDistance) {
		super(set);
		this.startPool = startPool;
		this.jigsawName = Optional.empty();
		this.maxDepth = maxDepth;
		this.startHeight = startHeight;
		this.projectStartToHeightmap = heightmap;
		this.maxDistanceFromCenter = maxDistance;
	}

	public SMStructure(Structure.StructureSettings set, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight, Heightmap.Types heightmap) {
		this(set, startPool, Optional.empty(), maxDepth, startHeight, Optional.of(heightmap), 1024);
	}

	public SMStructure(Structure.StructureSettings set, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight) {
		this(set, startPool, Optional.empty(), maxDepth, startHeight, Optional.empty(), 1024);
	}

	public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext gen) {
		ChunkPos chunkPos = gen.chunkPos();
		int i = this.startHeight.sample(gen.random(), new WorldGenerationContext(gen.chunkGenerator(), gen.heightAccessor()));
		BlockPos pos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());
		Pools.forceBootstrap();
		return JigsawPlacement.addPieces(gen, this.startPool, this.jigsawName, this.maxDepth, pos, true, this.projectStartToHeightmap, this.maxDistanceFromCenter);
	}

	public StructureType<?> type() {
		return StructureInit.SM_JIGSAW_STRUCTURE.get();
	}
}
