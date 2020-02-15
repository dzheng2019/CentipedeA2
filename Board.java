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

  IList<IList<ITile>> makeBoard(int row, int col) {
    if (row < 0) {
      return new MtList<IList<ITile>>();
    }
    else {
      return new ConsList<IList<ITile>>(this.makeRow(this.row - 1 - row, col), this.makeBoard(row - 1, col));
    }
  }

  // row is constant and not recursed upon
  IList<ITile> makeRow(int row, int col) {
    if (col < 0) {
      return new MtList<ITile>();
    }
    else {
      return new ConsList<ITile>(new Grass(new Posn(this.col - 1 - col, row)), this.makeRow(row, col - 1));
    }
  }

  WorldImage draw() {
    return new DrawBoard().apply(this.gameBoard);
  }

  WorldScene drawOnBoard(WorldScene scene) {
    return  scene.placeImageXY(this.draw(), this.col * this.units / 2 , this.row * this.units / 2);
  }

  boolean collisionOccursLeft(Posn collidePos, int size) {
    Posn collideGridLocLeft = u.convertAbsoluteToGrid(new Posn(collidePos.x - size, collidePos.y), this.units, this.row);

    boolean tileCollisionLeft = 
        new OrMap<IList<ITile>>(
            new OrMapListPred<ITile>(
                new TileCollision(collideGridLocLeft))).apply(this.gameBoard);


    boolean xCollision = collidePos.x - size < 0;

    return tileCollisionLeft || xCollision ;
  }

  boolean collisionOccursRight(Posn collidePos, int size) {
    Posn collideGridLocRight = u.convertAbsoluteToGrid(new Posn(collidePos.x + size, collidePos.y), this.units, this.row);

    boolean tileCollisionRight = 
        new OrMap<IList<ITile>>(
            new OrMapListPred<ITile>(
                new TileCollision(collideGridLocRight))).apply(this.gameBoard);

    // subtract 3 to make it closer to edge
    // unsure why right edge needs special treatement compared to left edge
    boolean xCollision = collidePos.x + size - 3>= this.col * this.units;

    return tileCollisionRight || xCollision;
  }

  boolean collisionOccurs(Posn collidePos, int size) {
    boolean yCollision = collidePos.y - size < 0 || collidePos.y + size > this.row  * this.units;

    return yCollision || 
        this.collisionOccursRight(collidePos, size) ||
        this.collisionOccursLeft(collidePos, size);
  }

  int getRow(int y, int size) {
    return (y - size) / units;
  }

  Dart produceDart(Gnome player, int speed) {
    Posn location = player.getGrid(this.units, this.row);
    return new Dart(new Posn(location.x * this.units + this.units / 2 , this.row*this.units - this.units / 2), speed, this.units / 2);
  }
  
  Board randomBoard(boolean isPebbles, int num, Random rand) {
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
  
  Board changeAtLocation(Posn location, boolean createPebble) {
    Posn gridLoc = u.convertAbsoluteToGrid(location, this.units, this.row);
    if (createPebble) {
      return new Board(new ChangeAtXY<ITile>(new ChangeToPebbles(), gridLoc.x, gridLoc.y).apply(this.gameBoard));      
    }
    else {
      return new Board(new ChangeAtXY<ITile>(new ChangeToDandelion(), gridLoc.x, gridLoc.y).apply(this.gameBoard));
    }
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


interface IFuncTile<T> extends IFunc<ITile, T> {
  T visitGrass(Grass grass);
  T visitDandelions(Dandelions dande);
  T visitPebbles(Pebbles pebb);
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