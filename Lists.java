
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

// a function interface
interface IFunc<A, R> {
  /* Template:
   * Methods:
   * this.apply(A arg) - R
   */
  // applies this function to A
  R apply(A arg);
}

// a predicate interface
interface IPred<A> extends IFunc<A, Boolean> {}

//a visitor that visits lists
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {

  /* Template:
   * Same as IFunc
   * Methods:
   * this.visitMt(MtList<T> mt) - R
   * this.visitCons(ConsList<T> cons) - R
   */

  // visits an empty list
  R visitMt(MtList<T> mt);

  // visits an non empty list
  R visitCons(ConsList<T> cons);
}

// represents an or map
class OrMap<T> implements IListVisitor<T, Boolean> {

  /* Template:
   * Same as interface
   * Fields:
   * this.pred - IPred<T>
   * Methods on Fields:
   * this.pred.apply(T arg) - Boolean
   */

  IPred<T> pred;

  // constructs an or map with a given predicate
  OrMap(IPred<T> pred) {
    this.pred = pred;
  }

  // applies this or map on a list
  public Boolean apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }

  // base case for an or map is false
  public Boolean visitMt(MtList<T> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return false;
  }

  // determines if cons satisfies the predicate, or the rest of 
  // the cons satisfies the predicate
  public Boolean visitCons(ConsList<T> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
    return pred.apply(cons.first) || this.apply(cons.rest);
  }
}

// a predicate that ormaps a list and returns the result
class OrMapListPred<T> implements IPred<IList<T>> {
  IPred<T> pred;

  /* Template:
   * Same as interface
   * Fields:
   * this.pred - IPred<T>
   * Methods on Fields:
   * this.pred.apply(T arg) - Boolean
   */

  // constructs an ormap predicate with a given predicate
  OrMapListPred(IPred<T> pred) {
    this.pred = pred;
  }


  public Boolean apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return new OrMap<T>(this.pred).apply(arg);
  }
}

// a function to get the length of a row in a 2D list
class RowLength<T> implements IListVisitor<IList<T>, Integer> {

  /* Template:
   * Same as interface template
   */

  // applies this func on a 2D list
  public Integer apply(IList<IList<T>> arg) {
    /*Template:
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }

  // an empty row has a length has 0
  public Integer visitMt(MtList<IList<T>> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return 0;
  }

  // the length of a cons row is the length of list in the cons
  public Integer visitCons(ConsList<IList<T>> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
    return cons.first.length();
  }
}

// maps a function a list
class Map<T, R> implements IListVisitor<T, IList<R>> {

  /* Template:
   * Same as interface
   * Fields:
   * this.func - IFunc<T, R>
   * Methods on Fields:
   * this.func.apply(T arg) - R
   */


  IFunc<T, R> func; 

  // constructs a map with a given function 
  Map(IFunc<T, R> func) {
    this.func = func;
  }

  // applies this map on a list
  public IList<R> apply(IList<T> arg) {
    return arg.accept(this);
  }


  public IList<R> visitMt(MtList<T> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return new MtList<R>();
  }


  public IList<R> visitCons(ConsList<T> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return new ConsList<R>(func.apply(cons.first), cons.rest.accept(this));
  }
}

// helps applies map to a 2D list
class MapList<T, R> implements IFunc<IList<T>, IList<R>> {

  IFunc<T, R> func;


  /* Template:
   * Same as interface
   * Fields:
   * this.func - IFunc<T, R>
   * Methods on Fields:
   * this.pred.apply(T arg) - R
   */

  // constructs a function that maps over a list and returns the result
  MapList(IFunc<T, R> func) {
    this.func = func;
  }

  // maps a function to a list
  public IList<R> apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return new Map<T, R>(func).apply(arg);
  } 
}

// determines if an int is a given value
class ContainsInt implements IPred<Integer> {

  int check;

  /* Template:
   * Fields:
   * this.check - int 
   * Methods:
   * this.apply(Integer arg) - Boolean
   */

  // constructs a function with a given integer
  ContainsInt(int check) {
    this.check = check;
  }

  // applies this function to a given integer
  public Boolean apply(Integer arg) {
    /* Template:
     * Same as class
     */
    return arg.intValue() == check;
  }
}

// changes the Xth element in a list
class ChangeAtX<T> implements IListVisitor<T, IList<T>> {
  IFunc<T, T> func;
  int n;

  /* Template:
   * Same as interface
   * Fields:
   * this.n - int
   * this.func - IFunc<T, T>
   * Methods on Fields:
   * this.func.apply(T arg) - T
   */


