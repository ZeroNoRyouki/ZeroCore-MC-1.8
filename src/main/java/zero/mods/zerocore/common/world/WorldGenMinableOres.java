package zero.mods.zerocore.common.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.FMLLog;


import java.util.List;
import java.util.Random;


public class WorldGenMinableOres extends WorldGenerator {

    public void addOre(Block ore, int minY, int maxY, int minBlockCount, int maxBlockCount, int chance, Block blockToReplace) {

        if (null == this._generators)
            this._generators = Lists.newArrayList();

        this._generators.add(new VeinGenerator(ore, minY, maxY, minBlockCount, maxBlockCount, chance, blockToReplace));
    }

    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {

        return this.generate(world, random, blockPos.getX(), blockPos.getZ());
    }

    public boolean generate(World world, Random random, int chunkX, int chunkZ) {

        if ((null == this._generators) || (this._generators.isEmpty()))
            return false;

        boolean result = false;
        int x = chunkX * 16;
        int z = chunkZ * 16;

        for (int i = 0; i < this._generators.size(); ++i)
            result |= this._generators.get(i).generate(world, random, x, z);

        return result;
    }


    private List<VeinGenerator> _generators;


    private class VeinGenerator {

        public VeinGenerator(Block ore, int minY, int maxY, int minBlockCount, int maxBlockCount, int chance, Block blockToReplace) {

            if ((null == ore) || (null == blockToReplace))
                throw new IllegalArgumentException();

            if ((minY < 0) || (maxY < minY) || (maxY > 256))
                throw new IllegalArgumentException("Invalid Y coordinates");

            if ((minBlockCount < 0) || (maxBlockCount < minBlockCount))
                throw new IllegalArgumentException("Invalid vein size");

            if (chance <= 0)
                throw new IllegalArgumentException("Invalid number of iterations");

            this._oreBlock = ore.getDefaultState();
            this._replaceCheck = BlockHelper.forBlock(blockToReplace);
            this._minBlocksCount = minBlockCount;
            this._maxBlocksCount = maxBlockCount;
            this._minY = minY;
            this._maxY = maxY;
            this._chance = chance;
        }

        public boolean generate(World world, Random random, int x, int z) {

            //FMLLog.info("Generating vein of %s at %d,%d,%d", this._oreBlock.toString(), x, -1, z);

            for (int i = 0; i < this._chance; ++i) {

                if (this.generateVein(world, random, x, z))
                    ;//return true;
            }

            return false;
        }

        protected boolean generateVein(World world, Random random, int x, int z) {

            final int blocksCount = this._minBlocksCount + random.nextInt(this._maxBlocksCount - this._minBlocksCount);
            final int y = this._minY + random.nextInt(this._maxY - this._minY);

            float f = random.nextFloat() * (float)Math.PI;  // 0.0<>1.0 * PI
            double d0 = (double)((float)(x + 8) + MathHelper.sin(f) * (float)blocksCount / 8.0F);
            double d1 = (double)((float)(x + 8) - MathHelper.sin(f) * (float)blocksCount / 8.0F);
            double d2 = (double)((float)(z + 8) + MathHelper.cos(f) * (float)blocksCount / 8.0F);
            double d3 = (double)((float)(z + 8) - MathHelper.cos(f) * (float)blocksCount / 8.0F);
            double d4 = (double)(y + random.nextInt(3) - 2);
            double d5 = (double)(y + random.nextInt(3) - 2);

            for (int i = 0; i < blocksCount; ++i)  {

                float f1 = (float)i / (float)blocksCount;
                double d6 = d0 + (d1 - d0) * (double)f1;
                double d7 = d4 + (d5 - d4) * (double)f1;
                double d8 = d2 + (d3 - d2) * (double)f1;
                double d9 = random.nextDouble() * (double)blocksCount / 16.0D;
                double d10 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
                double d11 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
                int j = MathHelper.floor_double(d6 - d10 / 2.0D);
                int k = MathHelper.floor_double(d7 - d11 / 2.0D);
                int l = MathHelper.floor_double(d8 - d10 / 2.0D);
                int i1 = MathHelper.floor_double(d6 + d10 / 2.0D);
                int j1 = MathHelper.floor_double(d7 + d11 / 2.0D);
                int k1 = MathHelper.floor_double(d8 + d10 / 2.0D);

                for (int l1 = j; l1 <= i1; ++l1)
                {
                    double d12 = ((double)l1 + 0.5D - d6) / (d10 / 2.0D);

                    if (d12 * d12 < 1.0D)
                    {
                        for (int i2 = k; i2 <= j1; ++i2)
                        {
                            double d13 = ((double)i2 + 0.5D - d7) / (d11 / 2.0D);

                            if (d12 * d12 + d13 * d13 < 1.0D)
                            {
                                for (int j2 = l; j2 <= k1; ++j2)
                                {
                                    double d14 = ((double)j2 + 0.5D - d8) / (d10 / 2.0D);

                                    if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D)
                                    {
                                        BlockPos blockpos1 = new BlockPos(l1, i2, j2);

                                        if (world.getBlockState(blockpos1).getBlock().isReplaceableOreGen(world, blockpos1, this._replaceCheck))
                                        {
                                            world.setBlockState(blockpos1, this._oreBlock, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }

        private final IBlockState _oreBlock;
        private final Predicate _replaceCheck;
        private final int _minBlocksCount;
        private final int _maxBlocksCount;
        private final int _minY;
        private final int _maxY;
        private final int _chance;

    }
}
