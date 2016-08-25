package de.ducane.roguelike.gameobject;

import de.androbin.gfx.util.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.awt.image.*;

public abstract class GameObject
{
	private final BufferedImage	image;
	protected boolean			passable;
	
	protected GameObject( final String name )
	{
		image = ImageUtil.loadImage( "gameobject/" + name + ".png" );
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public abstract void onPlayerEntered( final Level level, final Player player );
}