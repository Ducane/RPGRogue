package de.ducane.roguelike.item;

import java.awt.image.*;

public final class Weapon extends Item
{
	public final int attack;
	
	public Weapon( final String name, final String description, final int attack, final BufferedImage image )
	{
		super( name, description, image );
		
		this.attack = attack;
	}
}