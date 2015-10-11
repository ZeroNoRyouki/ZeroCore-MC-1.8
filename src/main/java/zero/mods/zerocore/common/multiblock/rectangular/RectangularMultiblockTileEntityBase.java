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
import zero.mods.zerocore.common.lib.BlockFacings;
import zero.mods.zerocore.common.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.common.multiblock.MultiblockTileEntityBase;
import zero.mods.zerocore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase {

	private PartPosition position;
	private BlockFacings outwardFacings;
	
	public RectangularMultiblockTileEntityBase() {
		
		this.position = PartPosition.Unknown;
		this.outwardFacings = BlockFacings.NONE;
	}

	// Positional Data

	/**
	 * Get the outward facing of the part in the formed multiblock
	 *
	 * @return the outward facing of the part. A face is "set" in the BlockFacings object if that face is facing outward
	 */
	public BlockFacings getOutwardsDir() {

		return this.outwardFacings;
	}

	/**
	 * Get the position of the part in the formed multiblock
	 *
	 * @return the position of the part
	 */
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

		// Discover where I am on the reactor
		this.recalculateOutwardsDirection(controller.getMinimumCoord(), controller.getMaximumCoord());
	}

	@Override
	public void onMachineBroken() {

		this.position = PartPosition.Unknown;
		this.outwardFacings = BlockFacings.NONE;
	}
	
	// Positional helpers

	public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord) {

		BlockPos myPosition = this.getPos();
		int myX = myPosition.getX();
		int myY = myPosition.getY();
		int myZ = myPosition.getZ();
		int facesMatching = 0;


		// witch direction are we facing?

		boolean downFacing = myY == minCoord.getY();
		boolean upFacing = myY == maxCoord.getY();
		boolean northFacing = myZ == minCoord.getZ();
		boolean southFacing = myZ == maxCoord.getZ();
		boolean westFacing = myX == minCoord.getX();
		boolean eastFacing = myX == maxCoord.getX();

		this.outwardFacings = BlockFacings.from(downFacing, upFacing, northFacing, southFacing, westFacing, eastFacing);


		// how many faces are facing outward?

		if (eastFacing || westFacing)
			++facesMatching;

		if (upFacing || downFacing)
			++facesMatching;

		if (southFacing || northFacing)
			++facesMatching;


		// what is our position in the multiblock structure?

		if (facesMatching <= 0)
			this.position = PartPosition.Interior;

		else if (facesMatching >= 3)
			this.position = PartPosition.FrameCorner;

		else if (facesMatching == 2)
			this.position = PartPosition.Frame;

		else {

			// only 1 face matches

			if (eastFacing) {

				this.position = PartPosition.EastFace;

			} else if (westFacing) {

				this.position = PartPosition.WestFace;

			} else if (southFacing) {

				this.position = PartPosition.SouthFace;

			} else if (northFacing) {

				this.position = PartPosition.NorthFace;

			} else if (upFacing) {

				this.position = PartPosition.TopFace;

			} else {

				this.position = PartPosition.BottomFace;
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
