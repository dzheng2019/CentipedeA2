import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import tester.Tester;



// represents a list of ints
interface ILoInt {
  /* Template:
   * Methods:
   * this.contains(int val) - boolean
   * this.incrementAboveX(int x) - boolean
   * this.alterTiles(boolean isPebbles, ILoLoTile tiles, int width) - ILoLoTile
   * this.incrementAllByX(int x) - ILoInt
   */

  // checks if this list contains a given int
  boolean contains(int val);

  // increases all ints larger than or equal to a given value x by 1
  // used to insure a random list of unique integers
  ILoInt incrementAboveX(int x);

  // returns a ILoLoTile which is a modified version of the given tiles
  // based on this list of ints and if they should be pebbles
  ILoLoTile alterTiles(boolean isPebbles, ILoLoTile tiles, int width);

  // increases this list of ints by x
  // used to insure no element is modified in the bottom row of the grid
  ILoInt incrementAllByX(int x);
}

// represents an empty list of ints
class MtLoInt implements ILoInt {
  /* Template:
   * Same as interface template
   */

  // checks if this empty list contains a given value
  public boolean contains(int val) {
    /* Template:
     * Same as class template
     * Parameter:
     * val - int
     */
    return false;
  }

  // returns a new list of ints with all values above x increased by one
  public ILoInt incrementAboveX(int x) {
    /* Template:
     * Same as class template
     * Parameter:
     * x - int
     */
    return this;
  }

  // returns a ILoLoTile which is a modified version of the given tiles
  // based on this list of ints and if they should be pebbles 
  public ILoLoTile alterTiles(boolean isPebbles, ILoLoTile tiles, int width) {
    /* Template:
     * Same as class template
     * Parameter:
     * isPebbles - boolean
     * tiles - ILoLoTile 
     * width - int
     * Methods on parameters:
     * tiles.clickNthElementInRow(int n, int row, boolean isLeft) - ILoLoTile
     * tiles.alterNthElementInRow(int n, int row, boolean isPebbles) - ILoLoTile
     * tiles.drawAll() - WorldImage
     */
    return tiles;
  }

  // increases this list of ints by x
  public ILoInt incrementAllByX(int x) {
    /* Template:
     * Same as class template
     * Parameters:
     * x - int
     */
    return this;
  }
}

// represents a non empty list of ints
class ConsLoInt implements ILoInt {
  int first;
  ILoInt rest;
  /* Template: 
   * Same as interface template
   * Fields:
   * this.first - int
   * this.rest - ILoInt
   * Methods on fields:
   * this.rest.contains(int val) - boolean
   * this.rest.incrementAboveX(int x) - boolean
   * this.rest.alterTiles(boolean isPebbles, ILoLoTile tiles, int width) - ILoLoTile
   * this.rest.incrementAllByX(int x) - ILoInt
   */

  ConsLoInt(int first, ILoInt rest) {
    this.first = first;
    this.rest = rest;
  }

  // checks if this list contains a given value
  public boolean contains(int val) {
    /* Template:
     * Same as class template
     * Parameter:
     * val - int
     */
    if (first == val) {
      return true;
    }
    else {
      return rest.contains(val);
    }
  }

  // returns a new list of ints with all values above x increased by one
  public ILoInt incrementAboveX(int x) {
    /* Template:
     * Same as class template
     * Parameter:
     * x - int
     */
    if (first >= x) {
      return new ConsLoInt(first + 1, rest.incrementAboveX(x));
    }
    else {
      return new ConsLoInt(first, rest.incrementAboveX(x));
    }
  }

  // returns a ILoLoTile which is a modified version of the given tiles
  // based on this list of ints and if they should be pebbles 
  public ILoLoTile alterTiles(boolean isPebbles, ILoLoTile tiles, int width) {
    /* Template:
     * Same as class template
     * Parameter:
     * isPebbles - boolean
     * tiles - ILoLoTile 
     * width - int
     * Methods on parameters:
     * tiles.clickNthElementInRow(int n, int row, boolean isLeft) - ILoLoTile
     * tiles.alterNthElementInRow(int n, int row, boolean isPebbles) - ILoLoTile
     * tiles.drawAll() - WorldImage
     */
    return this.rest.alterTiles(isPebbles,
        tiles.alterNthElementInRow(this.first % width, this.first / width, isPebbles), width);
  }

  // increases this list of ints by x
  public ILoInt incrementAllByX(int x) {
    /* Template:
     * Same as class template
     * Parameter:
     * x - int
     */
    return new ConsLoInt(this.first + x, this.rest.incrementAllByX(x));
  }
}

// represents a list of tiles
interface ILoTile {
  /* Template:
   * Methods:
   * this.clickNthElement(int n, boolean isLeft) - ILoTile 
   * this.alterNthElement(int n, boolean isPebbles) - ILoTile
   * this.drawRow() - WorldImage
   */

  // returns a new ILoTile with the nth element in this ILoTile is clicked
  ILoTile clickNthElement(int n, boolean isLeft);

  // returns a new ILotile with the nth element in this ILoTIle is now a
  // Pebble of Dandelion based on isPebbles
  ILoTile alterNthElement(int n, boolean isPebbles);

  // draws this row of tiles
  WorldImage drawRow();
}

// represents an empty list of tiles
class MtLoTile implements ILoTile {
  /* Template:
   * Same as interface template
   */

  // returns a new ILoTile with the nth element in this ILoTile is clicked
  public ILoTile clickNthElement(int n, boolean isLeft) {
    return this;
  }

  // returns a new ILotile with the nth element in this ILoTIle is now a
  // Pebble of Dandelion based on isPebbles
  public ILoTile alterNthElement(int n, boolean isPebbles) {
    return this;
  }

  // draws this row of tiles
  public WorldImage drawRow() {
    return new EmptyImage();
  }
}

// represents a non empty list of tiles
class ConsLoTile implements ILoTile {
  ITile first;
  ILoTile rest;

  /* Template:
   * Same as interface template
   * Fields:
   * this.first - ITile
   * this.rest - ILoTile
   * Methods on fields:
   * this.first.click(boolean isLeft) - ITIle
   * this.first.draw() - WorldImage
   */

