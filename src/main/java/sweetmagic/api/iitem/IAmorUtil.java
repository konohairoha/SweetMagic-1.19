package sweetmagic.api.iitem;

import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.SoundInit;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.init.render.entity.model.PorchModel;
import sweetmagic.init.render.entity.model.SMRobeModel;

public interface IAmorUtil {

	public static final UUID SMUPSPEED = UUID.fromString("7f10172d-de69-49d7-81bd-9594286a0425");
	public static final UUID SMUPHEALTH = UUID.fromString("7f10172d-de69-49d7-81bd-9594286a0730");
	public static final UUID SMATTACKREACH = UUID.fromString("7f10172d-de69-49d7-81bd-9594286a0927");

	public final class ArmorRobeRender implements IClientItemExtensions {

		public static final ArmorRobeRender INSTANCE = new ArmorRobeRender();

		@Override
		public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> model) {
			ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(SMRobeModel.LAYER);
			return new SMRobeModel<>(root);
		}
	}

	public final class ArmorPorchRender implements IClientItemExtensions {

		public static final ArmorPorchRender INSTANCE = new ArmorPorchRender();

		@Override
		public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> model) {
			ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(PorchModel.LAYER);
			return new PorchModel(root);
		}
	}

	public static ArmorMaterial getArmorMaterial (int data) {
		return data >= 1 ? SMArmorMaterialTier2.INSTANCE : SMArmorMaterialTier1.INSTANCE;
	}

	public static Item.Properties getArmorPro () {
		return SMItem.setItem(999, SweetMagicCore.smMagicTab);
	}

	public class SMArmorMaterialTier1 implements ArmorMaterial {

		public static final SMArmorMaterialTier1 INSTANCE = new SMArmorMaterialTier1();

		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
			switch (slot) {
			case HEAD: return 3;
			case CHEST: return 6;
			case LEGS: return 7;
			case FEET: return 3;
			default : return 0;
			}
		}

		@Override
		public int getEnchantmentValue() {
			return 30;
		}

		@NotNull
		@Override
		public SoundEvent getEquipSound() {
			return SoundInit.ROBE_SMALL;
		}

		@NotNull
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@NotNull
		@Override
		public String getName() {
			return "sm_tier1";
		}

		@Override
		public float getToughness() {
			return 1;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.15F;
		}
	}

	public class SMArmorMaterialTier2 implements ArmorMaterial {

		public static final SMArmorMaterialTier2 INSTANCE = new SMArmorMaterialTier2();

		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
			switch (slot) {
			case HEAD: return 3;
			case CHEST: return 8;
			case LEGS: return 6;
			case FEET: return 3;
			default : return 0;
			}
		}

		@Override
		public int getEnchantmentValue() {
			return 30;
		}

		@NotNull
		@Override
		public SoundEvent getEquipSound() {
			return SoundInit.ROBE_SMALL;
		}

		@NotNull
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@NotNull
		@Override
		public String getName() {
			return "sm_tier2";
		}

		@Override
		public float getToughness() {
			return 2;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.2F;
		}
	}
}
