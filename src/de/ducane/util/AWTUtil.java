package de.ducane.util;

import java.awt.*;

public final class AWTUtil
{
	private AWTUtil()
	{
	}
	
	public static void drawBorderedString( final Graphics2D g, final String string, final float x, final float y, final float shift, final Color border, final Color stringColor )
	{
		g.setColor( border );
		g.drawString( string, x - shift, y - shift );
		g.drawString( string, x - shift, y + shift );
		g.drawString( string, x + shift, y - shift );
		g.drawString( string, x + shift, y + shift );
		
		g.setColor( stringColor );
		g.drawString( string, x, y );
	}
}