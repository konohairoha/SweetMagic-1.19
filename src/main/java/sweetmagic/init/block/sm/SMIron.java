package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMIron extends BaseSMBlock {

	private final float enchaPower;

	public SMIron(String name, float enchaPower) {
		super(name, setState(Material.METAL).sound(SoundType.METAL).strength(1F, 8192F));
		this.enchaPower = enchaPower;
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	public SMIron(String name, float enchaPower, int level) {
		super(name, setState(Material.METAL).sound(SoundType.METAL).strength(1F, 8192F).lightLevel((l) -> level));
		this.enchaPower = enchaPower;
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	public float getEnchantPower () {
		return this.enchaPower;
	}
}
