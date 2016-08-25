package de.ducane.roguelike.entity;

import static de.androbin.util.JSONUtil.*;
import de.androbin.gfx.util.*;
import de.androbin.rpg.*;
import java.awt.image.*;
import org.json.simple.*;

public final class MobTypeParser
{
	private MobTypeParser()
	{
	}
	
	public static MobType parse( final String type )
	{
		final JSONObject mob_data = (JSONObject) parseJSON( "mobs/" + type + ".json" );
		
		final int offense = ( (Number) mob_data.get( "offense" ) ).intValue();
		final int defense = ( (Number) mob_data.get( "defense" ) ).intValue();
		final int hp = ( (Number) mob_data.get( "hp" ) ).intValue();
		final int exp = ( (Number) mob_data.get( "exp" ) ).intValue();
		
		final Direction[] directions = Direction.values();
		final BufferedImage[][] images = new BufferedImage[ directions.length ][ 2 ];
		
		for ( int i = 0; i < directions.length; i++ )
		{
			final BufferedImage image = ImageUtil.loadImage( "mobs/" + type + "_" + Direction.values()[ i ].name().toLowerCase() + ".png" );
			
			for ( int j = 0; j < images[ i ].length; j++ )
			{
				images[ i ][ j ] = image.getSubimage( j * 16, 0, 16, 16 );
			}
		}
		
		return new MobType( type, images, hp, offense, defense, exp );
	}
}