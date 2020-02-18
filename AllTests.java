import java.awt.Color;
import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.AboveImage;
import javalib.worldimages.BesideImage;
import javalib.worldimages.CircleImage;
import javalib.worldimages.EmptyImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;
import tester.*;

class ExamplesAll {
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


  // tests the click method of ITile
  void testClick(Tester t) {
    ITile grass = new Grass(new Posn(0,0));
    ITile dandelions = new Dandelions(new Posn(0,0));
    ITile pebbles = new Pebbles(new Posn(0,0));

    t.checkExpect(grass.click(true), dandelions);
    t.checkExpect(grass.click(false), pebbles);
    t.checkExpect(dandelions.click(true), grass);
    t.checkExpect(dandelions.click(false), dandelions);
    t.checkExpect(pebbles.click(true), grass);
    t.checkExpect(pebbles.click(false), pebbles);
  }

  // test draw in ITile
  void testDrawITile(Tester t) {
    ITile grass = new Grass(new Posn(0,0));
    ITile dandelions = new Dandelions(new Posn(0,0));
    ITile pebbles = new Pebbles(new Posn(0,0));
    int units = ITile.units;

    t.checkExpect(grass.draw(), 
        new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(units, units, OutlineMode.SOLID, Color.GREEN)));
    t.checkExpect(dandelions.draw(), 
        new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(units, units, OutlineMode.SOLID, Color.YELLOW)));
    t.checkExpect(pebbles.draw(), 
        new OverlayImage(new RectangleImage(units, units, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(units, units, OutlineMode.SOLID, Color.GRAY)));
  }

  // test collidesWith in ITile
  void testCollidesWith(Tester t) {
    ITile grass = new Grass(new Posn(0,0));
    ITile dandelions = new Dandelions(new Posn(0,0));
    ITile pebbles = new Pebbles(new Posn(0,0));
    t.checkExpect(grass.collidesWith(new Posn(0, 0)), false);
    t.checkExpect(dandelions.collidesWith(new Posn(0, 0)), true);
    t.checkExpect(dandelions.collidesWith(new Posn(1, 0)), false);
    t.checkExpect(pebbles.collidesWith(new Posn(0, 0)), false);
  }

  // test accept in ITile
  void testAccept(Tester t) {
    ITile grass = new Grass(new Posn(0,0));
    ITile dandelions = new Dandelions(new Posn(0,0));
    ITile pebbles = new Pebbles(new Posn(0,0));
    MakeNumber makeNumber = new MakeNumber();

    t.checkExpect(grass.accept(makeNumber), 0);
    t.checkExpect(dandelions.accept(makeNumber), 1);
    t.checkExpect(pebbles.accept(makeNumber), 2);
  }

  // for test purposes only
  void testMakeNumber(Tester t) {
    ITile grass = new Grass(new Posn(0,0));
    ITile dandelions = new Dandelions(new Posn(0,0));
    ITile pebbles = new Pebbles(new Posn(0,0));
    MakeNumber makeNumber = new MakeNumber();

    t.checkExpect(makeNumber.apply(grass), 0);
    t.checkExpect(makeNumber.apply(dandelions), 1);
    t.checkExpect(makeNumber.apply(pebbles), 2);
  }


  IList<Integer> MT = new MtList<Integer>();
  IList<Integer> L55 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(5, MT));
  IList<Integer> L53 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(3, MT));
  IList<Integer> L35 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(5, MT));
  IList<Integer> L33 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(3, MT));

  IList<IList<Integer>> MTT = new MtList<IList<Integer>>();
  IList<IList<Integer>> L55L55 = 
      new ConsList<IList<Integer>>(L55,
          new ConsList<IList<Integer>>(L55, MTT));
  IList<IList<Integer>> L55L53 = 
      new ConsList<IList<Integer>>(L55,
          new ConsList<IList<Integer>>(L53, MTT));
  IList<IList<Integer>> L53L55 = 
      new ConsList<IList<Integer>>(L53,
          new ConsList<IList<Integer>>(L55, MTT));
  IList<IList<Integer>> L35L55 = 
      new ConsList<IList<Integer>>(L35,
          new ConsList<IList<Integer>>(L55, MTT));
  IList<IList<Integer>> L33L33 = 
      new ConsList<IList<Integer>>(L33,
          new ConsList<IList<Integer>>(L33, MTT));

  IList<Integer> L3333 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(3,
              new ConsList<Integer>(3,
                  new ConsList<Integer>(3, MT))));

  IList<Integer> L5533 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(5,
              new ConsList<Integer>(3,
                  new ConsList<Integer>(3, MT))));

  IList<Integer> L5333 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(3,
              new ConsList<Integer>(3,
                  new ConsList<Integer>(3, MT))));

  IList<Integer> L3533 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(5,
              new ConsList<Integer>(3,
                  new ConsList<Integer>(3, MT))));

  IList<Integer> L3355 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(3,
              new ConsList<Integer>(5,
                  new ConsList<Integer>(5, MT))));

  IList<Integer> L5555 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(5,
              new ConsList<Integer>(5,
                  new ConsList<Integer>(5, MT))));

  IList<Integer> L5355 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(3,
              new ConsList<Integer>(5,
                  new ConsList<Integer>(5, MT))));

  IList<Integer> L3555 = 
      new ConsList<Integer>(3,
          new ConsList<Integer>(5,
              new ConsList<Integer>(5,
                  new ConsList<Integer>(5, MT))));


  // test accept in IList
  void testAcceptIList(Tester t) {
    MakeNum<Integer> MN = new MakeNum<Integer>();
    t.checkExpect(MT.accept(MN), 0);
    t.checkExpect(L55.accept(MN), 1);
    t.checkExpect(L53.accept(MN), 1);
  }

  // test length in IList
  void testLength(Tester t) {
    t.checkExpect(MT.length(), 0);
    t.checkExpect(L55.length(), 2);
    t.checkExpect(L53.length(), 2);
  }

  // test apply in IFunc
  /* This test contains tests for:
   * MapList<T, R>
   */
  void testApply(Tester t) {
    MakeThree MakeT = new MakeThree();
    MapList<Integer, Integer> MakeAllThree = new MapList<Integer, Integer>(MakeT);

    t.checkExpect(MakeAllThree.apply(L33), L33);
    t.checkExpect(MakeAllThree.apply(L55), L33);
    t.checkExpect(MakeAllThree.apply(L53), L33);
    t.checkExpect(MakeAllThree.apply(L35), L33);
  }

  // test apply in IPred
  /* This test contains tests for:
   * OrMapListPred<T>
   * ContainsInt
   */
  void testAppyPred(Tester t) {
    ContainsInt Contain3 = new ContainsInt(3);
    OrMapListPred<Integer> Any3 = new OrMapListPred<Integer>(Contain3);

    t.checkExpect(Contain3.apply(3), true);
    t.checkExpect(Contain3.apply(5), false);
    t.checkExpect(Contain3.apply(1), false);
    t.checkExpect(Contain3.apply(-1), false);

    t.checkExpect(Any3.apply(L33), true);
    t.checkExpect(Any3.apply(L55), false);
    t.checkExpect(Any3.apply(L53), true);
    t.checkExpect(Any3.apply(L35), true);
    t.checkExpect(Any3.apply(MT), false);
  }

  // test apply in IListVisitor
  /* This test contains test for:
   * OrMap<T>
   * RowLength<T>
   * Map<T, R>
   * ChangeAtX<T>
   * ChangeAtXY<T>
   * AppendHelper<T>
   * FirstNElements<T>
   * IncrementAboveX
   * IncrementAllByX
   */
  void testApplyList(Tester t) {
    ContainsInt contain3 = new ContainsInt(3);
    OrMap<Integer> Any3 = new OrMap<Integer>(contain3);
    t.checkExpect(Any3.apply(L33), true);
    t.checkExpect(Any3.apply(L55), false);
    t.checkExpect(Any3.apply(L53), true);
    t.checkExpect(Any3.apply(L35), true);
    t.checkExpect(Any3.apply(MT), false);

    RowLength<Integer> rowLength = new RowLength<Integer>();
    t.checkExpect(rowLength.apply(L55L55), 2);
    t.checkExpect(rowLength.apply(L55L55), 2);
    t.checkExpect(rowLength.apply(L53L55), 2);
    t.checkExpect(rowLength.apply(L33L33), 2);
    t.checkExpect(rowLength.apply(MTT), 0);

    MakeThree makeThree = new MakeThree();

    ChangeAtX<Integer> change0 = new ChangeAtX<Integer>(makeThree, 0);
    ChangeAtX<Integer> change1 = new ChangeAtX<Integer>(makeThree, 1);
    ChangeAtX<Integer> change2 = new ChangeAtX<Integer>(makeThree, 2);

    t.checkExpect(change0.apply(L33), L33);
    t.checkExpect(change0.apply(L55), L35);
    t.checkExpect(change0.apply(L53), L33);
    t.checkExpect(change0.apply(L35), L35);
    t.checkExpect(change0.apply(MT), MT);

    t.checkExpect(change1.apply(L33), L33);
    t.checkExpect(change1.apply(L55), L53);
    t.checkExpect(change1.apply(L53), L53);
    t.checkExpect(change1.apply(L35), L33);
    t.checkExpect(change1.apply(MT), MT);

    t.checkExpect(change2.apply(L33), L33);
    t.checkExpect(change2.apply(L55), L55);
    t.checkExpect(change2.apply(L53), L53);
    t.checkExpect(change2.apply(L35), L35);
    t.checkExpect(change2.apply(MT), MT);

    ChangeAtXY<Integer> change00 = new ChangeAtXY<Integer>(makeThree, 0, 0);
    ChangeAtXY<Integer> change10 = new ChangeAtXY<Integer>(makeThree, 1, 0);
    ChangeAtXY<Integer> change11 = new ChangeAtXY<Integer>(makeThree, 1, 1);
    ChangeAtXY<Integer> change22 = new ChangeAtXY<Integer>(makeThree, 2, 2);

    t.checkExpect(change00.apply(L55L55), L35L55);
    t.checkExpect(change10.apply(L55L55), L53L55);
    t.checkExpect(change11.apply(L55L55), L55L53);
    t.checkExpect(change22.apply(L55L55), L55L55);
    t.checkExpect(change00.apply(MTT), MTT);

    AppendHelper<Integer> appendL33 = new AppendHelper<Integer>(L33);
    AppendHelper<Integer> appendL55 = new AppendHelper<Integer>(L55);

    t.checkExpect(appendL33.apply(L33), L3333);
    t.checkExpect(appendL33.apply(L55), L5533);
    t.checkExpect(appendL33.apply(L53), L5333);
    t.checkExpect(appendL33.apply(L35), L3533);
    t.checkExpect(appendL33.apply(MT), L33);

    t.checkExpect(appendL55.apply(L33), L3355);
    t.checkExpect(appendL55.apply(L55), L5555);
    t.checkExpect(appendL55.apply(L53), L5355);
    t.checkExpect(appendL55.apply(L35), L3555);
    t.checkExpect(appendL55.apply(MT), L55);

    FirstNElements<Integer> first0Element = new FirstNElements<Integer>(0);
    FirstNElements<Integer> first1Element = new FirstNElements<Integer>(1);
    FirstNElements<Integer> first2Element = new FirstNElements<Integer>(2);
    FirstNElements<Integer> first3Element = new FirstNElements<Integer>(3);

    t.checkExpect(first0Element.apply(L33), MT);
    t.checkExpect(first0Element.apply(L55), MT);
    t.checkExpect(first0Element.apply(L53), MT);
    t.checkExpect(first0Element.apply(L35), MT);
    t.checkExpect(first0Element.apply(MT), MT);

    t.checkExpect(first1Element.apply(L33), new ConsList<Integer>(3, MT));
    t.checkExpect(first1Element.apply(L55), new ConsList<Integer>(5, MT));
    t.checkExpect(first1Element.apply(L53), new ConsList<Integer>(5, MT));
    t.checkExpect(first1Element.apply(L35), new ConsList<Integer>(3, MT));
    t.checkExpect(first1Element.apply(MT), MT);

    t.checkExpect(first2Element.apply(L33), L33);
    t.checkExpect(first2Element.apply(L55), L55);
    t.checkExpect(first2Element.apply(L53), L53);
    t.checkExpect(first2Element.apply(L35), L35);
    t.checkExpect(first2Element.apply(MT), MT);

    t.checkExpect(first3Element.apply(L33), L33);
    t.checkExpect(first3Element.apply(L55), L55);
    t.checkExpect(first3Element.apply(L53), L53);
    t.checkExpect(first3Element.apply(L35), L35);
    t.checkExpect(first3Element.apply(MT), MT);

    IncrementAboveX aboveX = new IncrementAboveX(3);
    IncrementAllByX allbyX = new IncrementAllByX(2);

    t.checkExpect(aboveX.apply(L33),       
        new ConsList<Integer>(4,
            new ConsList<Integer>(4, MT)));
    t.checkExpect(aboveX.apply(L55), 
        new ConsList<Integer>(6,
            new ConsList<Integer>(6, MT)));
    t.checkExpect(aboveX.apply(L53), 
        new ConsList<Integer>(6,
            new ConsList<Integer>(4, MT)));
    t.checkExpect(aboveX.apply(L35),        
        new ConsList<Integer>(4,
            new ConsList<Integer>(6, MT)));
    t.checkExpect(aboveX.apply(MT), MT);

    t.checkExpect(allbyX.apply(L33), L55);
    t.checkExpect(allbyX.apply(L55), 
        new ConsList<Integer>(7,
            new ConsList<Integer>(7, MT)));
    t.checkExpect(allbyX.apply(L53), 
        new ConsList<Integer>(7,
            new ConsList<Integer>(5, MT)));
    t.checkExpect(allbyX.apply(L35), 
        new ConsList<Integer>(5,
            new ConsList<Integer>(7, MT)));
    t.checkExpect(allbyX.apply(MT), MT);
  }

  // test visitMt in IListVisitor
  /* This test contains test for:
   * OrMap<T>
   * RowLength<T>
   * Map<T, R>
   * ChangeAtX<T>
   * ChangeAtXY<T>
   * AppendHelepr<T>
   * FirstNElements<T>
   */
  void testVisitMt(Tester t) {

    MtList<Integer> mtL = new MtList<Integer>();
    MtList<IList<Integer>> mtLL = new MtList<IList<Integer>>();


    ContainsInt contain3 = new ContainsInt(3);
    OrMap<Integer> Any3 = new OrMap<Integer>(contain3);
    t.checkExpect(Any3.visitMt(mtL), false);

    RowLength<Integer> rowLength = new RowLength<Integer>();
    t.checkExpect(rowLength.visitMt(mtLL), 0);

    MakeThree makeThree = new MakeThree();

    ChangeAtX<Integer> change0 = new ChangeAtX<Integer>(makeThree, 0);
    ChangeAtX<Integer> change1 = new ChangeAtX<Integer>(makeThree, 1);
    ChangeAtX<Integer> change2 = new ChangeAtX<Integer>(makeThree, 2);

    t.checkExpect(change0.visitMt(mtL), MT);
    t.checkExpect(change1.visitMt(mtL), MT);
    t.checkExpect(change2.visitMt(mtL), MT);

    ChangeAtXY<Integer> change00 = new ChangeAtXY<Integer>(makeThree, 0, 0);
    ChangeAtXY<Integer> change10 = new ChangeAtXY<Integer>(makeThree, 1, 0);
    ChangeAtXY<Integer> change11 = new ChangeAtXY<Integer>(makeThree, 1, 1);
    ChangeAtXY<Integer> change22 = new ChangeAtXY<Integer>(makeThree, 2, 2);

    t.checkExpect(change00.visitMt(mtLL), mtLL);
    t.checkExpect(change10.visitMt(mtLL), mtLL);
    t.checkExpect(change11.visitMt(mtLL), mtLL);
    t.checkExpect(change22.visitMt(mtLL), mtLL);

    AppendHelper<Integer> appendL33 = new AppendHelper<Integer>(L33);
    AppendHelper<Integer> appendL55 = new AppendHelper<Integer>(L55);

    t.checkExpect(appendL33.visitMt(mtL), L33);

    t.checkExpect(appendL55.visitMt(mtL), L55);

    FirstNElements<Integer> first0Element = new FirstNElements<Integer>(0);
    FirstNElements<Integer> first1Element = new FirstNElements<Integer>(1);
    FirstNElements<Integer> first2Element = new FirstNElements<Integer>(2);
    FirstNElements<Integer> first3Element = new FirstNElements<Integer>(3);

    t.checkExpect(first0Element.visitMt(mtL), mtL);

    t.checkExpect(first1Element.visitMt(mtL), mtL);

    t.checkExpect(first2Element.visitMt(mtL), mtL);

    t.checkExpect(first3Element.visitMt(mtL), mtL);

    IncrementAboveX aboveX = new IncrementAboveX(3);
    IncrementAllByX allbyX = new IncrementAllByX(2);

    t.checkExpect(aboveX.visitMt(mtL), MT);

    t.checkExpect(allbyX.visitMt(mtL), MT);
  }

  // test visitCons in IListVisitor
  /* This test contains test for:
   * OrMap<T>
   * RowLength<T>
   * Map<T, R>
   * ChangeAtX<T>
   * ChangeAtXY<T>
   * AppendHelepr<T>
   * FirstNElements<T>
   */
  // will cast lists that are not empty to cons for testing
  void testVisitCons(Tester t) {
    ContainsInt contain3 = new ContainsInt(3);
    OrMap<Integer> Any3 = new OrMap<Integer>(contain3);
    t.checkExpect(Any3.visitCons((ConsList<Integer>) L33), true);
    t.checkExpect(Any3.visitCons((ConsList<Integer>) L55), false);
    t.checkExpect(Any3.visitCons((ConsList<Integer>) L53), true);
    t.checkExpect(Any3.visitCons((ConsList<Integer>) L35), true);

    RowLength<Integer> rowLength = new RowLength<Integer>();
    t.checkExpect(rowLength.visitCons((ConsList<IList<Integer>>) L55L55), 2);
    t.checkExpect(rowLength.visitCons((ConsList<IList<Integer>>) L55L55), 2);
    t.checkExpect(rowLength.visitCons((ConsList<IList<Integer>>) L53L55), 2);
    t.checkExpect(rowLength.visitCons((ConsList<IList<Integer>>) L33L33), 2);

    MakeThree makeThree = new MakeThree();

    ChangeAtX<Integer> change0 = new ChangeAtX<Integer>(makeThree, 0);
    ChangeAtX<Integer> change1 = new ChangeAtX<Integer>(makeThree, 1);
    ChangeAtX<Integer> change2 = new ChangeAtX<Integer>(makeThree, 2);

    t.checkExpect(change0.visitCons((ConsList<Integer>) L33), L33);
    t.checkExpect(change0.visitCons((ConsList<Integer>) L55), L35);
    t.checkExpect(change0.visitCons((ConsList<Integer>) L53), L33);
    t.checkExpect(change0.visitCons((ConsList<Integer>) L35), L35);

    t.checkExpect(change1.visitCons((ConsList<Integer>) L33), L33);
    t.checkExpect(change1.visitCons((ConsList<Integer>) L55), L53);
    t.checkExpect(change1.visitCons((ConsList<Integer>) L53), L53);
    t.checkExpect(change1.visitCons((ConsList<Integer>) L35), L33);

    t.checkExpect(change2.visitCons((ConsList<Integer>) L33), L33);
    t.checkExpect(change2.visitCons((ConsList<Integer>) L55), L55);
    t.checkExpect(change2.visitCons((ConsList<Integer>) L53), L53);
    t.checkExpect(change2.visitCons((ConsList<Integer>) L35), L35);

    ChangeAtXY<Integer> change00 = new ChangeAtXY<Integer>(makeThree, 0, 0);
    ChangeAtXY<Integer> change10 = new ChangeAtXY<Integer>(makeThree, 1, 0);
    ChangeAtXY<Integer> change11 = new ChangeAtXY<Integer>(makeThree, 1, 1);
    ChangeAtXY<Integer> change22 = new ChangeAtXY<Integer>(makeThree, 2, 2);

    t.checkExpect(change00.visitCons((ConsList<IList<Integer>>) L55L55), L35L55);
    t.checkExpect(change10.visitCons((ConsList<IList<Integer>>) L55L55), L53L55);
    t.checkExpect(change11.visitCons((ConsList<IList<Integer>>) L55L55), L55L53);
    t.checkExpect(change22.visitCons((ConsList<IList<Integer>>) L55L55), L55L55);

    AppendHelper<Integer> appendL33 = new AppendHelper<Integer>(L33);
    AppendHelper<Integer> appendL55 = new AppendHelper<Integer>(L55);

    t.checkExpect(appendL33.visitCons((ConsList<Integer>) L33), L3333);
    t.checkExpect(appendL33.visitCons((ConsList<Integer>) L55), L5533);
    t.checkExpect(appendL33.visitCons((ConsList<Integer>) L53), L5333);
    t.checkExpect(appendL33.visitCons((ConsList<Integer>) L35), L3533);

    t.checkExpect(appendL55.visitCons((ConsList<Integer>) L33), L3355);
    t.checkExpect(appendL55.visitCons((ConsList<Integer>) L55), L5555);
    t.checkExpect(appendL55.visitCons((ConsList<Integer>) L53), L5355);
    t.checkExpect(appendL55.visitCons((ConsList<Integer>) L35), L3555);

    FirstNElements<Integer> first0Element = new FirstNElements<Integer>(0);
    FirstNElements<Integer> first1Element = new FirstNElements<Integer>(1);
    FirstNElements<Integer> first2Element = new FirstNElements<Integer>(2);
    FirstNElements<Integer> first3Element = new FirstNElements<Integer>(3);

    t.checkExpect(first0Element.visitCons((ConsList<Integer>) L33), MT);
    t.checkExpect(first0Element.visitCons((ConsList<Integer>) L55), MT);
    t.checkExpect(first0Element.visitCons((ConsList<Integer>) L53), MT);
    t.checkExpect(first0Element.visitCons((ConsList<Integer>) L35), MT);

    t.checkExpect(first1Element.visitCons((ConsList<Integer>) L33), new ConsList<Integer>(3, MT));
    t.checkExpect(first1Element.visitCons((ConsList<Integer>) L55), new ConsList<Integer>(5, MT));
    t.checkExpect(first1Element.visitCons((ConsList<Integer>) L53), new ConsList<Integer>(5, MT));
    t.checkExpect(first1Element.visitCons((ConsList<Integer>) L35), new ConsList<Integer>(3, MT));

    t.checkExpect(first2Element.visitCons((ConsList<Integer>) L33), L33);
    t.checkExpect(first2Element.visitCons((ConsList<Integer>) L55), L55);
    t.checkExpect(first2Element.visitCons((ConsList<Integer>) L53), L53);
    t.checkExpect(first2Element.visitCons((ConsList<Integer>) L35), L35);

    t.checkExpect(first3Element.visitCons((ConsList<Integer>) L33), L33);
    t.checkExpect(first3Element.visitCons((ConsList<Integer>) L55), L55);
    t.checkExpect(first3Element.visitCons((ConsList<Integer>) L53), L53);
    t.checkExpect(first3Element.visitCons((ConsList<Integer>) L35), L35);

    IncrementAboveX aboveX = new IncrementAboveX(3);
    IncrementAllByX allbyX = new IncrementAllByX(2);

    t.checkExpect(aboveX.visitCons((ConsList<Integer>) L33),       
        new ConsList<Integer>(4,
            new ConsList<Integer>(4, MT)));
    t.checkExpect(aboveX.visitCons((ConsList<Integer>) L55), 
        new ConsList<Integer>(6,
            new ConsList<Integer>(6, MT)));
    t.checkExpect(aboveX.visitCons((ConsList<Integer>) L53), 
        new ConsList<Integer>(6,
            new ConsList<Integer>(4, MT)));
    t.checkExpect(aboveX.visitCons((ConsList<Integer>) L35),        
        new ConsList<Integer>(4,
            new ConsList<Integer>(6, MT)));

    t.checkExpect(allbyX.visitCons((ConsList<Integer>) L33), L55);
    t.checkExpect(allbyX.visitCons((ConsList<Integer>) L55), 
        new ConsList<Integer>(7,
            new ConsList<Integer>(7, MT)));
    t.checkExpect(allbyX.visitCons((ConsList<Integer>) L53), 
        new ConsList<Integer>(7,
            new ConsList<Integer>(5, MT)));
    t.checkExpect(allbyX.visitCons((ConsList<Integer>) L35), 
        new ConsList<Integer>(5,
            new ConsList<Integer>(7, MT)));
  }

  // test apply in IFunc2
  /* This test contains tests for:
   * Append<T>
   */
  void testApplyFunc2(Tester t) {
    Append<Integer> append = new Append<Integer>();
    t.checkExpect(append.apply(L33, L33), L3333);
    t.checkExpect(append.apply(L55, L33), L5533);
    t.checkExpect(append.apply(L53, L33), L5333);
    t.checkExpect(append.apply(L35, L33), L3533);

    t.checkExpect(append.apply(L33, L55), L3355);
    t.checkExpect(append.apply(L55, L55), L5555);
    t.checkExpect(append.apply(L53, L55), L5355);
    t.checkExpect(append.apply(L35, L55), L3555);
  }

  int speed = 4;
  int size = 20;
  int unit = 40;
  int row = 2;
  Board board = new Board(row, 2);

  Gnome g1 = new Gnome(new Posn(20, 20), speed, size);
  Dart d1 = new Dart(new Posn(20, 20), speed, size);
  Gnome g2 = new Gnome(new Posn(77, 20), speed, size);
  Dart d2 = new Dart(new Posn(20, 3), speed, size);

  Posn g1PosMR = new Posn(24, 20);
  Posn d1PosMR = new Posn(24, 20);
  Posn g2PosMR = new Posn(81, 20);
  Posn d2PosMR = new Posn(24, 3);

  Posn g1PosMU = new Posn(20, 16);
  Posn d1PosMU = new Posn(20, 16);
  Posn g2PosMU = new Posn(77, 16);
  Posn d2PosMU = new Posn(20, -1);

  Gnome g1MR = new Gnome(new Posn(24, 20), speed, size);
  Gnome g2MR = new Gnome(new Posn(81, 20), speed, size);

  Dart d1MR = new Dart(new Posn(24, 20), speed, size);
  Dart d2MR = new Dart(new Posn(24, 3), speed, size);

  Gnome g1MU = new Gnome(new Posn(20, 16), speed, size);
  Dart d1MU = new Dart(new Posn(20, 16), speed, size);
  Gnome g2MU = new Gnome(new Posn(77, 16), speed, size);

  IList<CentipedeHead> centipedes = 
      new ConsList<CentipedeHead>(
          new CentipedeHead (
              new Posn(20, 20), 
              4, 
              this.unit / 2, 
              new Posn(20, 20),
              new Posn(20, 20), 
              this.unit,
              new MtList<CentipedeFollower>()), 
          new MtList<CentipedeHead>());


  // test getGrid in AMoveableObject
  void testGetGrid(Tester t) {
    t.checkExpect(g1.getGrid(unit, row), new Posn(0, 1));
    t.checkExpect(d1.getGrid(unit, row), new Posn(0, 1));
    t.checkExpect(g2.getGrid(unit, row), new Posn(1, 1));
    t.checkExpect(d2.getGrid(unit, row), new Posn(0, 1));
  }

  // test movedPos in AMoveableObject
  void testMovedPos(Tester t) {
    t.checkExpect(g1.movedPos(new Posn(1, 0)), g1PosMR);
    t.checkExpect(d1.movedPos(new Posn(1, 0)), d1PosMR);
    t.checkExpect(g2.movedPos(new Posn(1, 0)), g2PosMR);
    t.checkExpect(d2.movedPos(new Posn(1, 0)), d2PosMR);

    t.checkExpect(g1.movedPos(new Posn(0, -1)), g1PosMU);
    t.checkExpect(d1.movedPos(new Posn(0, -1)), d1PosMU);
    t.checkExpect(g2.movedPos(new Posn(0, -1)), g2PosMU);
    t.checkExpect(d2.movedPos(new Posn(0, -1)), d2PosMU);
  }

  // test validMove in AMoveableObject
  void testValidMove(Tester t) {
    t.checkExpect(g1.validMove(board, new Posn(1, 0)), true);
    t.checkExpect(d1.validMove(board, new Posn(1, 0)), true);
    t.checkExpect(g2.validMove(board, new Posn(1, 0)), false);
    t.checkExpect(d2.validMove(board, new Posn(1, 0)), false);

    t.checkExpect(g1.validMove(board, new Posn(0, 1)), true);
    t.checkExpect(d1.validMove(board, new Posn(0, 1)), true);
    t.checkExpect(g2.validMove(board, new Posn(0, -1)), false);
    t.checkExpect(d2.validMove(board, new Posn(0, -1)), false);
  }

  // test draw in AMoveableObject
  void testDraw(Tester t) {
    t.checkExpect(g1.draw(), 
        new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(d1.draw(), 
        new RectangleImage(this.size / 3, this.size / 2, 
            OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(g2.draw(),
        new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(d2.draw(), 
        new RectangleImage(this.size / 3, this.size / 2, 
            OutlineMode.SOLID, Color.BLUE));
  }

  // test drawOnBoard in AMoveableObject
  void testDrawOnBoard(Tester t) {
    WorldScene scene = new WorldScene(2 * this.unit, this.row * this.unit);
    t.checkExpect(g1.drawOnBoard(scene), scene.placeImageXY(g1.draw(), g1.location.x, g1.location.y));
    t.checkExpect(d1.drawOnBoard(scene), scene.placeImageXY(d1.draw(), d1.location.x, d1.location.y));
    t.checkExpect(g2.drawOnBoard(scene), scene.placeImageXY(g2.draw(), g2.location.x, g2.location.y));
    t.checkExpect(d2.drawOnBoard(scene), scene.placeImageXY(d2.draw(), d2.location.x, d2.location.y));
  }

  // test move in gnome
  void testMoveGnome(Tester t) {
    t.checkExpect(g1.move(board), g1);
    t.checkExpect(g2.move(board), g2);
  }

  // test moveIfPossible in gnome
  void testMoveIfPosGnome(Tester t) {
    t.checkExpect(g1.moveIfPossible(board, new Posn(1, 0)), g1MR);
    t.checkExpect(g2.moveIfPossible(board, new Posn(1, 0)), g2);
  }

  // test moveInDirection in gnome
  void testMoveInDirGnome(Tester t) {
    t.checkExpect(g1.moveInDirection(board, new Posn(1, 0)), g1MR);
    t.checkExpect(g2.moveInDirection(board, new Posn(1, 0)), g2MR);
  }

  // test gnomeWithSpeed in gnome
  void testGnomeWithSpeed(Tester t) {
    t.checkExpect(g1.gnomeWithSpeed(0), new Gnome(new Posn(20, 20), 0, 20));
    t.checkExpect(g2.gnomeWithSpeed(0), new Gnome(new Posn(77, 20), 0, 20));
  }

  // test collisonWithCentipedes in gnome
  void testCollisionWithCentipedesGnome(Tester t) {
    t.checkExpect(g1.collisionWithCentipedes(centipedes), true);
    t.checkExpect(g2.collisionWithCentipedes(centipedes), false);
  }

  // test move in dart
  void testMoveDart(Tester t) {
    t.checkExpect(d1.move(board), new Dart(new Posn(20, 16), 4, 20));
    t.checkExpect(d2.move(board), new Dart(new Posn(20, -1), 4, 20));
  }

  // test moveInDirection in dart
  void testMoveInDirDart(Tester t) {
    t.checkExpect(d1.moveInDirection(board, new Posn(0, -1)), 
        new Dart(new Posn(20, 16), 4, 20));
    t.checkExpect(d2.moveInDirection(board, new Posn(0, -1)), 
        new Dart(new Posn(20, -1), 4, 20));
  }

  // test collisionWithBoard in dart
  void testCollisionWithBoard(Tester t) {
    t.checkExpect(d1.collisionWithBoard(board), 
        false);
    t.checkExpect(d2.moveInDirection(board, new Posn(0, -1)).collisionWithBoard(board), 
        true);
  }

  // test transformBoard in dart
  void testTransformBoard(Tester t) {
    t.checkExpect(d1.transformBoard(board, true), 
        board.changeAtLocation(d1.location, true));
    t.checkExpect(d2.transformBoard(board, false), 
        board.changeAtLocation(d1.location, false));
  }

  // test collisionWithCentipedes in dart
  void testCollisionWithCentipedesDart(Tester t) {
    t.checkExpect(d1.collisionWithCentipedes(centipedes), true);
    t.checkExpect(d2.collisionWithCentipedes(centipedes), true);
  }

  // test transformCentipedes in dart
  void testTransformCentipedes(Tester t) {
    t.checkExpect(d1.transformCentipedes(centipedes), new MtList<CentipedeHead>());
    t.checkExpect(d2.transformCentipedes(centipedes), new MtList<CentipedeHead>());  
  } 

  IList<ITile> mtTileList = new MtList<ITile>();

  IList<ITile> tileGrid1row0 = 
      new ConsList<ITile>(new Grass(new Posn(0, 0)),
          new ConsList<ITile>(new Grass(new Posn(1, 0)), 
              mtTileList));

  IList<ITile> tileGrid1row1 = 
      new ConsList<ITile>(new Grass(new Posn(0, 1)),
          new ConsList<ITile>(new Grass(new Posn(1, 1)), 
              mtTileList));

  IList<ITile> tileGrid2row0 = 
      new ConsList<ITile>(new Grass(new Posn(0, 0)),
          new ConsList<ITile>(new Grass(new Posn(1, 0)), 
              mtTileList));

  IList<ITile> tileGrid2row1 = 
      new ConsList<ITile>(new Grass(new Posn(0, 1)),
          new ConsList<ITile>(new Dandelions(new Posn(1, 1)), 
              mtTileList));

  WorldImage drawRow0 = new DrawRow().apply(tileGrid1row0);
  WorldImage drawRow1 = new DrawRow().apply(tileGrid1row1);

  IList<IList<ITile>> mtListTileList = new MtList<IList<ITile>>();

  IList<IList<ITile>> tileGrid1 = 
      new ConsList<IList<ITile>>(tileGrid1row0,
          new ConsList<IList<ITile>>(tileGrid1row1, mtListTileList));

  IList<IList<ITile>> tileGrid2 = 
      new ConsList<IList<ITile>>(tileGrid2row0,
          new ConsList<IList<ITile>>(tileGrid2row1, mtListTileList));

  Board board2x2 = new Board(2, 2);
  Board board0x0 = new Board(0, 0);
  Board board2x2D = new Board(tileGrid2);

  // test makeBoard in Board
  void testMakeBoard(Tester t) {
    t.checkExpect(board2x2.makeBoard(1, 1), tileGrid1);
    t.checkExpect(board2x2.makeBoard(-1, -1), mtListTileList);
  }

  // test makeRow in Board
  void testMakeRow(Tester t) {
    t.checkExpect(board2x2.makeRow(1, 1), tileGrid1row1);
    t.checkExpect(board2x2.makeRow(-1, -1), mtTileList);
  }

  // test draw in Board 
  void testDrawInBoard(Tester t) {
    t.checkExpect(board2x2.draw(), new DrawBoard().apply(tileGrid1));
    t.checkExpect(board0x0.draw(), new EmptyImage());
  }

  // test drawOnBoard in Board 
  void testDrawOnBoardinBoard(Tester t) {
    WorldScene scene = new WorldScene(80, 80);
    t.checkExpect(board2x2.drawOnBoard(scene), 
        scene.placeImageXY(new DrawBoard().apply(tileGrid1), 40, 40));
    t.checkExpect(board0x0.drawOnBoard(scene), scene);
  }

  // test collisionOccursLeft in Board
  void testCollisionOccursLeft(Tester t) {
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(40, 20), 30), false);
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(20, 20), 30), true);
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(20, 40), 30), true);
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(80, 20), 30), false);
    t.checkExpect(board2x2.collisionOccursLeft(new Posn(80, 40), 30), false);

    t.checkExpect(board0x0.collisionOccursLeft(new Posn(40, 20), 30), false);
    t.checkExpect(board0x0.collisionOccursLeft(new Posn(40, 40), 30), false);
    t.checkExpect(board0x0.collisionOccursLeft(new Posn(20, 20), 30), true);
    t.checkExpect(board0x0.collisionOccursLeft(new Posn(20, 40), 30), true);
    t.checkExpect(board0x0.collisionOccursLeft(new Posn(80, 20), 30), false);
    t.checkExpect(board0x0.collisionOccursLeft(new Posn(80, 40), 30), false);

    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(40, 20), 30), false);
    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(20, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(20, 40), 30), true);
    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(80, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccursLeft(new Posn(80, 40), 30), false);
  }

  // test collisionOccursRight in Board
  void testCollisionOccursRight(Tester t) {
    t.checkExpect(board2x2.collisionOccursRight(new Posn(40, 20), 30), false);
    t.checkExpect(board2x2.collisionOccursRight(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2.collisionOccursRight(new Posn(20, 20), 30), false);
    t.checkExpect(board2x2.collisionOccursRight(new Posn(20, 40), 30), false);
    t.checkExpect(board2x2.collisionOccursRight(new Posn(80, 20), 30), true);
    t.checkExpect(board2x2.collisionOccursRight(new Posn(80, 40), 30), true);

    t.checkExpect(board0x0.collisionOccursRight(new Posn(40, 20), 30), true);
    t.checkExpect(board0x0.collisionOccursRight(new Posn(40, 40), 30), true);
    t.checkExpect(board0x0.collisionOccursRight(new Posn(20, 20), 30), true);
    t.checkExpect(board0x0.collisionOccursRight(new Posn(20, 40), 30), true);
    t.checkExpect(board0x0.collisionOccursRight(new Posn(80, 20), 30), true);
    t.checkExpect(board0x0.collisionOccursRight(new Posn(80, 40), 30), true);

    t.checkExpect(board2x2D.collisionOccursRight(new Posn(40, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccursRight(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2D.collisionOccursRight(new Posn(20, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccursRight(new Posn(20, 40), 30), false);
    t.checkExpect(board2x2D.collisionOccursRight(new Posn(80, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccursRight(new Posn(80, 40), 30), true);
  }

  // test collisionOccurs in Board
  void testCollisionOccurs(Tester t) {
    t.checkExpect(board2x2.collisionOccurs(new Posn(40, 20), 30), true);
    t.checkExpect(board2x2.collisionOccurs(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2.collisionOccurs(new Posn(20, 20), 30), true);
    t.checkExpect(board2x2.collisionOccurs(new Posn(20, 40), 30), true);
    t.checkExpect(board2x2.collisionOccurs(new Posn(80, 20), 30), true);
    t.checkExpect(board2x2.collisionOccurs(new Posn(80, 40), 30), true);

    t.checkExpect(board0x0.collisionOccurs(new Posn(40, 20), 30), true);
    t.checkExpect(board0x0.collisionOccurs(new Posn(40, 40), 30), true);
    t.checkExpect(board0x0.collisionOccurs(new Posn(20, 20), 30), true);
    t.checkExpect(board0x0.collisionOccurs(new Posn(20, 40), 30), true);
    t.checkExpect(board0x0.collisionOccurs(new Posn(80, 20), 30), true);
    t.checkExpect(board0x0.collisionOccurs(new Posn(80, 40), 30), true);

    t.checkExpect(board2x2D.collisionOccurs(new Posn(40, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccurs(new Posn(40, 40), 30), false);
    t.checkExpect(board2x2D.collisionOccurs(new Posn(20, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccurs(new Posn(20, 40), 30), true);
    t.checkExpect(board2x2D.collisionOccurs(new Posn(80, 20), 30), true);
    t.checkExpect(board2x2D.collisionOccurs(new Posn(80, 40), 30), true);
  }

  // test produceDart in Board
  void testProduceDart(Tester t) {  
    Gnome player = new Gnome(new Posn(20, 60), 10, 20);
    t.checkExpect(board2x2.produceDart(player, 10), 
        new Dart(
            new Posn(20, 60),
            10,
            20));
    t.checkExpect(board2x2.produceDart(player, 10), 
        new Dart(
            new Posn(20, 60),
            10,
            20));
    t.checkExpect(board0x0.produceDart(player, 10), 
        new Dart(
            new Posn(20, -20),
            10,
            20));
  }

  // test randomBoard in Board
  void testRandomBoard(Tester t) {
    t.checkExpect(board2x2.randomBoard(false, 0, new Random(123)), new Board(tileGrid1));
    t.checkExpect(board2x2.randomBoard(false, 10, new Random(1234)), new Board(tileGrid2));
    t.checkExpect(board2x2D.randomBoard(false, 10, new Random(1234)), board2x2D);
    t.checkExpect(board0x0.randomBoard(false, 10, new Random(1234)), board0x0);

  }

  // test changeAtLocation in Board
  void testChangeAtLocation(Tester t) {
    t.checkExpect(board2x2.changeAtLocation(new Posn(-20, -20), true), new Board(tileGrid1));
    t.checkExpect(board2x2.changeAtLocation(new Posn(60, 20), false), new Board(tileGrid2));
    t.checkExpect(board2x2D.changeAtLocation(new Posn(60, 20), false), board2x2D);
    t.checkExpect(board0x0.changeAtLocation(new Posn(60, 20), true), board0x0);
  }

  // test clickAtLocation in Board
  void testClikcAtLocation(Tester t) {
    t.checkExpect(board2x2.clickAtLocation(new Posn(-1, -1), false), new Board(tileGrid1));
    t.checkExpect(board2x2.clickAtLocation(new Posn(1, 1), true), new Board(tileGrid2));
    t.checkExpect(board2x2D.clickAtLocation(new Posn(1, 1), false), board2x2D);
    t.checkExpect(board0x0.clickAtLocation(new Posn(0, 1), true), board0x0);
  }

  ITile grass = new Grass(new Posn(0, 0));
  ITile dande = new Dandelions(new Posn(0, 0));
  ITile pebbs = new Pebbles(new Posn(0, 0));

  IList<Integer> mtInt = new MtList<Integer>();
  IList<Integer> yesChange = new ConsList<Integer>(0, mtInt);
  IList<Integer> noChange= new ConsList<Integer>(5, mtInt);

  ClickFunc clickLeft = new ClickFunc(true);
  ClickFunc clickRight = new ClickFunc(false);
  ChangeToGrass cTg = new ChangeToGrass();
  ChangeToDandelion cTd = new ChangeToDandelion();
  ChangeToPebbles cTp = new ChangeToPebbles();
  ChangeTilesToIf yesChangePebb= new ChangeTilesToIf(yesChange, true, 4);
  ChangeTilesToIf yesChangeDande= new ChangeTilesToIf(yesChange, false, 4);
  ChangeTilesToIf noChangePebb= new ChangeTilesToIf(noChange, true, 4);
  ChangeTilesToIf noChangeDande= new ChangeTilesToIf(noChange, false, 4);
  // test apply on IFuncTile
  // this will also test visitGrass, visitDandelions, and visitPebbles
  /* This test includes tests for:
   * ClickFunc
   * ChangeToGrass
   * ChangeToDande
   * ChangeToPebbles
   * ChangeTilesToIf
   */
  void testApplyIFuncTile(Tester t) {
    t.checkExpect(clickLeft.apply(grass), dande);
    t.checkExpect(clickLeft.apply(dande), grass);
    t.checkExpect(clickLeft.apply(pebbs), grass);

    t.checkExpect(clickRight.apply(grass), pebbs);
    t.checkExpect(clickRight.apply(dande), dande);
    t.checkExpect(clickRight.apply(pebbs), pebbs);

    t.checkExpect(cTg.apply(grass), grass);
    t.checkExpect(cTg.apply(dande), grass);
    t.checkExpect(cTg.apply(pebbs), grass);

    t.checkExpect(cTd.apply(grass), dande);
    t.checkExpect(cTd.apply(dande), dande);
    t.checkExpect(cTd.apply(pebbs), dande);

    t.checkExpect(cTp.apply(grass), pebbs);
    t.checkExpect(cTp.apply(dande), pebbs);
    t.checkExpect(cTp.apply(pebbs), pebbs);

    t.checkExpect(yesChangePebb.apply(grass), pebbs);
    t.checkExpect(yesChangePebb.apply(dande), pebbs);
    t.checkExpect(yesChangePebb.apply(pebbs), pebbs);

    t.checkExpect(yesChangeDande.apply(grass), dande);
    t.checkExpect(yesChangeDande.apply(dande), dande);
    t.checkExpect(yesChangeDande.apply(pebbs), dande);

    t.checkExpect(noChangePebb.apply(grass), grass);
    t.checkExpect(noChangePebb.apply(dande), dande);
    t.checkExpect(noChangePebb.apply(pebbs), pebbs);

    t.checkExpect(noChangeDande.apply(grass), grass);
    t.checkExpect(noChangeDande.apply(dande), dande);
    t.checkExpect(noChangeDande.apply(pebbs), pebbs);
  }

  IList<ITile> singletonRow = 
      new ConsList<ITile>(new Grass(new Posn(0, 0)), mtTileList);


  IList<IList<ITile>> singletonGrid = 
      new ConsList<IList<ITile>>(singletonRow, mtListTileList);
  // test DrawBoard functional class
  void testDrawBoard(Tester t) {
    DrawBoard db = new DrawBoard();
    t.checkExpect(db.apply(singletonGrid), new AboveImage(new EmptyImage(), new DrawRow().apply(singletonRow)));
    t.checkExpect(db.apply(mtListTileList), new EmptyImage());
  }

  // test DrawRow functional class
  void testDrawRow(Tester t) {
    DrawRow dr = new DrawRow();
    t.checkExpect(dr.apply(singletonRow), new BesideImage(grass.draw(), new EmptyImage()));
    t.checkExpect(dr.apply(mtTileList), new EmptyImage());
  }

  // test TileCollision functional class
  void testTileCollision(Tester t) {
    TileCollision yes = new TileCollision(new Posn(0, 0));
    TileCollision no = new TileCollision(new Posn(60, 60));
    t.checkExpect(yes.apply(grass), false);
    t.checkExpect(yes.apply(dande), true);
    t.checkExpect(yes.apply(pebbs), false);

    t.checkExpect(no.apply(grass), false);
    t.checkExpect(no.apply(dande), false);
    t.checkExpect(no.apply(pebbs), false);
  }
  
  int rowW = 6;
  int colL = 6;

  boolean testBigBang(Tester t) {
    World w = new StartingWorld(rowW, colL);
    int worldWidth = 40 * colL;
    int worldHeight = 40 * rowW;
    double tickRate = 1.0 / 28.0;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}

