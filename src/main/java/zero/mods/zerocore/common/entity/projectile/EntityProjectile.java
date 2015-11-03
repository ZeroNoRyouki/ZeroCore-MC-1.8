package zero.mods.zerocore.common.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.common.entity.ModEntity;


//Shamelessly ripped off from WayofTime who ripped it from x3n0ph0b3 //Shame^2!
//https://github.com/WayofTime/BloodMagic/blob/bloodmagic1.8/src/main/java/WayofTime/alchemicalWizardry/common/entity/projectile/EnergyBlastProjectile.java

public class EntityProjectile extends ModEntity implements IProjectile, IThrowableEntity  {

    public EntityProjectile(World world) {

        super(world);
        this.setSize(0.5F, 0.5F);
        this._maxTicksInAir = 600;
    }

    public EntityProjectile(World world, double x, double y, double z) {

        super(world);
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this._maxTicksInAir = 600;
    }

    public EntityProjectile(World world, EntityLivingBase shooter, int damage) {

        super(world);
        this._shootingEntity = shooter;
        float par3 = 0.8F;
        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.2D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, par3 * 1.5F, 1.0F);
        this._projectileDamage = damage;
        this._maxTicksInAir = 600;
    }

    public EntityProjectile(World world, EntityLivingBase shooter, int damage, int maxTicksInAir,
                            double x, double y, double z, float rotationYaw, float rotationPitch)  {

        super(world);
        this._shootingEntity = shooter;
        float par3 = 0.8F;
        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(x, y, z, rotationYaw, rotationPitch);
        x -= MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        y -= 0.2D;
        z -= MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(x, y, z);
        this.motionX = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        this.motionZ = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        this.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, par3 * 1.5F, 1.0F);
        this._projectileDamage = damage;
        this._maxTicksInAir = maxTicksInAir;
    }

    public EntityProjectile(World world, EntityLivingBase shooter, EntityLivingBase target,
                            float par4, float par5, int damage, int maxTicksInAir)  {

        super(world);
        this.renderDistanceWeight = 10.0D;
        this._shootingEntity = shooter;
        this.posY = shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D;
        double d0 = target.posX - shooter.posX;
        double d1 = target.getBoundingBox().minY + (double) (target.height / 1.5F) - this.posY;
        double d2 = target.posZ - shooter.posZ;
        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D) {

            float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f2, f3);
            this.setThrowableHeading(d0, d1, d2, par4, par5);
        }

        this._projectileDamage = damage;
        this._maxTicksInAir = maxTicksInAir;
    }

    @Override
    protected void entityInit() {

        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
     * direction.
     */
    @Override
    public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {

        float var9 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);

        var1 /= var9;
        var3 /= var9;
        var5 /= var9;
        var1 += this.rand.nextGaussian() * 0.007499999832361937D * var8;
        var3 += this.rand.nextGaussian() * 0.007499999832361937D * var8;
        var5 += this.rand.nextGaussian() * 0.007499999832361937D * var8;
        var1 *= var7;
        var3 *= var7;
        var5 *= var7;
        this.motionX = var1;
        this.motionY = var3;
        this.motionZ = var5;

        float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);

        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(var1, var5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(var3, var10) * 180.0D / Math.PI);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double motionX, double motionY, double motionZ) {

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {

            float var7 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(motionY, var7) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {

        super.onUpdate();

        if (this._ticksInAir > this._maxTicksInAir)
            this.setDead();

        if (this._shootingEntity == null) {

            List players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(posX - 1, posY - 1, posZ - 1, posX + 1, posY + 1, posZ + 1));
            Iterator i = players.iterator();
            double closestDistance = Double.MAX_VALUE;
            EntityPlayer closestPlayer = null;

            while (i.hasNext()) {

                EntityPlayer e = (EntityPlayer)i.next();
                double distance = e.getDistanceToEntity(this);

                if (distance < closestDistance)
                    closestPlayer = e;
            }

            if (closestPlayer != null)
                this._shootingEntity = closestPlayer;
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {

            float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, var1) * 180.0D / Math.PI);
        }

        IBlockState state = this.worldObj.getBlockState(new BlockPos(this._xTile, this._yTile, this._zTile));
        Block var16 = state.getBlock();

        if (var16 != null) {

            var16.setBlockBoundsBasedOnState(this.worldObj, new BlockPos(this._xTile, this._yTile, this._zTile));

            AxisAlignedBB var2 = var16.getCollisionBoundingBox(worldObj, new BlockPos(this._xTile, this._yTile, this._zTile), state);

            if (var2 != null && var2.isVecInside(new Vec3(this.posX, this.posY, this.posZ)))
                this._inGround = true;
        }

        if (!this._inGround) {

            ++this._ticksInAir;

            if (this._ticksInAir > 1 && this._ticksInAir < 3) {

                for (int particles = 0; particles < 3; particles++) {
                    this.doFiringParticles();
                }
            }

            Vec3 var17 = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 var3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition var4 = this.worldObj.rayTraceBlocks(var17, var3, true, false, false);
            var17 = new Vec3(this.posX, this.posY, this.posZ);
            var3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (var4 != null)
                var3 = new Vec3(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);

            Entity var5 = null;
            List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;
            Iterator var9 = var6.iterator();
            float var11;
            boolean scheduledForDeath = false;

            while (var9.hasNext()) {

                Entity var10 = (Entity) var9.next();

                if (var10.canBeCollidedWith() && (var10 != this._shootingEntity || this._ticksInAir >= 5)) {

                    var11 = 0.3F;

                    AxisAlignedBB var12 = var10.getBoundingBox().expand(var11, var11, var11);
                    MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);

                    if (var13 != null) {

                        double var14 = var17.distanceTo(var13.hitVec);

                        if (var14 < var7 || var7 == 0.0D) {

                            var5 = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if (var5 != null)
                var4 = new MovingObjectPosition(var5);

            if (var4 != null) {

                this.onImpact(var4);

                if (scheduledForDeath)
                    this.setDead();
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    public void doFiringParticles() {

        this.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, this.posX + smallGauss(0.1D), this.posY + smallGauss(0.1D), this.posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        this.worldObj.spawnParticle(EnumParticleTypes.FLAME, this.posX, this.posY, this.posZ, gaussian(this.motionX), gaussian(this.motionY), gaussian(this.motionZ));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {

        tagCompound.setShort("_xTile", (short) this._xTile);
        tagCompound.setShort("_yTile", (short) this._yTile);
        tagCompound.setShort("_zTile", (short) this._zTile);
        tagCompound.setByte("_inTile", (byte) this._inTile);
        tagCompound.setByte("_inData", (byte) this._inData);
        tagCompound.setByte("_inGround", (byte) (this._inGround ? 1 : 0));
        tagCompound.setInteger("_ticksInAir", this._ticksInAir);
        tagCompound.setInteger("_maxTicksInAir", this._maxTicksInAir);
        tagCompound.setInteger("_projectileDamage", this._projectileDamage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {

        this._xTile = tagCompund.getShort("_xTile");
        this._yTile = tagCompund.getShort("_yTile");
        this._zTile = tagCompund.getShort("_zTile");
        this._inTile = tagCompund.getByte("_inTile") & 255;
        this._inData = tagCompund.getByte("_inData") & 255;
        this._inGround = tagCompund.getByte("_inGround") == 1;
        this._ticksInAir = tagCompund.getInteger("_ticksInAir");
        this._maxTicksInAir = tagCompund.getInteger("_maxTicksInAir");
        this._projectileDamage = tagCompund.getInteger("_projectileDamage");
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {

        return false;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem() {

        return false;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public void setIsCritical(boolean par1) {

        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (par1)
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 | 1)));
        else
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 & -2)));
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public boolean getIsCritical() {

        byte var1 = this.dataWatcher.getWatchableObjectByte(16);

        return (var1 & 1) != 0;
    }

    public void onImpact(MovingObjectPosition mop) {

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {

            if (mop.entityHit == this._shootingEntity)
                return;

            this.onImpact(mop.entityHit);

        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

            this.worldObj.createExplosion(this._shootingEntity, this.posX, this.posY, this.posZ, (float)0.1, true);
            this.setDead();
        }
    }

    public void onImpact(Entity mop) {

        if (mop == this._shootingEntity && this._ticksInAir > 3) {

            this._shootingEntity.attackEntityFrom(DamageSource.causeMobDamage(this._shootingEntity), 1);
            this.setDead();

        } else {

            if (mop instanceof EntityLivingBase)
                ((EntityLivingBase)mop).addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 2));

            this.doDamage(this._projectileDamage, mop);
            this.worldObj.createExplosion(this._shootingEntity, this.posX, this.posY, this.posZ, (float)0.1, true);
        }

        this.spawnHitParticles(EnumParticleTypes.CRIT_MAGIC, 8);
        this.setDead();
    }

    public EntityLivingBase getShootingEntity() {

        return this._shootingEntity;
    }

    protected void spawnHitParticles(EnumParticleTypes type, int i) {

        for (int particles = 0; particles < i; particles++)
            this.worldObj.spawnParticle(type, this.posX, this.posY - (type == EnumParticleTypes.PORTAL ? 1 : 0),
                    this.posZ, this.gaussian(this.motionX), this.gaussian(this.motionY), this.gaussian(this.motionZ));
    }

    protected void doDamage(int i, Entity mop) {

        mop.attackEntityFrom(this.getDamageSource(), i);
    }

    public DamageSource getDamageSource() {

        return DamageSource.causeMobDamage(this._shootingEntity);
    }


    private int getRicochetMax() {

        return 0;
    }

    @Override
    public Entity getThrower() {

        return this._shootingEntity;
    }

    @Override
    public void setThrower(Entity entity) {

        if (entity instanceof EntityLivingBase)
            this._shootingEntity = (EntityLivingBase) entity;
    }

    /**
     * The owner of this arrow.
     */
    protected EntityLivingBase _shootingEntity;

    protected int _xTile = -1;
    protected int _yTile = -1;
    protected int _zTile = -1;
    protected int _inTile = 0;
    protected int _inData = 0;
    protected boolean _inGround = false;
    protected int _ticksInAir = 0;
    protected int _maxTicksInAir = 600;
    protected int _projectileDamage;

}
