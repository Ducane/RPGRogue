package de.ducane.roguelike.item;

import java.awt.image.*;
import java.util.*;

public class Item
{
	public static final Map<String, Item> ITEMS		= new HashMap<String, Item>();
	
	public final BufferedImage			  image;
	public final String					  name;
	public final String					  description;
	
	protected Item( final String name, final String description, final BufferedImage image )
	{
		this.image = image;
		this.name = name;
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public static Item getItem( final String type )
	{
		if ( ITEMS.containsKey( type ) )
		{
			return ITEMS.get( type );
		}
		else
		{
			final Item item = ItemParser.parse( type );
			ITEMS.put( type, item );
			return item;
		}
	}
	
	public String getName()
	{
		return name;
	}
}