  ChangeAtX(IFunc<T, T> func, int n) {
    this.func = func;
    this.n = n;
  }

  // applies this change on a list
  public IList<T> apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }

  // applies this change on an mt list
  public IList<T> visitMt(MtList<T> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return new MtList<T>();
  }


  // applies a change to this cons.first if the index is 0 or
  // recurses through rest and moves the index down
  public IList<T> visitCons(ConsList<T> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
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

// changes the Xth element of the Yth row
class ChangeAtXY<T> implements IListVisitor<IList<T>, IList<IList<T>>> {

  IFunc<T, T> func;
  int n;
  int r;

  /* Template:
   * Same as interface
   * Fields:
   * this.r - int
   * this.n - int
   * this.func - IFunc<T, T>
   * Methods on Fields:
   * this.func.apply(T arg) - T
   */

  ChangeAtXY(IFunc<T, T> func, int n, int r) {
    this.func = func;
    this.n = n;
    this.r = r;
  }

  // applies this to a 2D list
  public IList<IList<T>> apply(IList<IList<T>> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }


  public IList<IList<T>> visitMt(MtList<IList<T>> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return mt;
  }


  public IList<IList<T>> visitCons(ConsList<IList<T>> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
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

// a function that takes in two arguements and produces a result 
interface IFunc2<A1, A2, R> {
  /* Template:
   * this.apply(A1 a1, A2 a2) - R
   */
  R apply(A1 a1, A2 a2);
}

// appends two lists together
class Append<T> implements IFunc2<IList<T>, IList<T>, IList<T>> { 
  /* Template:
   * Same as interface
   */
  public IList<T> apply(IList<T> a1, IList<T> a2) {
    /*Template:
     * Same as class
     * Parameter:
     * a1 - IList<T>
     * a2 - IList<T>
     * Methods on Parameter:
     * a1.accept(IListVisitor<T, U> visitor) - U
     * a1.length() - int
     * a2.accept(IListVisitor<T, U> visitor) - U
     * a2a.length() - int
     */
    return new AppendHelper<T>(a2).apply(a1);
  }
}

class AppendHelper<T> implements IListVisitor<T, IList<T>> {

  IList<T> secondList;

  /* Template:
   * Same as interface
   * Fields:
   * this.secondList - IList<T>
   * Methods on Fields:
   * secondList.accept(IListVisitor<T, U> visitor) - U
   * secondList.length() - int
   */

  // constructs a helper with a given second list
  AppendHelper(IList<T> secondList) {
    this.secondList = secondList;
  }

  public IList<T> apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }


  public IList<T> visitMt(MtList<T> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */

    return this.secondList;
  }


  public IList<T> visitCons(ConsList<T> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
    return new ConsList<T>(cons.first, cons.rest.accept(this));
  }
}

// gets the first nth elements in a list 
class FirstNElements<T> implements IListVisitor<T, IList<T>> {

  int n;

  /* Template:
   * Same as interface template:
   * Fields:
   * this.n - int
   */

  // constructs a function with a given n, being the number of
  // elements from the front of the list to return 
  FirstNElements(int n) {
    this.n = n;
  }

  public IList<T> apply(IList<T> arg) {
    /*Template:
     * Same as class
     * Parameter:
     * arg - IList<T>
     * Methods on Parameter:
     * arg.accept(IListVisitor<T, U> visitor) - U
     * arg.length() - int
     */
    return arg.accept(this);
  }


  public IList<T> visitMt(MtList<T> mt) {
    /*Template:
     * Same as class
     * Parameter:
     * mt - MtList<T>
     * Methods/Fields on Parameter:
     * mt.accept(IListVisitor<T, U> visitor) - U
     * mt.length() - int
     */
    return mt;
  }


  public IList<T> visitCons(ConsList<T> cons) {
    /*Template:
     * Same as class
     * Parameter:
     * cons - ConsList<T>
     * Methods/Fields on Parameter:
     * cons.first - T
     * cons.rest - IList<T>
     * cons.accept(IListVisitor<T, U> visitor) - U
     * cons.length() - int
     */
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

// created for test purposes
// returns 0 if applied on empty
// returns 1 if applied on cons
class MakeNum<T> implements IListVisitor<T, Integer> {

  @Override
  public Integer apply(IList<T> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public Integer visitMt(MtList<T> mt) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Integer visitCons(ConsList<T> cons) {
    // TODO Auto-generated method stub
    return 1;
  }

}





