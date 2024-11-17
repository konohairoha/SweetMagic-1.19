package sweetmagic.init.item.magic;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.misc.NotNull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IAmorUtil;
import sweetmagic.api.iitem.IHarness;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;

public class SMHarness extends ArmorItem implements IHarness, IAmorUtil {

	private final String name;
	public final int data;
	public int maxMF;

	private int tickTime = 0;

	public SMHarness(String name, int data, int maxMF) {
		super(IAmorUtil.getArmorMaterial(data), EquipmentSlot.FEET, IAmorUtil.getArmorPro());
		this.name = name;
		this.data = data;
		this.setMaxMF(maxMF);
		ItemInit.itemMap.put(this, this.name);
	}

	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {

		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.putAll(super.getDefaultAttributeModifiers(slot));
		map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(IAmorUtil.SMUPSPEED, "SM speedUP", 0.04F, AttributeModifier.Operation.ADDITION));

		return slot == EquipmentSlot.FEET ? map.build() : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {

		if (this.data == 1) {
			player.fallDistance = 0F;

			if (!player.isCreative() && !player.isSpectator() && player.getAbilities().flying && !this.isMFEmpty(stack)) {

				if (world.isClientSide) {
					RandomSource rand = world.random;
					Vec3 vec = player.getDeltaMovement();

					float x = (float) (player.xo - 0.5F + rand.nextFloat());
					float y = (float) (player.yo - 0.4F + rand.nextFloat() * 0.5F);
					float z = (float) (player.zo - 0.5F + rand.nextFloat());

					float f1 = (float) (-vec.x * 0.5F);
					float f2 = (float) (-0.025F + -vec.y * 0.5F);
					float f3 = (float) (-vec.z * 0.5F);
					world.addParticle(ParticleTypes.END_ROD, x, y, z, f1, f2, f3);
				}

				else if (!player.isShiftKeyDown()) {
					Vec3 vec = player.getDeltaMovement();

					if (vec.y < 0) {
						player.setDeltaMovement(new Vec3(vec.x, 0D, vec.z));
					}
				}
			}
		}

		// 飛行中なら終了
		if (this.getMF(stack) < 1 || ++this.tickTime % 60 != 0) { return; }

		this.tickTime = 0;
		int useMF = 0;
		int costDown = Math.min(99, this.getEnchantLevel(EnchantInit.mfCostDown, stack) * 7);

		if (this.data != 0 && !player.isCreative() && !player.isSpectator() && player.getAbilities().flying) {
			useMF = 30;

			if (this.isMFEmpty(stack)) {
				player.getAbilities().mayfly = false;
			}
		}

		if (costDown > 0) {
			useMF *= (100 - costDown) / 100F;
		}

		this.setMF(stack, this.getMF(stack) - useMF);
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

	//アイテムにダメージを与える処理を無効
	public void setDamage(ItemStack stack, int damage) {
		return;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public int getTier () {
		return this.data + 1;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(IAmorUtil.ArmorRobeRender.INSTANCE);
	}

	@Override
	@Nullable
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String typeIn) {
		return SweetMagicCore.MODID + ":textures/block/empty.png";
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
		return ENCHACATELIST.contains(enchant.category) || ( enchant != Enchantments.UNBREAKING && enchant.category.canEnchant(stack.getItem()));
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("aether_boot").withStyle(GOLD));
		toolTip.add(this.getText("aether_boot_move").withStyle(GOLD));

		if (this.data >= 1) {
			toolTip.add(this.getText("angel_harness").withStyle(GOLD));
		}
	}
}

