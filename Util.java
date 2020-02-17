import javalib.worldimages.Posn;
import java.util.*;
import tester.Tester;

class Util {
  // converts a absolute grid position to a grid location 
  Posn convertAbsoluteToGrid(Posn location, int unit, int row) {   
    /* Template:
     * Parameters:
     * location - Posn
     * unit - int
     * row - int
     * Methods/Fields of Parameters:
     * location.x - int
     * location.y - int
     */
    if (unit < 1)
      throw new IllegalArgumentException("Units have to be positive");

    return new Posn(location.x / unit, row - (int)Math.floor((double) location.y / (double) unit) - 1);
  }
  
  Posn flipX(Posn toFlip) {
    return new Posn(-1 * toFlip.x, toFlip.y); 
  }
  

  // returns a new posn which is the given posn moved in a given direction by 
  // a given amound (speed)
  Posn moveInDirection(Posn location, Posn direction, int speed) {
    /* Template:
     * Parameters:
     * location - Posn
     * direction - Posn
     * speed - int
     * Methods/Fields of Parameters:
     * location.x - int
     * location.y - int
     * direction.x - int
     * direction.y - int
     */
    return new Posn(location.x + direction.x*speed, location.y + direction.y*speed);
  }

  // checks if two posns are equal based on their coordinates
  boolean posnEqual(Posn p1, Posn p2) {
    /* Template:
     * Parameters:
     * p1 - Posn
     * p2 - Posn
     * Methods/Fields of Parameters:
     * p1.x - int
     * p1.y - int
     * p2.x - int
     * p2.y - int
     */
    return (p1.x == p2.x) && (p1.y == p2.y);
  }

  // checks if the distance between p1 and p2 is less than the given radius
  boolean posnInRadius(Posn p1, Posn p2, int radius) {
    /* Template:
     * Parameters:
     * p1 - Posn
     * p2 - Posn
     * radius - int
     * Methods/Fields of Parameters:
     * p1.x - int
     * p1.y - int
     * p2.x - int
     * p2.y - int
     */
    double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    return dist < radius;
  }

  // creates a list of random unique integers that is num long and
  // all integers are below max
  IList<Integer> createRandomInts(int num, int max, Random rand) {
    /* Template:
     * Parameters:
     * num - int
     * max - int 
     * rand - Random
     * Methods on Parameters:
     * rand.nextInt(int max) - int
     */
    if (num < 1 || max < 1) {
      return new MtList<Integer>();
    }
    else {
      int r = rand.nextInt(max);
      return new ConsList<Integer>(
          r, new IncrementAboveX(r).apply(
              createRandomInts(num - 1, max - 1, rand)));
    }
  }
  
  // given a location, finds the center of the grid cell the location is in 
  Posn getCenter(Posn pos, int unit) {
    /* Template:
     * Parameters:
     * pos - Posn
     * unit - int
     * Methods/Fields of Parameters:
     * pos.x - int
     * pos.y - int
     */
    int centerX = pos.x/unit * unit + unit/2;
    int centerY = pos.y/unit * unit + unit/2;
    return new Posn(centerX, centerY);
  }
  
}

class ExamplesUtil {
  Util u = new Util();

  // test convertAbsoluteToGrid in Util
  void testConvertAbsToGrid(Tester t) {
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(0, 0), 40, 5), new Posn(0, 4));
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(0, 180), 40, 5), new Posn(0, 0));
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(180, 0), 40, 5), new Posn(4, 4));
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(180, 180), 40, 5), new Posn(4, 0));
    //Issues with using checkException, but this should pass
    //    t.checkException(
    //        new IllegalArgumentException("Units have to be positive"), 
    //        "Util", "convertAbsoluteToGrid", new Posn(0, 180), 0, 5);

  }
  
  // test flipX in Util
  void testFlipX(Tester t) {
    t.checkExpect(u.flipX(new Posn(1, 0)), new Posn(-1, 0));
    t.checkExpect(u.flipX(new Posn(0, 0)), new Posn(0, 0));
    t.checkExpect(u.flipX(new Posn(-1, 0)), new Posn(1, 0));
    t.checkExpect(u.flipX(new Posn(1, 1)), new Posn(-1, 1));
    t.checkExpect(u.flipX(new Posn(0, 1)), new Posn(0, 1));
    t.checkExpect(u.flipX(new Posn(-1, 1)), new Posn(1, 1));
  }

  // test moveInDirection in Util
  void testMoveInDirection(Tester t) {
    t.checkExpect(u.moveInDirection(new Posn(0, 0), new Posn(1, 0), 15), new Posn(15, 0));
    t.checkExpect(u.moveInDirection(new Posn(15, 0), new Posn(1, 0), 15), new Posn(30, 0));
    t.checkExpect(u.moveInDirection(new Posn(0, 0), new Posn(0, 1), 15), new Posn(0, 15));
    t.checkExpect(u.moveInDirection(new Posn(0, 15), new Posn(0, -1), 15), new Posn(0, 0));    
  }

  // test posnEqual in Util
  void testPosnEqual(Tester t) {
    t.checkExpect(u.posnEqual(new Posn(0, 0), new Posn(0, 0)), true);
    t.checkExpect(u.posnEqual(new Posn(5, 0), new Posn(5, 0)), true);
    t.checkExpect(u.posnEqual(new Posn(5, 0), new Posn(0, 0)), false);
    t.checkExpect(u.posnEqual(new Posn(0, 5), new Posn(5, 0)), false);
    
  }

  // test posnInRadius in Util
  void testPosnInRadius(Tester t) {
    t.checkExpect(u.posnInRadius(new Posn(0, 0), new Posn(0, 1), 2), true);
    t.checkExpect(u.posnInRadius(new Posn(5, 0), new Posn(2, 0), 4), true);
    t.checkExpect(u.posnInRadius(new Posn(5, 0), new Posn(2, 0), 2), false);
    t.checkExpect(u.posnInRadius(new Posn(0, 5), new Posn(5, 0), 1), false);
  }

  // test createRandomInts in Util
  void testCreateRandInt(Tester t) { 
    t.checkExpect(u.createRandomInts(2, 10, new Random(1234)), 
        new ConsList<Integer>(8,
            new ConsList<Integer>(5, new MtList<Integer>())));
    
    t.checkExpect(u.createRandomInts(1, 10, new Random(1234)), 
        new ConsList<Integer>(8,
            new MtList<Integer>()));
    
    t.checkExpect(u.createRandomInts(2, 4, new Random(1234)), 
        new ConsList<Integer>(2,
            new ConsList<Integer>(3, new MtList<Integer>())));
    
    t.checkExpect(u.createRandomInts(0, 10, new Random(1234)), new MtList<Integer>());
    
    t.checkExpect(u.createRandomInts(-1, 10, new Random(1234)), new MtList<Integer>());
  }
  
  // test getCenter in util
  void testGetCenter(Tester t) {
    t.checkExpect(u.getCenter(new Posn(50, 50), 40), new Posn(60, 60));
    t.checkExpect(u.getCenter(new Posn(55, 55), 40), new Posn(60, 60));
    t.checkExpect(u.getCenter(new Posn(30, 50), 40), new Posn(20, 60));
    t.checkExpect(u.getCenter(new Posn(30, 30), 40), new Posn(20, 20));
    t.checkExpect(u.getCenter(new Posn(0, 0), 40), new Posn(20, 20));
  }


}