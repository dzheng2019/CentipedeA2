import javalib.worldimages.Posn;
import java.util.*;
import tester.Tester;

class Util {

  // converts a absolute grid position to a grid location 
  Posn convertAbsoluteToGrid(Posn location, int unit, int row) {
    return new Posn(location.x / unit, row - (int)Math.floor((double) location.y / (double) unit) - 1);
  }

  // returns a new posn which is the given posn moved in a given direction by 
  // a given amound (speed)
  Posn moveInDirection(Posn location, Posn direction, int speed) {
    return new Posn(location.x + direction.x*speed, location.y + direction.y*speed);
  }

  // checks if two posns are equal based on their coordinates
  boolean posnEqual(Posn p1, Posn p2) {
    return (p1.x == p2.x) && (p1.y == p2.y);
  }
  
  // checks if the distance between p1 and p2 is less than the given radius
  boolean posnInRadius(Posn p1, Posn p2, int radius) {
    double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    return dist < radius;
  }

  // creates a list of random unique integers that is num long and
  // all integers are below max
  IList<Integer> createRandomInts(int num, int max, Random rand) {
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
  
  // Reverse the magnitude of the given posn in the x direction
  Posn reverseDirX(Posn p) {
    return new Posn(-1 * p.x, p.y);
  }
  
}

abstract class c {
  
}

class d extends c {
  int get() {
    return 0;
  }
}

class ExamplesUtil {
  Util u = new Util();
    
  void testConvertAbsToGrid(Tester t) {
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(0, 0), 40, 5), new Posn(0, 4));
    t.checkExpect(
        u.convertAbsoluteToGrid(new Posn(0, 180), 40, 5), new Posn(0, 0));
    
  }

  void testMoveInDirection(Tester t) {

  }


  void testPosnEqual(Tester t) {

  }
  
  void testCreateRandInt(Tester t) {
    
  }
}