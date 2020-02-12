import javalib.funworld.WorldScene;
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
  
  WorldScene drawOnBoard(WorldScene scene) {
    return  scene.placeImageXY(this.draw(), this.col * this.units / 2 , this.row * this.units / 2);
  }
  
  boolean collisionOccursLeft(Posn collidePos, int size) {
    Posn collideGridLocLeft = u.convertAbsoluteToGrid(new Posn(collidePos.x - size, collidePos.y), this.col, this.row);

    boolean tileCollisionLeft = 
        new OrMap<IList<ITile>>(
        new OrMapListPred<ITile>(
            new TileCollision(collideGridLocLeft))).apply(this.gameBoard);
    
    
    boolean xCollision = collidePos.x - size < 0;
    
    return tileCollisionLeft || xCollision ;
  }
  
  boolean collisionOccursRight(Posn collidePos, int size) {
    Posn collideGridLocRight = u.convertAbsoluteToGrid(new Posn(collidePos.x + size, collidePos.y), this.col, this.row);

    
    boolean tileCollisionRight = 
        new OrMap<IList<ITile>>(
        new OrMapListPred<ITile>(
            new TileCollision(collideGridLocRight))).apply(this.gameBoard);
    
    int x = collidePos.x + size;
    boolean xCollision = collidePos.x + size >= this.col * this.units;
    
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