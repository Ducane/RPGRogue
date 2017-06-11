package de.ducane.roguelike.screen;

// import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.game.*;
import de.androbin.gfx.transition.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class NewGameScreen extends Screen {
  private final StringBuilder name;
  private FontMetrics fm;
  
  private Rectangle2D.Float buttonBounds;
  private boolean buttonSelection;
  
  public NewGameScreen( final Game game ) {
    super( game );
    
    name = new StringBuilder();
  }
  
  private void startGame() {
    game.gsm.crossfadeSwitch( new PlayScreen( game, 48f, name.toString() ),
        ColorCrossfade.BLACK, 1f );
  }
  
  @ Override
  public KeyListener getKeyListener() {
    return new NameKeyListener();
  }
  
  @ Override
  public MouseListener getMouseListener() {
    return new NameMouseListener();
  }
  
  @ Override
  public MouseMotionListener getMouseMotionListener() {
    return new NameMouseMotionListener();
  }
  
  public String getName() {
    return name.toString();
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    fm = game.getFontMetrics( new Font( "Determination Mono", 0, (int) ( 0.1f * getHeight() ) ) );
    buttonBounds = new Rectangle2D.Float( (int) ( 0.85f * getWidth() ),
        (int) ( 0.9f * getHeight() ) - ( fm.getAscent() - fm.getLeading() ), fm.stringWidth( "OK" ),
        fm.getAscent() - fm.getLeading() );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    g.setColor( Color.BLACK );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setFont( new Font( "Determination Mono", 0, (int) ( 0.05625f * getWidth() ) ) );
    final String output = name.toString();
    
    g.setColor( Color.WHITE );
    g.drawString( "Benenne deinen Helden!", (int) ( 0.05f * getWidth() ),
        fm.getAscent() + (int) ( 0.1f * getHeight() ) );
    
    g.setColor( buttonSelection ? Color.YELLOW : Color.WHITE );
    // drawRect( g, buttonBounds );
    g.drawString( "OK", (int) buttonBounds.getX(), (int) ( buttonBounds.getY()
        + ( buttonBounds.getHeight() - fm.getAscent() - fm.getLeading() ) / 2 + fm.getAscent() ) );
    
    g.setColor( Color.WHITE );
    g.drawString( output, ( getWidth() - fm.stringWidth( output ) ) / 2, getHeight() / 2 );
  }
  
  @ Override
  public void update( final float delta ) {
  }
  
  private class NameKeyListener extends KeyAdapter {
    @ Override
    public void keyPressed( final KeyEvent event ) {
      final char c = event.getKeyChar();
      
      if ( Character.isLetterOrDigit( c ) && name.length() <= 12 ) {
        name.append( c );
      }
      
      if ( name.length() > 0 ) {
        switch ( c ) {
          case KeyEvent.VK_BACK_SPACE:
            name.deleteCharAt( name.length() - 1 );
            break;
          case KeyEvent.VK_ENTER:
            startGame();
            break;
        }
      }
    }
  }
  
  private class NameMouseListener extends MouseAdapter {
    @ Override
    public void mousePressed( final MouseEvent event ) {
      if ( buttonSelection && name.length() > 0 ) {
        startGame();
      }
    }
  }
  
  private class NameMouseMotionListener extends MouseAdapter {
    @ Override
    public void mouseMoved( final MouseEvent event ) {
      if ( buttonBounds.contains( event.getPoint() ) ) {
        buttonSelection = true;
      } else {
        buttonSelection = false;
      }
    }
  }
}