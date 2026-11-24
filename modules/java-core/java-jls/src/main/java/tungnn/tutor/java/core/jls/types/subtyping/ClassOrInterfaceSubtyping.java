package tungnn.tutor.java.core.jls.types.subtyping;

public class ClassOrInterfaceSubtyping {

  static void main(String[] args) {
    // 1. non-generic
    nonGeneric();

    // 2. generic & raw type
    rawType();

    // 3. generic & parameterized type
    parameterizedType();

    // 4. generic & parameterized type which has wildcard
    wildcardType();

    // 5. intersection type
    intersectionType();

    // 6. type variable
    typeVariable();

    // 7. lower-bound of type variable
    lowerBoundOfTypeVariable();
  }

  // 1. Non-generic subtyping (JLS §4.10.1)
  // Diagram: SubT <: T, T <: S, T <: I
  static void nonGeneric() {
    class S {}
    interface I {}
    class T extends S implements I {}
    class SubT extends T {}

    T t = new T();
    SubT subT = new SubT();

    S s;
    I i;

    s = t; // Direct Supertype: T <: S
    i = t; // Direct Supertype: T <: I
    s = subT; // Transitive subtyping: SubT <: T and T <: S  =>  SubT <: S
  }

  // 2. Generic & Raw type (JLS §4.10.2)
  // Rule: C<X> is a subtype of the raw type C
  static void rawType() {
    class T {}
    class C<X> {}

    C<T> cParameterized = new C<T>();

    @SuppressWarnings("rawtypes")
    C cRaw;

    cRaw = cParameterized; // C<T> <: C (Raw)
  }

  // 3. Generic & Parameterized type (JLS §4.10.2)
  // Subtyping rule when the type argument is kept:
  // If D<X> extends C<X> then D<T> <: C<T>
  // Note: C<SubT> is NOT a subtype of C<T> (invariance)
  static void parameterizedType() {
    class S<F> {}
    class T<F> extends S<F> {}

    S<String> sOfT;
    T<String> tOfT = new T<>();

    // S<X> >1 T<X>, S >1 T
    sOfT = tOfT;

    // Compile error: T<Integer> is NOT a subtype of S<Number>
    // S<Number> invalid = new T<Integer>();
  }

  // 4. Generic & Parameterized type which has wildcard (JLS §4.5.1 / §4.10.2)
  // Covariance (? extends T) and contravariance (? super T) rules
  static void wildcardType() {
    class S {}
    class T extends S {}
    class SubT extends T {}

    class C<X> {}

    C<SubT> cOfSubT = new C<SubT>();
    C<S> cOfS = new C<S>();

    // Covariance: C<SubT> <: C<? extends T> (do SubT <: T)
    C<? extends T> cExtendsT = cOfSubT;

    // Contravariance: C<S> <: C<? super T> (do T <: S)
    C<? super T> cSuperT = cOfS;
  }

  // 5. Intersection type (JLS §4.9)
  // Rule: an intersection type (I & J) is a subtype of both I and J
  static void intersectionType() {
    interface I {}
    interface J {}

    class IntersectionImpl implements I, J {}

    IntersectionImpl target = new IntersectionImpl();

    // Cast to create an intersection type: (I & J)
    Object obj = target;

    I i = target; // (I & J) <: I
    J j = target; // (I & J) <: J
  }

  // 6. Type variable (JLS §4.4)
  // Rule: a type variable X extends T satisfies X <: T
  static void typeVariable() {
    class S {}
    interface I {}
    class T extends S implements I {}
    class SubT extends T {}

    class GenericHolder<X extends T> {
      void process(X x) {
        T t = x; // X <: T
        S s = x; // X <: S (by transitivity: X <: T and T <: S)
        I i = x; // X <: I (by transitivity: X <: T and T <: I)
      }
    }

    GenericHolder<SubT> holder = new GenericHolder<>();
    holder.process(new SubT());
  }

  // 7. Lower-bound of type variable (JLS §5.1.10 / §4.5.1)
  // Rule: through wildcard contravariance C<? super X>,
  // a method accepts values of type X (or a subtype of X)
  static void lowerBoundOfTypeVariable() {
    class S {}
    class T extends S {}
    class C<X> {
      void add(X item) {}
    }

    class Helper {
      // The type variable X acts as the lower bound of the wildcard parameter C<? super X>
      <X> void accept(C<? super X> container, X value) {
        // C<? super X> accepts writes of type X
      }
    }

    Helper helper = new Helper();
    C<S> cOfS = new C<S>();
    T t = new T();

    // X is inferred as T.
    // Valid because cOfS has type C<S>, which matches C<? super T> (since T <: S)
    helper.accept(cOfS, t);
  }
}
