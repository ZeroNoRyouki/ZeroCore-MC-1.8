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

/**
 * An exception thrown when trying to validate a multiblock. Requires a string describing why the multiblock
 * could not assemble.
 * @author Erogenous Beef
 */
public class MultiblockValidationException extends Exception {

	public MultiblockValidationException(String reason) {
		super(reason);
	}
}