  ConsLoTile(ITile first, ILoTile rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns a new ILoTile with the nth element in this ILoTile is clicked
  public ILoTile clickNthElement(int n, boolean isLeft) {
    /* Template:
     * Same as class template
     * Parameters:
     * n - int 
     * isLeft - boolean
     */
    if (n == 0) {
      return new ConsLoTile(this.first.click(isLeft), this.rest);
    } 
    else {
      return new ConsLoTile(this.first, this.rest.clickNthElement(n - 1, isLeft));
    }
  }

  // returns a new ILotile with the nth element in this ILoTIle is now a
  // Pebble of Dandelion based on isPebbles
  public ILoTile alterNthElement(int n, boolean isPebbles) {
    /* Template:
     * Same as class template
     * Parameters:
     * n - int 
     * isPebbles - boolean
     */
    if (n == 0) {
      if (isPebbles) {
        return new ConsLoTile(new Pebbles(), this.rest);
      }
      else {
        return new ConsLoTile(new Dandelions(), this.rest);
      }
    }
    else {
      return new ConsLoTile(this.first, this.rest.alterNthElement(n - 1, isPebbles));
    }
  }

  // draws this row of tiles
  public WorldImage drawRow() {
    /* Template:
     * Same as class template
     */
    return new BesideImage(this.first.draw(), this.rest.drawRow());
  }
}

// represents a list of list of tiles
interface ILoLoTile {
  /* Template:
   * Methods:
   * this.clickNthElementInRow(int n, int row, boolean isLeft) - ILoLoTile
   * this.alterNthElementInRow(int n, int row, boolean isPebbles) - ILoLoTile
   * this.drawAll() - WorldImage
   */

  // returns a new ILoLoTile with the nth element in the row "row" in this ILoLoTile is clicked
  ILoLoTile clickNthElementInRow(int n, int row, boolean isLeft);

  // returns a new ILotile with the nth element in the row "row" in this ILoLoTIle is now a
  // Pebble of Dandelion based on isPebbles
  ILoLoTile alterNthElementInRow(int n, int row, boolean isPebbles);

  // draws this entire 2d grid
  WorldImage drawAll();
}

// represents an empty list of list of tiles
class MtLoLoTile implements ILoLoTile {

  /* Template:
   * Same as interface template
   */

  // returns a new ILoLoTile with the nth element in the row "row" in this ILoLoTile is clicked
  public ILoLoTile clickNthElementInRow(int n, int row, boolean isLeft) {
    /* Template:
     * Same as class template:
     * Parameters:
     * n - int
     * row - int
     * isLeft - boolean
     */
    return this;
  }

  // returns a new ILotile with the nth element in the row "row" in this ILoLoTIle is now a
  // Pebble of Dandelion based on isPebbles
  public ILoLoTile alterNthElementInRow(int n, int row, boolean isPebbles) {
    /* Template:
     * Same as class template:
     * Parameters:
     * n - int
     * row - int
     * isPebbles - boolean
     */
    return this;
  }

  // draws this entire 2d grid
  public WorldImage drawAll() {
    /* Template:
     * Same as class template
     */
    return new EmptyImage();
  }
}

// represents a non empty list of list of tiles
class ConsLoLoTile implements ILoLoTile {
  ILoTile first;
  ILoLoTile rest;

  /* Template:
   * Fields:
   * this.first - ILoTile
   * this.rest - ILoTile
   * Methods on Fields: 
   * this.first.clickNthElement(int n, boolean isLeft) - ILoTile 
   * this.first.alterNthElement(int n, boolean isPebbles) - ILoTile
   * this.first.drawRow() - WorldImage
   * this.rest.clickNthElementInRow(int n, int row, boolean isLeft) - ILoLoTile
   * this.rest.alterNthElementInRow(int n, int row, boolean isPebbles) - ILoLoTile
   * this.rest.drawAll() - WorldImage
   */

  ConsLoLoTile(ILoTile first, ILoLoTile rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns a new ILoLoTile with the nth element in the row "row" in this ILoLoTile is clicked
  public ILoLoTile clickNthElementInRow(int n, int row, boolean isLeft) {
    /* Template:
     * Same as class template:
     * Parameters:
     * n - int
     * row - int
     * isLeft - boolean
     */
    if (row == 0) {
      return new ConsLoLoTile(this.first.clickNthElement(n, isLeft), this.rest);
    } 
    else {
      return new ConsLoLoTile(this.first, this.rest.clickNthElementInRow(n, row - 1, isLeft));
    }
  }

  // returns a new ILotile with the nth element in the row "row" in this ILoLoTIle is now a
  // Pebble of Dandelion based on isPebbles
  public ILoLoTile alterNthElementInRow(int n, int row, boolean isPebbles) {
    /* Template:
     * Same as class template:
     * Parameters:
     * n - int
     * row - int
     * isPebbles - boolean
     */
    if (row == 0) {
      return new ConsLoLoTile(this.first.alterNthElement(n, isPebbles), this.rest);
    } 
    else {
      return new ConsLoLoTile(this.first, this.rest.alterNthElementInRow(n, row - 1, isPebbles));
    }
  }

  // draws this entire 2d grid
  public WorldImage drawAll() {
    /* Template:
     * Same as class template
     */
    return new AboveImage(rest.drawAll(), this.first.drawRow());
  }
}

// represents the game of Centipede
class MyGame extends World {
  Random rand;
  int width;  
  int height; 
  ILoLoTile tiles;
  Player player;

  //Constants
  int units = ITile.units;

