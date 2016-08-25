package de.ducane.roguelike.item;

import java.awt.image.*;

public final class Accessoire extends Item
{
	public final int hp;
	
	public Accessoire( final String name, final String description, final int hp, final BufferedImage image )
	{
		super( name, description, image );
		
		this.hp = hp;
	}
}