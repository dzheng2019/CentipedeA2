import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import tester.Tester;

interface IMoveableObject {
  Posn getGrid(int width, int row);
  IMoveableObject move(Posn direction);
  boolean collision(Board board);
  WorldImage draw();
  WorldScene drawOnBoard(WorldScene scene);
  Posn location();
  Util u = new Util();
}

abstract class AMoveableObject implements IMoveableObject {
  Posn location;
  int speed;
  int size;

  AMoveableObject(Posn location, int speed, int size) {
    this.location = location;
    this.speed = speed;
    this.size = size;
  }

  public Posn getGrid(int width, int row) {
    return u.convertAbsoluteToGrid(location, width, row);
  }

  public abstract IMoveableObject move(Posn direction);

  @Override
  public boolean collision(Board board) {
    return board.collisionOccurs(this.location, this.size);
  }

  public Posn location() {
    return this.location;
  }
  
}

class Gnome extends AMoveableObject {

  Gnome(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  public IMoveableObject move(Posn direction) {
    return new Gnome(u.moveInDirection(this.location, direction, this.speed), this.speed, this.size);
  }

  @Override
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE);
  }

  @Override
  public WorldScene drawOnBoard(WorldScene scene) {
    // TODO Auto-generated method stub
    return null;
  }

}

class Dart extends AMoveableObject {

  Dart(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  @Override
  public IMoveableObject move(Posn direction) {
    return new Dart(u.moveInDirection(this.location, direction, this.speed), this.speed, this.size);
  }


  @Override
  public WorldImage draw() {
    return null;
  }

  @Override
  public boolean collision(Board board) {
    return false;
  }

  @Override
  public WorldScene drawOnBoard(WorldScene scene) {
    // TODO Auto-generated method stub
    return null;
  }


}

class CentipedeHead extends AMoveableObject{

  IList<CentipedeSegment> tail;
  boolean isLeft;
  CentipedeHead (Posn location, int speed, int size, IList<CentipedeSegment> tail, boolean isLeft) {
    super(location, speed, size);
    this.tail = tail;
    this.isLeft = isLeft;
  }
  
  @Override
  public IMoveableObject move(Posn direction) {
    return new CentipedeHead(
        u.moveInDirection(this.location, direction, this.speed), 
        this.speed,
        this.size,
        new MoveFoward(u.moveInDirection(this.location, direction, this.speed)).apply(this.tail),
        this.isLeft);
  }

  @Override
  public WorldImage draw() {
    return null;
  }

  @Override
  public WorldScene drawOnBoard(WorldScene scene) {
    // TODO Auto-generated method stub
    return null;
  }


}

class MoveFoward implements IListVisitor<CentipedeSegment, IList<CentipedeSegment>> {

  Posn moveLocation;
  MoveFoward(Posn moveLocation) {
    this.moveLocation = moveLocation;
  }

  @Override
  public IList<CentipedeSegment> apply(IList<CentipedeSegment> arg) {
    return arg.accept(this);
  }

  @Override
  public IList<CentipedeSegment> visitMt(MtList<CentipedeSegment> mt) {
    return mt;
  }

  @Override
  public IList<CentipedeSegment> visitCons(ConsList<CentipedeSegment> cons) {
    return new ConsList<CentipedeSegment>(cons.first.moveToLocation(moveLocation), 
        new MoveFoward(cons.first.currentLocation()).apply(cons.rest));
  }



}

class CentipedeSegment extends AMoveableObject{
  CentipedeSegment(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  @Override
  public IMoveableObject move(Posn direction) {
    return this;
  }


  @Override
  public WorldImage draw() {
    return null;
  }

  Posn currentLocation() {
    return this.location;
  }

  CentipedeSegment moveToLocation(Posn location) {
    return new CentipedeSegment(location, this.speed, this.size);
  }

  @Override
  public WorldScene drawOnBoard(WorldScene scene) {
    // TODO Auto-generated method stub
    return null;
  }
}

class ExamplesMovable {
  Board b = new Board(6, 6);
  IMoveableObject p = new Gnome(new Posn(0, 240), 10, 10);
  boolean x = p.collision(b);
}


