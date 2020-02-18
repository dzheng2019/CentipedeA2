import javalib.funworld.WorldScene;
import java.util.Random;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.*;
import tester.Tester;
class Board {
  IList<IList<ITile>> gameBoard;
  int row;
  int col;
  Util u = new Util();
  int units = 40;

  /* Template:
   * Fields:
   * this.gameBoard - IList<IList<ITile>> 
   * this.row - int
   * this.col - int
   * this.u - Util
   * this.units - int
   * Methods:
   * this.makeBoard(int row, int col) - IList<IList<ITile>>
   * this.makeRow(int row, int col) - IList<ITile>
   * this.draw() - WorldImage
   * this.drawOnBoard() - WorldScene 
   * this.collisionOccursLeft(Posn location, int size) - boolean
   * this.collisionOccursRight(Posn location, int size) - boolean
   * this.collisionOccurs(Posn location, int size) - boolean
   * this.produceDart(Gnome gnome, int speed) - Dart
   * this.randomBoard(boolean isPebbles, int num, Random rand) - Board
   * this.changeAtLocation(Posn location, boolean createPebbles) - Board
   * this.clickAtLocation(Posn location, boolean isLeft) - Board
   * Methods on Fields:
   * this.gameBoard.accept(IListVisitor<IList<ITIle>, R> visitor) - R
   * this.gameBoard.length() - int
   * u.convertAbsoluteToGrid(Posn location, int unit, int row) - Posn
   * u.flipX(Posn toFlip) - Posn
   * u.moveInDirection(Posn location, Posn direciton, int speed) - Posn
   * u.posnEqual(Posn p1, Posn p2, int radius) - boolean
   * u.posnInRadius(Posn p1, Posn p2, int radius) - boolean
   * u.createRandomInts(int num, int max, Random rand) - IList<Integer>
   * u.getCenter(Posn pos, int unit) - Posn 
   */

  Board(IList<IList<ITile>> gameBoard) {
    this.row = gameBoard.length();
    this.col = new RowLength<ITile>().apply(gameBoard);
    this.gameBoard = gameBoard;
  }

  Board(int row, int col) {
    this.row = row;
    this.col = col;
    this.gameBoard = this.makeBoard(row - 1, col - 1);
  }

  // makes a board with the dimensions row + 1, col + 1
  IList<IList<ITile>> makeBoard(int row, int col) {
    /* Template:
     * Same as class template:
     * Parameter:
     * row - int 
     * col - int
     */
    if (row < 0) {
      return new MtList<IList<ITile>>();
    }
    else {
      return new ConsList<IList<ITile>>(this.makeRow(this.row - 1 - row, col), this.makeBoard(row - 1, col));
    }
  }

  // makes a row 
  // row is constant and not recursed upon
  IList<ITile> makeRow(int row, int col) {
    /* Template:
     * Same as class template:
     * Parameter:
     * row - int 
     * col - int
     */
    if (col < 0) {
      return new MtList<ITile>();
    }
    else {
      return new ConsList<ITile>(new Grass(new Posn(this.col - 1 - col, row)), this.makeRow(row, col - 1));
    }
  }

  // draws a board
  WorldImage draw() {
    return new DrawBoard().apply(this.gameBoard);
  }

  // draws a board on a scene
  WorldScene drawOnBoard(WorldScene scene) {
    /* Template:
     * Same as class template
     * Parameter:
     * scene - WorldScene
     * Methods on Parameter:
     * scene.placeImageXY(WorldImage, int x, int y);
     */
    return  scene.placeImageXY(this.draw(), this.col * this.units / 2 , this.row * this.units / 2);
  }

  // determines if a collisions occurs on the left of a pos 
  boolean collisionOccursLeft(Posn collidePos, int size) {
    /* Template:
     * Same as class
     * Parameter:
     * collidPos - Posn
     * size - int
     * Fields of parameters
     * collidePos.x - int
     * collidePos.y - int
     */

    Posn collideGridLocLeft = u.convertAbsoluteToGrid(new Posn(collidePos.x - size, collidePos.y), this.units, this.row);

    boolean tileCollisionLeft = 
        new OrMap<IList<ITile>>(
            new OrMapListPred<ITile>(
                new TileCollision(collideGridLocLeft))).apply(this.gameBoard);


    boolean xCollision = collidePos.x - size < 0;

    return tileCollisionLeft || xCollision ;
  }

