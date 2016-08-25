package de.ducane.roguelike.screen;

import java.awt.geom.*;

public enum State
{
	Menu,
	Inventory,
	ItemSelect;
	
	public int					selection;
	public Rectangle2D.Float[]	buttonBounds;
	
	public State previous()
	{
		final int o = ordinal();
		return o == 0 ? null : State.values()[ o - 1 ];
	}
}