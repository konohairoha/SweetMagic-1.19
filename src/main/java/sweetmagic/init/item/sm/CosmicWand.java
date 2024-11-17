package sweetmagic.init.item.sm;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.SoundInit;

public class CosmicWand extends SMWand implements ISMTip {

	private final SMElement ele;
	private final boolean isScope;

	public CosmicWand(String name, int tier, int maxMF, int slot, SMElement ele) {
		super(name, tier, maxMF, slot);
		this.ele = ele;
		this.isScope = false;
	}

	public CosmicWand(String name, int tier, int maxMF, int slot, SMElement ele, boolean isScope) {
		super(name, tier, maxMF, slot);
		this.ele = ele;
		this.isScope = isScope;
	}

	// 杖の属性
	public SMElement getWandElement () {
		return this.ele;
	}

	public boolean isScope () {
		return this.isScope;
	}

	public void shotSound (Player player) {
		if (this.isScope()) {
			this.playSound(player.level, player, SoundInit.RIFLE_SHOT, 0.2F, 1F);
		}

		else {
			super.shotSound(player);
		}
	}

	public List<Component> addTip () {

		List<Component> tipList = new ArrayList<>();

		if (this.isScope()) {
			tipList.add(this.getText("scope_sneak").withStyle(GREEN));
			tipList.add(this.getText("scope_shot").withStyle(GREEN));
			tipList.add(this.getText("scope_dig").withStyle(RED));
		}

		return tipList;
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return !this.isScope() ? super.getDestroySpeed(stack, state) : 1F;
	}

	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		return !this.isScope();
	}

	// 属性一致時の効果値設定
	@Override
	public void setElementBonus(WandInfo wInfo, MagicInfo mInfo) {

		int level = this.getEnchaLevel(wInfo.getStack(), EnchantInit.elementBonus);

		if (!this.ele.is(SMElement.ALL)) {
			this.elementBonusPower = this.isElementEqual(mInfo.getMagicItem(), this.ele) ? 0.75F + level * 0.05F : 0F;
		}

		else {
			this.elementBonusPower = 0.25F + level * 0.025F;
		}
	}

	// 杖のレンダー時の設定
	public void renderWand (PoseStack pose, MultiBufferSource buffer, Player player, float parTick) {

		if (!this.isScope()) {
			super.renderWand(pose, buffer, player, parTick);
			return;
		}

		pose.translate(0.6D, 0.5D, 0.325D);
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		pose.mulPose(Vector3f.ZP.rotationDegrees(90F));

		// スニーク時
		if (player.isShiftKeyDown() && !player.getAbilities().flying) {
			pose.translate(0.05D, 0.725D, 0.06D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(225F));
		}

		// 通常
		else {
			pose.translate(0.05D, 0.55D, 0.15D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(225F));
		}
	}
}
