package zero.mods.zerocore.common.multiblock;

/*
 * A multiblock library for making irregularly-shaped multiblock machines
 *
 * Original author: Erogenous Beef
 * https://github.com/erogenousbeef/BeefCore
 *
 * Ported to Minecraft 1.8 by ZeroNoRyouki
 * https://github.com/ZeroNoRyouki/ZeroCore
 */

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.FMLLog;
import zero.mods.zerocore.common.helpers.CodeHelper;
import zero.mods.zerocore.common.helpers.ModObjects;


/**
 * This class contains the base logic for "multiblock controllers". Conceptually, they are
 * meta-TileEntities. They govern the logic for an associated group of TileEntities.
 * 
 * Subordinate TileEntities implement the IMultiblockPart class and, generally, should not have an update() loop.
 */
public abstract class MultiblockControllerBase {

	public static final short DIMENSION_UNBOUNDED = -1;

	// Multiblock stuff - do not mess with
	protected World worldObj;
	
	// Disassembled -> Assembled; Assembled -> Disassembled OR Paused; Paused -> Assembled
	protected enum AssemblyState { Disassembled, Assembled, Paused };
	protected AssemblyState assemblyState;

	protected HashSet<IMultiblockPart> connectedParts;
	
	/** This is a deterministically-picked coordinate that identifies this
	 * multiblock uniquely in its dimension.
	 * Currently, this is the coord with the lowest X, Y and Z coordinates, in that order of evaluation.
	 * i.e. If something has a lower X but higher Y/Z coordinates, it will still be the reference.
	 * If something has the same X but a lower Y coordinate, it will be the reference. Etc.
	 */
	private BlockPos referenceCoord;

	/**
	 * Minimum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private BlockPos minimumCoord;

	/**
	 * Maximum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private BlockPos maximumCoord;
	
	/**
	 * Set to true whenever a part is removed from this controller.
	 */
	private boolean shouldCheckForDisconnections;
	
	/**
	 * Set whenever we validate the multiblock
	 */
	private MultiblockValidationException lastValidationException;
	
	protected boolean debugMode;
	
	protected MultiblockControllerBase(World world) {

		// Multiblock stuff
		this.worldObj = world;
		this.connectedParts  = new HashSet<IMultiblockPart>();

		this.referenceCoord = null;
		this.assemblyState = AssemblyState.Disassembled;

		this.minimumCoord = this.maximumCoord = null;

		this.shouldCheckForDisconnections = true;
		this.lastValidationException = null;

		this.debugMode = false;
	}

	public void setDebugMode(boolean active) {
		this.debugMode = active;
	}
	
	public boolean isDebugMode() { return this.debugMode; }
	
