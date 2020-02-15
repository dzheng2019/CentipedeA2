import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.lang.reflect.AccessibleObject;

import tester.Tester;

abstract class AMoveableObject {
  Posn location;
  int speed;
  int size;

  Util u = new Util();
  
  AMoveableObject(Posn location, int speed, int size) {
    this.location = location;
    this.speed = speed;
    this.size = size;
  }

  public Posn getGrid(int unit, int row) {
    return u.convertAbsoluteToGrid(location, unit, row);
  }
  
  public Posn movedPos(Posn direction) {
    return u.moveInDirection(this.location, direction, this.speed);
  }

  public boolean validMove(Board board, Posn moveDir) {
    return !board.collisionOccurs(u.moveInDirection(this.location, moveDir, this.speed), this.size);
  }

  abstract WorldImage draw();
  
  public WorldScene drawOnBoard(WorldScene scene) {
    return scene.placeImageXY(this.draw(), location.x, location.y);
  }
}

class Gnome extends AMoveableObject {

  Gnome(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE);
  }

  // Moves this gnome for each tick which passes in this game
  // (this does nothing because gnomes do not move without key input)
  Gnome move(Board board) {
    // TODO Auto-generated method stub
    return this;
  }

  Gnome moveIfPossible(Board board, Posn moveDir) {
    if (this.validMove(board, moveDir)) {
      return this.moveInDirection(board, moveDir);
    }
    else {
      return this;
    }
  }
  
  // Moves this gnome in the given direction without consideration for collisions
  Gnome moveInDirection(Board board, Posn direction) {
    // TODO Auto-generated method stub
    return new Gnome(this.movedPos(direction), this.speed, this.size);
  }

  Gnome gnomeWithSpeed(int speed) {
    return new Gnome(this.location, speed, size);
  }
  
}

class Dart extends AMoveableObject{

  Dart(Posn location, int speed, int size) {
    super(location, speed, size);
  }

   
  public WorldImage draw() {
    return new RectangleImage(this.size / 3, this.size / 2, 
        OutlineMode.SOLID, Color.BLUE);
  }

   
  // Moves this dart up if possible to do so
  Dart move(Board board) {
    return this.moveInDirection(board, new Posn(0, -1));
  }

   
  // Moves this dart in the 
  Dart moveInDirection(Board board, Posn direction) {
    // TODO Auto-generated method stub
    return new Dart(this.movedPos(direction), this.speed, this.size);
  }

  boolean collisionWithBoard(Board b) {
    // darts only collide with things in their column
    return b.collisionOccurs(this.location, 0);
  }
  
  
  // only used when a collision occurs
  Board transformBoard(Board board, boolean createPebble) {
    return board.changeAtLocation(this.location, createPebble);
  }
  
  boolean collisionWithCentipedes(IList<CentipedeSeg> centipedes) {
    return new OrMap<CentipedeSeg>(new DartCentipedeCollide(this.location)).apply(centipedes);
  }

}

class DartCentipedeCollide implements ICentipedeVisitor<Boolean>, IPred<CentipedeSeg> {

  Posn location;
  Util u = new Util();
  
  DartCentipedeCollide(Posn location) {
    this.location = location;
  }
  
  @Override
  public Boolean apply(CentipedeSeg arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public Boolean visitSeg(CentipedeSeg seg) {
    // TODO Auto-generated method stub
    return u.posnInRadius(seg.location, this.location, seg.size);
  }

  @Override
  public Boolean visitHead(CentipedeHead head) {
    // TODO Auto-generated method stub
    return u.posnInRadius(head.location, this.location, head.size);
  }  
}







class CentipedeSeg extends AMoveableObject {
  int currentLevel;
  Posn leftOrRight;
  CentipedeSeg (
      Posn location, 
      int speed, 
      int size, 
      int currentLevel,
      Posn leftOrRight) {
    super(location, speed, size);
    this.currentLevel = currentLevel;
    this.leftOrRight = leftOrRight;
  }

   
  CentipedeSeg move(Board board) {
    return this.moveIfPossible(board, leftOrRight);
  }


   CentipedeSeg moveInDirection(Board board,Posn direction) {
    if (board.getRow(this.location.y, size) == currentLevel) {
    return new CentipedeSeg(
        this.movedPos(direction), 
        this.speed, 
        this.size, 
        this.currentLevel,
        this.leftOrRight);
    } 
    else {
      return this.turnAround(board);
    }
  }

  CentipedeSeg moveIfPossible(Board board, Posn moveDir) {
    if (this.validMove(board, moveDir)) {
      return this.moveInDirection(board, moveDir);
    }
    else {
      return this.handleCollision(board);
    }
  }
  
   
  CentipedeSeg handleCollision(Board board) {
    if (board.getRow(this.location.y, size) == currentLevel) {
      return this.moveInDirection(board, new Posn(0, 1));
    }
    else {
      return this.turnAround(board);
    }
  }