  // determines if a collisions occurs on the right of a pos
  boolean collisionOccursRight(Posn collidePos, int size) {
    /* Template:
     * Same as class
     * Parameter:
     * collidPos - Posn
     * size - int
     * Fields of parameters
     * collidePos.x - int
     * collidePos.y - int
     */

    Posn collideGridLocRight = u.convertAbsoluteToGrid(new Posn(collidePos.x + size, collidePos.y), this.units, this.row);

    boolean tileCollisionRight = 
        new OrMap<IList<ITile>>(
            new OrMapListPred<ITile>(
                new TileCollision(collideGridLocRight))).apply(this.gameBoard);

    boolean xCollision = collidePos.x + size>= this.col * this.units;

    return tileCollisionRight || xCollision;
  }

  // determines if collision has a occurred at a pos
  boolean collisionOccurs(Posn collidePos, int size) {
    /* Template:
     * Same as class
     * Parameter:
     * collidPos - Posn
     * size - int
     * Fields of parameters
     * collidePos.x - int
     * collidePos.y - int
     */

    boolean yCollision = collidePos.y - size < 0 || collidePos.y + size > this.row  * this.units;

    return yCollision || 
        this.collisionOccursRight(collidePos, size) ||
        this.collisionOccursLeft(collidePos, size);
  }

  // produces a dart at the player's grid location
  Dart produceDart(Gnome player, int speed) {
    /* Template:
     * Same as class template
     * Parameter:
     * player - Gnome
     * speed - int
     * Methods on parameter:
     * player.getGrid(int unit, int row) - Posn
     */
    Posn location = player.getGrid(this.units, this.row);
    // makes a dart at the middle of the player location 
    return new Dart(new Posn(location.x * this.units + this.units / 2 , this.row * this.units - this.units / 2), speed, this.units / 2);
  }

  // generates a random board 
  Board randomBoard(boolean isPebbles, int num, Random rand) {
    /* Template:
     * Same as class template
     * Parameters:
     * isPebbles - boolean
     * num - int
     * rand - Random
     * Methods on parmeters:
     * rand.nextInt(int max) - int
     */
    IList<Integer> randomLocs =  
        new IncrementAllByX(col).apply(u.createRandomInts(
            num, row*col, rand));
    IList<IList<ITile>> newGameBoard = new Map<IList<ITile>, IList<ITile>>(
        new MapList<ITile, ITile>(
            new ChangeTilesToIf(
                randomLocs, isPebbles, this.col))).apply(
                    this.gameBoard);
    return new Board(new ChangeAtXY<ITile>(
        new ChangeToGrass(), 0, row - 1).apply(newGameBoard));
  }

  // changes a tile at a given location to either a pebble or dandelion
  Board changeAtLocation(Posn location, boolean createPebble) {
    /* Template:
     * Same as class template
     * Parameters:
     * createPebbles - boolean
     * location - Posn
     * Methods on parmeters:
     * location.x - int 
     * location.y - int
     */
    Posn gridLoc = u.convertAbsoluteToGrid(location, this.units, this.row);
    if (createPebble) {
      return new Board(new ChangeAtXY<ITile>(new ChangeToPebbles(), gridLoc.x, gridLoc.y).apply(this.gameBoard));      
    }
    else {
      return new Board(new ChangeAtXY<ITile>(new ChangeToDandelion(), gridLoc.x, gridLoc.y).apply(this.gameBoard));
    }
  }

  // clicks a tile at a given location 
  Board clickAtLocation(Posn gridLoc, boolean isLeft) {
    /* Template:
     * Same as class template
     * Parameters:
     * isLeft - boolean
     * gridLoc - Posn
     * Methods on parmeters:
     * gridLoc.x - int 
     * gridLoc.y - int
     */
    return new Board(new ChangeAtXY<ITile>(new ClickFunc(isLeft), gridLoc.x, gridLoc.y).apply(this.gameBoard));      
  }
}

// visit tiles
interface IFuncTile<T> extends IFunc<ITile, T> {

  /* Template:
   * Same as interface
   * Methods:
   * visitGrass(Grass grass) - T
   * visitDandelions(Dandelions dande) - T
   * visitPebbles(Pebbles pebb) - T
   */