	/**
	 * Call when a block with cached save-delegate data is added to the multiblock.
	 * The part will be notified that the data has been used after this call completes.
	 * @param part The NBT tag containing this controller's data.
	 */
	public abstract void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data);
	
	/**
	 * Check if a block is being tracked by this machine.
	 * @param blockPos Coordinate to check.
	 * @return True if the tile entity at blockCoord is being tracked by this machine, false otherwise.
	 */
	public boolean hasBlock(BlockPos blockPos) {

		return this.connectedParts.contains(blockPos);
	}
	
	/**
	 * Attach a new part to this machine.
	 * @param part The part to add.
	 */
	public void attachBlock(IMultiblockPart part) {

		IMultiblockPart candidate;
		BlockPos coord = part.getPos();

		if (!this.connectedParts.add(part))
			FMLLog.warning("[%s] Controller %s is double-adding part %d @ %s. This is unusual. If you encounter odd behavior, please tear down the machine and rebuild it.",
					(worldObj.isRemote ? "CLIENT" : "SERVER"), hashCode(), part.hashCode(), coord);

		part.onAttached(this);
		this.onBlockAdded(part);

		if (part.hasMultiblockSaveData()) {

			NBTTagCompound savedData = part.getMultiblockSaveData();

			this.onAttachedPartWithMultiblockData(part, savedData);
			part.onMultiblockDataAssimilated();
		}
		
		if (this.referenceCoord == null) {

			this.referenceCoord = coord;
			part.becomeMultiblockSaveDelegate();

		} else if (coord.compareTo(this.referenceCoord) < 0) {

			TileEntity te = this.worldObj.getTileEntity(this.referenceCoord);

			((IMultiblockPart)te).forfeitMultiblockSaveDelegate();

			this.referenceCoord = coord;
			part.becomeMultiblockSaveDelegate();
		}
		else {
			part.forfeitMultiblockSaveDelegate();
		}



		BlockPos partPos = part.getPos();
		int curX, curY, curZ;
		int newX, newY, newZ;
		int partCoord;

		if (this.minimumCoord != null) {

			curX = this.minimumCoord.getX();
			curY = this.minimumCoord.getY();
			curZ = this.minimumCoord.getZ();

			partCoord = partPos.getX();
			newX = partCoord < curX ? partCoord : curX;

			partCoord = partPos.getY();
			newY = partCoord < curY ? partCoord : curY;

			partCoord = partPos.getZ();
			newZ = partCoord < curZ ? partCoord : curZ;

			if ((newX != curX) || (newY != curY) || (newZ != curZ))
				this.minimumCoord = new BlockPos(newX, newY, newZ);
		}

		if (this.maximumCoord != null) {

			curX = this.maximumCoord.getX();
			curY = this.maximumCoord.getY();
			curZ = this.maximumCoord.getZ();

			partCoord = partPos.getX();
			newX = partCoord > curX ? partCoord : curX;

			partCoord = partPos.getY();
			newY = partCoord > curY ? partCoord : curY;

			partCoord = partPos.getZ();
			newZ = partCoord > curZ ? partCoord : curZ;

			if ((newX != curX) || (newY != curY) || (newZ != curZ))
				this.maximumCoord = new BlockPos(newX, newY, newZ);
		}

		/*
		if (this.minimumCoord != null) {
			if(part.xCoord < minimumCoord.x) { minimumCoord.x = part.xCoord; }
			if(part.yCoord < minimumCoord.y) { minimumCoord.y = part.yCoord; }
			if(part.zCoord < minimumCoord.z) { minimumCoord.z = part.zCoord; }
		}
		
		if (this.maximumCoord != null) {
			if(part.xCoord > maximumCoord.x) { maximumCoord.x = part.xCoord; }
			if(part.yCoord > maximumCoord.y) { maximumCoord.y = part.yCoord; }
			if(part.zCoord > maximumCoord.z) { maximumCoord.z = part.zCoord; }
		}
		*/
		
		MultiblockRegistry.addDirtyController(this.worldObj, this);
	}

	/**
	 * Called when a new part is added to the machine. Good time to register things into lists.
	 * @param newPart The part being added.
	 */
	protected abstract void onBlockAdded(IMultiblockPart newPart);

	/**
	 * Called when a part is removed from the machine. Good time to clean up lists.
	 * @param oldPart The part being removed.
	 */
	protected abstract void onBlockRemoved(IMultiblockPart oldPart);
	
	/**
	 * Called when a machine is assembled from a disassembled state.
	 */
	protected abstract void onMachineAssembled();
	
	/**
	 * Called when a machine is restored to the assembled state from a paused state.
	 */
	protected abstract void onMachineRestored();

	/**
	 * Called when a machine is paused from an assembled state
	 * This generally only happens due to chunk-loads and other "system" events.
	 */
	protected abstract void onMachinePaused();
	
	/**
	 * Called when a machine is disassembled from an assembled state.
	 * This happens due to user or in-game actions (e.g. explosions)
	 */
	protected abstract void onMachineDisassembled();
	
	/**
	 * Callback whenever a part is removed (or will very shortly be removed) from a controller.
	 * Do housekeeping/callbacks, also nulls min/max coords.
	 * @param part The part being removed.
	 */
	private void onDetachBlock(IMultiblockPart part) {

		// Strip out this part
		part.onDetached(this);
		this.onBlockRemoved(part);
		part.forfeitMultiblockSaveDelegate();

		this.minimumCoord = this.maximumCoord = null;
		
		if (this.referenceCoord != null && this.referenceCoord.equals(part.getPos()))
			this.referenceCoord = null;

		this.shouldCheckForDisconnections = true;
	}
	
	/**
	 * Call to detach a block from this machine. Generally, this should be called
	 * when the tile entity is being released, e.g. on block destruction.
	 * @param part The part to detach from this machine.
	 * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
	 */
	public void detachBlock(IMultiblockPart part, boolean chunkUnloading) {

		if (chunkUnloading && this.assemblyState == AssemblyState.Assembled) {

			this.assemblyState = AssemblyState.Paused;
			this.onMachinePaused();
		}

		// Strip out this part
		this.onDetachBlock(part);
		if (!this.connectedParts.remove(part)) {

			BlockPos position = part.getPos();

			FMLLog.warning("[%s] Double-removing part (%d) @ %d, %d, %d, this is unexpected and may cause problems. If you encounter anomalies, please tear down the reactor and rebuild it.",
					this.worldObj.isRemote ? "CLIENT" : "SERVER", part.hashCode(), position.getX(), position.getY(), position.getZ());
		}

		if (this.connectedParts.isEmpty()) {

			// Destroy/unregister
			MultiblockRegistry.addDeadController(this.worldObj, this);
			return;
		}

		MultiblockRegistry.addDirtyController(this.worldObj,  this);

		// Find new save delegate if we need to.
		if (referenceCoord == null)
			this.selectNewReferenceCoord();
	}

	/**
	 * Helper method so we don't check for a whole machine until we have enough blocks
	 * to actually assemble it. This isn't as simple as xmax*ymax*zmax for non-cubic machines
	 * or for machines with hollow/complex interiors.
	 * @return The minimum number of blocks connected to the machine for it to be assembled.
	 */
	protected abstract int getMinimumNumberOfBlocksForAssembledMachine();

	/**
	 * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum X dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumXSize();

	/**
	 * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Z dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumZSize();

	/**
	 * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Y dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumYSize();
	
	/**
	 * Returns the minimum X dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum X dimension size of the machine
	 */
	protected int getMinimumXSize() { return 1; }

	/**
	 * Returns the minimum Y dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Y dimension size of the machine
	 */
	protected int getMinimumYSize() { return 1; }

	/**
	 * Returns the minimum Z dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Z dimension size of the machine
	 */
	protected int getMinimumZSize() { return 1; }
	
	
	/**
	 * @return An exception representing the last error encountered when trying to assemble this
	 * multiblock, or null if there is no error.
	 */
	public MultiblockValidationException getLastValidationException() { return lastValidationException; }
	
	/**
	 * Checks if a machine is whole. If not, throws an exception with the reason why.
	 */
	protected abstract void isMachineWhole() throws MultiblockValidationException;
	
	/**
	 * Check if the machine is whole or not.
	 * If the machine was not whole, but now is, assemble the machine.
	 * If the machine was whole, but no longer is, disassemble the machine.
	 * @return 
	 */
	public void checkIfMachineIsWhole() {

		AssemblyState oldState = this.assemblyState;
		boolean isWhole;

		this.lastValidationException = null;

		try {
			this.isMachineWhole();
			isWhole = true;

		} catch (MultiblockValidationException e) {

			this.lastValidationException = e;
			isWhole = false;
		}
		
		if (isWhole) {

			// This will alter assembly state
			this.assembleMachine(oldState);

		} else if (oldState == AssemblyState.Assembled) {

			// This will alter assembly state
			this.disassembleMachine();
		}
		// Else Paused, do nothing
	}
	
	/**
	 * Called when a machine becomes "whole" and should begin
	 * functioning as a game-logically finished machine.
	 * Calls onMachineAssembled on all attached parts.
	 */
	private void assembleMachine(AssemblyState oldState) {

		for (IMultiblockPart part : this.connectedParts)
			part.onMachineAssembled(this);

		this.assemblyState = AssemblyState.Assembled;

		if (oldState == AssemblyState.Paused)
			this.onMachineRestored();
		else
			this.onMachineAssembled();
	}
	
	/**
	 * Called when the machine needs to be disassembled.
	 * It is not longer "whole" and should not be functional, usually
	 * as a result of a block being removed.
	 * Calls onMachineBroken on all attached parts.
	 */
	private void disassembleMachine() {

		for(IMultiblockPart part : this.connectedParts)
			part.onMachineBroken();

		this.assemblyState = AssemblyState.Disassembled;
		this.onMachineDisassembled();
	}
	
	/**
	 * Assimilate another controller into this controller.
	 * Acquire all of the other controller's blocks and attach them
	 * to this one.
	 * 
	 * @param other The controller to merge into this one.
	 */
	public void assimilate(MultiblockControllerBase other) {

		BlockPos otherReferenceCoord = other.getReferenceCoord();

		if (otherReferenceCoord != null && this.getReferenceCoord().compareTo(otherReferenceCoord) >= 0)
			throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");

		TileEntity te;
		Set<IMultiblockPart> partsToAcquire = new HashSet<IMultiblockPart>(other.connectedParts);

		// releases all blocks and references gently so they can be incorporated into another multiblock
		other._onAssimilated(this);
		
		for (IMultiblockPart acquiredPart : partsToAcquire) {

			// By definition, none of these can be the minimum block.
			if (acquiredPart.isInvalid())
				continue;

			this.connectedParts.add(acquiredPart);
			acquiredPart.onAssimilated(this);
			this.onBlockAdded(acquiredPart);
		}

		this.onAssimilate(other);
		other.onAssimilated(this);
	}
	
	/**
	 * Called when this machine is consumed by another controller.
	 * Essentially, forcibly tear down this object.
	 * @param otherController The controller consuming this controller.
	 */
	private void _onAssimilated(MultiblockControllerBase otherController) {

		if (this.referenceCoord != null) {

			if (CodeHelper.blockChunkExists(this.worldObj.getChunkProvider(), this.referenceCoord)) {

				TileEntity te = this.worldObj.getTileEntity(this.referenceCoord);

				if (te instanceof IMultiblockPart)
					((IMultiblockPart)te).forfeitMultiblockSaveDelegate();
			}

			this.referenceCoord = null;
		}

		this.connectedParts.clear();
	}
	
	/**
	 * Callback. Called after this controller assimilates all the blocks
	 * from another controller.
	 * Use this to absorb that controller's game data.
	 * @param assimilated The controller whose uniqueness was added to our own.
	 */
	protected abstract void onAssimilate(MultiblockControllerBase assimilated);
	
	/**
	 * Callback. Called after this controller is assimilated into another controller.
	 * All blocks have been stripped out of this object and handed over to the
	 * other controller.
	 * This is intended primarily for cleanup.
	 * @param assimilator The controller which has assimilated this controller.
	 */
	protected abstract void onAssimilated(MultiblockControllerBase assimilator);
	
	/**
	 * Driver for the update loop. If the machine is assembled, runs
	 * the game logic update method.
	 * @see erogenousbeef.core.multiblock.MultiblockControllerBase#update() //TODO Fix this Javadoc
	 */
	public final void updateMultiblockEntity() {

		if (this.connectedParts.isEmpty()) {

			// This shouldn't happen, but just in case...
			MultiblockRegistry.addDeadController(this.worldObj, this);
			return;
		}

		if (this.assemblyState != AssemblyState.Assembled) {
			// Not assembled - don't run game logic
			return;
		}

		if (this.worldObj.isRemote)
			this.updateClient();

		else if (updateServer()) {

			// If this returns true, the server has changed its internal data.
			// If our chunks are loaded (they should be), we must mark our chunks as dirty.
			if (this.minimumCoord != null && this.maximumCoord != null &&
					/*
					this.worldObj.checkChunksExist(this.minimumCoord.x, this.minimumCoord.y, this.minimumCoord.z,
							this.maximumCoord.x, this.maximumCoord.y, this.maximumCoord.z)
					*/
					this.worldObj.isAreaLoaded(this.minimumCoord, this.maximumCoord)
					) {

				int minChunkX = CodeHelper.getChunkXFromBlock(this.minimumCoord);
				int minChunkZ = CodeHelper.getChunkZFromBlock(this.minimumCoord);
				int maxChunkX = CodeHelper.getChunkXFromBlock(this.maximumCoord);
				int maxChunkZ = CodeHelper.getChunkZFromBlock(this.maximumCoord);
				
				for (int x = minChunkX; x <= maxChunkX; x++)
					for (int z = minChunkZ; z <= maxChunkZ; z++) {

						// Ensure that we save our data, even if the our save delegate is in has no TEs.
						Chunk chunkToSave = this.worldObj.getChunkFromChunkCoords(x, z);

						chunkToSave.setChunkModified();
					}
			}
		}
		// Else: Server, but no need to save data.
	}
	
	/**
	 * The server-side update loop! Use this similarly to a TileEntity's update loop.
	 * You do not need to call your superclass' update() if you're directly
	 * derived from MultiblockControllerBase. This is a callback.
	 * Note that this will only be called when the machine is assembled.
	 * @return True if the multiblock should save data, i.e. its internal game state has changed. False otherwise.
	 */
	protected abstract boolean updateServer();
	
	/**
	 * Client-side update loop. Generally, this shouldn't do anything, but if you want
	 * to do some interpolation or something, do it here.
	 */
	protected abstract void updateClient();
	
	// Validation helpers
	/**
	 * The "frame" consists of the outer edges of the machine, plus the corners.
	 * 
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed on the machine's frame
	 */
	protected void isBlockGoodForFrame(World world, int x, int y, int z) throws MultiblockValidationException {

		throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
	}

	/**
	 * The top consists of the top face, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed on the machine's top face
	 */
	protected void isBlockGoodForTop(World world, int x, int y, int z) throws MultiblockValidationException {

		throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
	}
	
	/**
	 * The bottom consists of the bottom face, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed on the machine's bottom face
	 */
	protected void isBlockGoodForBottom(World world, int x, int y, int z) throws MultiblockValidationException {

		throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
	}
	
	/**
	 * The sides consists of the N/E/S/W-facing faces, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed on the machine's side faces
	 */
	protected void isBlockGoodForSides(World world, int x, int y, int z) throws MultiblockValidationException {

		throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
	}
	
	/**
	 * The interior is any block that does not touch blocks outside the machine.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed in the machine's interior
	 */
	protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException {

		throw new MultiblockValidationException(String.format("%d, %d, %d - Block is not valid for use in the machine's interior", x, y, z));
	}
	
	/**
	 * @return The reference coordinate, the block with the lowest x, y, z coordinates, evaluated in that order.
	 */
	public BlockPos getReferenceCoord() {

		if (this.referenceCoord == null)
			this.selectNewReferenceCoord();

		return this.referenceCoord;
	}
	
	/**
	 * @return The number of blocks connected to this controller.
	 */
	public int getNumConnectedBlocks() {

		return connectedParts.size();
	}

	public abstract void writeToNBT(NBTTagCompound data);
	public abstract void readFromNBT(NBTTagCompound data);

	/**
	 * Force this multiblock to recalculate its minimum and maximum coordinates
	 * from the list of connected parts.
	 */
	public void recalculateMinMaxCoords() {

		int minX, minY, minZ, maxX, maxY, maxZ;
		int partCoord;
		BlockPos partPos;

		minX = minY = minZ = Integer.MAX_VALUE;
		maxX = maxY = maxZ = Integer.MIN_VALUE;

		for (IMultiblockPart part : this.connectedParts) {

			partPos = part.getPos();

			partCoord = partPos.getX();
			if (partCoord < minX) minX = partCoord;
			if (partCoord > maxX) maxX = partCoord;

			partCoord = partPos.getY();
			if (partCoord < minY) minY = partCoord;
			if (partCoord > maxY) maxY = partCoord;

			partCoord = partPos.getZ();
			if (partCoord < minZ) minZ = partCoord;
			if (partCoord > maxZ) maxZ = partCoord;

			/*
			if (part.xCoord < minimumCoord.x) { minimumCoord.x = part.xCoord; }
			if (part.xCoord > maximumCoord.x) { maximumCoord.x = part.xCoord; }
			if (part.yCoord < minimumCoord.y) { minimumCoord.y = part.yCoord; }
			if (part.yCoord > maximumCoord.y) { maximumCoord.y = part.yCoord; }
			if (part.zCoord < minimumCoord.z) { minimumCoord.z = part.zCoord; }
			if (part.zCoord > maximumCoord.z) { maximumCoord.z = part.zCoord; }
			*/
		}

		this.minimumCoord = new BlockPos(minX, minY, minZ);
		this.maximumCoord = new BlockPos(maxX, maxY, maxZ);

	}
	
	/**
	 * @return The minimum bounding-box coordinate containing this machine's blocks.
	 */
	public BlockPos getMinimumCoord() {

		if (this.minimumCoord == null)
			this.recalculateMinMaxCoords();

		return this.minimumCoord;
	}

	/**
	 * @return The maximum bounding-box coordinate containing this machine's blocks.
	 */
	public BlockPos getMaximumCoord() {

		if (this.maximumCoord == null)
			this.recalculateMinMaxCoords();

		return this.maximumCoord;
	}

	/**
	 * Called when the save delegate's tile entity is being asked for its description packet
	 * @param data A fresh compound tag to write your multiblock data into
	 */
	public abstract void formatDescriptionPacket(NBTTagCompound data);

	/**
	 * Called when the save delegate's tile entity receiving a description packet
	 * @param data A compound tag containing multiblock data to import
	 */
	public abstract void decodeDescriptionPacket(NBTTagCompound data);

	/**
	 * @return True if this controller has no associated blocks, false otherwise
	 */
	public boolean isEmpty() {
		return connectedParts.isEmpty();
	}

	/**
	 * Tests whether this multiblock should consume the other multiblock
	 * and become the new multiblock master when the two multiblocks
	 * are adjacent. Assumes both multiblocks are the same type.
	 * @param otherController The other multiblock controller.
	 * @return True if this multiblock should consume the other, false otherwise.
	 */
	public boolean shouldConsume(MultiblockControllerBase otherController) {

		if (!otherController.getClass().equals(this.getClass()))
			throw new IllegalArgumentException("Attempting to merge two multiblocks with different master classes - this should never happen!");

		if (otherController == this)
			return false; // Don't be silly, don't eat yourself.

		int res = this._shouldConsume(otherController);

		if (res < 0)
			return true;
		else if (res > 0)
			return false;
		else {

			// Strip dead parts from both and retry
			FMLLog.warning("[%s] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.", this.worldObj.isRemote ? "CLIENT" : "SERVER");

			this.auditParts();
			otherController.auditParts();
			
			res = this._shouldConsume(otherController);

			if (res < 0)
				return true;
			else if (res > 0)
				return false;
			else {

				FMLLog.severe("My Controller (%d): size (%d), parts: %s", hashCode(), this.connectedParts.size(), this.getPartsListString());
				FMLLog.severe("Other Controller (%d): size (%d), coords: %s", otherController.hashCode(), otherController.connectedParts.size(), otherController.getPartsListString());
				throw new IllegalArgumentException("[" + (this.worldObj.isRemote ? "CLIENT" : "SERVER") +
						"] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
			}

		}
	}
	
	private int _shouldConsume(MultiblockControllerBase otherController) {

		BlockPos myCoord = this.getReferenceCoord();
		BlockPos theirCoord = otherController.getReferenceCoord();
		
		// Always consume other controllers if their reference coordinate is null - this means they're empty and can be assimilated on the cheap
		if (theirCoord == null)
			return -1;
		else
			return myCoord.compareTo(theirCoord);
	}
	
	private String getPartsListString() {

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		BlockPos partPos;

		for (IMultiblockPart part : this.connectedParts) {

			if(!first)
				sb.append(", ");

			partPos = part.getPos();
			sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), partPos.getX(), partPos.getY(), partPos.getZ()));
			first = false;
		}
		
		return sb.toString();
	}
	
	/**
	 * Checks all of the parts in the controller. If any are dead or do not exist in the world, they are removed.
	 */
	private void auditParts() {

		HashSet<IMultiblockPart> deadParts = new HashSet<IMultiblockPart>();

		for (IMultiblockPart part : this.connectedParts) {

			if (part.isInvalid() || this.worldObj.getTileEntity(part.getPos()) != part) {

				this.onDetachBlock(part);
				deadParts.add(part);
			}
		}

		this.connectedParts.removeAll(deadParts);

		FMLLog.warning("[%s] Controller found %d dead parts during an audit, %d parts remain attached", this.worldObj.isRemote ? "CLIENT" : "SERVER", deadParts.size(), this.connectedParts.size());
	}

	/**
	 * Called when this machine may need to check for blocks that are no
	 * longer physically connected to the reference coordinate.
	 * @return
	 */
	public Set<IMultiblockPart> checkForDisconnections() {

		if (!this.shouldCheckForDisconnections)
			return null;

		if (this.isEmpty()) {

			MultiblockRegistry.addDeadController(this.worldObj, this);
			return null;
		}
		
		TileEntity te;
		IChunkProvider chunkProvider = this.worldObj.getChunkProvider();

		// Invalidate our reference coord, we'll recalculate it shortly
		this.referenceCoord = null;
		
		// Reset visitations and find the minimum coordinate
		Set<IMultiblockPart> deadParts = new HashSet<IMultiblockPart>();
		BlockPos position;
		IMultiblockPart referencePart = null;

		int originalSize = this.connectedParts.size();

		for (IMultiblockPart part : this.connectedParts) {

			position = part.getPos();

			// This happens during chunk unload.

			if (!CodeHelper.blockChunkExists(chunkProvider, position) || part.isInvalid()) {

				deadParts.add(part);
				this.onDetachBlock(part);
				continue;
			}
			
			if (this.worldObj.getTileEntity(position) != part) {

				deadParts.add(part);
				this.onDetachBlock(part);
				continue;
			}

			part.setUnvisited();
			part.forfeitMultiblockSaveDelegate();
			
			if (this.referenceCoord == null) {

				this.referenceCoord = position;
				referencePart = part;

			} else if(position.compareTo(this.referenceCoord) < 0) {

				this.referenceCoord = position;
				referencePart = part;
			}
		}

		this.connectedParts.removeAll(deadParts);
		deadParts.clear();
		
		if (referencePart == null || isEmpty()) {

			// There are no valid parts remaining. The entire multiblock was unloaded during a chunk unload. Halt.
			this.shouldCheckForDisconnections = false;
			MultiblockRegistry.addDeadController(this.worldObj, this);
			return null;
		}
		else {
			referencePart.becomeMultiblockSaveDelegate();
		}

		// Now visit all connected parts, breadth-first, starting from reference coord's part
		IMultiblockPart part;
		LinkedList<IMultiblockPart> partsToCheck = new LinkedList<IMultiblockPart>();
		IMultiblockPart[] nearbyParts = null;
		int visitedParts = 0;

		partsToCheck.add(referencePart);
		
		while (!partsToCheck.isEmpty()) {

			part = partsToCheck.removeFirst();
			part.setVisited();
			visitedParts++;

			nearbyParts = part.getNeighboringParts(); // Chunk-safe on server, but not on client
			for (IMultiblockPart nearbyPart : nearbyParts) {

				// Ignore different machines
				if (nearbyPart.getMultiblockController() != this)
					continue;

				if (!nearbyPart.isVisited()) {

					nearbyPart.setVisited();
					partsToCheck.add(nearbyPart);
				}
			}
		}
		
		// Finally, remove all parts that remain disconnected.
		Set<IMultiblockPart> removedParts = new HashSet<IMultiblockPart>();

		for (IMultiblockPart orphanCandidate : this.connectedParts) {

			if (!orphanCandidate.isVisited()) {

				deadParts.add(orphanCandidate);
				orphanCandidate.onOrphaned(this, originalSize, visitedParts);
				this.onDetachBlock(orphanCandidate);
				removedParts.add(orphanCandidate);
			}
		}

		// Trim any blocks that were invalid, or were removed.
		this.connectedParts.removeAll(deadParts);
		
		// Cleanup. Not necessary, really.
		deadParts.clear();
		
		// Juuuust in case.
		if(this.referenceCoord == null)
			this.selectNewReferenceCoord();

		// We've run the checks from here on out.
		this.shouldCheckForDisconnections = false;
		
		return removedParts;
	}

	/**
	 * Detach all parts. Return a set of all parts which still
	 * have a valid tile entity. Chunk-safe.
	 * @return A set of all parts which still have a valid tile entity.
	 */
	public Set<IMultiblockPart> detachAllBlocks() {

		if (this.worldObj == null)
			return new HashSet<IMultiblockPart>();
		
		IChunkProvider chunkProvider = this.worldObj.getChunkProvider();

		for (IMultiblockPart part : this.connectedParts) {

			if (CodeHelper.blockChunkExists(chunkProvider, part.getPos()))
				this.onDetachBlock(part);
		}

		Set<IMultiblockPart> detachedParts = this.connectedParts;

		this.connectedParts = new HashSet<IMultiblockPart>();
		return detachedParts;
	}

	/**
	 * @return True if this multiblock machine is considered assembled and ready to go.
	 */
	public boolean isAssembled() {
		return this.assemblyState == AssemblyState.Assembled;
	}
	
	private void selectNewReferenceCoord() {

		IChunkProvider chunkProvider = this.worldObj.getChunkProvider();
		TileEntity theChosenOne = null;
		BlockPos position;

		this.referenceCoord = null;

		for (IMultiblockPart part : this.connectedParts) {

			position = part.getPos();
			if (part.isInvalid() || !CodeHelper.blockChunkExists(chunkProvider, position)) {

				// Chunk is unloading, skip this coord to prevent chunk thrashing
				continue;
			}

			if (this.referenceCoord == null || this.referenceCoord.compareTo(position) > 0) {

				this.referenceCoord = position;
				theChosenOne = part;
			}
		}

		if (theChosenOne != null)
			((IMultiblockPart)theChosenOne).becomeMultiblockSaveDelegate();
	}
	
	/**
	 * Marks the reference coord dirty & updateable.
	 * 
	 * On the server, this will mark the for a data-update, so that
	 * nearby clients will receive an updated description packet from the server
	 * after a short time. The block's chunk will also be marked dirty and the
	 * block's chunk will be saved to disk the next time chunks are saved.
	 * 
	 * On the client, this will mark the block for a rendering update.
	 */
	protected void markReferenceCoordForUpdate() {

		BlockPos referenceCoord = this.getReferenceCoord();

		if (this.worldObj != null && referenceCoord != null)
			this.worldObj.markBlockForUpdate(referenceCoord);
	}
	
	/**
	 * Marks the reference coord dirty.
	 * 
	 * On the server, this marks the reference coord's chunk as dirty; the block (and chunk)
	 * will be saved to disk the next time chunks are saved. This does NOT mark it dirty for
	 * a description-packet update.
	 * 
	 * On the client, does nothing.
	 * @see MultiblockControllerBase#markReferenceCoordForUpdate()
	 */
	protected void markReferenceCoordDirty() {

		if (this.worldObj == null || this.worldObj.isRemote)
			return;

		BlockPos referenceCoord = this.getReferenceCoord();

		if (referenceCoord == null)
			return;

		TileEntity saveTe = this.worldObj.getTileEntity(referenceCoord);

		//this.worldObj.markTileEntityChunkModified(referenceCoord.x, referenceCoord.y, referenceCoord.z, saveTe);
		this.worldObj.markChunkDirty(referenceCoord, saveTe);
	}

	
}