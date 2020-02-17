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

  boolean collisionWithCentipedes(IList<CentipedeHead> centipedes) {
    return new OrMap<CentipedeHead>(new LocationCollidesWithHead(this.location)).apply(centipedes);
  }

    IList<CentipedeHead> transformCentipedes(IList<CentipedeHead> centipedes) {
      return new ChangeCentipedeHeadsAtLocation(this.location).apply(centipedes);
    }

}

class ChangeCentipedeHeadsAtLocation implements IListVisitor<CentipedeHead, IList<CentipedeHead>> {

  IFunc<CentipedeHead, IList<CentipedeHead>> splitHeadAtLocation;
  Posn location;
  ChangeCentipedeHeadsAtLocation(Posn location) {
    this.splitHeadAtLocation = new SplitHeadAtLocation(location);
    this.location = location;
  }

  @Override
  public IList<CentipedeHead> apply(IList<CentipedeHead> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeHead> visitMt(MtList<CentipedeHead> mt) {
    // TODO Auto-generated method stub
    return mt;
  }

  @Override
  public IList<CentipedeHead> visitCons(ConsList<CentipedeHead> cons) {

    if (new LocationCollidesWithHead(this.location).apply(cons.first)) {
      return new Append<CentipedeHead>().apply(
          this.splitHeadAtLocation.apply(cons.first), 
          cons.rest);
    }
    else {
      return new Append<CentipedeHead>().apply(
          new ConsList<CentipedeHead>(cons.first, new MtList<CentipedeHead>()), 
          cons.rest.accept(this));
    }
  }  
}

class SplitHeadAtLocation implements IFunc<CentipedeHead, IList<CentipedeHead>> {
  Posn locationToChange;
  Util u = new Util();

  SplitHeadAtLocation(Posn location) {
    this.locationToChange = location;
  }


  @Override
  public IList<CentipedeHead> apply(CentipedeHead head) {
    /* Template:
     * head.location
     * head.size
     * head.tails
     */
    if (u.posnInRadius(this.locationToChange, head.location, head.size)) {
      return new CreateHead().apply(head.tail);
    }
    else if (new LocationCollidesWithHead(this.locationToChange).apply(head)) {
      int index = new FindCollisionIndex(this.locationToChange).apply(head.tail);
      IList<CentipedeHead> newHead = 
          new ConsList<CentipedeHead>(
              head.shortenTail(new FirstNElements<CentipedeFollower>(index).apply(head.tail)),
              new MtList<CentipedeHead>());
      IList<CentipedeHead> newHeadTail = new MakeHeadAtIndex(index + 1).apply(head.tail);
      return new Append<CentipedeHead>().apply(newHead, newHeadTail);
    }
    return new ConsList<CentipedeHead>(head, new MtList<CentipedeHead>());
  }

}


// assumed to be ran
class CreateHead implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {
  
  @Override
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return new MtList<CentipedeHead>();
  }

  @Override
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    // TODO Auto-generated method stub
    return new ConsList<CentipedeHead>(cons.first.transformToHead(cons.rest), new MtList<CentipedeHead>());
  }
}

class UpdatePreviousLoc implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {

  @Override
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return mt;
  }

  @Override
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    // TODO Auto-generated method stub
    return new ConsList<CentipedeFollower>(cons.first.setPreviousToLoc(),
        cons.rest.accept(this));
  }
  
}


class MakeHeadAtIndex implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {

  int index;
  
  MakeHeadAtIndex(int index) {
    this.index = index;
  }
  
  @Override
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return new MtList<CentipedeHead>();
  }

  @Override
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    if (index == 0) {
      return new CreateHead().apply(new UpdatePreviousLoc().apply(cons));
    }
    else {
      return new MakeHeadAtIndex(index - 1).apply(cons.rest);
    }
  }
}

class FindCollisionIndex implements IListVisitor<CentipedeFollower, Integer> {

  Posn locationToChange;

