package zero.mods.zerocore.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zero.mods.zerocore.common.blocks.ModTileEntity;


public class ModGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {

        TileEntity te;

        if ((-1 == guiId) && (null != (te = world.getTileEntity(new BlockPos(x, y, z)))) && (te instanceof ModTileEntity))
            return ((ModTileEntity)te).getServerGuiElement(player.inventory);

        return null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {

        TileEntity te;

        if ((-1 == guiId) && (null != (te = world.getTileEntity(new BlockPos(x, y, z)))) && (te instanceof ModTileEntity))
            return ((ModTileEntity)te).getClientGuiElement(player.inventory);

        return null;
    }
}
