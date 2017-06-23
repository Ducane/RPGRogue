package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.gfx.util.*;
import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.List;

public enum Menu {
  Main {
    private final String[] labels = { "Inventory", "Character", "Exit" };
    
    @ Override
    protected String getLabel( final int index, final Player player ) {
      return labels[ index ];
    }
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      
      bounds = new Rectangle2D.Float( 0f, 0.3f * height, 0.2f * width, 0.4f * height );
      
      final Rectangle2D.Float singleButtonBounds = new Rectangle2D.Float(
          0.025f * width, 0.35f * height, 0.15f * width, 0.05f * height );
      
      final float buttonDY = 0.075f * height;
      
      buttonBounds = new Rectangle2D.Float[ labels.length ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
    }
    
    @ Override
    public int runCommand( final Player player ) {
      switch ( selection ) {
        case 0:
          return Inventory.ordinal() + 1;
        case 1:
          return Stats.ordinal() + 1;
        case 2:
          return -2;
      }
      
      return 0;
    }
  },
  Inventory {
    private int pageIndex;
    
    private Rectangle2D.Float statBounds;
    private Rectangle2D.Float statImageBounds;
    
    private Rectangle2D.Float pageCursorRight;
    private Rectangle2D.Float pageCursorLeft;
    
    @ Override
    protected String getLabel( final int index, final Player player ) {
      final int i = index + 8 * pageIndex;
      final LockedList<Item> inventory = player.inventory;
      
      final Item item = inventory.tryGet( i );
      
      if ( item == null ) {
        return "----------";
      }
      
      final String name = item.name;
      return name.length() > 10 ? name.substring( 0, 10 ) : name;
    }
    
    @ Override
    public Point2D.Float getOffset() {
      return new Point2D.Float( 0f, buttonBounds[ selection % 8 ].y );
    }
    
    @ Override
    public int onClick( final Point2D.Float p, final Player player ) {
      if ( pageCursorLeft.contains( p ) ) {
        pageIndex = Math.max( 0, pageIndex - 1 );
        return 0;
      } else if ( pageCursorRight.contains( p ) ) {
        final LockedList<Item> inventory = player.inventory;
        pageIndex = Math.min( pageIndex + 1, ( inventory.size() - 1 ) / 8 );
        return 0;
      }
      
      return super.onClick( p, player );
    }
    
    @ Override
    public boolean onHover( final Point2D.Float p ) {
      if ( super.onHover( p ) ) {
        selection += pageIndex * 8;
        return true;
      } else {
        return false;
      }
    }
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      
      bounds = new Rectangle2D.Float( 0.25f * width, 0f, 0.25f * width, 0.7f * height );
      
      final Rectangle2D.Float singleButtonBounds = new Rectangle2D.Float(
          0.3f * width, 0.01f * height, 0.15f * width, 0.075f * height );
      
      final float buttonDY = 0.075f * height;
      
      buttonBounds = new Rectangle2D.Float[ 8 ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
      
      statBounds = new Rectangle2D.Float( 0f, 0f, 0.2f * width, 0.25f * height );
      statImageBounds = new Rectangle2D.Float(
          0.055f * width, 0.045f * height, 0.09f * width, 0.16f * height );
      
      pageCursorLeft = new Rectangle2D.Float(
          bounds.x + ( 0.1f * bounds.width ),
          bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
      pageCursorRight = new Rectangle2D.Float(
          bounds.x + ( 0.9f * bounds.width ),
          bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
    }
    
    @ Override
    public void render( final Graphics2D g, final Player player ) {
      super.render( g, player );
      
      final LockedList<Item> inventory = player.inventory;
      
      final FontMetrics fm = g.getFontMetrics();
      
      final String fold = ( pageIndex + 1 ) + "/" + ( ( inventory.size() - 1 ) / 8 + 1 );
      g.setColor( Color.WHITE );
      g.drawString( fold,
          bounds.x + ( bounds.width - fm.stringWidth( fold ) ) * 0.5f,
          bounds.y + 0.95f * bounds.height );
      fillRect( g, pageCursorLeft );
      fillRect( g, pageCursorRight );
      
      final Item item = inventory.tryGet( selection );
      
      if ( item != null ) {
        g.setColor( Color.BLACK );
        fillRect( g, statBounds );
        g.setStroke( stroke );
        g.setColor( Color.WHITE );
        drawRect( g, statBounds.x, bounds.y, statBounds.width, statBounds.height );
        drawImage( g, item.image, statImageBounds );
      }
    }
    
    @ Override
    protected int runCommand( final Player player ) {
      final LockedList<Item> inventory = player.inventory;
      
      if ( selection < inventory.size() ) {
        return ItemSelect.ordinal() + 1;
      }
      
      return 0;
    }
  },
  Stats {
    private final String[] labels = { "Weapon", "Armor", "Accessoire" };
    
    private Rectangle2D.Float statBounds;
    
    @ Override
    protected String getLabel( final int index, final Player player ) {
      return null;
    }
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      
      bounds = new Rectangle2D.Float( 0.25f * width, 0f, 0.5f * width, 0.7f * height );
      
      statBounds = new Rectangle2D.Float(
          0.3f * width, 0.0525f * height, 0.15f * width, 0.56f * height );
      
      final Rectangle2D.Float singleButtonBounds = new Rectangle2D.Float(
          0.65f * width, 0.0525f * height, 0.05f * width, 0.05f * width );
      
      final float buttonDY = 0.21f * height;
      
      buttonBounds = new Rectangle2D.Float[ 3 ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
    }
    
    @ Override
    public void render( final Graphics2D g, final Player player ) {
      super.render( g, player );
      
      final Equipment equipment = player.equipment;
      final Item[] items = {
          equipment.getWeapon(),
          equipment.getArmor(),
          equipment.getAccessoire()
      };
      
      final Stats stats = player.getStats();
      
      final Stats stats0 = new Stats();
      equipment.applyTo( stats0 );
      
      final String[] statStrings = {
          "Stats:",
          "Level: " + stats.level(),
          "HP: " + stats.hp + "/" + stats.maxHp + "(+" + stats0.hp + ")",
          "ATK: " + stats.attack + "(+" + stats0.attack + ")",
          "DEF: " + stats.defense + "(+" + stats0.defense + ")", "EXP: " + stats.exp,
          "REXP: " + stats.remExp()
      };
      
      final FontMetrics fm = g.getFontMetrics();
      g.setColor( Color.WHITE );
      
      for ( int i = 0; i < statStrings.length; i++ ) {
        final String statString = statStrings[ i ];
        
        g.drawString( statString,
            statBounds.x + ( statBounds.width - fm.stringWidth( statString ) ) * 0.5f,
            statBounds.y + ( i * 2f + 1 ) * fm.getAscent() );
      }
      
      g.drawString( "Name: " + player.name,
          bounds.x + ( bounds.width - fm.stringWidth( "Name: " + player.name ) ) * 0.5f,
          statBounds.y + statBounds.height + fm.getAscent() );
      
      for ( int i = 0; i < labels.length; i++ ) {
        if ( items[ i ] == null ) {
          final BufferedImage icon = ImageUtil.loadImage(
              "menu/character/" + labels[ i ] + "-Icon.png" );
          drawImage( g, icon, buttonBounds[ i ] );
        } else {
          final BufferedImage frame = ImageUtil.loadImage(
              "menu/character/icon.png" );
          final BufferedImage icon = items[ i ].image;
          drawImage( g, frame, buttonBounds[ i ] );
          drawImage( g, icon, buttonBounds[ i ] );
        }
      }
      
      for ( int i = 0; i < items.length; i++ ) {
        final Item item = items[ i ];
        
        if ( item == null ) {
          continue;
        }
        
        final Rectangle2D.Float rect = buttonBounds[ i ];
        
        drawImage( g, item.image, rect );
        
        final String name = item.name;
        final String trimName = name.length() > 10
            ? name.substring( 0, 10 ) : name;
        
        g.drawString( trimName,
            rect.x - fm.stringWidth( trimName ) - rect.width * 0.5f,
            rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
      }
    }
    
    @ Override
    protected int runCommand( final Player player ) {
      final Equipment equipment = player.equipment;
      final Item[] items = {
          equipment.getWeapon(),
          equipment.getArmor(),
          equipment.getAccessoire()
      };
      
      if ( items[ selection ] == null ) {
        return 0;
      }
      
      final LockedList<Item> inventory = player.inventory;
      
      switch ( selection ) {
        case 0:
          inventory.add( equipment.setWeapon( null ) );
          break;
        case 1:
          inventory.add( equipment.setArmor( null ) );
          break;
        case 2:
          inventory.add( equipment.setAccessoire( null ) );
          break;
      }
      
      return 0;
    }
  },
  ItemSelect {
    @ Override
    protected String getLabel( final int index, final Player player ) {
      final LockedList<Item> inventory = player.inventory;
      final Item item = inventory.tryGet( Inventory.selection );
      
      if ( item == null ) {
        return null;
      }
      
      switch ( index ) {
        case 0:
          return item instanceof Food ? "Eat" : "Equip";
        case 1:
          return "Throw";
        case 2:
          return "Description";
      }
      
      return null;
    }
    
    @ Override
    public Point2D.Float getOffset() {
      final int selection = Inventory.selection % 8;
      return new Point2D.Float( 0f, ( selection > 3 ? -1.4f : 1f ) * bounds.height );
    }
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      bounds = new Rectangle2D.Float( 0.55f * width, 0f, 0.2f * width, 0.25f * height );
      
      final Rectangle2D.Float singleButtonBounds = new Rectangle2D.Float(
          0.575f * width, 0.025f * height, 0.15f * width, 0.05f * height );
      
      final float buttonDY = 0.075f * height;
      
      buttonBounds = new Rectangle2D.Float[ 3 ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
    }
    
    @ Override
    public int runCommand( final Player player ) {
      final LockedList<Item> inventory = player.inventory;
      final Item item = inventory.get( Inventory.selection );
      
      switch ( selection ) {
        case 0:
          player.equip( Inventory.selection );
          return -1;
        case 1:
          inventory.remove( item );
          return -1;
        case 2:
          return Description.ordinal() + 1;
      }
      
      return 0;
    }
  },
  Description {
    @ Override
    protected String getLabel( final int index, final Player player ) {
      return null;
    }
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      bounds = new Rectangle2D.Float(
          0.55f * width, 0.05f * height, 0.35f * width, 0.25f * height );
      
      buttonBounds = new Rectangle2D.Float[ 0 ];
    }
    
    @ Override
    public void render( final Graphics2D g, final Player player ) {
      super.render( g, player );
      
      final LockedList<Item> inventory = player.inventory;
      final Item item = inventory.get( Inventory.selection );
      
      final FontMetrics fm = g.getFontMetrics();
      
      g.setColor( Color.RED );
      g.drawString( item.name,
          bounds.x + bounds.width * 0.05f,
          bounds.y + bounds.height * 0.075f + fm.getAscent() );
      
      final List<String> lines = wrapLines( item.description, fm,
          (int) ( bounds.width - stroke.getLineWidth() ) );
      
      g.setColor( Color.WHITE );
      
      for ( int i = 0; i < lines.size(); i++ ) {
        g.drawString( lines.get( i ),
            bounds.x + bounds.width * 0.05f,
            bounds.y + bounds.height * 0.45f + fm.getAscent() * i );
      }
    }
    
    @ Override
    protected int runCommand( final Player player ) {
      return 0;
    }
  };
  
  protected int selection;
  protected Rectangle2D.Float[] buttonBounds;
  
  protected BasicStroke stroke;
  protected Rectangle2D.Float bounds;
  
  protected abstract String getLabel( int index, Player player );
  
  public Point2D.Float getOffset() {
    return new Point2D.Float();
  }
  
  public int onClick( final Point2D.Float p, final Player player ) {
    for ( final Rectangle2D.Float bounds : buttonBounds ) {
      if ( bounds.contains( p ) ) {
        return runCommand( player );
      }
    }
    
    return 0;
  }
  
  public boolean onHover( final Point2D.Float p ) {
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      if ( buttonBounds[ i ].contains( p ) ) {
        selection = i;
        return true;
      }
    }
    
    return false;
  }
  
  public abstract void onResized( int width, int height );
  
  public void render( final Graphics2D g, final Player player ) {
    g.setColor( Color.BLACK );
    fillRect( g, bounds );
    g.setStroke( stroke );
    g.setColor( Color.WHITE );
    drawRect( g, bounds );
    
    final FontMetrics fm = g.getFontMetrics();
    
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      final String label = getLabel( i, player );
      
      if ( label == null ) {
        continue;
      }
      
      final Rectangle2D.Float rect = buttonBounds[ i ];
      g.setColor( i == selection ? Color.YELLOW : Color.WHITE );
      g.drawString( label,
          rect.x + ( rect.width - fm.stringWidth( label ) ) * 0.5f,
          rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
    }
  }
  
  protected abstract int runCommand( Player player );
}