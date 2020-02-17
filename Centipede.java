import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import tester.Tester;

abstract class ACentipedeWorld extends World {
  Random rand; 
  Board board;
  Gnome player;
  int col;  
  int row;
  IList<CentipedeHead> centipedes;


  int units = 40;

  ACentipedeWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    this.rand = rand;
    this.board = board;
    this.player = player;
    this.col = col;
    this.row = row;
    this.centipedes = centipedes;
  }

  ACentipedeWorld(int row, int col) {
    this(row, col, new Random());
  }

  ACentipedeWorld(int row, int col, Random rand) {
    this.rand = rand;
    this.col = col;
    this.row = row;
    this.board = new Board(row, col);
    this.player = new Gnome(new Posn(this.units / 2, row * this.units - this.units / 2) , 15, this.units / 2);
    this.centipedes = 
        new ConsList<CentipedeHead>(
            new CentipedeHead (
                new Posn(20, 20), 
                4, 
                this.units / 2, 
                new Posn(20, 20),
                new Posn(20, 20), 
                this.units), 
            new MtList<CentipedeHead>());
  }

  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    WorldScene sceneWithoutCenti = this.player.drawOnBoard(this.board.drawOnBoard(scene));
    return new DrawAllCentipedes(sceneWithoutCenti).apply(this.centipedes);
  }

}

class StartingWorld extends ACentipedeWorld {
  StartingWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }

  StartingWorld(int row, int col) {
    this(col, row, new Random());
    this.player = new Gnome(new Posn(20, row*40 - 20) , 40, this.units / 2);
  }

  StartingWorld(int row, int col, Random rand) {
    super(col, row, rand);
  }

  StartingWorld updateStartWorld(Board board, Gnome player) {
    return new StartingWorld(this.rand, 
        board, player, 
        this.row, this.col, this.centipedes);
  }

  Posn clickToGrid(Posn loc) {
    return new Posn(loc.x / units, this.row - (loc.y / units) - 1);
  }

  public World onKeyEvent(String key) {
    int totalUpperTiles = (this.row - 1) * this.col;
    // returns a new world where 5% of the tiles excluding the bottom row are now dandelions
    if (key.equals("D") || key.equals("d")) {
      return updateStartWorld(
          this.board.randomBoard(false, totalUpperTiles / 20, this.rand),
          this.player);
    }
    // returns a new world where 5% of the tiles excluding the bottom row are now pebbles
    if (key.equals("P") || key.equals("p")) {
      return updateStartWorld(
          this.board.randomBoard(true, totalUpperTiles / 20, this.rand),
          this.player);
    }
    // returns a new world where the tiles are just grass
    if (key.equals("R") || key.equals("r")) {
      return new StartingWorld(row, col);
    }

    //ends the world
    if (key.equals("S") || key.equals("s")) {
      return new ShootingWorld(this.rand,
          this.board, this.player.gnomeWithSpeed(6), 
          this.row, this.col, 
          this.centipedes);
    }

    // returns a new world with the player one to the right
    if (key.equals("right")) {
      return updateStartWorld(
          this.board,
          this.player.moveIfPossible(board, new Posn(1, 0)));
    }

    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return updateStartWorld(
          this.board,
          this.player.moveIfPossible(board, new Posn(-1, 0)));
    }
    return this;
  }

  //returns a new world based on the mouse event
  public World onMouseClicked(Posn pos, String buttonName) {
    Posn gridLoc = this.clickToGrid(pos);
    // does not modify the world if clicked on the bottom row
    if (gridLoc.y == 0) {
      return this;
    }
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("RightButton")) {
      return updateStartWorld(
          board.clickAtLocation(gridLoc, false),
          this.player);
    } 
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("LeftButton")) {
      return updateStartWorld(
          board.clickAtLocation(gridLoc, true),
          this.player);    }
    return this;
  }
}

abstract class APlayWorld extends ACentipedeWorld {

  APlayWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }

  abstract public World onTick();

  public World onKeyEvent(String key) {
    if (key.equals("right")) {
      return this.updatePlayWorld(this.player.moveIfPossible(this.board, new Posn(1 ,0)), this.centipedes, this.board);
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return this.updatePlayWorld(this.player.moveIfPossible(this.board, new Posn(-1 ,0)), this.centipedes, this.board);
    }
    if (key.equals(" ")) {
      return this.onSpaceEvent();
    }
    return this;
  }

  abstract World onSpaceEvent();

  abstract World updatePlayWorld(Gnome newPlayer, IList<CentipedeHead> newCentipedes, Board newBoard);

  public WorldEnd worldEnds() {
    OffMap om = new OffMap(units, row);
    boolean AnyOffMap = new OrMap<CentipedeHead>(om).apply(this.centipedes);
    if (player.collisionWithCentipedes(this.centipedes) || AnyOffMap) {
      return new WorldEnd(true, this.lastScene("YOU LOSE"));
    } 
    else if (this.centipedes.length() == 0){
      return new WorldEnd(true, this.lastScene("YOU WIN"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  public WorldScene lastScene(String msg) {
    WorldImage msgImage = new TextImage(msg, 60, Color.RED);
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    return scene.placeImageXY(msgImage, this.col * this.units / 2, this.row * this.units / 2);
  }
}

class OffMap implements IPred<CentipedeHead> {

  int units;
  int row;
  
  OffMap(int units, int row) {
    this.units = units;
    this.row = row;
  }
  
  public Boolean apply(CentipedeHead arg) {
    return arg.getGrid(units, row).y < 0;
  }
}

// has no dart on board
class ShootingWorld extends APlayWorld {

  ShootingWorld(Random rand, Board board, Gnome player, int row, int col,
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }


  public World updatePlayWorld(Gnome newPlayer, IList<CentipedeHead> newCentipedes, Board newBoard) {

    return new ShootingWorld(this.rand, board, newPlayer, this.row, this.col, 
        newCentipedes);
  }

  public World onTick() {
    return this.updatePlayWorld(
        this.player, 
        new MoveHeads(this.board).apply(this.centipedes),
        this.board);
  }


  public World onSpaceEvent() {
    return new NonShootingWorld(this.rand, this.board, this.player, this.row, this.col,
        this.centipedes, this.board.produceDart(this.player, 10));
  }

}

//has a dart on board
class NonShootingWorld extends APlayWorld {

  Dart dart;

  NonShootingWorld(Random rand, Board board, Gnome player, int row, int col,
      IList<CentipedeHead> centipedes, Dart dart) {
    super(rand, board, player, row, col, centipedes);
    this.dart = dart;
  }

  public World onTick() {
    if (this.dart.collisionWithBoard(this.board)) {
      return new ShootingWorld (this.rand, this.dart.transformBoard(this.board, true), 
          this.player, this.row, this.col, this.centipedes);
    }
    else if (this.dart.collisionWithCentipedes(this.centipedes)) {
      return new ShootingWorld(this.rand, this.dart.transformBoard(this.board, false), this.player, 
          this.row, this.col, this.dart.transformCentipedes(centipedes));
    }
    else {
      return this.updatePlayWorld(
          this.player, 
          new MoveHeads(this.board).apply(this.centipedes), 
          this.board);
    }
  }


  World onSpaceEvent() {
    return this;
  }



  World updatePlayWorld(Gnome newPlayer, IList<CentipedeHead> newCentipedes, Board newBoard) {

    return new NonShootingWorld(this.rand, board, newPlayer, this.row, this.col, 
        newCentipedes, this.dart.move(this.board));
  }

  public WorldScene makeScene() {
    WorldScene noDart = super.makeScene();
    return this.dart.drawOnBoard(noDart);
  }
}

class DrawAllCentipedes implements IListVisitor<CentipedeHead, WorldScene> {
  WorldScene scene;
  DrawAllCentipedes(WorldScene scene) {
    this.scene = scene;
  }

  public WorldScene apply(IList<CentipedeHead> arg) {

    return arg.accept(this);
  }

  public WorldScene visitMt(MtList<CentipedeHead> mt) {

    return scene;
  }

  public WorldScene visitCons(ConsList<CentipedeHead> cons) {

    return new DrawAllCentipedes(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }
}

class MoveHeads implements IListVisitor<CentipedeHead, IList<CentipedeHead>> {

  Board board;

  MoveHeads(Board board) {
    this.board = board;
  }


  public IList<CentipedeHead> apply(IList<CentipedeHead> arg) {
    return arg.accept(this);
  }


  public IList<CentipedeHead> visitMt(MtList<CentipedeHead> mt) {
    return mt;
  }


  public IList<CentipedeHead> visitCons(ConsList<CentipedeHead> cons) {
    return new ConsList<CentipedeHead>(cons.first.move(board), cons.rest.accept(this));
  } 

}

// ROW THEN COLUMN
class ExamplesGame {

  int row = 10;
  int col = 20;

  boolean testBigBang(Tester t) {
    World w = new StartingWorld(row, col);
    int worldWidth = 40 * col;
    int worldHeight = 40 * row;
    double tickRate = 1.0 / 28.0;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}


