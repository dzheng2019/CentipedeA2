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
  
  <T> T accept(IFuncTile<T> func);
}

// represents a tile 
abstract class ATile implements ITile {
  Posn gridLocation; // x is column, y is row, where row 0 is the bottom 

  ATile(Posn gridLocation) {
    this.gridLocation = gridLocation;
  }

  /* Template:
   * Methods:
   * click(boolean isLeft) - ITile 
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
    // TODO Auto-generated method stub
    return false;
  }
  
}

// represents a grass tile
class Grass extends ATile {
  Grass(Posn gridLocation) {
    super(gridLocation);
  }

  /* Template:
   * Methods:
   * this.click(boolean isLeft) - ITile 
   * this.draw() - WorldImage
   */
  // returns a new Dandelions of Pebbles object 
  // based on isLeft
  public ITile click(boolean isLeft) {
    /* Template: 
     * Same as class template
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



  @Override
  public <T> T accept(IFuncTile<T> func) {
    // TODO Auto-generated method stub
    return func.visitGrass(this);
  }


}

// represents a dandelion tile
class Dandelions extends ATile {
  Dandelions(Posn gridLocation) {
    super(gridLocation);
    // TODO Auto-generated constructor stub
  }

  /* Template:
   * Methods:
   * this.click(boolean isLeft) - ITile 
   * this.draw() - WorldImage
   */
  // draws this tile 
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, Color.YELLOW));
  }

  @Override
  public <T> T accept(IFuncTile<T> func) {
    // TODO Auto-generated method stub
    return func.visitDandelions(this);
  }
  
  public boolean collidesWith(Posn collidePos) {
    return util.posnEqual(this.gridLocation, collidePos);
  }
}

// represents a pebbles tile
class Pebbles extends ATile {
  Pebbles(Posn gridLocation) {
    super(gridLocation);
  }

  /* Template:
   * Methods:
   * this.click(boolean isLeft) - ITile 
   * this.draw() - WorldImage
   */
  // draw this tile
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(units, units, OutlineMode.SOLID, Color.GRAY));
  }

  @Override
  public <T> T accept(IFuncTile<T> func) {
    // TODO Auto-generated method stub
    return func.visitPebbles(this);
  }
}
