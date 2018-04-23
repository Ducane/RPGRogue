package de.ducane.roguelike.level;

import de.androbin.rpg.*;
import de.androbin.rpg.tile.*;

public final class Downstairs {
  public static final Ident TYPE = Ident.fromSerial( "rogue/downstairs" );
  public static final RogueTile MOCK = (RogueTile) Tiles.create( Ident.fromSerial( "rogue/granite" ) );
}