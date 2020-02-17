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



class TestMovableObject {
  int speed = 10;
  int size = 20;
  Board board = new Board(5, 5);
  //CentipedeFollower f1 = new CentipedeFollower(new Posn(20, 20), speed, size);
  
}

