package de.ducane.roguelike;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;

public final class RectangularBlackout extends Blackout
{
	private final float	width;
	private final float	height;
	
	public RectangularBlackout( final Color color, final float width, final float height )
	{
		super( color );
		
		this.width = width;
		this.height = height;
	}
	
	@ Override
	public boolean contains( final float cx, final float cy, final float px, final float py )
	{
		return px >= cx && px < cx + width && py >= cy && py < cy + height;
	}
	
	@ Override
	public void darken( final Graphics2D g, final float x, final float y, final float w, final float h )
	{
		g.setColor( color );
		
		fillRect( g, 0, 0, w, y );
		fillRect( g, 0, y, x, height );
		fillRect( g, x + width, y, w - x - width, height );
		fillRect( g, 0, y + height, w, h - y - height );
	}
}