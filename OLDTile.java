import java.awt.Color;
import java.util.ArrayList;

import javalib.worldimages.*;

// represents a tile in game
interface ITile {
  Util util = new Util();
  int units = 40; // Constant cell width 

  // returns a tile based on a click behavior
  ITile click(boolean isLeft);

  // draws this tile
  WorldImage draw();

  // does the given position collide with this tile
  boolean collidesWithSolid(Posn collidePos);

  boolean collidesWithNonSolid(Posn collidePos);

  // accepts a function of tiles
  <T> T accept(IFuncTile<T> func);

  // lowers the hp of tile by a given amount
  ITile lowerHp(int amount);

  ArrayList<Posn> pebbleExplosionTiles();

}

// represents a tile 
abstract class ATile implements ITile {
  Posn gridLocation; // x is column, y is row, where row 0 is the bottom 

  ATile(Posn gridLocation) {
    this.gridLocation = gridLocation;
  }

  /* Template:
   * Fields:
   * this.gridLocation - Posn
   * Fields of Fields:
   * this.gridLocation.x - int
   * this.gridLocation.y - int
   * Methods:
   * this.click(boolean isLeft) - ITile 
   * this.draw() - WorldImage
   * this.collidesWith(Posn collidePos) - boolean
   * this.accept(IFuncTile<T> func) - T
   */

  // returns a new Grass tile or itself based
  // on isLeft
  public ITile click(boolean isLeft) {
    /* Template:
     * Same as class template
     */
    if (isLeft) {
      return new Grass(this.gridLocation);
    }
    return this;
  }

  //does the given position collide with this solid tile
  public boolean collidesWithSolid(Posn collidePos) {
    /* Template:
     * Same as class template
     * Parameter:
     * collidePosn - Posn
     * Fields of Parameter:
     * collidePosn.x - int
     * collidePons.y - int
     */
    return false;
  }

  //does the given position collide with this tile
  public boolean collidesWithNonSolid(Posn collidePos) {
    /* Template:
     * Same as class template
     * Parameter:
     * collidePosn - Posn
     * Fields of Parameter:
     * collidePosn.x - int
     * collidePons.y - int
     */
    return false;
  }

  public ITile lowerHp(int amount) {
    return this;
  }

  public ArrayList<Posn> pebbleExplosionTiles() {
    return new ArrayList<Posn>();
  }

}

// represents a grass tile
class Grass extends ATile {

  /* Template:
   * Same as abstract class
   */

  // constructs a grass at the given location
  Grass(Posn gridLocation) {
    super(gridLocation);
  }

  // returns a new Dandelions of Pebbles object 
  // based on isLeft
  public ITile click(boolean isLeft) {
    /* Template: 
     * Same as class template
     * Parameter:
     * isLeft - boolean
     */
    if (isLeft) {
      return new Dandelions(this.gridLocation);
    }
    else {
      return new Pebbles(this.gridLocation);
    }
  }

  // draws this tile 
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, Color.GREEN));
  }



  // accepts a function of type tile
  public <T> T accept(IFuncTile<T> func) {
    /* Template:
     * Same as class template
     * Parameter: 
     * func - IFuncTile<T>
     * Methods on parameter:
     * func.apply(ITile tile) - T
     * func.visitGrass(Grass grass) - T
     * func.visitDandelion(Dandelion dande) - T
     * func.visitPebble(Pebble pebb) - T
     */
    return func.visitGrass(this);
  }

}

// represents a dandelion tile
class Dandelions extends ATile {

  int hp;

  /* Template:
   * Same as abstract class
   */

  Dandelions(Posn gridLocation, int hp) {
    super(gridLocation);
    this.hp = hp;
  }

  // constructs a dandelion at a given position and full hp
  Dandelions(Posn gridLocation) {
    super(gridLocation);
    this.hp = 3;
  }
  
  // changes the hp of this dandelion by the given amount
  Dandelions changeHp(int changeHealthAmt) {
    return new Dandelions(this.gridLocation, this.hp + changeHealthAmt);
  }

  // draws this tile 
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */

    Color dandelionColor;    
    if (this.hp == 1) {
      dandelionColor = new Color(201, 192, 12); // darkest yellow
    }

    else if (this.hp == 2) {
      dandelionColor = new Color(227, 216, 14); //darker yellow 
    }

    else {
      dandelionColor = new Color(250, 238, 12); //bright yellow
    }

    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, dandelionColor));
  }

  // accepts a function of type tile
  public <T> T accept(IFuncTile<T> func) {
    /* Template:
     * Same as class template
     * Parameter: 
     * func - IFuncTile<T>
     * Methods on parameter:
     * func.apply(ITile tile) - T
     * func.visitGrass(Grass grass) - T
     * func.visitDandelion(Dandelion dande) - T
     * func.visitPebble(Pebble pebb) - T
     */
    return func.visitDandelions(this);
  }

  // determines if a given grid position is the same as the
  // grid position of this Dandelion
  public boolean collidesWithSolid(Posn collidePos) {
    /* Template:
     * Same as class template
     * Parameter:
     * collidePosn - Posn
     * Fields of Parameter:
     * collidePosn.x - int
     * collidePons.y - int
     */
    return util.posnEqual(this.gridLocation, collidePos);
  }

  //  public ArrayList<Posn> pebbleExplosionTiles() {
  //    ArrayList<Posn> explodePosns = new ArrayList<Posn>();
  //    if (this.hp <= 0) {
  //      explodePosns.add(new Posn(this.gridLocation.x, 5));
  //    }
  //    return null;
  //  }

}

// represents a pebbles tile
class Pebbles extends ATile {

  /* Template:
   * Same as abstract class
   */

  boolean partOfPile;
  
  // constructs a Pebbles at a given grid location that may or may not be part of a pile
  Pebbles(Posn gridLocation, boolean partOfPile) {
    super(gridLocation);
    this.partOfPile = partOfPile;
  }
  
  // constructs a Pebble at a give grid location, assumed to not be part of a pile
  Pebbles(Posn gridLocation) {
    this(gridLocation, false);
  }
  
  // draw this tile
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, Color.GRAY));
  }

  // accepts a function of type tile
  public <T> T accept(IFuncTile<T> func) {
    /* Template:
     * Same as class template
     * Parameter: 
     * func - IFuncTile<T>
     * Methods on parameter:
     * func.apply(ITile tile) - T
     * func.visitGrass(Grass grass) - T
     * func.visitDandelion(Dandelion dande) - T
     * func.visitPebble(Pebble pebb) - T
     */
    return func.visitPebbles(this);
  }

  // determines if a given grid position is the same as the
  // grid position of this Dandelion
  public boolean collidesWithNonSolid(Posn collidePos) {
    /* Template:
     * Same as class template
     * Parameter:
     * collidePosn - Posn
     * Fields of Parameter:
     * collidePosn.x - int
     * collidePons.y - int
     */
    return util.posnEqual(this.gridLocation, collidePos);
  }
}

// created for test purposes only 
// returns 0 when visiting a grass
// returns 1 when visiting a dandelion
// returns 2 when visiting a pebble
class MakeNumber implements IFuncTile<Integer> {

  public Integer apply(ITile arg) {
    return arg.accept(this);
  }

  public Integer visitGrass(Grass grass) {
    return 0;
  }

  public Integer visitDandelions(Dandelions dande) {
    return 1;
  }

  public Integer visitPebbles(Pebbles pebb) {
    return 2;
  }
}


