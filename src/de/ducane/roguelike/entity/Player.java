package de.ducane.roguelike.entity;

import de.androbin.gfx.util.*;
import de.androbin.rpg.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.*;

public final class Player extends RogueEntity
{
	private final List<Item>	inventory	= new LinkedList<Item>();
	
	private Weapon				weapon;
	private Armor				armor;
	private Accessoire			accessoire;
	
	public BufferedImage[][]	moveAnimation;
	
	public final String			name;
	
	private boolean				running;
	
	public Player( final PlayScreen screen, final Level level, final int x, final int y, final String name )
	{
		super( screen, level, x, y );
		
		this.name = name;
		
		viewDir = Direction.DOWN;
		
		renderer = new PlayerRenderer( this );
		
		initPlayer();
		initImages();
	}
	
	private void addExp( final int exp )
	{
		stats.exp += exp;
		System.out.println( stats.exp );
	}
	
	@ Override
	public void attack( final Direction viewDir )
	{
		final RogueEntity entityToHit = level.getEntityAt( getX() + viewDir.dx, getY() + viewDir.dy );
		
		if ( entityToHit == null )
		{
			return;
		}
		
		final Random random = ThreadLocalRandom.current();
		
		final int minDamage = getAttack() - entityToHit.getDef();
		final int maxDamage = minDamage + random.nextInt( stats.stage + 1 );
		
		damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
		
		entityToHit.takeDamage( damage );
		
		if ( entityToHit.getHp() <= 0 )
		{
			addExp( entityToHit.getExp() );
		}
	}
	
	public void eat( final Food food )
	{
		stats.hp = Math.min( stats.hp + food.hp, getMaxHp() );
	}
	
	public Accessoire dequipAccessoire()
	{
		final Accessoire accessoire = this.accessoire;
		this.accessoire = null;
		return accessoire;
	}
	
	public Armor dequipArmor()
	{
		final Armor armor = this.armor;
		this.armor = null;
		return armor;
	}
	
	public Weapon dequipWeapon()
	{
		final Weapon weapon = this.weapon;
		this.weapon = null;
		return weapon;
	}
	
	public void equipAccessoire( final Accessoire accessoire )
	{
		this.accessoire = accessoire;
	}
	
	public void equipArmour( final Armor armour )
	{
		this.armor = armour;
	}
	
	public void equipWeapon( final Weapon weapon )
	{
		this.weapon = weapon;
	}
	
	public List<Item> getInventory()
	{
		return inventory;
	}
	
	public Accessoire getAccessoire()
	{
		return accessoire;
	}
	
	public Armor getArmor()
	{
		return armor;
	}
	
	@ Override
	public int getAttack()
	{
		return super.getAttack() + ( weapon == null ? 0 : weapon.attack );
	}
	
	@ Override
	public int getDef()
	{
		return super.getDef() + ( armor == null ? 0 : armor.defense );
	}
	
	@ Override
	public int getMaxHp()
	{
		return super.getMaxHp() + ( accessoire == null ? 0 : accessoire.hp );
	}
	
	public Weapon getWeapon()
	{
		return weapon;
	}
	
	private void initImages()
	{
		moveAnimation = new BufferedImage[ Direction.values().length ][ 3 ];
		
		for ( int i = 0; i < moveAnimation.length; i++ )
		{
			final BufferedImage image = ImageUtil.loadImage( "player/" + Direction.values()[ i ].name().toLowerCase() + ".png" );
			
			for ( int j = 0; j < moveAnimation[ i ].length; j++ )
			{
				moveAnimation[ i ][ j ] = image.getSubimage( j * 16, 0, 16, 18 );
			}
		}
	}
	
	private void initPlayer()
	{
		stats.maxHp = 300;
		stats.hp = 300;
		stats.stage = 1;
		stats.offense = 1;
		stats.defense = 0;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	@ Override
	public void collectItem( final Item item )
	{
		inventory.add( item );
	}
	
	@ Override
	public float moveSpeed()
	{
		return running ? 7f : 2f;
	}
	
	public void setRunning( final boolean running )
	{
		this.running = running;
	}
	
	public void levelUp()
	{
		stats.stage++;
		
		final Random random = ThreadLocalRandom.current();
		
		stats.hp += random.nextInt( stats.stage ) + 1;
		stats.offense += random.nextInt( stats.stage ) + 1;
		stats.defense += random.nextInt( stats.stage ) + 1;
	}
	
	@ Override
	public void requestAttack()
	{
		if ( level.canAttack( this, viewDir ) )
		{
			attacking = true;
		}
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
				attack( viewDir );
				
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