  // visits grass
  T visitGrass(Grass grass);

  // visits dandelions
  T visitDandelions(Dandelions dande);

  // visits pebbles
  T visitPebbles(Pebbles pebb);
}

// clicks a tile
class ClickFunc implements IFuncTile<ITile> {

  boolean isLeft;

  /* Template:
   * Fields:
   * this.isLeft - boolean
   */

  ClickFunc(boolean isLeft) {
    this.isLeft = isLeft;
  }

  // applies this to a tile
  public ITile apply(ITile arg) {
    /* Template:
     * Same as class
     * Parameter:
     * arg - ITile
     * Methods on Parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */
    return arg.accept(this);
  }

  // clicks a grass
  public ITile visitGrass(Grass grass) {
    /* Template:
     * Same as class
     * Parameter:
     * arg - ITile
     * Methods on Parameter:
     * grass.click(boolean isLeft) - ITile 
     * grass.draw() - WorldImage
     * grass.collidesWith(Posn collidePos) - boolean
     * grass.accept(IFuncTile<T> func) - T
     */
    return grass.click(isLeft);
  }

  // clicks a dandelion
  public ITile visitDandelions(Dandelions dande) {
    /* Template:
     * Same as class
     * Parameter:
     * arg - ITile
     * Methods on Parameter:
     * dande.click(boolean isLeft) - ITile 
     * dande.draw() - WorldImage
     * dande.collidesWith(Posn collidePos) - boolean
     * dande.accept(IFuncTile<T> func) - T
     */
    return dande.click(isLeft);
  }

  // clicks a pebbles
  public ITile visitPebbles(Pebbles pebb) {
    /* Template:
     * Same as class
     * Parameter:
     * arg - ITile
     * Methods on Parameter:
     * pebb.click(boolean isLeft) - ITile 
     * pebb.draw() - WorldImage
     * pebb.collidesWith(Posn collidePos) - boolean
     * pebb.accept(IFuncTile<T> func) - T
     */
    return pebb.click(isLeft);
  }
}

// draws a board
class DrawBoard implements IListVisitor<IList<ITile>, WorldImage> {

  /* Template:
   * Same as interface
   */

  // applies this function to a 2D list 
  public WorldImage apply(IList<IList<ITile>> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }

  // visits an empty row 
  public WorldImage visitMt(MtList<IList<ITile>> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return new EmptyImage();
  }

  // visits and draws a row
  public WorldImage visitCons(ConsList<IList<ITile>> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
    IListVisitor<ITile, WorldImage> rowVisitor = new DrawRow();
    return new AboveImage(cons.rest.accept(this), rowVisitor.apply(cons.first));
  }
}

// draws a row of tiles
class DrawRow implements IListVisitor<ITile, WorldImage> {

  // applies this function to a list of tiles
  public WorldImage apply(IList<ITile> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }

  // draws an empty row
  public WorldImage visitMt(MtList<ITile> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return new EmptyImage();
  }

  // draws an non empty row
  public WorldImage visitCons(ConsList<ITile> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
    return new BesideImage(cons.first.draw(), this.apply(cons.rest));
  }
}

// checks if a position is in a tile
class TileCollision implements IPred<ITile> {
  Posn collisionPoint;

  /* Template:
   * fields; 
   * this.collisionPoint - posn
   * methods
   * this.apply(Itile arg) - boolean
   * methods on fields:
   * this.collisionPoint.x 
   * this.colliosinPoint.y
   */
  
  TileCollision(Posn collisionPoint) {
    this.collisionPoint = collisionPoint;
  }

  // checks if a given tile collides with a given point
  public Boolean apply(ITile arg) {
    /* Template:
     * Parameter:
     * arg - ITile
     * Methods on parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */
    return arg.collidesWith(this.collisionPoint);
  }
}

// change a tile to grass 
class ChangeToGrass implements IFuncTile<ITile> {

  /* Template:
   * Same as interface
   */

  // applies this function to a tile 
  public ITile apply(ITile arg) {
    /* Template:
     * Parameter:
     * arg - ITile
     * Methods on parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */
    return arg.accept(this);
  }

