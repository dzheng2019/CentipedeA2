import tester.*;
// represents a list of type t
interface IList<T> {
  
  /* Template:
   * Methods:
   * this.accept(IListVisitor<T, U> visitor) - U
   * this.length() - int
   */
  
  // accept the given list visitor into this
  <U> U accept(IListVisitor<T, U> visitor);
  // return the length of this list
  int length();
}

// represents an empty list of type t
class MtList<T> implements IList<T> {
  
  /* Template:
   * Same as interface
   */
  
  // accepts a visitor of type t
  public <U> U accept(IListVisitor<T, U> visitor) {
    /* Template:
     * Same as class
     * Parameter:
     * visitor - IListVisitor<T, U>
     * Methods on Parameter:
     * visitor.visitnMt(MtList<T> mt);
     * visitor.visitCons(ConsList<T> cons)
     * visitor.apply(IList<T> arg)
     */
    return visitor.visitMt(this);
  }

  // return the length of this (0 because empty)
  public int length() {
    /* Template:
     * Same as class template
     */
    return 0;
  }

}

// represents a element in a list of type t
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  /* Template:
   * Same as interface
   * Fields:
   * this.first - T
   * this.rest - IList<T>
   * Methods on Fields:
   * rest.accept(IListVisito<T, U> visitor) - u
   * rest.length() - int
   */
  
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }  

  // accepts a visitor of type t
  public <U> U accept(IListVisitor<T, U> visitor) {
    /* Template:
     * Same as class
     * Parameter:
     * visitor - IListVisitor<T, U>
     * Methods on Parameter:
     * visitor.visitnMt(MtList<T> mt);
     * visitor.visitCons(ConsList<T> cons)
     * visitor.apply(IList<T> arg)
     */
    return visitor.visitCons(this);
  }

  //return the length of this (0 because empty)
  public int length() {
    /* Template:
     * Same as class template
     */
    return 1 + this.rest.length();
  }

}

//a function interface
interface IFunc<A, R> {
  R apply(A arg);
}

interface IPred<A> extends IFunc<A, Boolean> {

}

//a visitor that visits lists
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {

  // visits an empty list
  R visitMt(MtList<T> mt);

  // visits an non empty list
  R visitCons(ConsList<T> cons);
}

class OrMap<T> implements IListVisitor<T, Boolean> {

  IPred<T> pred;

  OrMap(IPred<T> pred) {
    this.pred = pred;
  }

   
  public Boolean apply(IList<T> arg) {
 
    return arg.accept(this);
  }

   
  public Boolean visitMt(MtList<T> mt) {
 
    return false;
  }

   
  public Boolean visitCons(ConsList<T> cons) {
 
    return pred.apply(cons.first) || this.apply(cons.rest);
  }

}

class OrMapListPred<T> implements IPred<IList<T>> {

  IPred<T> pred;

  OrMapListPred(IPred<T> pred) {
    this.pred = pred;
  }

   
  public Boolean apply(IList<T> arg) {
    return new OrMap<T>(this.pred).apply(arg);
  }

}

class RowLength<T> implements IListVisitor<IList<T>, Integer> {
   
  public Integer apply(IList<IList<T>> arg) {
 
    return arg.accept(this);
  }

   
  public Integer visitMt(MtList<IList<T>> mt) {
 
    return 0;
  }

   
  public Integer visitCons(ConsList<IList<T>> cons) {
 
    return cons.first.length();
  }
}

class Map<T, R> implements IListVisitor<T, IList<R>> {

  IFunc<T, R> func; 

  Map(IFunc<T, R> func) {
    this.func = func;
  }

   
  public IList<R> apply(IList<T> arg) {
 
    return arg.accept(this);
  }

   
  public IList<R> visitMt(MtList<T> mt) {
 
    return new MtList<R>();
  }

   
  public IList<R> visitCons(ConsList<T> cons) {
 
    return new ConsList<R>(func.apply(cons.first), cons.rest.accept(this));
  }

}

class MapList<T, R> implements IFunc<IList<T>, IList<R>> {

  IFunc<T, R> func;

