package sweetmagic.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;

public class DimentionInit {

	public static ResourceKey<Level> SweetMagicWorld;

	public static void init() {
		SweetMagicWorld = ResourceKey.create(Registry.DIMENSION_REGISTRY, SweetMagicCore.getSRC("sweetmagic_world"));
	}
}
