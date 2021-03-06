package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.androbin.math.util.floats.FloatMathUtil.*;
import static de.ducane.roguelike.Configuration.gui_.intro_.*;
import de.androbin.gfx.util.*;
import de.androbin.screen.*;
import de.androbin.screen.transit.*;
import de.androbin.shell.*;
import de.androbin.shell.gfx.*;
import de.androbin.shell.input.*;
import de.ducane.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

public final class IntroScreen extends AbstractShell implements AWTGraphics {
  private final SmoothScreenManager<AWTTransition> screens;
  
  private final BufferedImage[] images;
  
  private Font font;
  
  private float padding;
  
  private int pageIndex;
  private float charProgress;
  
  private final float charSpeed;
  
  private boolean stepwiseOutput;
  private boolean imageShown;
  
  public IntroScreen( final SmoothScreenManager<AWTTransition> screens ) {
    this.screens = screens;
    
    getInputs().keyboard = new IntroKeyInput();
    
    charSpeed = TEXT_SPEED;
    stepwiseOutput = true;
    
    images = new BufferedImage[ 4 ];
    
    for ( int i = 0; i < images.length; i++ ) {
      images[ i ] = ImageUtil.loadImage( "intro/intro" + i + ".png" );
    }
  }
  
  @ Override
  protected void onResized( final int width, final int height ) {
    padding = TEXT_DX * width;
    
    font = new Font( TEXT_FONT_NAME, TEXT_FONT_STYLE, (int) ( TEXT_FONT_SIZE * height ) );
  }
  
  @ Override
  public void onResumed() {
    super.onResumed();
    
    pageIndex = 0;
    charProgress = 0f;
    stepwiseOutput = true;
    imageShown = false;
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    g.setColor( BACKGROUND_COLOR );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setColor( FONT_COLOR );
    g.setFont( font );
    
    final FontMetrics fm = g.getFontMetrics();
    final List<String> list = new ArrayList<>();
    
    final String page = TEXT[ pageIndex ];
    final String[] lines = page.split( "\n" );
    
    for ( int i = 0; i < lines.length; i++ ) {
      list.addAll( AWTUtil.wrapLines( lines[ i ], fm, getWidth() - (int) ( 2f * padding ) ) );
    }
    
    float y = getHeight() - fm.getHeight() * list.size() + fm.getAscent() - padding;
    
    int charsLeft = (int) charProgress;
    
    for ( int i = 0; i < list.size(); i++ ) {
      final String line = list.get( i );
      
      if ( stepwiseOutput ) {
        if ( charsLeft >= line.length() ) {
          g.drawString( line, padding, y );
          charsLeft -= line.length();
          
          if ( i == list.size() - 1 ) {
            stepwiseOutput = false;
          }
        } else {
          g.drawString( line.substring( 0, charsLeft ), padding, y );
          break;
        }
      } else {
        charProgress = TEXT[ pageIndex ].length();
        g.drawString( line, padding, y );
      }
      
      y += fm.getHeight();
    }
    
    drawImage( g, images[ pageIndex ],
        getWidth() * 0.1f, getHeight() * 0.1f,
        getWidth() * 0.8f, getHeight() * 0.6f );
    
    if ( !imageShown ) {
      final float alpha = inter( 1f, charProgress / TEXT[ pageIndex ].length(), 0f );
      g.setColor( new Color( 0f, 0f, 0f, alpha ) );
      fillRect( g, getWidth() * 0.1f, getHeight() * 0.1f, getWidth() * 0.8f,
          getHeight() * 0.6f );
    }
  }
  
  @ Override
  public void update( final float delta ) {
    charProgress += delta * charSpeed;
    
    if ( charProgress >= TEXT[ pageIndex ].length() ) {
      imageShown = true;
    }
  }
  
  private final class IntroKeyInput implements KeyInput {
    @ Override
    public void keyReleased( final int keycode ) {
      switch ( keycode ) {
        case KeyEvent.VK_SPACE:
        case KeyEvent.VK_ENTER: {
          stepwiseOutput ^= true;
          
          if ( stepwiseOutput ) {
            if ( pageIndex < TEXT.length - 1 ) {
              pageIndex++;
              charProgress = 0f;
              imageShown = false;
            } else {
              screens.fadeCall( new MenuScreen( screens ),
                  new AWTColorCrossfade( Color.BLACK, 0.5f, 1f ) );
            }
          } else {
            imageShown = true;
          }
          
          break;
        }
      }
    }
  }
}