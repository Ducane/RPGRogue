package de.ducane.roguelike.entity;

import java.awt.image.*;
import java.util.*;

public final class MobType
{
	public static final Map<String, MobType>	TYPES	= new HashMap<String, MobType>();
	
	public BufferedImage[][]					moveAnimation;
	
	public final int							offense;
	public final int							defense;
	
	public final int							hp;
	public final int							exp;
	
	public final String							name;
	
	public MobType( final String name, final BufferedImage[][] images, final int hp, final int offense, final int defense, final int exp )
	{
		this.name = name;
		this.hp = hp;
		this.offense = offense;
		this.defense = defense;
		this.exp = exp;
		this.moveAnimation = images;
	}
	
	public static MobType get( final String type )
	{
		if ( TYPES.containsKey( type ) )
		{
			return TYPES.get( type );
		}
		else
		{
			final MobType tile = MobTypeParser.parse( type );
			TYPES.put( type, tile );
			return tile;
		}
	}
}