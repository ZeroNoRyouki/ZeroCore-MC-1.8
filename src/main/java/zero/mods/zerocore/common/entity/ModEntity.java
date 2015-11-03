package zero.mods.zerocore.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class ModEntity extends Entity {

    public ModEntity(World world) {

        super(world);
    }

    protected double smallGauss(double d) {

        return (this.worldObj.rand.nextFloat() - 0.5D) * d;
    }

    protected double gaussian(double d) {

        return d + d * ((this.rand.nextFloat() - 0.5D) / 4);
    }
}
