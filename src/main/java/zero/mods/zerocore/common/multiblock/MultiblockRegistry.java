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

import java.util.HashMap;
import java.util.Set;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

/**
 * This is a very static singleton registry class which directs incoming events to sub-objects, which
 * actually manage each individual world's multiblocks.
 * @author Erogenous Beef
 */
public class MultiblockRegistry {

	// World > WorldRegistry map
	private static HashMap<World, MultiblockWorldRegistry> registries = new HashMap<World, MultiblockWorldRegistry>();
	
	/**
	 * Called before Tile Entities are ticked in the world. Do bookkeeping here.
	 * @param world The world being ticked
	 */
	public static void tickStart(World world) {

		if (MultiblockRegistry.registries.containsKey(world)) {

			MultiblockWorldRegistry registry = MultiblockRegistry.registries.get(world);

			registry.processMultiblockChanges();
			registry.tickStart();
		}
	}
	
	/**
	 * Called when the world has finished loading a chunk.
	 * @param world The world which has finished loading a chunk
	 * @param chunkX The X coordinate of the chunk
	 * @param chunkZ The Z coordinate of the chunk
	 */
	public static void onChunkLoaded(World world, int chunkX, int chunkZ) {

		if (MultiblockRegistry.registries.containsKey(world))
			MultiblockRegistry.registries.get(world).onChunkLoaded(chunkX, chunkZ);
	}

	/**
	 * Register a new part in the system. The part has been created either through user action or via a chunk loading.
	 * @param world The world into which this part is loading.
	 * @param part The part being loaded.
	 */
	public static void onPartAdded(World world, IMultiblockPart part) {

		MultiblockWorldRegistry registry = MultiblockRegistry.getOrCreateRegistry(world);

		registry.onPartAdded(part);
	}
	
	/**
	 * Call to remove a part from world lists.
	 * @param world The world from which a multiblock part is being removed.
	 * @param part The part being removed.
	 */
	public static void onPartRemovedFromWorld(World world, IMultiblockPart part) {

		if (MultiblockRegistry.registries.containsKey(world))
			MultiblockRegistry.registries.get(world).onPartRemovedFromWorld(part);
	}

	
	/**
	 * Called whenever a world is unloaded. Unload the relevant registry, if we have one.
	 * @param world The world being unloaded.
	 */
	public static void onWorldUnloaded(World world) {

		if (MultiblockRegistry.registries.containsKey(world)) {

			MultiblockRegistry.registries.get(world).onWorldUnloaded();
			MultiblockRegistry.registries.remove(world);
		}
	}

	/**
	 * Call to mark a controller as dirty. Dirty means that parts have
	 * been added or removed this tick.
	 * @param world The world containing the multiblock
	 * @param controller The dirty controller 
	 */
	public static void addDirtyController(World world, MultiblockControllerBase controller) {

		if (MultiblockRegistry.registries.containsKey(world))
			MultiblockRegistry.registries.get(world).addDirtyController(controller);
		else
			throw new IllegalArgumentException("Adding a dirty controller to a world that has no registered controllers!");
	}
	
	/**
	 * Call to mark a controller as dead. It should only be marked as dead
	 * when it has no connected parts. It will be removed after the next world tick.
	 * @param world The world formerly containing the multiblock
	 * @param controller The dead controller
	 */
	public static void addDeadController(World world, MultiblockControllerBase controller) {

		if (MultiblockRegistry.registries.containsKey(world))
			MultiblockRegistry.registries.get(world).addDeadController(controller);
		else
			FMLLog.warning("Controller %d in world %s marked as dead, but that world is not tracked! Controller is being ignored.", controller.hashCode(), world);
	}

	/**
	 * @param world The world whose controllers you wish to retrieve.
	 * @return An unmodifiable set of controllers active in the given world, or null if there are none.
	 */
	public static Set<MultiblockControllerBase> getControllersFromWorld(World world) {

		if (MultiblockRegistry.registries.containsKey(world))
			return MultiblockRegistry.registries.get(world).getControllers();

		return null;
	}
	
	/// *** PRIVATE HELPERS *** ///
	
	private static MultiblockWorldRegistry getOrCreateRegistry(World world) {

		if (MultiblockRegistry.registries.containsKey(world))
			return MultiblockRegistry.registries.get(world);

		else {

			MultiblockWorldRegistry newRegistry = new MultiblockWorldRegistry(world);

			MultiblockRegistry.registries.put(world, newRegistry);
			return newRegistry;
		}
	}

}
