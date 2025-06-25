package sweetmagic.init;

import com.mojang.math.Vector3f;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class AnimationInit {

	public static AnimationChannel.Interpolation LINE = AnimationChannel.Interpolations.LINEAR;

	public static final AnimationDefinition BELIAL_ATTACK = AnimationDefinition.Builder.withLength(26F)
		.addAnimation("body", createRot(key(0F, degree(67.5F, 0F, 0F), LINE), key(9F, degree(67.5F, 0F, 0F), LINE), key(10F, degree(68F, 4.7F, 11.6F), LINE), key(12F, degree(68F, 4.7F, 11.6F), LINE), key(13F, degree(68F, -5.7F, -14F), LINE), key(16F, degree(68F, -5.7F, -14F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(20.04F, degree(-10F, 0F, 0F), LINE), key(23.04F, degree(-10F, 0F, 0F), LINE), key(23.52F, degree(37.5F, 0F, 0F), LINE), key(24.6F, degree(37.5F, 0F, 0F), LINE), key(25.48F, degree(), LINE), key(26F, degree(), LINE)))
		.addAnimation("body", createPos(key(0F, pos(), LINE), key(1F, pos(0F, 8F, 0F), LINE), key(3F, pos(), LINE), key(4F, pos(0F, 8F, 0F), LINE), key(6F, pos(), LINE), key(7F, pos(0F, 8F, 0F), LINE), key(9F, pos(), LINE), key(10F, pos(0F, 8F, 0F), LINE), key(12F, pos(), LINE), key(13F, pos(0F, -3F, 0F), LINE), key(16F, pos(0F, -3F, 0F), LINE), key(18F, pos(), LINE), key(19.52F, pos(), LINE), key(25.48F, pos(), LINE)))
		.addAnimation("head", createRot(key(0F, degree(7.5F, 0F, 0F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(23F, degree(), LINE), key(23.52F, degree(-32.5F, 0F, 0F), LINE), key(24.6F, degree(-32.5F, 0F, 0F), LINE), key(25.48F, degree(), LINE), key(26F, degree(), LINE)))
		.addAnimation("right_arm", createRot(key(0F, degree(-30F, 0F, 0F), LINE), key(9F, degree(-30F, 0F, 0F), LINE), key(10F, degree(-69F, 16.4F, 6.2F), LINE), key(12F, degree(-69F, 16.4F, 6.2F), LINE), key(13F, degree(-69.4F, 14F, 5.2F), LINE), key(16F, degree(-69.4F, 14F, 5.2F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(21.12F, degree(-153F, -3.8F, 22.2F), LINE), key(23.04F, degree(-153F, -3.8F, 22.2F), LINE), key(23.52F, degree(32.5F, 0F, 0F), LINE), key(24.6F, degree(32.5F, 0F, 0F), LINE), key(25.48F, degree(), LINE), key(26F, degree(), LINE)))
		.addAnimation("right_arm", createPos(key(9F, pos(), LINE), key(10F, pos(4F, -4F, 9F), LINE), key(12F, pos(4F, -4F, 9F), LINE), key(13F, pos(2F, -7F, 7F), LINE), key(16F, pos(2F, -7F, 7F), LINE), key(18F, pos(), LINE), key(19.52F, pos(), LINE), key(25.48F, pos(), LINE)))
		.addAnimation("left_arm", createRot(key(0F, degree(-30F, 0F, 0F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(21.12F, degree(-153F, -3.8F, -22.2F), LINE), key(23.04F, degree(-153F, -3.8F, -22.2F), LINE), key(23.52F, degree(32.5F, 0F, 0F), LINE), key(24.6F, degree(32.5F, 0F, 0F), LINE), key(25.48F, degree(), LINE), key(26F, degree(), LINE)))
		.addAnimation("right_leg", createRot(key(0F, degree(35F, 0F, 0F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(25.48F, degree(), LINE)))
		.addAnimation("right_leg", createPos(key(0F, pos(0F, 5F, 7F), LINE), key(1F, pos(0F, 13F, 7F), LINE), key(3F, pos(0F, 5F, 7F), LINE), key(4F, pos(0F, 13F, 7F), LINE), key(6F, pos(0F, 5F, 7F), LINE), key(7F, pos(0F, 13F, 7F), LINE), key(9F, pos(0F, 5F, 7F), LINE), key(10F, pos(0F, 13F, 7F), LINE), key(12F, pos(0F, 5F, 7F), LINE), key(13F, pos(0F, 0F, 7F), LINE), key(16F, pos(0F, 0F, 7F), LINE), key(18F, pos(), LINE), key(19.52F, pos(), LINE), key(25.48F, pos(), LINE)))
		.addAnimation("left_leg", createRot(key(0F, degree(35F, 0F, 0F), LINE), key(18F, degree(), LINE), key(19.52F, degree(), LINE), key(25.48F, degree(), LINE)))
		.addAnimation("left_leg", createPos(key(0F, pos(0F, 5F, 7F), LINE), key(1F, pos(0F, 13F, 7F), LINE), key(3F, pos(0F, 5F, 7F), LINE), key(4F, pos(0F, 13F, 7F), LINE), key(6F, pos(0F, 5F, 7F), LINE), key(7F, pos(0F, 13F, 7F), LINE), key(9F, pos(0F, 5F, 7F), LINE), key(10F, pos(0F, 13F, 7F), LINE), key(12F, pos(0F, 5F, 7F), LINE), key(13F, pos(0F, 0F, 7F), LINE), key(16F, pos(0F, 0F, 7F), LINE), key(18F, pos(), LINE), key(19.52F, pos(), LINE), key(25.48F, pos(), LINE))).build();

	public static final AnimationDefinition BELIAL_LANDING = AnimationDefinition.Builder.withLength(6F)
		.addAnimation("body", createRot(key(0F, degree(), LINE), key(0.48F, degree(20F, 0F, 0F), LINE), key(2.48F, degree(20F, 0F, 0F), LINE), key(3F, degree(82.5F, 0F, 0F), LINE), key(4F, degree(82.5F, 0F, 0F), LINE), key(5F, degree(42F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("body", createPos(key(0F, pos(), LINE), key(2.48F, pos(), LINE), key(3F, pos(0F, -7F, 0F), LINE), key(4F, pos(0F, -7F, 0F), LINE), key(5F, pos(0F, -4F, 0F), LINE), key(6F, pos(), LINE)))
		.addAnimation("head", createRot(key(0F, degree(), LINE), key(0.48F, degree(20F, 0F, 0F), LINE), key(2.48F, degree(20F, 0F, 0F), LINE), key(4F, degree(20F, 0F, 0F), LINE), key(5F, degree(), LINE)))
		.addAnimation("right_arm", createRot(key(0F, degree(), LINE), key(0.48F, degree(-172.6F, 33.46F, -0.35F), LINE), key(2.48F, degree(-172.6F, 33.46F, -0.35F), LINE), key(3F, degree(-131F, 12.6F, -8.56F), LINE), key(4F, degree(-131F, 12.6F, -8.56F), LINE), key(5F, degree(-60F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("right_arm", createPos(key(0F, pos(), LINE), key(0.48F, pos(2F, -4F, 0F), LINE), key(2.48F, pos(2F, -4F, 0F), LINE), key(4F, pos(2F, -4F, 0F), LINE), key(5F, pos(), LINE)))
		.addAnimation("left_arm", createRot(key(0F, degree(), LINE), key(0.48F, degree(-172.6F, -33.46F, -0.35F), LINE), key(2.48F, degree(-172.6F, -33.46F, -0.35F), LINE), key(3F, degree(-131F, -12.6F, -8.56F), LINE), key(4F, degree(-131F, -12.6F, -8.56F), LINE), key(5F, degree(-60F, -4F, -3F), LINE), key(6F, degree(), LINE)))
		.addAnimation("left_arm", createPos(key(0F, pos(), LINE), key(0.48F, pos(-2F, -4F, 0F), LINE), key(2.48F, pos(-2F, -4F, 0F), LINE), key(3F, pos(-2F, -4F, 0F), LINE), key(4F, pos(-2F, -4F, 0F), LINE), key(5F, pos(), LINE), key(6F, pos(), LINE)))
		.addAnimation("right_leg", createRot(key(0F, degree(), LINE), key(0.48F, degree(30F, 0F, 0F), LINE), key(2.48F, degree(30F, 0F, 0F), LINE), key(4F, degree(30F, 0F, 0F), LINE), key(5F, degree(30F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("right_leg", createPos(key(0F, pos(), LINE), key(0.48F, pos(0F, 1F, 2F), LINE), key(2.48F, pos(0F, 1F, 2F), LINE), key(3F, pos(0F, -2F, 5F), LINE), key(4F, pos(0F, -2F, 5F), LINE), key(5F, pos(0F, -2F, 5F), LINE), key(6F, pos(), LINE)))
		.addAnimation("left_leg", createRot(key(0F, degree(), LINE), key(0.48F, degree(30F, 0F, 0F), LINE), key(2.48F, degree(30F, 0F, 0F), LINE), key(4F, degree(30F, 0F, 0F), LINE), key(5F, degree(), LINE)))
		.addAnimation("left_leg", createPos(key(0F, pos(), LINE), key(0.48F, pos(0F, 1F, 2F), LINE), key(2.48F, pos(0F, 1F, 2F), LINE), key(3F, pos(0F, -2F, 5F), LINE), key(4F, pos(0F, -2F, 5F), LINE), key(5F, pos(), LINE))).build();

	public static final AnimationDefinition BELIAL_LASER = AnimationDefinition.Builder.withLength(9.56F)
		.addAnimation("body", createRot(key(0F, degree(), LINE), key(0.56F, degree(15F, 0F, 0F), LINE), key(2.52F, degree(15F, 0F, 0F), LINE), key(4.48F, degree(-7.5F, 0F, 0F), LINE), key(8F, degree(-7.5F, 0F, 0F), LINE), key(9.56F, degree(), LINE)))
		.addAnimation("right_ribcage", createRot(key(2.52F, degree(), LINE), key(4F, degree(0F, 150F, 0F), LINE), key(8F, degree(0F, 150F, 0F), LINE), key(9.56F, degree(), LINE)))
		.addAnimation("left_ribcage", createRot(key(2.52F, degree(), LINE), key(4F, degree(0F, -150F, 0F), LINE), key(8F, degree(0F, -150F, 0F), LINE), key(9.56F, degree(), LINE)))
		.addAnimation("right_arm", createRot(key(0F, degree(), LINE), key(0.56F, degree(-58.5F, -17.3F, -10.3F), LINE), key(2.52F, degree(-58.5F, -17.3F, -10.3F), LINE), key(4.48F, degree(50F, -3F, 90F), LINE), key(8F, degree(50F, -3F, 90F), LINE), key(9.56F, degree(), LINE)))
		.addAnimation("right_arm", createPos(key(0F, pos(), LINE), key(0.56F, pos(0F, -4F, 0F), LINE), key(2.52F, pos(0F, -4F, 0F), LINE), key(4.48F, pos(5F, -4F, 3F), LINE), key(8F, pos(5F, -4F, 3F), LINE), key(9.56F, pos(), LINE)))
		.addAnimation("left_arm", createRot(key(0F, degree(), LINE), key(0.56F, degree(-67F, 21.8552F, -8.1111F), LINE), key(2.52F, degree(-67F, 21.86F, -8.11F), LINE), key(4.48F, degree(50F, 3F, -90F), LINE), key(8F, degree(50F, 3F, -90F), LINE), key(9.56F, degree(), LINE)))
		.addAnimation("left_arm", createPos(key(0F, pos(), LINE), key(0.56F, pos(0F, -4F, 0F), LINE), key(2.52F, pos(0F, -4F, 0F), LINE), key(4.48F, pos(-5F, -4F, 3F), LINE), key(8F, pos(-5F, -4F, 3F), LINE), key(9.56F, pos(), LINE))).build();

	public static final AnimationDefinition BELIAL_SWING = AnimationDefinition.Builder.withLength(4F)
		.addAnimation("body", createRot(key(1F, degree(), LINE), key(2F, degree(-12.5F, 0F, 7.5F), LINE), key(3.52F, degree(-12.5F, 0F, 7.5F), LINE), key(4F, degree(), LINE)))
		.addAnimation("body", createPos(key(0F, pos(), LINE), key(1F, pos(0F, 4F, 0F), LINE)))
		.addAnimation("head", createRot(key(1F, degree(), LINE), key(2F, degree(10F, 0F, -7.5F), LINE), key(3.52F, degree(10F, 0F, -7.5F), LINE), key(4F, degree(), LINE)))
		.addAnimation("right_arm", createRot(key(1F, degree(), LINE), key(2F, degree(-165.1489F, 1.7279F, -9.8511F), LINE), key(3.52F, degree(-165.15F, 1.73F, -9.85F), LINE), key(4F, degree(), LINE)))
		.addAnimation("right_arm", createPos(key(1F, pos(), LINE), key(2F, pos(2F, -7F, 0F), LINE), key(3.52F, pos(2F, -7F, 0F), LINE), key(4F, pos(), LINE)))
		.addAnimation("left_arm", createRot(key(1F, degree(), LINE), key(2F, degree(0F, 0F, -10F), LINE), key(3.52F, degree(0F, 0F, -10F), LINE), key(4F, degree(), LINE)))
		.addAnimation("left_arm", createPos(key(1F, pos(), LINE), key(2F, pos(-2F, 0F, 0F), LINE), key(3.52F, pos(-2F, 0F, 0F), LINE), key(4F, pos(), LINE)))
		.addAnimation("right_leg", createPos(key(0F, pos(), LINE), key(1F, pos(0F, 4F, 0F), LINE), key(2F, pos(0F, 5F, 0F), LINE), key(3.52F, pos(0F, 5F, 0F), LINE), key(4F, pos(), LINE)))
		.addAnimation("left_leg", createPos(key(0F, pos(), LINE), key(1F, pos(0F, 4F, 0F), LINE), key(2F, pos(0F, 4F, 0F), LINE), key(3.52F, pos(0F, 4F, 0F), LINE), key(4F, pos(), LINE))).build();

	public static final AnimationDefinition BELIAL_METEOR = AnimationDefinition.Builder.withLength(6F)
		.addAnimation("body", createRot(key(0F, degree(), LINE), key(1F, degree(32.5F, 0F, 0F), LINE), key(1.52F, degree(32.5F, 0F, 0F), LINE), key(2F, degree(-15F, 0F, 0F), LINE), key(5F, degree(-15F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("head", createRot(key(0F, degree(), LINE), key(1F, degree(37.5F, 0F, 0F), LINE), key(1.52F, degree(37.5F, 0F, 0F), LINE), key(2F, degree(-12.5F, 0F, 0F), LINE), key(5F, degree(-12.5F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("right_arm", createRot(key(0F, degree(), LINE), key(1F, degree(-54F, -12F, -9F), LINE), key(1.52F, degree(-54F, -12F, -9F), LINE), key(2F, degree(-156F, 0F, 0F), LINE), key(5F, degree(-156F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("left_arm", createRot(key(0F, degree(), LINE), key(1F, degree(-54F, 12F, 9F), LINE), key(1.52F, degree(-54F, 12F, 9F), LINE), key(2F, degree(-156F, 0F, 0F), LINE), key(5F, degree(-156F, 0F, 0F), LINE), key(6F, degree(), LINE)))
		.addAnimation("right_leg", createRot(key(0F, degree(), LINE), key(1F, degree(-12.5F, 0F, 0F), LINE), key(1.52F, degree(-12.5F, 0F, 0F), LINE), key(2F, degree(), LINE), key(5F, degree(), LINE), key(6F, degree(), LINE)))
		.addAnimation("right_leg", createPos(key(0F, pos(), LINE), key(1F, pos(0F, -1F, 2F), LINE), key(1.52F, pos(0F, -1F, 2F), LINE), key(2F, pos(), LINE), key(5F, pos(), LINE), key(6F, pos(), LINE)))
		.addAnimation("left_leg", createRot(key(0F, degree(), LINE), key(1F, degree(22.5F, 0F, 0F), LINE), key(1.52F, degree(22.5F, 0F, 0F), LINE), key(2F, degree(), LINE), key(5F, degree(), LINE), key(6F, degree(), LINE)))
		.addAnimation("left_leg", createPos(key(0F, pos(), LINE), key(1F, pos(0F, -1F, 3F), LINE), key(1.52F, pos(0F, -1F, 3F), LINE), key(2F, pos(), LINE), key(5F, pos(), LINE), key(6F, pos(), LINE))).build();

	public static final AnimationDefinition BELIAL_DOWN = AnimationDefinition.Builder.withLength(1.52F)
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, -90F, 0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, -90F, 0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(6F, 0F, -5F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, -90F, 0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(-6F, 0F, 5F), AnimationChannel.Interpolations.LINEAR)
				))
				.build();

	public static final AnimationDefinition WITCH_Attack = AnimationDefinition.Builder.withLength(1.8F)
		.addAnimation("armLeft", createRot(key(0F, degree(), LINE), key(0.4F, degree(-208F, 16F, -34F), LINE), key(0.8F, degree(-208F, 16F, -34F), LINE), key(1F, degree(-26.3F, -25F, -35.5F), LINE), key(1.2F, degree(-26F, -24F, -35F), LINE), key(1.4F, degree(-20F, -13F, -14F), LINE), key(1.6F, degree(-15F, -5F, -6F), LINE), key(1.8F, degree(), LINE)))
		.addAnimation("armJacketLeft", createRot(key(0F, degree(), LINE), key(0.4F, degree(-208F, 16F, -34F), LINE), key(0.8F, degree(-208F, 16F, -34F), LINE), key(1F, degree(-26.3F, -25F, -35.5F), LINE), key(1.2F, degree(-26F, -24F, -35F), LINE), key(1.4F, degree(-20F, -13F, -14F), LINE), key(1.6F, degree(-15F, -5F, -6F), LINE), key(1.8F, degree(), LINE))).build();

	public static final AnimationDefinition ALLAY_ATTACK = AnimationDefinition.Builder.withLength(0.96F)
		.addAnimation("head", createRot(key(0F, degree(), LINE), key(0.64F, degree(0F, -420F, 0F), LINE), key(0.72F, degree(0F, -390F, 0F), LINE), key(0.8F, degree(0F, -375F, 0F), LINE), key(0.88F, degree(0F, -360F, 0F), LINE)))
		.addAnimation("body", createRot(key(0F, degree(), LINE),key(0.64F, degree(0F, -400F, 0F), LINE), key(0.72F, degree(0F, -400F, 0F), LINE), key(0.88F, degree(0F, -375F, 0F), LINE), key(0.96F, degree(0F, -360F, 0F), LINE))).build();

	public static final AnimationDefinition ALLAY_WINK = AnimationDefinition.Builder.withLength(2F)
		.addAnimation("head", createRot(key(0F, degree(), LINE), key(0.36F, degree(0F, 0F, -20F), LINE), key(1.2F, degree(0F, 0F, -20F), LINE), key(1.6F, degree(), LINE))).build();

	public static Vector3f degree() {
		return degree(0F, 0F, 0F);
	}

	public static Vector3f degree(float x, float y, float z) {
		return KeyframeAnimations.degreeVec(x, y, z);
	}

	public static Vector3f pos() {
		return pos(0F, 0F, 0F);
	}

	public static Vector3f pos(float x, float y, float z) {
		return KeyframeAnimations.posVec(x, y, z);
	}

	public static Keyframe key(float timestamp, Vector3f vec, AnimationChannel.Interpolation inter) {
		return new Keyframe(timestamp, vec, inter);
	}

	public static AnimationChannel createRot(Keyframe... keys) {
		return new AnimationChannel(AnimationChannel.Targets.ROTATION, keys);
	}

	public static AnimationChannel createPos(Keyframe... keys) {
		return new AnimationChannel(AnimationChannel.Targets.POSITION, keys);
	}
}