  FindCollisionIndex(Posn locationToChange) {
    this.locationToChange = locationToChange;
  }

  @Override
  public Integer apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public Integer visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Integer visitCons(ConsList<CentipedeFollower> cons) {
    // TODO Auto-generated method stub
    if (new LocationCollidesWithFollower(locationToChange).apply(cons.first)) {
      return 0;
    }
    else {
      return 1 + cons.rest.accept(this);
    }
  }
}

class LocationCollidesWithHead implements IPred<CentipedeHead> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithHead(Posn location) {
    this.location = location;
  }

  @Override
  public Boolean apply(CentipedeHead head) {
    return u.posnInRadius(head.location, this.location, head.size)
        || new OrMap<CentipedeFollower>(new LocationCollidesWithFollower(this.location)).apply(head.tail);
  }  
}

class LocationCollidesWithFollower implements IPred<CentipedeFollower> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithFollower(Posn location) {
    this.location = location;
  }

  @Override
  public Boolean apply(CentipedeFollower follower) {
    return u.posnInRadius(follower.location, this.location, follower.size);
  }
}


class CentipedeHead extends AMoveableObject {
  Posn previousGoal;
  Posn goal;
  int unit;
  IList<CentipedeFollower> tail;
  CentipedeHead (
      Posn location, 
      int speed, 
      int size,
      Posn previousGoal,
      Posn goal,
      int unit,
      IList<CentipedeFollower> tail) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = tail;
  }

  CentipedeHead(Posn location, 
      int speed, 
      int size,
      int leftOrRight,
      Posn previousGoal,
      Posn goal,
      int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = this.createTail(10);

  }

  IList<CentipedeFollower> createTail(int numFollowers) {
    if (numFollowers <= 0) {
      return new MtList<CentipedeFollower>();
    }
    else {
      CentipedeFollower follower = 
          new CentipedeFollower(this.location, this.speed, this.size, this.previousGoal, this.unit);
      return new ConsList<CentipedeFollower>(follower, this.createTail(numFollowers - 1));
    }
  }

  CentipedeHead move(Board board) {
    int leftOrRight = this.location.y / this.unit + 1;
    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2)*2 - 1), 0), this.speed);
    boolean withinGoal = u.posnInRadius(this.location, goal, speed/2);

    if (leftOrRight % 2 == 1 && board.collisionOccursRight(moveInCurDir, this.size) && withinGoal) {
      CentipedeHead newGoal = this.headWithNewGoal(this.getNextGoal(board));
      return newGoal.moveTowardsGoal();
    }
    else if (leftOrRight % 2 == 0 && board.collisionOccursLeft(moveInCurDir, this.size) && withinGoal) {
      CentipedeHead newGoal = this.headWithNewGoal(this.getNextGoal(board));
      return newGoal;
    }
    else if (withinGoal) {
      CentipedeHead headNewGoal = this.headWithNewGoal(this.getNextGoal(board));
      return headNewGoal.moveTowardsGoal();
    }
    else {
      return this.headWithNewPos(this.nextPos());
    }
  }

  CentipedeHead shortenTail(IList<CentipedeFollower> followers) {
    return new CentipedeHead(
        this.location, 
        this.speed, 
        this.size,
        this.previousGoal,
        this.goal,
        this.unit,
        followers); 
  }

  CentipedeHead headWithNewPos(Posn newPos) {
    return new CentipedeHead(newPos, this.speed, this.size,
        this.previousGoal, this.goal, this.unit, 
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }

  CentipedeHead headWithNewGoal(Posn newGoal) {
    return new CentipedeHead(this.nextPos(), this.speed, this.size, 
        this.goal, newGoal, this.unit, 
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }


  Posn nextPos() {
    Util u = new Util(); 
    int moveX = (this.goal.x - this.location.x);
    int moveY = (this.goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0){
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }

  CentipedeHead moveTowardsGoal() {
    return this.headWithNewPos(this.nextPos()); 
  }

  Posn getNextGoal(Board b) {
    int leftOrRight = this.location.y / this.unit + 1;

    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2)*2 - 1), 0), this.speed);
    Posn myCenter = u.getCenter(this.location, this.unit);
    if (b.collisionOccurs(moveInCurDir, this.size)) {
      return new Posn(myCenter.x, myCenter.y + unit);
    }
    else {
      return new Posn(myCenter.x + ((leftOrRight % 2)*2 - 1) *  unit, myCenter.y);
    }
  }

  public WorldImage draw() {

    WorldImage head = 
        new EquilateralTriangleImage(
            this.size * 2, 
            OutlineMode.SOLID, Color.RED).movePinholeTo(new Posn(0, 0));
    

    int moveY = (this.goal.y - this.location.y);
    
    int leftOrRight = this.location.y / this.unit + 1;

    
    if (moveY > 0) {
      return new RotateImage(head, 180);
    }
    if (leftOrRight % 2 == 1) {
      return new RotateImage(head, 90);
    }
    else if (leftOrRight % 2 == 0) {
      return new RotateImage(head, -90);
    }
    else {
      return head;
    }

  }
  
  public WorldScene drawOnBoard(WorldScene scene) {
    return new DrawTail(scene.placeImageXY(this.draw(), this.location.x, this.location.y)).apply(this.tail);
  }
 
  public <T> T accept(IFunc<CentipedeHead, T> func) {
    return func.apply(this);
  } 
}

