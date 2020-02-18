import java.awt.Color;
import javalib.worldimages.*;

// represents a tile in game
interface ITile {
  Util util = new Util();
  int units = 40; // Constant

  // returns a tile based on a click behavior
  ITile click(boolean isLeft);

  // draws this tile
  WorldImage draw();

  // does the given position collide with this tile
  boolean collidesWith(Posn collidePos);

  // accepts a function of tiles
  <T> T accept(IFuncTile<T> func);
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

  //does the given position collide with this tile
  public boolean collidesWith(Posn collidePos) {
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

  /* Template:
   * Same as abstract class
   */

  // constructs a dandelion at a given position
  Dandelions(Posn gridLocation) {
    super(gridLocation);
  }

  // draws this tile 
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, Color.YELLOW));
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
  public boolean collidesWith(Posn collidePos) {
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

// represents a pebbles tile
class Pebbles extends ATile {

  /* Template:
   * Same as abstract class
   */

  // constructs a Pebbles at a given grid location
  Pebbles(Posn gridLocation) {
    super(gridLocation);
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


