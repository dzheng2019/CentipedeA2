import javalib.funworld.WorldScene;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.*;
import tester.Tester;
class Board {
  IList<IList<ITile>> gameBoard;
  int row;
  int col;
  Util u = new Util();
  
  Board(IList<IList<ITile>> gameBoard) {
    this.row = new RowLength<ITile>().apply(gameBoard);
    this.col = gameBoard.length();
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
      return new ConsList<IList<ITile>>(this.makeRow(row, col), this.makeBoard(row - 1, col));
    }
  }
  
  // row is constant and not recursed upon
  IList<ITile> makeRow(int row, int col) {
    if (col < 0) {
      return new MtList<ITile>();
    }
    else {
      return new ConsList<ITile>(new Grass(new Posn(row, col)), this.makeRow(row, col - 1));
    }
  }
  
  WorldImage draw() {
    return new DrawBoard().apply(this.gameBoard);
  }
  
  boolean collisionOccurs(Posn collidePos, int size) {
    Posn collidePosLeft = u.convertAbsoluteToGrid(new Posn(collidePos.x - size, collidePos.y), this.col, this.row);
    Posn collidePosRight = u.convertAbsoluteToGrid(new Posn(collidePos.x + size, collidePos.y), this.col, this.row);

    boolean tileCollisionLeft = 
        new OrMap<IList<ITile>>(
        new OrMapListPred<ITile>(
            new TileCollision(collidePosLeft))).apply(this.gameBoard);
    
    boolean tileCollisionRight = 
        new OrMap<IList<ITile>>(
        new OrMapListPred<ITile>(
            new TileCollision(collidePosRight))).apply(this.gameBoard);
    
    boolean xCollision = collidePos.x < 0 || collidePos.x > this.col;
    boolean yCollision = collidePos.y < 0 || collidePos.y > this.row;
    return tileCollisionLeft || tileCollisionRight || xCollision || yCollision;
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





class ExamplesBoard {
  Board b = new Board(6 ,6);
  boolean testDrawTree(Tester t) {
    WorldCanvas c = new WorldCanvas(6*40, 6*40);
    WorldScene s = new WorldScene(6*40, 6*40);
    return c.drawScene(s.placeImageXY(b.draw(), 120, 120)) && c.show();
  }
}