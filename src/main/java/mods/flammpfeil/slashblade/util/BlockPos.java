package mods.flammpfeil.slashblade.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import javax.vecmath.Point3i;

/**
 * Created by Furia on 2016/06/09.
 */
public class BlockPos extends Point3i {
    public BlockPos(int i, int i1, int i2) {
        super(i, i1, i2);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public double distanceSq(BlockPos pos) {
        double d0 = pos.x - this.x;
        double d1 = pos.y - this.y;
        double d2 = pos.z - this.z;
        return (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }
}
