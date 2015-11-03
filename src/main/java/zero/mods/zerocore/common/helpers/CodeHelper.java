package zero.mods.zerocore.common.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.Random;

public final class CodeHelper {

    /**
     * Get the side we are called on
     */
    public static Side getPhysicalSide() {

        return FMLCommonHandler.instance().getSide();
    }

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByLogicalServer(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return !((World)world).isRemote;
    }

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     */
    public static boolean calledByPhysicalServer() {

        return Side.SERVER == CodeHelper.getPhysicalSide();
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByLogicalClient(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return ((World)world).isRemote;
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     */
    public static boolean calledByPhysicalClient() {

        return Side.CLIENT == CodeHelper.getPhysicalSide();
    }

    /**
     * Retrieve the world random numbers generator from an IBlockAccess
     * In case of failure, a new Random object is instantiated
     *
     * @param world the IBlockAccess for the world
     */
    public static Random getRNGFromWorld(IBlockAccess world) {

        return world instanceof World ? ((World)world).rand : new Random();
    }


    public static boolean isEntityInRange(Entity entity, double x, double y, double z, double range) {

        return entity.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < (range * range);
    }

    public static boolean isEntityInRange(Entity entity, BlockPos position, double range) {

        return entity.getDistanceSq(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5) < (range * range);
    }




    public static <T, E> void setObjectPrivateFinalField(Class<? super T> objectClass, T object, E newValue, String... fieldNames) {

        Field field = ReflectionHelper.findField(objectClass, ObfuscationReflectionHelper.remapFieldNames(objectClass.getName(), fieldNames));

        try {

            Field modifiers = Field.class.getDeclaredField("modifiers");

            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(object, newValue);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }


    public static void spawnVanillaParticles(World world, EnumParticleTypes particle, int minCount, int maxCount, int x, int y, int z, int offsetX, int offsetY, int offsetZ) {

        if (null == world)
            return;

        Random rand = world.rand;
        int howMany = MathHelper.getRandomIntegerInRange(rand, minCount, maxCount);
        double motionX, motionY, motionZ, pX, pY, pZ, px1, px2, py1, py2, pz1, pz2;

        px1 = x - offsetX + 0.5D;
        px2 = x + offsetX + 0.5D;
        py1 = y;
        py2 = y + offsetY;
        pz1 = z - offsetZ + 0.5D;
        pz2 = z + offsetZ + 0.5D;

        if (world instanceof WorldServer) {

            WorldServer ws = (WorldServer)world;

            motionX = rand.nextGaussian() * 0.02D;
            motionY = rand.nextGaussian() * 0.02D;
            motionZ = rand.nextGaussian() * 0.02D;

            pX = MathHelper.getRandomDoubleInRange(rand, px1, px2);
            pY = MathHelper.getRandomDoubleInRange(rand, py1, py2);
            pZ = MathHelper.getRandomDoubleInRange(rand, pz1, pz2);

            ws.spawnParticle(particle, pX, pY, pZ, howMany, motionX, motionY, motionZ, rand.nextGaussian() * 0.02D, CodeHelper.EMPTY_INT_ARRAY);

        } else
            for (int i = 0; i < howMany; ++i) {

                motionX = rand.nextGaussian() * 0.02D;
                motionY = rand.nextGaussian() * 0.02D;
                motionZ = rand.nextGaussian() * 0.02D;

                pX = MathHelper.getRandomDoubleInRange(rand, px1, px2);
                pY = MathHelper.getRandomDoubleInRange(rand, py1, py2);
                pZ = MathHelper.getRandomDoubleInRange(rand, pz1, pz2);

                world.spawnParticle(particle, pX, pY, pZ, motionX, motionY, motionZ, CodeHelper.EMPTY_INT_ARRAY);
            }
    }




    public static int getChunkXFromBlock(int blockX) {

        return blockX >> 4;
    }

    public static int getChunkXFromBlock(BlockPos position) {

        return position.getX() >> 4;
    }

    public static int getChunkZFromBlock(int blockZ) {

        return blockZ >> 4;
    }

    public static int getChunkZFromBlock(BlockPos position) {

        return position.getZ() >> 4;
    }

    public static long getChunkXZHashFromBlock(int blockX, int blockZ) {

        return ChunkCoordIntPair.chunkXZ2Int(CodeHelper.getChunkXFromBlock(blockX), CodeHelper.getChunkZFromBlock(blockZ));
    }

    public static long getChunkXZHashFromBlock(BlockPos position) {

        return ChunkCoordIntPair.chunkXZ2Int(CodeHelper.getChunkXFromBlock(position), CodeHelper.getChunkZFromBlock(position));
    }

    public static boolean blockChunkExists(IChunkProvider chunkProvider, BlockPos position) {

        return chunkProvider.chunkExists(CodeHelper.getChunkXFromBlock(position), CodeHelper.getChunkZFromBlock(position));
    }


    public static double smallGauss(Random rand, double d) {

        return (rand.nextFloat() - 0.5D) * d;
    }

    public static double gaussian(Random rand, double d) {

        return d + d * ((rand.nextFloat() - 0.5D) / 4);
    }


    public static final int[] EMPTY_INT_ARRAY = new int[0];

    private CodeHelper() {
    }
}
