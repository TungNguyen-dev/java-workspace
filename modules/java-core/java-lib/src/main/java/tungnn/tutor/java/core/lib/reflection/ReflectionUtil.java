package tungnn.tutor.java.core.lib.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ReflectionUtil {

  private ReflectionUtil() {}

  public static List<Field> getInstanceFields(Class<?> clazz) {
    Objects.requireNonNull(clazz);

    return Stream.of(clazz.getDeclaredFields())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .toList();
  }

  public static List<Method> getInstanceMethods(Class<?> clazz) {
    Objects.requireNonNull(clazz);

    return Stream.of(clazz.getDeclaredMethods())
        .filter(method -> !Modifier.isStatic(method.getModifiers()))
        .toList();
  }

  public static <T> Constructor<T> getCanonicalConstructor(Class<T> clazz) {
    Objects.requireNonNull(clazz);

    try {
      Class<?>[] paramTypes;

      if (clazz.isRecord()) {
        paramTypes =
            Stream.of(clazz.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
      } else {
        paramTypes = getInstanceFields(clazz).stream().map(Field::getType).toArray(Class<?>[]::new);
      }

      return clazz.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Cannot find a canonical constructor matching the fields for: " + clazz.getName(), e);
    }
  }

  public static <T> T newInstance(Class<T> clazz, Object... args) {
    Objects.requireNonNull(clazz);

    try {
      var constructor = getCanonicalConstructor(clazz);

      if (!constructor.canAccess(null)) {
        constructor.setAccessible(true);
      }

      if (constructor.getParameterCount() == 0 && (args == null || args.length == 0)) {
        return constructor.newInstance();
      }

      return constructor.newInstance(args);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to instantiate class: " + clazz.getName(), e);
    }
  }
}
