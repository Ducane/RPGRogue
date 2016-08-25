package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;

public class Mob extends RogueEntity
{
	public final MobType	type;
	private Item			item;
	
	public Mob( final PlayScreen screen, final Level level, final MobType type, final int x, final int y )
	{
		super( screen, level, x, y );
		
		this.type = type;
		stats.offense = type.offense;
		stats.hp = type.hp;
		stats.defense = type.defense;
		stats.exp = type.exp;
		
		renderer = new MobRenderer( this );
		
		viewDir = Direction.DOWN;
	}
	
	public void calcDirection( final Entity entity )
	{
		final float dx = entity.getPX() - getPX();
		final float dy = entity.getPY() - getPY();
		
		final Direction dirX = dx < 0f ? Direction.LEFT : Direction.RIGHT;
		final Direction dirY = dy < 0f ? Direction.UP : Direction.DOWN;
		
		final boolean a = Math.abs( dx ) > Math.abs( dy );
		move( a && canMove( dirX ) || !a && !canMove( dirY ) ? dirX : dirY );
	}
	
	@ Override
	public void collectItem( final Item item )
	{
		this.item = item;
	}
	
	public boolean hasItem()
	{
		return item != null;
	}
	
	@ Override
	public float moveSpeed()
	{
		return 2f;
	}
	
	@ Override
	public void update( final float delta )
	{
		super.update( delta );
		
		if ( isAttacking() )
		{
			attackProgress += delta;
			
			if ( attackProgress >= 1f )
			{
				damaging = true;
				
				if ( level.canAttack( this, viewDir ) )
				{
					attack( viewDir );
				}
				
				attacking = false;
				attackProgress = 0f;
			}
		}
		
		if ( damaging )
		{
			damageProgress += delta;
			
			if ( damageProgress >= 1f )
			{
				damaging = false;
				damageProgress = 0f;
			}
		}
	}
}