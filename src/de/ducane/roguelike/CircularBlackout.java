package de.ducane.roguelike;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;
import java.awt.image.*;

public final class CircularBlackout extends Blackout
{
	private BufferedImage	mask;
	private float			r;
	
	public CircularBlackout( final Color color, final float radius )
	{
		super( color );
		this.r = radius;
		updateMask();
	}
	
	@ Override
	public boolean contains( final float cx, final float cy, final float px, final float py )
	{
		return Math.pow( px - cx, 2 ) + Math.pow( py - cy, 2 ) <= r * r;
	}
	
	@ Override
	public void darken( final Graphics2D g, final float x, final float y, final float w, final float h )
	{
		g.setColor( color );
		
		fillRect( g, 0, 0, w, y - r );
		fillRect( g, 0, y - r, x - r, r * 2 );
		drawImage( g, mask, x - r, y - r );
		fillRect( g, x + r, y - r, w - x - r, r * 2 );
		fillRect( g, 0, y + r, w, h - y - r );
	}
	
	@ Override
	public void setColor( final Color color )
	{
		super.setColor( color );
		updateMask();
	}
	
	public void setRadius( final float radius )
	{
		if ( radius != this.r )
		{
			this.r = radius;
			updateMask();
		}
	}
	
	private void updateMask()
	{
		final int rgba = color.getRGB();
		
		mask = new BufferedImage( (int) ( 2 * r ), (int) ( 2 * r ), BufferedImage.TYPE_INT_ARGB );
		
		for ( int y = 0; y < mask.getHeight(); y++ )
		{
			for ( int x = 0; x < mask.getHeight(); x++ )
			{
				if ( Math.pow( x - r, 2 ) + Math.pow( y - r, 2 ) > r * r )
				{
					mask.setRGB( x, y, rgba );
				}
			}
		}
	}
}