  // makes a grass a grass
  public ITile visitGrass(Grass grass) {
    /* Template:
     * Parameter:
     * grass - Grass
     * Methods on parameter:
     * grass.click(boolean isLeft) - ITile 
     * grass.draw() - WorldImage
     * grass.collidesWith(Posn collidePos) - boolean
     * grass.accept(IFuncTile<T> func) - T
     * grass.gridLocation - Posn
     */
    return grass;
  }

  // makes a dandelion a grass
  public ITile visitDandelions(Dandelions dande) {
    /* Template:
     * Parameter:
     * dande - Dandelions
     * Methods on parameter:
     * dande.click(boolean isLeft) - ITile 
     * dande.draw() - WorldImage
     * dande.collidesWith(Posn collidePos) - boolean
     * dande.accept(IFuncTile<T> func) - T
     * dande.gridLocation - Posn
     */
    return new Grass(dande.gridLocation);
  }

  // makes a pebbles a grass
  public ITile visitPebbles(Pebbles pebb) {
    /* Template:
     * Parameter:
     * pebb - Pebbles
     * Methods on parameter:
     * pebb.click(boolean isLeft) - ITile 
     * pebb.draw() - WorldImage
     * pebb.collidesWith(Posn collidePos) - boolean
     * pebb.accept(IFuncTile<T> func) - T
     * pebb.gridLocation - Posn
     */
    return new Grass(pebb.gridLocation);
  }

}

// changes a tile to a dandelion
class ChangeToDandelion implements IFuncTile<ITile> {

  /* Template
   * Same as interface
   */

  // applies this function to a tile 
  public ITile apply(ITile arg) {
    /* Template:
     * Parameter:
     * arg - ITile
     * Methods on parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */   
    return arg.accept(this);
  }

  // makes a grass a dandelion
  public ITile visitGrass(Grass grass) {
    /* Template:
     * Parameter:
     * grass - Grass
     * Methods on parameter:
     * grass.click(boolean isLeft) - ITile 
     * grass.draw() - WorldImage
     * grass.collidesWith(Posn collidePos) - boolean
     * grass.accept(IFuncTile<T> func) - T
     * grass.gridLocation - Posn
     */
    return new Dandelions(grass.gridLocation);
  }

  // makes a dandelion a dandelion
  public ITile visitDandelions(Dandelions dande) {
    /* Template:
     * Parameter:
     * dande - Dandelions
     * Methods on parameter:
     * dande.click(boolean isLeft) - ITile 
     * dande.draw() - WorldImage
     * dande.collidesWith(Posn collidePos) - boolean
     * dande.accept(IFuncTile<T> func) - T
     * dande.gridLocation - Posn
     */
    return dande;
  }

  // makes a pebbles a dandelion
  public ITile visitPebbles(Pebbles pebb) {
    /* Template:
     * Parameter:
     * pebb - Pebbles
     * Methods on parameter:
     * pebb.click(boolean isLeft) - ITile 
     * pebb.draw() - WorldImage
     * pebb.collidesWith(Posn collidePos) - boolean
     * pebb.accept(IFuncTile<T> func) - T
     * pebb.gridLocation - Posn
     */
    return new Dandelions(pebb.gridLocation);
  }

}

class ChangeToPebbles implements IFuncTile<ITile> {

  /* Template
   * Same as interface
   */

  // applies this function to a tile 
  public ITile apply(ITile arg) {
    /* Template:
     * Parameter:
     * arg - ITile
     * Methods on parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */   
    return arg.accept(this);
  }

  // makes this grass a pebbles
  public ITile visitGrass(Grass grass) {
    /* Template:
     * Parameter:
     * grass - Grass
     * Methods on parameter:
     * grass.click(boolean isLeft) - ITile 
     * grass.draw() - WorldImage
     * grass.collidesWith(Posn collidePos) - boolean
     * grass.accept(IFuncTile<T> func) - T
     * grass.gridLocation - Posn
     */
    return new Pebbles(grass.gridLocation);
  }

  // makes this dandelion a pebble
  public ITile visitDandelions(Dandelions dande) {
    /* Template:
     * Parameter:
     * dande - Dandelions
     * Methods on parameter:
     * dande.click(boolean isLeft) - ITile 
     * dande.draw() - WorldImage
     * dande.collidesWith(Posn collidePos) - boolean
     * dande.accept(IFuncTile<T> func) - T
     * dande.gridLocation - Posn
     */
    return new Pebbles(dande.gridLocation);
  }

