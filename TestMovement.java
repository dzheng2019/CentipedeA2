import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.Posn;
import tester.Tester;


class TestMove extends World {
  Random rand;
  int col;  
  int row; 
  Board b;
  Gnome player;
  CentipedeSeg centipedeH;
  Dart dart;
  
  // most general constructor 
  TestMove(int col, int row, Random rand, Board b, 
      Gnome player, CentipedeSeg centipedeH, Dart dart) {
    this.rand = rand; 
    this.col = col;
    this.row = row;
    this.b = b;
    this.player = player;
    this.centipedeH = centipedeH;
    this.dart = dart;
  }

  // The constructor for use in "real" games
  TestMove(int col, int row) { 
    this(col, row, new Random());
  }

  // The constructor for use in testing, with a specified Random object
  TestMove(int col, int row, Random rand) { 
    this.rand = rand; 
    this.col = col;
    this.row = row;
    this.b = new Board(col, row).randomBoard(false, 20, new Random());
    this.player = new Gnome(new Posn(20, row*40 - 20) , 5, 15);
    this.centipedeH =  new CentipedeHead (
        new Posn(20, 20), 
        10, 
        20, 
        0,
        new Posn(1, 0),
        new Posn(1, 0));
    this.dart = new Dart(new Posn(20, row*40 - 20) , 5, 15);
  }

  TestMove updateTest(Gnome player, CentipedeSeg centipede, Dart dart) {
    return new TestMove(this.col, this.row, this.rand, this.b, player, centipede, dart);
  }

  //Constants
  int units = ITile.units;

  public WorldScene makeScene() {   
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    return centipedeH.drawOnBoard(b.drawOnBoard(scene)); 
  }

  public World onKeyEvent(String key) {
    if (key.equals("right")) {
      return this.updateTest(this.player.moveIfPossible(this.b, new Posn(1 ,0)), this.centipedeH, this.dart);
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return this.updateTest(this.player.moveIfPossible(this.b, new Posn(-1 ,0)), this.centipedeH, this.dart);
    }
    

    return this;
  }

  public World onTick() {
    //return this;
    return updateTest(this.player, this.centipedeH.move(this.b), this.dart);
  }
}  

class ExamplesMove {
  int row = 19;
  int col = 5;
  World w = new TestMove(row, col);
  
  boolean testBigBang(Tester t) {
    int worldWidth = 40 * col;
    int worldHeight = 40 * row;
    double tickRate = .05;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}