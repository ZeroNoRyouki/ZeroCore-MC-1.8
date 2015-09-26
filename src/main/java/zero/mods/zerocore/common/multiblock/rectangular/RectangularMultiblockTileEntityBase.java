package zero.mods.zerocore.common.multiblock.rectangular;

/*
 * A multiblock library for making irregularly-shaped multiblock machines
 *
 * Original author: Erogenous Beef
 * https://github.com/erogenousbeef/BeefCore
 *
 * Ported to Minecraft 1.8 by ZeroNoRyouki
 * https://github.com/ZeroNoRyouki/ZeroCore
 */

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import zero.mods.zerocore.common.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.common.multiblock.MultiblockTileEntityBase;
import zero.mods.zerocore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase {

	private PartPosition position;
	private EnumFacing outwards;
	
	public RectangularMultiblockTileEntityBase() {

		super();
		
		this.position = PartPosition.Unknown;
		this.outwards = null; // ForgeDirection.UNKNOWN;
	}

	// Positional Data
	@Deprecated
	public EnumFacing getOutwardsDir() {
		return this.outwards;
	}
	
	public PartPosition getPartPosition() {
		return this.position;
	}

	// Handlers from MultiblockTileEntityBase 
	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);
		this.recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
	}
	
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {

		BlockPos maxCoord = controller.getMaximumCoord();
		BlockPos minCoord = controller.getMinimumCoord();
		
		// Discover where I am on the reactor
		this.recalculateOutwardsDirection(minCoord, maxCoord);
	}

	@Override
	public void onMachineBroken() {

		this.position = PartPosition.Unknown;
		this.outwards = null;
	}
	
	// Positional helpers
	public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord) {

		BlockPos myPosition = this.getPos();
		int myX, myY, myZ;
		int maxX, maxY, maxZ;
		int minX, minY, minZ;
		int facesMatching = 0;

		this.outwards = null;
		this.position = PartPosition.Unknown;

		myX = myPosition.getX();
		myY = myPosition.getY();
		myZ = myPosition.getZ();

		maxX = maxCoord.getX();
		maxY = maxCoord.getY();
		maxZ = maxCoord.getZ();

		minX = minCoord.getX();
		minY = minCoord.getY();
		minZ = minCoord.getZ();

		if (maxX == myX || minX == myX)
			facesMatching++;

		if (maxY == myY || minY == myY)
			facesMatching++;

		if (maxZ == myZ || minZ == myZ)
			facesMatching++;
		
		if (facesMatching <= 0)
			this.position = PartPosition.Interior;
		else if (facesMatching >= 3)
			this.position = PartPosition.FrameCorner;
		else if (facesMatching == 2)
			this.position = PartPosition.Frame;
		else {

			// 1 face matches
			if (maxX == myX) {

				position = PartPosition.EastFace;
				outwards = EnumFacing.EAST;

			} else if(minX == myX) {

				position = PartPosition.WestFace;
				outwards = EnumFacing.WEST;

			} else if(maxZ == myZ) {

				position = PartPosition.SouthFace;
				outwards = EnumFacing.SOUTH;

			} else if(minZ == myZ) {

				position = PartPosition.NorthFace;
				outwards = EnumFacing.NORTH;

			} else if(maxY == myY) {

				position = PartPosition.TopFace;
				outwards = EnumFacing.UP;

			} else {

				position = PartPosition.BottomFace;
				outwards = EnumFacing.DOWN;
			}
		}
	}
	
	///// Validation Helpers (IMultiblockPart)
	public abstract void isGoodForFrame() throws MultiblockValidationException;

	public abstract void isGoodForSides() throws MultiblockValidationException;

	public abstract void isGoodForTop() throws MultiblockValidationException;

	public abstract void isGoodForBottom() throws MultiblockValidationException;

	public abstract void isGoodForInterior() throws MultiblockValidationException;
}
