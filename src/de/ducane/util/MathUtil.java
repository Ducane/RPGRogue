package de.ducane.util;

import java.awt.*;

public final class MathUtil
{
	private MathUtil()
	{
	}
	
	public static boolean intersects( final Rectangle a, final Rectangle b, final int c )
	{
		return a.x - c < b.x + b.width && a.y - c < b.y + b.height && a.x + a.width > b.x - c && a.y + a.height > b.y - c;
	}
}