  // makes this pebble a pebble
  public ITile visitPebbles(Pebbles pebb) {
    /* Template:
     * Parameter:
     * pebb - Pebbles
     * Methods on parameter:
     * pebb.click(boolean isLeft) - ITile 
     * pebb.draw() - WorldImage
     * pebb.collidesWith(Posn collidePos) - boolean
     * pebb.accept(IFuncTile<T> func) - T
     * pebb.gridLocation - Posn
     */
    return pebb;
  }
}

// changes a tile if its location is a list 
class ChangeTilesToIf implements IFuncTile<ITile> {

  IList<Integer> locs;
  boolean isPebbles;
  int cols;

  /* Template:
   * Same a interface
   * Fields:
   * locs - IList<Integer>
   * isPebbles - boolean 
   * cols - int
   */

  ChangeTilesToIf(IList<Integer> locs, boolean isPebbles, int cols) {
    this.locs = locs;
    this.isPebbles = isPebbles;
    this.cols = cols;
  }

  // applies this function to a tile
  public ITile apply(ITile arg) {
    /* Template:
     * Parameter:
     * arg - ITile
     * Methods on parameter:
     * arg.click(boolean isLeft) - ITile 
     * arg.draw() - WorldImage
     * arg.collidesWith(Posn collidePos) - boolean
     * arg.accept(IFuncTile<T> func) - T
     */ 
    return arg.accept(this);
  }

  // changes a grass to a pebble or dandelion if its location is in the list
  public ITile visitGrass(Grass grass) {
    /* Template:
     * Parameter:
     * grass - Grass
     * Methods on parameter:
     * grass.click(boolean isLeft) - ITile 
     * grass.draw() - WorldImage
     * grass.collidesWith(Posn collidePos) - boolean
     * grass.accept(IFuncTile<T> func) - T
     * grass.gridLocation - Posn
     */
    if (containsLoc(grass.gridLocation)) {
      return this.pebbleOrDande(isPebbles, grass.gridLocation);
    } 
    else {
      return grass;
    }
  }

  // changes a dandelion to a pebble or dandelion if its location is in the list
  public ITile visitDandelions(Dandelions dande) {
    /* Template:
     * Parameter:
     * dande - Dandelions
     * Methods on parameter:
     * dande.click(boolean isLeft) - ITile 
     * dande.draw() - WorldImage
     * dande.collidesWith(Posn collidePos) - boolean
     * dande.accept(IFuncTile<T> func) - T
     * dande.gridLocation - Posn
     */
    if (containsLoc(dande.gridLocation)) {
      return this.pebbleOrDande(isPebbles, dande.gridLocation);
    } 
    else {
      return dande;
    }
  }

  // changes a pebbles to a pebble or dandelion if its location is in the list
  public ITile visitPebbles(Pebbles pebb) {
    /* Template:
     * Parameter:
     * pebb - Pebbles
     * Methods on parameter:
     * pebb.click(boolean isLeft) - ITile 
     * pebb.draw() - WorldImage
     * pebb.collidesWith(Posn collidePos) - boolean
     * pebb.accept(IFuncTile<T> func) - T
     * pebb.gridLocation - Posn
     */
    if (containsLoc(pebb.gridLocation)) {
      return this.pebbleOrDande(isPebbles, pebb.gridLocation);
    } 
    else {
      return pebb;
    }
  }

  // determines if a list of ints contains a given loc converted to an int
  boolean containsLoc(Posn location) {
    /* Template:
     * Same as class
     * Parameter:
     * location - posn
     * fields of parameters:
     * location.x - int
     * locaiton.y - int
     */
    return new OrMap<Integer>(
        new ContainsInt(
            (location.y * this.cols) + location.x)).apply(locs);
  }

  // makes a pebble or a dandelion at a location 
  ITile pebbleOrDande(boolean isPebbles, Posn location) {
    /* Template:
     * Same as class
     * Parameter:
     * location - posn
     * isPebbles - boolean
     * fields of parameters:
     * location.x - int
     * locaiton.y - int
     */
    if (isPebbles) {
      return new Pebbles(new Posn(location.x, location.y));
    } 
    else {
      return new Dandelions(new Posn(location.x, location.y));
    }
  }
}



