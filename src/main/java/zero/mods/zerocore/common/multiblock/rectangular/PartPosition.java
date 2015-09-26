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

public enum PartPosition {
	Unknown,
	Interior,
	FrameCorner,
	Frame,
	TopFace,
	BottomFace,
	NorthFace,
	SouthFace,
	EastFace,
	WestFace;
	
	public boolean isFace(PartPosition position) {
		switch(position) {
			case TopFace:
			case BottomFace:
			case NorthFace:
			case SouthFace:
			case EastFace:
			case WestFace:
				return true;
			default:
				return false;
		}
	}
}
