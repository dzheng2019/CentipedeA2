//import java.util.Random;
//import javalib.funworld.*;
//import javalib.worldimages.*;
//import java.awt.Color;
//import tester.Tester;
//
//interface IMoveableObject {
//  int units = ITile.units;
//  Posn getGrid(int width, int row);
//
//  IMoveableObject moveIfPossible(Board board, Posn direction);
//  IMoveableObject moveInDirection(Posn direction);
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
//      return this.moveInDirection(moveDir);
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
//
//  @Override
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
//
//  // Moves this gnome in the given direction without consideration for collisions
//  @Override
//  public IMoveableObject moveInDirection(Posn direction) {
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
//class Dart extends AMoveableObject {
//
//  Dart(Posn location, int speed, int size) {
//    super(location, speed, size);
//  }
//
//  @Override
//  public WorldImage draw() {
//    return new EmptyImage();
//  }
//
//  @Override
//  // Moves this dart up if possible to do so
//  public IMoveableObject move(Board board) {
//    return this.moveIfPossible(board, new Posn(0, 1));
//  }
//
//  @Override
//  // Moves this dart in the 
//  public IMoveableObject moveInDirection(Posn direction) {
//    // TODO Auto-generated method stub
//    return new Dart(this.movedPos(direction), this.speed, this.size);
//  }
//
//
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    // TODO Auto-generated method stub
//    return this;
//  }
//}
//
//class CentipedeHead extends AMoveableObject{
//
//  IList<ISegment> tail;
//  Posn leftOrRight;
//  Posn curDir;
//  int currentLevel;
//
//  CentipedeHead (
//      Posn location, 
//      int speed, 
//      int size, 
//      IList<ISegment> tail, 
//      Posn leftOrRight, 
//      Posn curDir,
//      int currentLevel) {
//    super(location, speed, size);
//    this.tail = tail;
//    this.leftOrRight = leftOrRight;
//    this.curDir = curDir;
//    this.currentLevel = currentLevel;
//  }
//
//  CentipedeHead (      
//      Posn location, 
//      int speed, 
//      int size, 
//      Posn leftOrRight, 
//      Posn curDir,
//      int currentLevel) {
//    super(location, speed, size);
//    this.tail = this.createTail(10);
//    this.leftOrRight = leftOrRight;
//    this.curDir = curDir;
//    this.currentLevel = currentLevel;    
//  }
//
//  IList<ISegment> createTail (int num) {
//
//    if (num == 1) {
//      return createNode(new MtList<ISegment>());
//    }
//    else {
//      return createNode(this.createTail(num-1));
//    }
//  }
//
//  IList<ISegment> createNode(IList<ISegment> rest) {
//    ISegment cent = new CentSeg(new Posn (-250, -250), 16);
//    ISegment gap = new Gap(new Posn(-250, -250));
//    return 
//        new ConsList<ISegment>(gap,
//            new ConsList<ISegment>(gap,
//                new ConsList<ISegment>(gap, 
//                    new ConsList<ISegment>(gap,
//                        new ConsList<ISegment>(gap,
//                            new ConsList<ISegment>(gap, 
//                                new ConsList<ISegment>(gap,
//                                    new ConsList<ISegment>(gap,
//                                        new ConsList<ISegment>(gap, 
//                                            new ConsList<ISegment>(gap,
//                                                new ConsList<ISegment>(gap,
//                                                    new ConsList<ISegment>(gap,
//                    new ConsList<ISegment>(cent, rest)))))))))))));
//  }
//
//  @Override
//  public IMoveableObject moveInDirection(Posn direction) {
//    return new CentipedeHead(
//        this.movedPos(direction), 
//        this.speed, 
//        this.size,
//        new MoveFoward(u.moveInDirection(this.location, direction, this.speed)).apply(this.tail),
//        this.leftOrRight,
//        direction, 
//        this.currentLevel);
//  }
//
//
//  @Override
//  public IMoveableObject move(Board board) {
//    return this.moveIfPossible(board, leftOrRight);
//  }
//
//
//  @Override
//  public IMoveableObject handleCollision(Board board) {
//    return handleCollisionHelper(
//        board.getRow(this.location.y, size) == currentLevel);
//  }
//
//  IMoveableObject handleCollisionHelper(boolean hit) {
//    if (hit) {
//      return this.moveInDirection(new Posn(0, 1));
//    }
//    else {
//      Posn direction = new Posn(-this.leftOrRight.x, 0);
//      return new CentipedeHead(
//          this.movedPos(direction), 
//          this.speed, 
//          this.size,
//          new MoveFoward(u.moveInDirection(this.location, direction, this.speed)).apply(this.tail),
//          direction, 
//          direction,
//          this.currentLevel + 1);
//    }
//  }
//
//  @Override
//  public WorldImage draw() {
//    return new EquilateralTriangleImage(this.size * 2, OutlineMode.SOLID, Color.RED).movePinholeTo(new Posn(0, 0));
//  }
//
//  public WorldScene drawOnBoard(WorldScene scene) {
//    return new DrawTail(scene.placeImageXY(this.draw(), location.x, location.y)).apply(this.tail);
//  }
//
//}
//
//class DrawTail implements IListVisitor<ISegment, WorldScene> {
//
//  WorldScene scene;
//
//  DrawTail(WorldScene scene) {
//    this.scene = scene;
//  }
//
//  @Override
//  public WorldScene apply(IList<ISegment> arg) {
//    // TODO Auto-generated method stub
//    return arg.accept(this);
//  }
//
//  @Override
//  public WorldScene visitMt(MtList<ISegment> mt) {
//    return this.scene;
//  }
//
//  @Override
//  public WorldScene visitCons(ConsList<ISegment> cons) {
//    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
//  }
//
//}
//
//
//class MoveFoward implements IListVisitor<ISegment, IList<ISegment>> {
//  Posn moveInDirectionLocation;
//  MoveFoward(Posn moveInDirectionLocation) {
//    this.moveInDirectionLocation = moveInDirectionLocation;
//  }
//
//  @Override
//  public IList<ISegment> apply(IList<ISegment> arg) {
//    return arg.accept(this);
//  }
//
//  @Override
//  public IList<ISegment> visitMt(MtList<ISegment> mt) {
//    return mt;
//  }
//
//  @Override
//  public IList<ISegment> visitCons(ConsList<ISegment> cons) {
//    return new ConsList<ISegment>(cons.first.moveInDirectionToLocation(moveInDirectionLocation), 
//        new MoveFoward(cons.first.currentLocation()).apply(cons.rest));
//  }
//}
//
//
//interface ISegment {
//  Posn currentLocation();
//  ISegment moveInDirectionToLocation(Posn location);
//  WorldImage draw();
//  WorldScene drawOnBoard(WorldScene scene);
//}
//
//class Gap implements ISegment{
//
//  Posn location;
//  Gap(Posn location) {
//    this.location = location;
//  }
//
//  public ISegment moveInDirectionToLocation(Posn location) {
//    return new Gap(location);
//  }
//
//  public Posn currentLocation() {
//    return this.location;
//  }
//
//  @Override
//  public WorldImage draw() {
//    // TODO Auto-generated method stub
//    return new EmptyImage();
//  }
//
//  @Override
//  public WorldScene drawOnBoard(WorldScene scene) {
//    // TODO Auto-generated method stub
//    return scene;
//  }
//
//}
//
//class CentSeg implements ISegment{
//  Posn location;
//  int size;
//
//  CentSeg(Posn location, int size) {
//    this.location = location;
//    this.size = size;
//  }
//  public Posn currentLocation() {
//    return this.location;
//  }
//
//  public WorldImage draw() {
//    return new CircleImage(this.size, OutlineMode.SOLID, Color.RED);
//  }
//
//  public ISegment moveInDirectionToLocation(Posn location) {
//    return new CentSeg(location, this.size);
//  }
//  @Override
//  public WorldScene drawOnBoard(WorldScene scene) {
//    // TODO Auto-generated method stub
//    return scene.placeImageXY(this.draw(), this.location.x, this.location.y);
//  }
//
//}
//
//class ExamplesMovable {
//  Board b = new Board(6, 6);
//  IMoveableObject p = new Gnome(new Posn(0, 240), 10, 10);
//
//}
//
//
