import javalib.worldimages.Posn;
import java.util.*;

class Util {
  
  /* Template:
   * Methods:
   * this.convertAbsoluteToGrid(Posn location, int unit, int row) - Posn
   * this.flipX(Posn toFlip) - Posn
   * this.moveInDirection(Posn location, Posn direciton, int speed) - Posn
   * this.posnEqual(Posn p1, Posn p2, int radius) - boolean
   * this.posnInRadius(Posn p1, Posn p2, int radius) - boolean
   * this.createRandomInts(int num, int max, Random rand) - IList<Integer>
   * this.getCenter(Posn pos, int unit) - Posn 
   */
  
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
    if (unit < 1) {
      throw new IllegalArgumentException("Units have to be positive");
    }
    
    return new Posn(location.x / unit, row 
        - (int)Math.floor((double) location.y / (double) unit) - 1);
  }
  
  // flips an posn's x
  Posn flipX(Posn toFlip) {
    /* Template:
     * Parameters:
     * toFlip - Posn 
     * Fields of Parameters:
     * toFlip.x - int
     * toFlip.y - int 
     */
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
    return new Posn(location.x + direction.x * speed, 
        location.y + direction.y * speed);
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
  boolean posnInsideRadius(Posn p1, Posn p2, int radius) {
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
  
  // checks if the distance between p1 and p2 is less than the given radius
  boolean posnInsideOrOnRadius(Posn p1, Posn p2, int radius) {
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
    return dist <= radius;
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
    int centerX = pos.x / unit * unit + unit / 2;
    int centerY = pos.y / unit * unit + unit / 2;
    return new Posn(centerX, centerY);
  }
}