  /* Template
   * Fields:
   * this.rand - Random
   * this.width - int
   * this.height - int
   * this.Tiles - ILoLoTiles
   * this.player - Player
   * Methods:
   * this.createRow(int cols) - ILoTile
   * this.createBoard(int rows, int cols) - ILoLoTile
   * this.clickToGrid(Posn loc) - Posn
   * this.generateRandomInts(int max, int num) - ILoInt
   * this.alterTiles(ILoInt rands, boolean isPebbles) - ILoLoTile
   * this.onKeyEvent(String key) - World
   * this.onMouseClicked(Posn pos, String buttonName) - World
   * this.makeScene() - WorldScene
   * Methods on fields:
   * this.rand.nextInt(int x) - int
   * this.tiles.clickNthElementInRow(int n, int row, boolean isLeft) - ILoLoTile
   * this.tiles.alterNthElementInRow(int n, int row, boolean isPebbles) - ILoLoTile
   * this.tiles.drawAll() - WorldImage
   * this.player.draw() - WorldImage
   * this.player.locationX() - int
   * this.player.locationY() - int
   * this.player.move(boolean isLeft) - Player
   */

  // most general constructor 
  MyGame(int width, int height, Random rand, ILoLoTile tiles, Player player) {
    this.rand = rand; 
    this.width = width;
    this.height = height;
    this.tiles = tiles;
    this.player = player;
  }

  // The constructor for use in "real" games
  MyGame(int width, int height) { 
    this(width, height, new Random());
  }

  // The constructor for use in testing, with a specified Random object
  MyGame(int width, int height, Random rand) { 
    this.rand = rand; 
    this.width = width;
    this.height = height;
    this.tiles = this.createBoard(width, height);
    this.player = new Player(0, 0);
  }

  // creates a row of tiles of a given length
  ILoTile createRow(int cols) {
    if (cols < 1) {
      return new MtLoTile();
    } 
    else {
      return new ConsLoTile(new Grass(), createRow(cols - 1));
    }
  }

  // creates a list of list of tiles with a given row and col
  ILoLoTile createBoard(int cols, int rows) {
    if (rows < 1) {
      return new MtLoLoTile();
    } 
    else {
      return new ConsLoLoTile(createRow(cols), createBoard(cols, rows - 1));
    }
  }

  // translates a location in the image to a grid location
  Posn clickToGrid(Posn loc) {
    return new Posn(loc.x / units, height - (loc.y / units) - 1);
  }

  // generates a list of random unique ints
  ILoInt generateRandomInts(int max, int num) {
    if (num < 1) {
      return new MtLoInt();
    }
    else {
      int r = rand.nextInt(max);
      return new ConsLoInt(r, generateRandomInts(max - 1, num - 1).incrementAboveX(r));
    }
  }

  // returns a new grid with a some elements based on rands
  // as Pebbles or Dandelions based on isPebbles
  ILoLoTile alterTiles(ILoInt rands, boolean isPebbles) {
    return rands.alterTiles(isPebbles, this.tiles, this.width);
  }

  // returns new worlds based on the key event
  public World onKeyEvent(String key) {
    int totalUpperTiles = (this.height - 1) * this.width;
    // returns a new world where 5% of the tiles excluding the bottom row are now dandelions
    if (key.equals("D") || key.equals("d")) {
      ILoInt rands = this.generateRandomInts(
          totalUpperTiles, totalUpperTiles / 20).incrementAllByX(this.width);
      ILoLoTile newTiles = alterTiles(rands, false);
      return new MyGame(this.width, this.height, this.rand, newTiles, this.player);
    }
    // returns a new world where 5% of the tiles excluding the bottom row are now pebbles
    if (key.equals("P") || key.equals("p")) {
      ILoInt rands = this.generateRandomInts(
          totalUpperTiles, totalUpperTiles / 20).incrementAllByX(this.width);
      ILoLoTile newTiles = alterTiles(rands, true);
      return new MyGame(this.width, this.height, this.rand, newTiles, this.player);
    }
    // returns a new world where the tiles are just grass
    if (key.equals("R") || key.equals("r")) {
      return new MyGame(this.width, this.height, this.rand, 
          this.createBoard(width, height), this.player);
    }
    // ends the world
    if (key.equals("S") || key.equals("s")) {
      return this.endOfWorld("Done");
    }
    // returns a new world with the player one to the right
    if (key.equals("right")) {
      return new MyGame(this.width, this.height, this.rand, this.tiles, this.player.move(true));
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return new MyGame(this.width, this.height, this.rand, this.tiles, this.player.move(false));
    }
    return this;
  }

  // returns a new world based on the mouse event
  public World onMouseClicked(Posn pos, String buttonName) {
    Posn gridLoc = this.clickToGrid(pos);
    // does not modify the world if clicked on the bottom row
    if (gridLoc.y == 0) {
      return this;
    }
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("RightButton")) {
      return new MyGame(this.width, this.height, this.rand, 
          this.tiles.clickNthElementInRow(gridLoc.x, gridLoc.y, false), this.player);
    } 
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("LeftButton")) {
      return new MyGame(this.width, this.height, this.rand, 
          this.tiles.clickNthElementInRow(gridLoc.x, gridLoc.y, true), this.player);
    }
    return this;
  }

  // draws this world 
  public WorldScene makeScene() {   
    WorldScene scene = new WorldScene(this.width * this.units, this.height * this.units);
    return scene.placeImageXY(
        tiles.drawAll(), this.width * this.units / 2 , this.height * this.units / 2).placeImageXY(
            player.draw(), player.locationX(), this.height * this.units - player.locationY());
  }
}

// represents example worlds
class ExamplesMyWorldProgram {
  // tests the world 
  boolean testBigBang(Tester t) {
    World w = new MyGame(5, 6);
    int worldWidth = 50 * 5;
    int worldHeight = 50 * 6;
    double tickRate = .1;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }

  // tests the click method of ITile
  boolean testClick(Tester t) {
    ITile grass = new Grass();
    ITile dandelions = new Dandelions();
    ITile pebbles = new Pebbles();
    return t.checkExpect(grass.click(true), dandelions)
        && t.checkExpect(grass.click(false), pebbles)
        && t.checkExpect(dandelions.click(true), grass)
        && t.checkExpect(dandelions.click(false), dandelions)
        && t.checkExpect(pebbles.click(true), grass)
        && t.checkExpect(pebbles.click(false), pebbles);
  }

