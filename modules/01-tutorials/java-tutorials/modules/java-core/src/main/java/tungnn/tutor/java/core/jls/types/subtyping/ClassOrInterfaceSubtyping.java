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
  // Sơ đồ: SubT <: T, T <: S, T <: I
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
    s = subT; // Transitive Subtyping: SubT <: T và T <: S  =>  SubT <: S
  }

  // 2. Generic & Raw type (JLS §4.10.2)
  // Quy tắc: C<X> là subtype của Raw Type C
  static void rawType() {
    class T {}
    class C<X> {}

    C<T> cParameterized = new C<T>();

    @SuppressWarnings("rawtypes")
    C cRaw;

    cRaw = cParameterized; // C<T> <: C (Raw)
  }

  // 3. Generic & Parameterized type (JLS §4.10.2)
  // Quy tắc Subtyping khi giữ nguyên Type Argument:
  // Nếu D<X> extends C<X> thì D<T> <: C<T>
  // Chú ý: C<SubT> KHÔNG PHẢI là subtype của C<T> (Invariance)
  static void parameterizedType() {
    class S<F> {}
    class T<F> extends S<F> {}

    S<String> sOfT;
    T<String> tOfT = new T<>();

    // S<X> >1 T<X>, S >1 T
    sOfT = tOfT;

    // Lỗi biên dịch: T<Integer> KHÔNG PHẢI là subtype của S<Number>
    // S<Number> invalid = new T<Integer>();
  }

  // 4. Generic & Parameterized type which has wildcard (JLS §4.5.1 / §4.10.2)
  // Quy tắc Covariance (? extends T) và Contravariance (? super T)
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
  // Quy tắc: Kiểu giao (I & J) là subtype của cả I và J
  static void intersectionType() {
    interface I {}
    interface J {}

    class IntersectionImpl implements I, J {}

    IntersectionImpl target = new IntersectionImpl();

    // Ép kiểu tạo Intersection Type: (I & J)
    Object obj = (I & J) target;

    I i = (I & J) target; // (I & J) <: I
    J j = (I & J) target; // (I & J) <: J
  }

  // 6. Type variable (JLS §4.4)
  // Quy tắc: Biến kiểu X extends T sẽ thỏa mãn X <: T
  static void typeVariable() {
    class S {}
    interface I {}
    class T extends S implements I {}
    class SubT extends T {}

    class GenericHolder<X extends T> {
      void process(X x) {
        T t = x; // X <: T
        S s = x; // X <: S (do bắc cầu: X <: T và T <: S)
        I i = x; // X <: I (do bắc cầu: X <: T và T <: I)
      }
    }

    GenericHolder<SubT> holder = new GenericHolder<>();
    holder.process(new SubT());
  }

  // 7. Lower-bound of type variable (JLS §5.1.10 / §4.5.1)
  // Quy tắc: Thông qua Wildcard Contravariance C<? super X>,
  // phương thức nhận giá trị kiểu X (hoặc subtype của X)
  static void lowerBoundOfTypeVariable() {
    class S {}
    class T extends S {}
    class C<X> {
      void add(X item) {}
    }

    class Helper {
      // Biến kiểu X đóng vai trò là Lower-bound cho Wildcard parameter C<? super X>
      <X> void accept(C<? super X> container, X value) {
        // C<? super X> chấp nhận thao tác nạp dữ liệu kiểu X
      }
    }

    Helper helper = new Helper();
    C<S> cOfS = new C<S>();
    T t = new T();

    // X được suy luận là T.
    // Thỏa mãn vì cOfS có kiểu C<S> tương ứng C<? super T> (do T <: S)
    helper.accept(cOfS, t);
  }
}
