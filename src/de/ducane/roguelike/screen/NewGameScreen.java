package de.ducane.roguelike.screen;

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
    
    inputs.keyboard = new KeyInput();
    inputs.mouse = new MouseInput();
    inputs.mouseMotion = new MouseMotionInput();
    
    name = new StringBuilder();
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    fm = game.getFontMetrics( new Font( "Determination Mono", 0, (int) ( 0.1f * getHeight() ) ) );
    
    final float buttonHeight = fm.getAscent() - fm.getLeading();
    buttonBounds = new Rectangle2D.Float(
        0.85f * getWidth(), 0.9f * getHeight() - buttonHeight,
        fm.stringWidth( "OK" ), buttonHeight );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    g.setColor( Color.BLACK );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setFont( new Font( "Determination Mono", 0, (int) ( 0.05625f * getWidth() ) ) );
    final String output = name.toString();
    
    g.setColor( Color.WHITE );
    g.drawString( "Benenne deinen Helden!",
        0.05f * getWidth(), fm.getAscent() + 0.1f * getHeight() );
    
    g.setColor( buttonSelection ? Color.YELLOW : Color.WHITE );
    g.drawString( "OK", buttonBounds.x, buttonBounds.y
        + ( buttonBounds.height - fm.getAscent() - fm.getLeading() ) * 0.5f + fm.getAscent() );
    
    g.setColor( Color.WHITE );
    g.drawString( output, ( getWidth() - fm.stringWidth( output ) ) * 0.5f, getHeight() * 0.5f );
  }
  
  private void startGame() {
    game.gsm.crossfadeSwitch( new PlayScreen( game, 48f, name.toString() ),
        ColorCrossfade.BLACK, 1f );
  }
  
  @ Override
  public void update( final float delta ) {
  }
  
  private final class KeyInput extends KeyAdapter {
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
  
  private final class MouseInput extends MouseAdapter {
    @ Override
    public void mousePressed( final MouseEvent event ) {
      if ( buttonSelection && name.length() > 0 ) {
        startGame();
      }
    }
  }
  
  private final class MouseMotionInput extends MouseAdapter {
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