  // tests the draw method of ITile 
  boolean testDrawITile(Tester t) {
    ITile grass = new Grass();
    ITile dandelions = new Dandelions();
    ITile pebbles = new Pebbles();
    int units = ITile.units;
    return 
        t.checkExpect(grass.draw(), 
            new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(units, units, OutlineMode.SOLID, Color.GREEN)))
        && t.checkExpect(dandelions.draw(), 
            new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(units, units, OutlineMode.SOLID, Color.YELLOW)))
        && t.checkExpect(pebbles.draw(), 
            new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(units, units, OutlineMode.SOLID, Color.GRAY)));
  }

  // tests the draw method of Player
  boolean testDrawPlayer(Tester t) {
    Player p1 = new Player(0, 0);
    Player p2 = new Player(5, 4);
    Player p3 = new Player(-2, 1);
    int units = ITile.units;
    return 
        t.checkExpect(p1.draw(), 
            (WorldImage) new CircleImage(units / 3, OutlineMode.SOLID, Color.MAGENTA))
        && t.checkExpect(p2.draw(), 
            (WorldImage) new CircleImage(units / 3, OutlineMode.SOLID, Color.MAGENTA))
        && t.checkExpect(p3.draw(), 
            (WorldImage) new CircleImage(units / 3, OutlineMode.SOLID, Color.MAGENTA));
  }

  // test locationX method of Player
  boolean testLocationX(Tester t) {
    Player p1 = new Player(0, 0);
    Player p2 = new Player(5, 4);
    Player p3 = new Player(-2, 1);
    return t.checkExpect(p1.locationX(), 25)
        && t.checkExpect(p2.locationX(), 275)
        && t.checkExpect(p3.locationX(), -75);
  }

  // test locationY method of Player
  boolean testLocationY(Tester t) {
    Player p1 = new Player(0, 0);
    Player p2 = new Player(5, 4);
    Player p3 = new Player(-2, 1);
    return t.checkExpect(p1.locationY(), 25)
        && t.checkExpect(p2.locationY(), 225)
        && t.checkExpect(p3.locationY(), 75);
  }

  // test move method of Player
  boolean testMove(Tester t) {
    Player p1 = new Player(0, 0);
    Player p2 = new Player(5, 4);
    Player p3 = new Player(-2, 1);
    return t.checkExpect(p1.move(false), new Player(-1, 0))
        && t.checkExpect(p1.move(true), new Player(1, 0))
        && t.checkExpect(p2.move(false), new Player(4, 4))
        && t.checkExpect(p2.move(true), new Player(6, 4))
        && t.checkExpect(p3.move(false), new Player(-3, 1))
        && t.checkExpect(p3.move(true), new Player(-1, 1));
  }

  // tests contains method in ILoInt
  boolean testContains(Tester t) {
    ILoInt ListOf123 =
        new ConsLoInt(1, 
            new ConsLoInt(2, 
                new ConsLoInt(3, new MtLoInt())));
    ILoInt empty = new MtLoInt();
    return t.checkExpect(ListOf123.contains(1), true)
        && t.checkExpect(ListOf123.contains(4), false)
        && t.checkExpect(empty.contains(1), false);
  }

  // test incrementAboveX method in ILoInt
  boolean testIncrementAboveX(Tester t) {
    ILoInt ListOf123 =
        new ConsLoInt(1, 
            new ConsLoInt(2, 
                new ConsLoInt(3, new MtLoInt())));
    ILoInt ListOf234 =
        new ConsLoInt(2, 
            new ConsLoInt(3, 
                new ConsLoInt(4, new MtLoInt())));
    ILoInt ListOf134 =
        new ConsLoInt(1, 
            new ConsLoInt(3, 
                new ConsLoInt(4, new MtLoInt())));
    ILoInt empty = new MtLoInt();
    return t.checkExpect(ListOf123.incrementAboveX(1), ListOf234)
        && t.checkExpect(ListOf123.incrementAboveX(2), ListOf134)
        && t.checkExpect(ListOf123.incrementAboveX(5), ListOf123)
        && t.checkExpect(empty.incrementAboveX(1), empty);
  }

  // test alterTiles method in ILoInt
  boolean testAlterTiles(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile row = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoLoTile empty = new MtLoLoTile();
    ILoLoTile board =
        new ConsLoLoTile(row,
            new ConsLoLoTile(row, empty));
    ILoTile row1D = 
        new ConsLoTile(dande, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3D = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3P = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoInt mod03 = 
        new ConsLoInt(0,
            new ConsLoInt(3, new MtLoInt()));
    ILoInt mod05 = 
        new ConsLoInt(0,
            new ConsLoInt(5, new MtLoInt()));
    ILoInt mod0 = 
        new ConsLoInt(0, new MtLoInt());
    ILoInt mod = new MtLoInt();
    return t.checkExpect(mod03.alterTiles(true, board, 3), 
        new ConsLoLoTile(row1P,
            new ConsLoLoTile(row1P, empty)))
        && t.checkExpect(mod03.alterTiles(false, board, 3), 
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(row1D, empty)))
        && t.checkExpect(mod05.alterTiles(true, board, 3), 
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(row3P, empty)))
        && t.checkExpect(mod05.alterTiles(false, board, 3), 
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(row3D, empty)))
        && t.checkExpect(mod0.alterTiles(true, board, 3), 
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(row, empty)))
        && t.checkExpect(mod0.alterTiles(false, board, 3), 
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(row, empty)))
        && t.checkExpect(mod.alterTiles(true, board, 3), board)
        && t.checkExpect(mod.alterTiles(false, board, 3), board);
  }

  // test incrementAllByX method in ILoInt
  boolean testIncrementAllByX(Tester t) {
    ILoInt ListOf123 =
        new ConsLoInt(1, 
            new ConsLoInt(2, 
                new ConsLoInt(3, new MtLoInt())));
    ILoInt ListOf234 =
        new ConsLoInt(2, 
            new ConsLoInt(3, 
                new ConsLoInt(4, new MtLoInt())));
    ILoInt ListOf345 =
        new ConsLoInt(3, 
            new ConsLoInt(4, 
                new ConsLoInt(5, new MtLoInt())));
    ILoInt empty = new MtLoInt();
    return t.checkExpect(empty.incrementAllByX(10), empty)
        && t.checkExpect(ListOf123.incrementAllByX(1), ListOf234)
        && t.checkExpect(ListOf123.incrementAllByX(2), ListOf345)
        && t.checkExpect(ListOf234.incrementAllByX(1), ListOf345)
        && t.checkExpect(ListOf234.incrementAllByX(0), ListOf234);
  }

  // test clickNthElement method in ILoTile
  boolean testClickNthElement(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile rowG = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile rowD = 
        new ConsLoTile(dande, 
            new ConsLoTile(dande, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile rowP = 
        new ConsLoTile(peb, 
            new ConsLoTile(peb, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile row2GD = 
        new ConsLoTile(dande, 
            new ConsLoTile(grass, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile row2GP = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile row1D = 
        new ConsLoTile(dande, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row2D = 
        new ConsLoTile(grass, 
            new ConsLoTile(dande, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3D = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row2P = 
        new ConsLoTile(grass, 
            new ConsLoTile(peb, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3P = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile empty = new MtLoTile();
    return t.checkExpect(rowG.clickNthElement(0, true), row1D)
        && t.checkExpect(rowG.clickNthElement(1, true), row2D)
        && t.checkExpect(rowG.clickNthElement(2, true), row3D)
        && t.checkExpect(rowG.clickNthElement(0, false), row1P)
        && t.checkExpect(rowG.clickNthElement(1, false), row2P)
        && t.checkExpect(rowG.clickNthElement(2, false), row3P)
        && t.checkExpect(rowD.clickNthElement(1, true), row2GD)
        && t.checkExpect(rowD.clickNthElement(1, false), rowD)
        && t.checkExpect(rowP.clickNthElement(1, true), row2GP)
        && t.checkExpect(rowP.clickNthElement(1, false), rowP)
        && t.checkExpect(empty.clickNthElement(1, true), empty)
        && t.checkExpect(empty.clickNthElement(1, true), empty);
  }

  // test alterNthElement method of ILoTile
  boolean testAlterNthElement(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile rowG = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile rowD = 
        new ConsLoTile(dande, 
            new ConsLoTile(dande, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile rowP = 
        new ConsLoTile(peb, 
            new ConsLoTile(peb, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile row2PD = 
        new ConsLoTile(dande, 
            new ConsLoTile(peb, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile row2DP = 
        new ConsLoTile(peb, 
            new ConsLoTile(dande, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile row1D = 
        new ConsLoTile(dande, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row2D = 
        new ConsLoTile(grass, 
            new ConsLoTile(dande, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3D = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(dande, new MtLoTile())));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row2P = 
        new ConsLoTile(grass, 
            new ConsLoTile(peb, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3P = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile empty = new MtLoTile();
    return t.checkExpect(rowG.alterNthElement(0, true), row1P)
        && t.checkExpect(rowG.alterNthElement(1, true), row2P)
        && t.checkExpect(rowG.alterNthElement(2, true), row3P)
        && t.checkExpect(rowG.alterNthElement(0, false), row1D)
        && t.checkExpect(rowG.alterNthElement(1, false), row2D)
        && t.checkExpect(rowG.alterNthElement(2, false), row3D)
        && t.checkExpect(rowD.alterNthElement(1, true), row2PD)
        && t.checkExpect(rowD.alterNthElement(1, false), rowD)
        && t.checkExpect(rowP.alterNthElement(1, true), rowP)
        && t.checkExpect(rowP.alterNthElement(1, false), row2DP)
        && t.checkExpect(empty.alterNthElement(1, true), empty)
        && t.checkExpect(empty.alterNthElement(1, true), empty);
  }

  // test drawRow method of ILoTile
  boolean testDrawRow(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile rowGDP = 
        new ConsLoTile(grass, 
            new ConsLoTile(dande, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoTile empty = new MtLoTile();
    return t.checkExpect(empty.drawRow(), new EmptyImage())
        && t.checkExpect(rowGDP.drawRow(), 
            new BesideImage(grass.draw(), 
                new BesideImage(dande.draw(), 
                    new BesideImage(peb.draw(), new EmptyImage()))));
  }

  // test clickNthElementInRow in ILoLoTile
  boolean testClickNthElementInRow(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile rowG =  
        new ConsLoTile(grass, 
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile rowD =  
        new ConsLoTile(dande, 
            new ConsLoTile(dande, new MtLoTile()));
    ILoTile rowP =  
        new ConsLoTile(peb, 
            new ConsLoTile(peb, new MtLoTile()));
    ILoLoTile empty = new MtLoLoTile();
    ILoLoTile boardG =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowG, empty));
    ILoLoTile boardD =
        new ConsLoLoTile(rowD,
            new ConsLoLoTile(rowD, empty));
    ILoLoTile boardP =
        new ConsLoLoTile(rowP,
            new ConsLoLoTile(rowP, empty));
    ILoTile row1D = 
        new ConsLoTile(dande,  
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile row2D = 
        new ConsLoTile(grass, 
            new ConsLoTile(dande, new MtLoTile()));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile row2P = 
        new ConsLoTile(grass,  
            new ConsLoTile(peb, new MtLoTile()));
    return t.checkExpect(empty.clickNthElementInRow(0, 0, false), empty)
        && t.checkExpect(boardG.clickNthElementInRow(0, 0, true),  
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.clickNthElementInRow(0, 0, false),  
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.clickNthElementInRow(0, 0, true),  
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.clickNthElementInRow(1, 1, false),  
            new ConsLoLoTile(rowG,
                new ConsLoLoTile(row2P, empty)))
        && t.checkExpect(boardG.clickNthElementInRow(1, 1, true),  
            new ConsLoLoTile(rowG,
                new ConsLoLoTile(row2D, empty)))
        && t.checkExpect(boardD.clickNthElementInRow(0, 1, true),  
            new ConsLoLoTile(rowD,
                new ConsLoLoTile(row2D, empty)))
        && t.checkExpect(boardP.clickNthElementInRow(0, 1, true),  
            new ConsLoLoTile(rowP,
                new ConsLoLoTile(row2P, empty)))
        && t.checkExpect(boardD.clickNthElementInRow(0, 1, false),  
            new ConsLoLoTile(rowD,
                new ConsLoLoTile(rowD, empty)))
        && t.checkExpect(boardP.clickNthElementInRow(0, 1, false),  
            new ConsLoLoTile(rowP,
                new ConsLoLoTile(rowP, empty)));
  }

  // test alterNthElementInRow in ILoLoTile
  boolean testAlterNthElementInRow(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile rowG =  
        new ConsLoTile(grass, 
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile rowD =  
        new ConsLoTile(dande, 
            new ConsLoTile(dande, new MtLoTile()));
    ILoTile rowP =  
        new ConsLoTile(peb, 
            new ConsLoTile(peb, new MtLoTile()));
    ILoLoTile empty = new MtLoLoTile();
    ILoLoTile boardG =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowG, empty));
    ILoLoTile boardD =
        new ConsLoLoTile(rowD,
            new ConsLoLoTile(rowD, empty));
    ILoLoTile boardP =
        new ConsLoLoTile(rowP,
            new ConsLoLoTile(rowP, empty));
    ILoTile row1D = 
        new ConsLoTile(dande,  
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile row2D = 
        new ConsLoTile(grass, 
            new ConsLoTile(dande, new MtLoTile()));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, new MtLoTile()));
    ILoTile row2P = 
        new ConsLoTile(grass,  
            new ConsLoTile(peb, new MtLoTile()));
    return t.checkExpect(empty.alterNthElementInRow(0, 0, false), empty)
        && t.checkExpect(boardG.alterNthElementInRow(0, 0, true),  
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.alterNthElementInRow(0, 0, false),  
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.alterNthElementInRow(0, 0, true),  
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(rowG, empty)))
        && t.checkExpect(boardG.alterNthElementInRow(1, 1, false),  
            new ConsLoLoTile(rowG,
                new ConsLoLoTile(row2D, empty)))
        && t.checkExpect(boardG.alterNthElementInRow(1, 1, true),  
            new ConsLoLoTile(rowG,
                new ConsLoLoTile(row2P, empty)))
        && t.checkExpect(boardD.alterNthElementInRow(0, 1, true),  
            new ConsLoLoTile(rowD,
                new ConsLoLoTile(new ConsLoTile(peb, 
                    new ConsLoTile(dande, new MtLoTile())), empty)))
        && t.checkExpect(boardP.alterNthElementInRow(0, 1, true),  
            new ConsLoLoTile(rowP,
                new ConsLoLoTile(rowP, empty)))
        && t.checkExpect(boardD.alterNthElementInRow(0, 1, false),  
            new ConsLoLoTile(rowD,
                new ConsLoLoTile(rowD, empty)))
        && t.checkExpect(boardP.alterNthElementInRow(0, 1, false),  
            new ConsLoLoTile(rowP,
                new ConsLoLoTile(new ConsLoTile(dande, 
                    new ConsLoTile(peb, new MtLoTile())), empty)));
  }

  // TODO: test drawAll in ILoLoTile

  // test createRow in MyGame
  boolean testCreateRow(Tester t) {
    // Different Game Instances
    MyGame game1 = new MyGame(20, 20);
    MyGame game2 = new MyGame(15, 2);
    // Different Length Rows
    ILoTile empty = new MtLoTile();
    ILoTile row1 = new ConsLoTile(new Grass(), empty);
    ILoTile row2 = new ConsLoTile(new Grass(), row1);
    ILoTile row3 = new ConsLoTile(new Grass(), row2);
    ILoTile row4 = new ConsLoTile(new Grass(), row3);
    ILoTile row5 = new ConsLoTile(new Grass(), row4);

    return t.checkExpect(game1.createRow(0), empty)
        && t.checkExpect(game1.createRow(1), row1)
        && t.checkExpect(game1.createRow(2), row2)
        && t.checkExpect(game1.createRow(3), row3)
        && t.checkExpect(game1.createRow(4), row4)
        && t.checkExpect(game1.createRow(5), row5)
        && t.checkExpect(game2.createRow(0), empty)
        && t.checkExpect(game2.createRow(1), row1)
        && t.checkExpect(game2.createRow(2), row2)
        && t.checkExpect(game2.createRow(3), row3)
        && t.checkExpect(game2.createRow(4), row4)
        && t.checkExpect(game2.createRow(5), row5);
  }

  // test createBoard in MyGame
  boolean testcreateBoard(Tester t) {
    // Different Game Instances
    MyGame game1 = new MyGame(20, 20);
    MyGame game2 = new MyGame(15, 2);
    // Different Length Rows
    ILoTile empty1 = new MtLoTile();
    ILoLoTile empty2 = new MtLoLoTile();
    ILoTile row1 = new ConsLoTile(new Grass(), empty1);
    ILoLoTile col1 = new ConsLoLoTile(row1, empty2);
    ILoTile row2 = new ConsLoTile(new Grass(), row1);
    ILoLoTile col2 = new ConsLoLoTile(row2, 
        new ConsLoLoTile(row2, empty2));
    ILoTile row3 = new ConsLoTile(new Grass(), row2);
    ILoLoTile col3 = new ConsLoLoTile(row3, 
        new ConsLoLoTile(row3, 
            new ConsLoLoTile(row3, empty2)));
    ILoTile row4 = new ConsLoTile(new Grass(), row3);
    ILoLoTile col23 = new ConsLoLoTile(row3, 
        new ConsLoLoTile(row3, empty2));
    ILoLoTile col24 = new ConsLoLoTile(row4, 
        new ConsLoLoTile(row4, empty2));
    ILoLoTile col4 = new ConsLoLoTile(row4, 
        new ConsLoLoTile(row4, 
            new ConsLoLoTile(row4, 
                new ConsLoLoTile(row4, empty2))));
    ILoTile row5 = new ConsLoTile(new Grass(), row4);
    ILoLoTile col5 = new ConsLoLoTile(row5, 
        new ConsLoLoTile(row5,
            new ConsLoLoTile(row5, 
                new ConsLoLoTile(row5, 
                    new ConsLoLoTile(row5, empty2)))));
    ILoLoTile col54 = new ConsLoLoTile(row4, 
        new ConsLoLoTile(row4,
            new ConsLoLoTile(row4, 
                new ConsLoLoTile(row4, 
                    new ConsLoLoTile(row4, empty2)))));
    ILoLoTile col53 = new ConsLoLoTile(row3, 
        new ConsLoLoTile(row3,
            new ConsLoLoTile(row3, 
                new ConsLoLoTile(row3, 
                    new ConsLoLoTile(row3, empty2)))));

    return t.checkExpect(game1.createBoard(1, 1), col1)
        && t.checkExpect(game1.createBoard(2, 2), col2)
        && t.checkExpect(game1.createBoard(3, 2), col23)
        && t.checkExpect(game1.createBoard(4, 2), col24)
        && t.checkExpect(game1.createBoard(3, 3), col3)
        && t.checkExpect(game1.createBoard(4, 4), col4)
        && t.checkExpect(game1.createBoard(5, 5), col5)
        && t.checkExpect(game1.createBoard(4, 5), col54)
        && t.checkExpect(game1.createBoard(3, 5), col53)
        && t.checkExpect(game2.createBoard(1, 1), col1);
  }

  // test clickToGrid in MyGame
  boolean testClickToGrid(Tester t) {
    // Different Game Instances
    MyGame game1 = new MyGame(20, 20);
    MyGame game2 = new MyGame(15, 2);
    // Possible Posn Values
    Posn origin = new Posn(0,0);
    Posn q1 = new Posn(1, 1);
    Posn q11 = new Posn(1, 200);
    Posn q12 = new Posn(200, 200);
    Posn q13 = new Posn(200, 1);
    // Out of Bounds Cases
    Posn q2 = new Posn(-987, 123);
    Posn q3 = new Posn(-123, -321);
    Posn q4 = new Posn(666, -420);

    return t.checkExpect(game1.clickToGrid(origin), new Posn(0, 19))
        && t.checkExpect(game1.clickToGrid(q1), new Posn(0, 19))
        && t.checkExpect(game1.clickToGrid(q11), new Posn(0, 15))
        && t.checkExpect(game1.clickToGrid(q12), new Posn(4, 15))
        && t.checkExpect(game1.clickToGrid(q13), new Posn(4, 19))
        && t.checkExpect(game1.clickToGrid(q2), new Posn(-19, 17))
        && t.checkExpect(game1.clickToGrid(q3), new Posn(-2, 25))
        && t.checkExpect(game1.clickToGrid(q4), new Posn(13, 27))
        && t.checkExpect(game2.clickToGrid(origin), new Posn(0, 1))
        && t.checkExpect(game2.clickToGrid(q1), new Posn(0, 1))
        && t.checkExpect(game2.clickToGrid(q11), new Posn(0, -3))
        && t.checkExpect(game2.clickToGrid(q12), new Posn(4, -3))
        && t.checkExpect(game2.clickToGrid(q13), new Posn(4, 1))
        && t.checkExpect(game2.clickToGrid(q2), new Posn(-19, -1))
        && t.checkExpect(game2.clickToGrid(q3), new Posn(-2, 7))
        && t.checkExpect(game2.clickToGrid(q4), new Posn(13, 9));
  }

  // test generateRandomInts in MyGame
  boolean testGenerateRandomInts(Tester t) {
    // Different Game Instances
    MyGame game11 = new MyGame(20, 20, new Random(123));
    MyGame game21 = new MyGame(15, 2, new Random(321));
    MyGame game12 = new MyGame(20, 20, new Random(123));
    MyGame game22 = new MyGame(15, 2, new Random(321));

    ILoInt rand11 = game11.generateRandomInts(20, 17);
    ILoInt rand12 = game11.generateRandomInts(20, 17);
    ILoInt rand13 = game11.generateRandomInts(4, 1);
    ILoInt rand21 = game21.generateRandomInts(15, 15);
    ILoInt rand22 = game21.generateRandomInts(15, 15);
    ILoInt rand23 = game21.generateRandomInts(2, 1);

    return t.checkExpect(game11.generateRandomInts(1, 1), 
        new ConsLoInt(0, new MtLoInt()))
        && t.checkExpect(game11.generateRandomInts(2, 2), 
            new ConsLoInt(1, new ConsLoInt(0, new MtLoInt())))
        && t.checkExpect(game11.generateRandomInts(2, 2), 
            new ConsLoInt(0, new ConsLoInt(1, new MtLoInt())))
        && t.checkExpect(game12.generateRandomInts(20, 17), rand11)
        && t.checkExpect(game12.generateRandomInts(20, 17), rand12)
        && t.checkExpect(game12.generateRandomInts(4, 1), rand13)
        && t.checkExpect(game22.generateRandomInts(15, 15), rand21)
        && t.checkExpect(game22.generateRandomInts(15, 15), rand22)
        && t.checkExpect(game22.generateRandomInts(2, 1), rand23);
  }

  // test alterTiles in MyGame
  boolean testAlterTilesInGame(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoTile row = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoLoTile empty = new MtLoLoTile();
    ILoLoTile board =
        new ConsLoLoTile(row,
            new ConsLoLoTile(row, empty));
    ILoTile row1D = 
        new ConsLoTile(dande, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row1P = 
        new ConsLoTile(peb, 
            new ConsLoTile(grass, 
                new ConsLoTile(grass, new MtLoTile())));
    ILoTile row3P = 
        new ConsLoTile(grass, 
            new ConsLoTile(grass, 
                new ConsLoTile(peb, new MtLoTile())));
    ILoInt mod03 = 
        new ConsLoInt(0,
            new ConsLoInt(3, new MtLoInt()));
    ILoInt mod05 = 
        new ConsLoInt(0,
            new ConsLoInt(5, new MtLoInt()));
    ILoInt mod = new MtLoInt();
    MyGame game = new MyGame(3, 3, new Random(), board, new Player(0,0));

    return t.checkExpect(game.alterTiles(mod03, true), 
        new ConsLoLoTile(row1P,
            new ConsLoLoTile(row1P, empty)))
        && t.checkExpect(game.alterTiles(mod03, false), 
            new ConsLoLoTile(row1D,
                new ConsLoLoTile(row1D, empty)))
        && t.checkExpect(game.alterTiles(mod05, true), 
            new ConsLoLoTile(row1P,
                new ConsLoLoTile(row3P, empty)))
        && t.checkExpect(game.alterTiles(mod, true), board)
        && t.checkExpect(game.alterTiles(mod, false), board);
  }

  // test onKeyEvent in MyGame
  boolean testOnKeyEvent(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ILoLoTile empty = new MtLoLoTile();
    ILoTile rowG = 
        new ConsLoTile(grass, new MtLoTile());
    ILoTile rowD = 
        new ConsLoTile(dande, new MtLoTile());
    ILoLoTile boardGD =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowD, empty));
    MyGame game = new MyGame(1, 2,  new Random(123), boardGD, new Player(0,0));
    MyGame gameG = new MyGame(1, 2,  new Random(123), 
        game.createBoard(1, 2), new Player(0,0));
    return t.checkExpect(game.onKeyEvent("y"), game)
        && t.checkExpect(game.onKeyEvent("d"), game)
        && t.checkExpect(game.onKeyEvent("p"), game)
        && t.checkExpect(game.onKeyEvent("r"), gameG)
        && t.checkExpect(game.onKeyEvent("s"), game.endOfWorld("Done"))
        && t.checkExpect(game.onKeyEvent("right"), 
            new MyGame(1, 2, new Random(123), boardGD, new Player(0,0).move(true)))
        && t.checkExpect(game.onKeyEvent("left"), 
            new MyGame(1, 2,  new Random(123), boardGD, new Player(0,0).move(false)));
  }

  // test onMouseClicked in MyGame
  boolean testOnMouSeClicked(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoLoTile empty = new MtLoLoTile();
    ILoTile rowG = 
        new ConsLoTile(grass, new MtLoTile());
    ILoTile rowD = 
        new ConsLoTile(dande, new MtLoTile());
    ILoTile rowP = 
        new ConsLoTile(peb, new MtLoTile());
    ILoLoTile boardG =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowG, empty));
    ILoLoTile boardGD =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowD, empty));
    ILoLoTile boardGP =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowP, empty));
    MyGame gameGP = new MyGame(1, 2,  new Random(123), boardGP, new Player(0,0));
    MyGame gameGD = new MyGame(1, 2,  new Random(123), boardGD, new Player(0,0));
    MyGame gameG = new MyGame(1, 2,  new Random(123), boardG, new Player(0,0));
    return t.checkExpect(gameG.onMouseClicked(new Posn(25, 25), "RightButton"), gameGP)
        && t.checkExpect(gameG.onMouseClicked(new Posn(25, 25), "LeftButton"), gameGD)
        && t.checkExpect(gameGP.onMouseClicked(new Posn(25, 25), "RightButton"), gameGP)
        && t.checkExpect(gameGP.onMouseClicked(new Posn(25, 25), "LeftButton"), gameG)
        && t.checkExpect(gameGD.onMouseClicked(new Posn(25, 25), "RightButton"), gameGD)
        && t.checkExpect(gameGD.onMouseClicked(new Posn(25, 25), "LeftButton"), gameG)
        && t.checkExpect(gameG.onMouseClicked(new Posn(25, 55), "RightButton"), gameG)
        && t.checkExpect(gameG.onMouseClicked(new Posn(25, 55), "LeftButton"), gameG)
        && t.checkExpect(gameGP.onMouseClicked(new Posn(25, 55), "RightButton"), gameGP)
        && t.checkExpect(gameGP.onMouseClicked(new Posn(25, 55), "LeftButton"), gameGP)
        && t.checkExpect(gameGD.onMouseClicked(new Posn(25, 55), "RightButton"), gameGD)
        && t.checkExpect(gameGD.onMouseClicked(new Posn(25, 55), "LeftButton"), gameGD);
  }

  // test makeScene in MyGame
  boolean testMakeScene(Tester t) {
    ITile grass = new Grass();
    ITile dande = new Dandelions();
    ITile peb = new Pebbles();
    ILoLoTile empty = new MtLoLoTile();
    ILoTile rowG = 
        new ConsLoTile(grass, new MtLoTile());
    ILoTile rowD = 
        new ConsLoTile(dande, new MtLoTile());
    ILoTile rowP = 
        new ConsLoTile(peb, new MtLoTile());
    ILoLoTile boardG =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowG, empty));
    ILoLoTile boardGD =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowD, empty));
    ILoLoTile boardGP =
        new ConsLoLoTile(rowG,
            new ConsLoLoTile(rowP, empty));
    int units = ITile.units;
    int width = 1;
    int height = 2;
    Player player = new Player(0,0);
    WorldScene scene = new WorldScene(width * units, height * units); 
    MyGame gameGP = new MyGame(width, height,  new Random(123), boardGP, player);
    MyGame gameGD = new MyGame(width, height,  new Random(123), boardGD, player);
    MyGame gameG = new MyGame(width, height,  new Random(123), boardG, player);
    /* new WorldScene(this.width * this.units, this.height * this.units);
    return scene.placeImageXY(
        tiles.drawAll(), this.width * this.units / 2 , this.height * this.units / 2).placeImageXY(
            player.draw(), player.locationX(), this.height * this.units - player.locationY())
     */
    return t.checkExpect(gameGP.makeScene(), 
        scene.placeImageXY(
            boardGP.drawAll(), width * units / 2 , height * units / 2).placeImageXY(
                player.draw(), player.locationX(), height * units - player.locationY()))
        && t.checkExpect(gameGD.makeScene(), 
            scene.placeImageXY(
                boardGD.drawAll(), width * units / 2 , height * units / 2).placeImageXY(
                    player.draw(), player.locationX(), height * units - player.locationY()))
        && t.checkExpect(gameG.makeScene(), 
            scene.placeImageXY(
                boardG.drawAll(), width * units / 2 , height * units / 2).placeImageXY(
                    player.draw(), player.locationX(), height * units - player.locationY()));
  }
}