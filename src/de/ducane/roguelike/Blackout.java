package de.ducane.roguelike;

import java.awt.*;

public abstract class Blackout
{
	protected Color color;
	
	protected Blackout( final Color color )
	{
		this.color = color;
	}
	
	public abstract boolean contains( final float cx, final float cy, final float px, final float py );
	
	public abstract void darken( final Graphics2D g, float x, float y, float w, float h );
	
	public void setColor( final Color color )
	{
		this.color = color;
	}
}