  public boolean validMove(Board board, Posn moveDir) {
    if (this.leftOrRight.x == -1) {
      return !board.collisionOccursLeft(
          u.moveInDirection(new Posn(this.location.x, this.location.y - size), moveDir, this.speed), this.size);
    } else {
      return !board.collisionOccursRight(
          u.moveInDirection(new Posn(this.location.x, this.location.y - size), moveDir, this.speed), this.size)
          || this.location.x < 0;
    }
  }

  CentipedeSeg turnAround(Board board) {
    Posn direction = new Posn(-this.leftOrRight.x, 0);
    return new CentipedeSeg(
        this.movedPos(direction), 
        this.speed, 
        this.size,
        this.currentLevel + 1,
        direction);

  }

  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLACK);
  }
  
  public <T> T accept(ICentipedeVisitor<T> func) {
    return func.visitSeg(this);
  }
}


class CentipedeHead extends CentipedeSeg{

  IList<CentipedeSeg> tail;
  Posn curDir;


  CentipedeHead (
      Posn location, 
      int speed, 
      int size, 
      int currentLevel,
      Posn leftOrRight,
      IList<CentipedeSeg> tail, 
      Posn curDir) {
    super(location, speed, size, currentLevel, leftOrRight);
    this.tail = tail;
    this.leftOrRight = leftOrRight;
    this.curDir = curDir;
  }

  CentipedeHead (
      Posn location, 
      int speed, 
      int size, 
      int currentLevel,
      Posn leftOrRight, 
      Posn curDir) {
    super(location, speed, size, currentLevel, leftOrRight);
    this.tail = this.createTail(10);
    this.leftOrRight = leftOrRight;
    this.curDir = curDir;
  }

  IList<CentipedeSeg> createTail(int num) {
    if (num == 0) {
      return new MtList<CentipedeSeg>();
    }
    else {
      return new ConsList<CentipedeSeg>(
          new CentipedeSeg(
              new Posn(this.location.x - num*40, this.location.y), 
              this.speed, 
              this.size, 
              0,
              this.leftOrRight), createTail(num - 1));
    }
  }

  CentipedeHead moveInDirection(Board board, Posn moveDir) {
    if (board.getRow(this.location.y, size) == currentLevel) {
    return new CentipedeHead(
        this.movedPos(moveDir), 
        this.speed, 
        this.size,
        this.currentLevel,
        this.leftOrRight, 
        new MoveFoward(board).apply(this.tail),
        moveDir);
    } 
    else {
      return this.turnAround(board);
    }
  }

  CentipedeHead turnAround(Board board) {
    Posn direction = new Posn(-this.leftOrRight.x, 0);
    return new CentipedeHead(
        this.movedPos(direction), 
        this.speed, 
        this.size,
        this.currentLevel + 1,
        direction,
        new MoveFoward(board).apply(this.tail),
        direction);
  }

   
  public WorldImage draw() {
    WorldImage head = 
        new EquilateralTriangleImage(
            this.size * 2, 
            OutlineMode.SOLID, Color.RED).movePinholeTo(new Posn(0, 0));
    if (curDir.x == 1) {
      return new RotateImage(head, 90);
    }
    else if (curDir.x == -1) {
      return new RotateImage(head, -90);
    }
    else if (curDir.y == 1) {
      return new RotateImage(head, 180);
    }
    else {
      return head;
    }
  }

  public WorldScene drawOnBoard(WorldScene scene) {
    return new DrawTail(scene.placeImageXY(this.draw(), location.x, location.y)).apply(this.tail);
  }
  
  public <T> T accept(ICentipedeVisitor<T> func) {
    return func.visitHead(this);
  }
}

interface ICentipedeVisitor<T> extends IFunc<CentipedeSeg, T> {
  T visitSeg(CentipedeSeg seg);
  T visitHead(CentipedeHead head);
}




class MoveFoward implements IListVisitor<CentipedeSeg, IList<CentipedeSeg>> {

  Board board;

  MoveFoward(Board board) {
    this.board = board;
  }

   
  public IList<CentipedeSeg> apply(IList<CentipedeSeg> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

   
  public IList<CentipedeSeg> visitMt(MtList<CentipedeSeg> mt) {
    // TODO Auto-generated method stub
    return mt;
  }

   
  public IList<CentipedeSeg> visitCons(ConsList<CentipedeSeg> cons) {
    // TODO Auto-generated method stub
    return new ConsList<CentipedeSeg>(
        cons.first.move(board), cons.rest.accept(this));
  }

}

class DrawTail implements IListVisitor<CentipedeSeg, WorldScene> {

  WorldScene scene;

  DrawTail(WorldScene scene) {
    this.scene = scene;
  }

   
  public WorldScene apply(IList<CentipedeSeg> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

   
  public WorldScene visitMt(MtList<CentipedeSeg> mt) {
    // TODO Auto-generated method stub
    return scene;
  }

   
  public WorldScene visitCons(ConsList<CentipedeSeg> cons) {
    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }

}

