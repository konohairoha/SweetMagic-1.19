package sweetmagic.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class MathHelper {

	public static Vec3 changeBasisN(Vec3 newBasisYVector, Vec3 rot) {
        Vec3 y = newBasisYVector.normalize();
        Vec3 x = new Vec3(y.y, y.z, y.x).normalize();
        Vec3 z = y.cross(x).normalize();
        return changeBasis(x, y, z, rot);
    }

    public static Vec3 changeBasis(Vec3 newX, Vec3 newY, Vec3 newZ, Vec3 rot) {
        return newX.scale(rot.x).add(newY.scale(rot.y)).add(newZ.scale(rot.z));
    }

    public static Vec3 V3itoV3(Vec3i v) {
        return new Vec3(v.getX(), v.getY(), v.getZ());
	}
}