class CentipedeFollower extends AMoveableObject {

  Posn previousGoal;
  Util u = new Util();
  int unit;
  
  CentipedeFollower(Posn location, int speed, int size, Posn previousGoal, int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.unit = unit;
  }


  @Override
  WorldImage draw() {
    // TODO Auto-generated method stub
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLACK);
  }

  CentipedeFollower followToGoal(Posn goal) {
    if (u.posnInRadius(this.location, goal, this.speed/2)) {
      return new CentipedeFollower(this.location, this.speed, this.size, goal, this.unit);
    }
    else {
      return new CentipedeFollower(this.movedPosToGoal(goal), this.speed, this.size, this.previousGoal, this.unit);
    }
  }

  Posn movedPosToGoal(Posn goal) {
    Util u = new Util(); 
    int moveX = (goal.x - this.location.x);
    int moveY = (goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0){
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }

  MoveTailForward newMoveForward() {
    return new MoveTailForward(this.previousGoal);
  }

  CentipedeHead transformToHead(IList<CentipedeFollower> tail) {
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeHead(
        center, 
        this.speed, 
        this.size,
        center,
        new Posn(center.x, center.y + 40),
        this.unit,
        tail);
  }
  
  CentipedeFollower setPreviousToLoc() {
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeFollower(this.location, this.speed, this.size, center, this.unit);
  }

}


class MoveTailForward implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {

  Posn goal;

  MoveTailForward(Posn goal) {
    this.goal = goal;
  }

  @Override
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return mt;
  }

  @Override
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    // TODO Auto-generated method stub
    return new ConsList<CentipedeFollower>(cons.first.followToGoal(goal),
        cons.first.newMoveForward().apply(cons.rest));
  }

}

class DrawTail implements IListVisitor<CentipedeFollower, WorldScene> {

  WorldScene scene;

  DrawTail(WorldScene scene) {
    this.scene = scene;
  }


  public WorldScene apply(IList<CentipedeFollower> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }


  public WorldScene visitMt(MtList<CentipedeFollower> mt) {
    // TODO Auto-generated method stub
    return scene;
  }


  public WorldScene visitCons(ConsList<CentipedeFollower> cons) {
    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }

}

class TestMovableObject {
  int speed = 10;
  int size = 20;
  Board board = new Board(5, 5);
  //CentipedeFollower f1 = new CentipedeFollower(new Posn(20, 20), speed, size);
  
}

