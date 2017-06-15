package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.List;

public enum Menu {
  Main {
    private final String[] labels = { "Inventar", "Beenden" };
    
    private Rectangle2D.Float bounds;
    private Stroke stroke;
    
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
    public void render( final Graphics2D g, final PlayScreen screen ) {
      g.setColor( Color.BLACK );
      fillRect( g, bounds );
      g.setStroke( stroke );
      g.setColor( Color.WHITE );
      drawRect( g, bounds );
      
      final FontMetrics fm = g.getFontMetrics();
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        final Rectangle2D.Float rect = buttonBounds[ i ];
        g.setColor( i == selection ? Color.YELLOW : Color.WHITE );
        g.drawString( labels[ i ],
            rect.x + ( rect.width - fm.stringWidth( labels[ i ] ) ) * 0.5f,
            rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
      }
    }
    
    @ Override
    public int runCommand( final PlayScreen screen ) {
      switch ( selection ) {
        case 0:
          return 1;
        case 1:
          return -2;
      }
      
      return 0;
    }
  },
  Inventory {
    private int pageIndex;
    
    private Stroke stroke;
    
    private Rectangle2D.Float bounds;
    
    private Rectangle2D.Float statBounds;
    private Rectangle2D.Float statImageBounds;
    
    private Rectangle2D.Float pageCursorRight;
    private Rectangle2D.Float pageCursorLeft;
    
    @ Override
    public Point2D.Float getOffset() {
      return new Point2D.Float( 0f, buttonBounds[ selection % 8 ].y );
    }
    
    @ Override
    public int onClick( final Point2D.Float p, final PlayScreen screen ) {
      for ( final Rectangle2D.Float bounds : buttonBounds ) {
        if ( bounds.contains( p ) ) {
          return runCommand( screen );
        }
      }
      
      if ( pageCursorLeft.contains( p ) ) {
        pageIndex = Math.max( 0, pageIndex - 1 );
      } else if ( pageCursorRight.contains( p ) ) {
        final List<Item> inventory = screen.getPlayer().getInventory();
        pageIndex = Math.min( pageIndex + 1, ( inventory.size() - 1 ) / 8 );
      }
      
      return 0;
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
          0.3f * width, 0f * height, 0.15f * width, 0.075f * height );
      
      final float buttonDY = 0.075f * height;
      
      buttonBounds = new Rectangle2D.Float[ 8 ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
      
      statBounds = new Rectangle2D.Float( 0f, 0f, 0.2f * width, 0.25f * height );
      statImageBounds = new Rectangle2D.Float(
          0.05f * width, 0.05f * height, 0.1f * width, 0.15f * height );
      
      pageCursorLeft = new Rectangle2D.Float(
          bounds.x + ( 0.1f * bounds.width ),
          bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
      pageCursorRight = new Rectangle2D.Float(
          bounds.x + ( 0.9f * bounds.width ),
          bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
    }
    
    @ Override
    public void render( final Graphics2D g, final PlayScreen screen ) {
      g.setColor( Color.BLACK );
      fillRect( g, bounds );
      g.setStroke( stroke );
      g.setColor( Color.WHITE );
      drawRect( g, bounds );
      
      final List<Item> inventory = screen.getPlayer().getInventory();
      
      final FontMetrics fm = g.getFontMetrics();
      
      for ( int i = pageIndex * 8; i < ( pageIndex + 1 ) * 8; i++ ) {
        final Rectangle2D.Float rect = buttonBounds[ i % 8 ];
        
        if ( i < inventory.size() ) {
          final String itemName = inventory.get( i ).name;
          final String trimItemName = itemName.length() > 10
              ? itemName.substring( 0, 10 ) : itemName;
          g.setColor( i == selection ? Color.YELLOW : Color.WHITE );
          g.drawString( trimItemName,
              rect.x + ( rect.width - fm.stringWidth( trimItemName ) ) * 0.5f,
              rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
        } else {
          final String nothing = "----------";
          g.setColor( Color.WHITE );
          g.drawString( nothing,
              rect.x + ( rect.width - fm.stringWidth( nothing ) ) * 0.5f,
              rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
        }
      }
      
      final String fold = ( pageIndex + 1 ) + "/" + ( ( inventory.size() - 1 ) / 8 + 1 );
      g.setColor( Color.WHITE );
      g.drawString( fold,
          bounds.x + ( bounds.width - fm.stringWidth( fold ) ) * 0.5f,
          bounds.y + 0.95f * bounds.height );
      fillRect( g, pageCursorLeft );
      fillRect( g, pageCursorRight );
      
      if ( selection < inventory.size() ) {
        g.setColor( Color.BLACK );
        fillRect( g, statBounds );
        g.setStroke( stroke );
        g.setColor( Color.WHITE );
        drawRect( g, statBounds.x, bounds.y, statBounds.width, statBounds.height );
      }
      
      if ( inventory.size() > selection ) {
        final BufferedImage image = inventory.get( selection ).image;
        drawImage( g, image, statImageBounds );
      }
    }
    
    @ Override
    protected int runCommand( final PlayScreen screen ) {
      final Player player = screen.getPlayer();
      final List<Item> inventory = player.getInventory();
      
      if ( selection >= inventory.size() ) {
        return 0;
      }
      
      return 1;
    }
  },
  ItemSelect {
    private final String[] labels = new String[ 2 ];
    
    private Rectangle2D.Float bounds;
    private Stroke stroke;
    
    @ Override
    public void onResized( final int width, final int height ) {
      stroke = new BasicStroke( 0.005f * width );
      bounds = new Rectangle2D.Float( 0.55f * width, 0f, 0.2f * width, 0.2f * height );
      
      final Rectangle2D.Float singleButtonBounds = new Rectangle2D.Float(
          0.575f * width, 0.025f * height, 0.15f * width, 0.05f * height );
      
      final float buttonDY = 0.075f * height;
      
      buttonBounds = new Rectangle2D.Float[ labels.length ];
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        buttonBounds[ i ] = new Rectangle2D.Float(
            singleButtonBounds.x, singleButtonBounds.y + buttonDY * i,
            singleButtonBounds.width, singleButtonBounds.height );
      }
    }
    
    @ Override
    public void render( final Graphics2D g, final PlayScreen screen ) {
      g.setColor( Color.BLACK );
      fillRect( g, bounds.x, bounds.y,
          bounds.width, bounds.height );
      g.setStroke( stroke );
      g.setColor( Color.WHITE );
      drawRect( g, bounds.x, bounds.y,
          bounds.width, bounds.height );
      
      final List<Item> inventory = screen.getPlayer().getInventory();
      final Item item = inventory.get( Inventory.selection );
      
      if ( item instanceof Food ) {
        labels[ 0 ] = "Eat";
      } else if ( item instanceof Weapon || item instanceof Armor || item instanceof Accessoire ) {
        labels[ 0 ] = "Equip";
      }
      
      labels[ 1 ] = "Throw";
      
      final FontMetrics fm = g.getFontMetrics();
      
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        final Rectangle2D.Float bounds = buttonBounds[ i ];
        g.setColor( i == selection ? Color.YELLOW : Color.WHITE );
        g.drawString( labels[ i ],
            bounds.x + ( bounds.width - fm.stringWidth( labels[ i ] ) ) * 0.5f,
            bounds.y + ( bounds.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
      }
    }
    
    @ Override
    public int runCommand( final PlayScreen screen ) {
      final Player player = screen.getPlayer();
      final List<Item> inventory = player.getInventory();
      final Item item = inventory.get( Inventory.selection );
      
      switch ( selection ) {
        case 0:
          screen.equip( Inventory.selection );
          return -1;
        case 1:
          // TODO(Saltuk) display item info
          return 0;
        case 2:
          inventory.remove( item );
          return -1;
      }
      
      return 0;
    }
  };
  
  protected int selection;
  
  protected Rectangle2D.Float[] buttonBounds;
  
  public Point2D.Float getOffset() {
    return new Point2D.Float();
  }
  
  public int onClick( final Point2D.Float p, final PlayScreen screen ) {
    return runCommand( screen );
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
  
  public Menu next() {
    return values()[ ordinal() + 1 ];
  }
  
  public abstract void render( Graphics2D g, PlayScreen screen );
  
  protected abstract int runCommand( PlayScreen screen );
}