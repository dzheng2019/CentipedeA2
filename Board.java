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
  
  // makes a board
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
    return new Dart(new Posn(location.x * this.units + this.units / 2 , this.row*this.units - this.units / 2), speed, this.units / 2);
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
  
  ClickFunc(boolean isLeft) {
    this.isLeft = isLeft;
  }
  
  
  public ITile apply(ITile arg) {
    return arg.accept(this);
  }

  
  public ITile visitGrass(Grass grass) {
    return grass.click(isLeft);
  }

  public ITile visitDandelions(Dandelions dande) {
    return dande.click(isLeft);
  }

  public ITile visitPebbles(Pebbles pebb) {
    return pebb.click(isLeft);
  }
  
}

class DrawBoard implements IListVisitor<IList<ITile>, WorldImage> {

  @Override
  public WorldImage apply(IList<IList<ITile>> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public WorldImage visitMt(MtList<IList<ITile>> mt) {
    // TODO Auto-generated method stub
    return new EmptyImage();
  }

  @Override
  public WorldImage visitCons(ConsList<IList<ITile>> cons) {
    // TODO Auto-generated method stub
    IListVisitor<ITile, WorldImage> rowVisitor = new DrawRow();
    return new AboveImage(cons.rest.accept(this), rowVisitor.apply(cons.first));
  }

}

class DrawRow implements IListVisitor<ITile, WorldImage> {

  @Override
  public WorldImage apply(IList<ITile> arg) {
    return arg.accept(this);
  }

  @Override
  public WorldImage visitMt(MtList<ITile> mt) {
    return new EmptyImage();
  }

  @Override
  public WorldImage visitCons(ConsList<ITile> cons) {
    return new BesideImage(cons.first.draw(), this.apply(cons.rest));
  }
}

class TileCollision implements IPred<ITile> {
  Posn collisionPoint;
  TileCollision(Posn collisionPoint) {
    this.collisionPoint = collisionPoint;
  }
  @Override
  public Boolean apply(ITile arg) {
    return arg.collidesWith(this.collisionPoint);
  }
}

class ChangeToGrass implements IFuncTile<ITile> {

  @Override
  public ITile apply(ITile arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public ITile visitGrass(Grass grass) {
    // TODO Auto-generated method stub
    return grass;
  }

  @Override
  public ITile visitDandelions(Dandelions dande) {
    // TODO Auto-generated method stub
    return new Grass(dande.gridLocation);
  }

  @Override
  public ITile visitPebbles(Pebbles pebb) {
    // TODO Auto-generated method stub
    return new Grass(pebb.gridLocation);
  }

}

class ChangeToDandelion implements IFuncTile<ITile> {

  @Override
  public ITile apply(ITile arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public ITile visitGrass(Grass grass) {
    // TODO Auto-generated method stub
    return new Dandelions(grass.gridLocation);
  }

  @Override
  public ITile visitDandelions(Dandelions dande) {
    // TODO Auto-generated method stub
    return dande;
  }

  @Override
  public ITile visitPebbles(Pebbles pebb) {
    // TODO Auto-generated method stub
    return new Dandelions(pebb.gridLocation);
  }

}

class ChangeToPebbles implements IFuncTile<ITile> {

  @Override
  public ITile apply(ITile arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public ITile visitGrass(Grass grass) {
    // TODO Auto-generated method stub
    return new Pebbles(grass.gridLocation);
  }

  @Override
  public ITile visitDandelions(Dandelions dande) {
    // TODO Auto-generated method stub
    return new Pebbles(dande.gridLocation);
  }

  @Override
  public ITile visitPebbles(Pebbles pebb) {
    // TODO Auto-generated method stub
    return pebb;
  }
}



class ChangeTilesToIf implements IFuncTile<ITile> {

  IList<Integer> locs;
  boolean isPebbles;
  int cols;
  ChangeTilesToIf(IList<Integer> locs, boolean isPebbles, int cols) {
    this.locs = locs;
    this.isPebbles = isPebbles;
    this.cols = cols;
  }

  @Override
  public ITile apply(ITile arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public ITile visitGrass(Grass grass) {
    if (containsLoc(grass.gridLocation)) {
      return this.pebbleOrDande(isPebbles, grass.gridLocation);
    } 
    else {
      return grass;
    }
  }

  @Override
  public ITile visitDandelions(Dandelions dande) {
    if (containsLoc(dande.gridLocation)) {
      return this.pebbleOrDande(isPebbles, dande.gridLocation);
    } 
    else {
      return dande;
    }
  }

  @Override
  public ITile visitPebbles(Pebbles pebb) {
    if (containsLoc(pebb.gridLocation)) {
      return this.pebbleOrDande(isPebbles, pebb.gridLocation);
    } 
    else {
      return pebb;
    }
  }

  boolean containsLoc(Posn location) {
    return new OrMap<Integer>(
        new ContainsInt(
            (location.y * this.cols) + location.x)).apply(locs);
  }

  ITile pebbleOrDande(boolean isPebbles, Posn location) {
    if (isPebbles) {
      return new Pebbles(new Posn(location.x, location.y));
    } 
    else {
      return new Dandelions(new Posn(location.x, location.y));
    }
  }

}

class ExamplesBoard {
 
}