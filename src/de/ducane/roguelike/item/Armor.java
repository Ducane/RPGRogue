package de.ducane.roguelike.item;

import java.awt.image.*;

public final class Armor extends Item
{
	public final int defense;
	
	public Armor( final String name, final String description, final int defense, final BufferedImage image )
	{
		super( name, description, image );
		
		this.defense = defense;
	}
}