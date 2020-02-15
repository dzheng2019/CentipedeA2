//import java.util.Random;
//import javalib.funworld.*;
//import javalib.worldimages.*;
//import java.awt.Color;
//import java.lang.reflect.AccessibleObject;
//
//import tester.Tester;
//
//interface IMoveableObject {
//  int units = ITile.units;
//  Posn getGrid(int width, int row);
//
//  IMoveableObject moveIfPossible(Board board, Posn direction);
//  IMoveableObject moveInDirection(Board board, Posn direction);
//  IMoveableObject move(Board board);
//  IMoveableObject handleCollision(Board board);
//
//  Posn movedPos(Posn direction);
//
//  WorldImage draw();
//  WorldScene drawOnBoard(WorldScene scene);
//  Util u = new Util();
//}
//
//abstract class AMoveableObject implements IMoveableObject {
//  Posn location;
//  int speed;
//  int size;
//
//  AMoveableObject(Posn location, int speed, int size) {
//    this.location = location;
//    this.speed = speed;
//    this.size = size;
//  }
//
//  public Posn getGrid(int unit, int row) {
//    return u.convertAbsoluteToGrid(location, unit, row);
//  }
//
//  public IMoveableObject moveIfPossible(Board board, Posn moveDir) {
//    if (this.validMove(board, moveDir)) {
//      return this.moveInDirection(board, moveDir);
//    }
//    else {
//      return this.handleCollision(board);
//    }
//  }
//  
//  public Posn movedPos(Posn direction) {
//    return u.moveInDirection(this.location, direction, this.speed);
//  }
//
//  public boolean validMove(Board board, Posn moveDir) {
//    return !board.collisionOccurs(u.moveInDirection(this.location, moveDir, this.speed), this.size);
//  }
//
//  public WorldScene drawOnBoard(WorldScene scene) {
//    return scene.placeImageXY(this.draw(), location.x, location.y);
//  }
//}
//
//class Gnome extends AMoveableObject {
//
//  Gnome(Posn location, int speed, int size) {
//    super(location, speed, size);
//  }
//
//  public WorldImage draw() {
//    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE);
//  }
//
//  @Override
//  // Moves this gnome for each tick which passes in this game
//  // (this does nothing because gnomes do not move without key input)
//  public IMoveableObject move(Board board) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//
//  // Moves this gnome in the given direction without consideration for collisions
//  @Override
//  public IMoveableObject moveInDirection(Board board, Posn direction) {
//    // TODO Auto-generated method stub
//    return new Gnome(this.movedPos(direction), this.speed, this.size);
//  }
//
//  // Moves the gnome if the user key input results in a collision with a wall
//  // or obstacle (returns this because gnomes can not move in special ways after collisions)
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//
//}
//
//interface MaybeDart extends IMoveableObject {
//  boolean exisitsOnBoard();
//}
//
//class Dart extends AMoveableObject implements MaybeDart {
//
//  Dart(Posn location, int speed, int size) {
//    super(location, speed, size);
//  }
//
//  @Override
//  public WorldImage draw() {
//    return new RectangleImage(this.size / 4, this.size, 
//        OutlineMode.SOLID, Color.MAGENTA);
//  }
//
//  @Override
//  // Moves this dart up if possible to do so
//  public IMoveableObject move(Board board) {
//    return this.moveIfPossible(board, new Posn(0, -1));
//  }
//
//  @Override
//  // Moves this dart in the 
//  public IMoveableObject moveInDirection(Board board, Posn direction) {
//    // TODO Auto-generated method stub
//    return new Dart(this.movedPos(direction), this.speed, this.size);
//  }
//
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    return new NotDart();
//    
//  }
//
//  @Override
//  public boolean exisitsOnBoard() {
//    // TODO Auto-generated method stub
//    return true;
//  }
//}
//
//
//class NotDart extends AMoveableObject implements MaybeDart {
//
//  NotDart() {
//    super(new Posn(0,0), 0, 0);
//  }
//  
//  NotDart(Posn location, int speed, int size) {
//    super(location, speed, size);
//  }
//  
//  @Override
//  public IMoveableObject moveInDirection(Board board, Posn direction) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//
//  @Override
//  public IMoveableObject move(Board board) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//
//  @Override
//  public WorldImage draw() {
//    // TODO Auto-generated method stub
//    return new EmptyImage();
//  }
//
//  @Override
//  public boolean exisitsOnBoard() {
//    // TODO Auto-generated method stub
//    return false;
//  }
//  
//}
//
//
//class CentipedeSeg extends AMoveableObject {
//  int currentLevel;
//  Posn leftOrRight;
//  CentipedeSeg (
//      Posn location, 
//      int speed, 
//      int size, 
//      int currentLevel,
//      Posn leftOrRight) {
//    super(location, speed, size);
//    this.currentLevel = currentLevel;
//    this.leftOrRight = leftOrRight;
//  }
//
//  @Override
//  public IMoveableObject move(Board board) {
//    return this.moveIfPossible(board, leftOrRight);
//  }
//
//
//  public IMoveableObject moveInDirection(Board board,Posn direction) {
//    if (board.getRow(this.location.y, size) == currentLevel) {
//    return new CentipedeSeg(
//        this.movedPos(direction), 
//        this.speed, 
//        this.size, 
//        this.currentLevel,
//        this.leftOrRight);
//    } 
//    else {
//      return this.turnAround(board);
//    }
//  }
//
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    if (board.getRow(this.location.y, size) == currentLevel) {
//      return this.moveInDirection(board, new Posn(0, 1));
//    }
//    else {
//      return this.turnAround(board);
//    }
//  }
//
//  public boolean validMove(Board board, Posn moveDir) {
//    if (this.leftOrRight.x == -1) {
//      return !board.collisionOccursLeft(
//          u.moveInDirection(new Posn(this.location.x, this.location.y - size), moveDir, this.speed), this.size);
//    } else {
//      return !board.collisionOccursRight(
//          u.moveInDirection(new Posn(this.location.x, this.location.y - size), moveDir, this.speed), this.size)
//          || this.location.x < 0;
//    }
//  }
//
//  IMoveableObject turnAround(Board board) {
//    Posn direction = new Posn(-this.leftOrRight.x, 0);
//    return new CentipedeSeg(
//        this.movedPos(direction), 
//        this.speed, 
//        this.size,
//        this.currentLevel + 1,
//        direction);
//
//  }
//
//  public WorldImage draw() {
//    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLACK);
//  }
//}
//
//
//class CentipedeHead extends CentipedeSeg{
//
//  IList<IMoveableObject> tail;
//  Posn curDir;
//
//
//  CentipedeHead (
//      Posn location, 
//      int speed, 
//      int size, 
//      int currentLevel,
//      Posn leftOrRight,
//      IList<IMoveableObject> tail, 
//      Posn curDir) {
//    super(location, speed, size, currentLevel, leftOrRight);
//    this.tail = tail;
//    this.leftOrRight = leftOrRight;
//    this.curDir = curDir;
//  }
//
//  CentipedeHead (
//      Posn location, 
//      int speed, 
//      int size, 
//      int currentLevel,
//      Posn leftOrRight, 
//      Posn curDir) {
//    super(location, speed, size, currentLevel, leftOrRight);
//    this.tail = this.createTail(10);
//    this.leftOrRight = leftOrRight;
//    this.curDir = curDir;
//  }
//
//  IList<IMoveableObject> createTail(int num) {
//    if (num == 0) {
//      return new MtList<IMoveableObject>();
//    }
//    else {
//      return new ConsList<IMoveableObject>(
//          new CentipedeSeg(
//              new Posn(this.location.x - num*40, this.location.y), 
//              this.speed, 
//              this.size, 
//              0,
//              this.leftOrRight), createTail(num - 1));
//    }
//  }
//
//  public IMoveableObject moveInDirection(Board board, Posn moveDir) {
//    if (board.getRow(this.location.y, size) == currentLevel) {
//    return new CentipedeHead(
//        this.movedPos(moveDir), 
//        this.speed, 
//        this.size,
//        this.currentLevel,
//        this.leftOrRight, 
//        new MoveFoward(board).apply(this.tail),
//        moveDir);
//    } 
//    else {
//      return this.turnAround(board);
//    }
//  }
//
//  IMoveableObject turnAround(Board board) {
//    Posn direction = new Posn(-this.leftOrRight.x, 0);
//    return new CentipedeHead(
//        this.movedPos(direction), 
//        this.speed, 
//        this.size,
//        this.currentLevel + 1,
//        direction,
//        new MoveFoward(board).apply(this.tail),
//        direction);
//  }
//
//  @Override
//  public WorldImage draw() {
//    WorldImage head = 
//        new EquilateralTriangleImage(
//            this.size * 2, 
//            OutlineMode.SOLID, Color.RED).movePinholeTo(new Posn(0, 0));
//    if (curDir.x == 1) {
//      return new RotateImage(head, 90);
//    }
//    else if (curDir.x == -1) {
//      return new RotateImage(head, -90);
//    }
//    else if (curDir.y == 1) {
//      return new RotateImage(head, 180);
//    }
//    else {
//      return head;
//    }
//  }
//
//  public WorldScene drawOnBoard(WorldScene scene) {
//    return new DrawTail(scene.placeImageXY(this.draw(), location.x, location.y)).apply(this.tail);
//  }
//}
//
//class MoveFoward implements IListVisitor<IMoveableObject, IList<IMoveableObject>> {
//
//  Board board;
//
//  MoveFoward(Board board) {
//    this.board = board;
//  }
//
//  @Override
//  public IList<IMoveableObject> apply(IList<IMoveableObject> arg) {
//    // TODO Auto-generated method stub
//    return arg.accept(this);
//  }
//
//  @Override
//  public IList<IMoveableObject> visitMt(MtList<IMoveableObject> mt) {
//    // TODO Auto-generated method stub
//    return mt;
//  }
//
//  @Override
//  public IList<IMoveableObject> visitCons(ConsList<IMoveableObject> cons) {
//    // TODO Auto-generated method stub
//    return new ConsList<IMoveableObject>(
//        cons.first.move(board), cons.rest.accept(this));
//  }
//
//}
//
//class DrawTail implements IListVisitor<IMoveableObject, WorldScene> {
//
//  WorldScene scene;
//
//  DrawTail(WorldScene scene) {
//    this.scene = scene;
//  }
//
//  @Override
//  public WorldScene apply(IList<IMoveableObject> arg) {
//    // TODO Auto-generated method stub
//    return arg.accept(this);
//  }
//
//  @Override
//  public WorldScene visitMt(MtList<IMoveableObject> mt) {
//    // TODO Auto-generated method stub
//    return scene;
//  }
//
//  @Override
//  public WorldScene visitCons(ConsList<IMoveableObject> cons) {
//    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
//  }
//
//}
//
//