  MapList(IFunc<T, R> func) {
    this.func = func;
  }

   
  public IList<R> apply(IList<T> arg) {
    return new Map<T, R>(func).apply(arg);
  } 
}

class ContainsInt implements IPred<Integer> {

  int check;

  ContainsInt(int check) {
    this.check = check;
  }

   
  public Boolean apply(Integer arg) {
 
    return arg.intValue() == check;
  }

}

class ChangeAtX<T> implements IListVisitor<T, IList<T>> {
  IFunc<T, T> func;
  int n;

  ChangeAtX(IFunc<T, T> func, int n) {
    this.func = func;
    this.n = n;
  }

   
  public IList<T> apply(IList<T> arg) {
 
    return arg.accept(this);
  }

   
  public IList<T> visitMt(MtList<T> mt) {
 
    return new MtList<T>();
  }

   
  public IList<T> visitCons(ConsList<T> cons) {
    if (n < 0) {
      return cons;
    }
    if (n == 0) {
      return new ConsList<T>(func.apply(cons.first), cons.rest);
    }
    else {
      return new ConsList<T>(cons.first, new ChangeAtX<T>(func, n - 1).apply(cons.rest));
    }
  }
}

class ChangeAtXY<T> implements IListVisitor<IList<T>, IList<IList<T>>> {

  IFunc<T, T> func;
  int n;
  int r;

  ChangeAtXY(IFunc<T, T> func, int n, int r) {
    this.func = func;
    this.n = n;
    this.r = r;
  }

   
  public IList<IList<T>> apply(IList<IList<T>> arg) {
 
    return arg.accept(this);
  }

   
  public IList<IList<T>> visitMt(MtList<IList<T>> mt) {
 
    return mt;
  }

   
  public IList<IList<T>> visitCons(ConsList<IList<T>> cons) {
    if (r < 0) {
      return cons;
    }
    if (r == 0) {
      return new ConsList<IList<T>>(
          new ChangeAtX<T>(func, n).apply(cons.first), cons.rest);
    }
    else {
      return new ConsList<IList<T>>(cons.first,
          new ChangeAtXY<T>(func, n, r - 1).apply(cons.rest));
    }
  } 
}

interface IFunc2<A1, A2, R> {
  R apply(A1 a1, A2 a2);
}

class Append<T> implements IFunc2<IList<T>, IList<T>, IList<T>> { 
  public IList<T> apply(IList<T> a1, IList<T> a2) {
    return new AppendHelper<T>(a2).apply(a1);
  }
}

class AppendHelper<T> implements IListVisitor<T, IList<T>> {

  IList<T> secondList;
  
  AppendHelper(IList<T> secondList) {
    this.secondList = secondList;
  }
  
   
  public IList<T> apply(IList<T> arg) {
 
    return arg.accept(this);
  }

   
  public IList<T> visitMt(MtList<T> mt) {
 
    return this.secondList;
  }

   
  public IList<T> visitCons(ConsList<T> cons) {
 
    return new ConsList<T>(cons.first, cons.rest.accept(this));
  }
  
}

class FirstNElements<T> implements IListVisitor<T, IList<T>> {
  
  int n;
  
  FirstNElements(int n) {
    this.n = n;
  }
  
   
  public IList<T> apply(IList<T> arg) {
    return arg.accept(this);
  }

   
  public IList<T> visitMt(MtList<T> mt) {
    return mt;
  }

   
  public IList<T> visitCons(ConsList<T> cons) {
    if (n <= 0) {
      return new MtList<T>();
    }
    else {
      return new ConsList<T>(cons.first, 
          new FirstNElements<T>(n - 1).apply(cons.rest));
    }
    
  }
  
}

// created for test purposes
// always returns 3
class MakeThree implements IFunc<Integer, Integer> {
  // returns 3
  public Integer apply(Integer arg) {
    return 3;
  }
}

class ExamplesList {

  IList<Integer> MT = new MtList<Integer>();
  IList<Integer> L55 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(5, MT));
  IList<Integer> L53 = 
      new ConsList<Integer>(5,
          new ConsList<Integer>(3, MT));
  
  // test accept in IList
  void testAccept(Tester t) {
    
  }
  
  
}




