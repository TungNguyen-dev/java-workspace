package tungnn.tutor.java.core.lib.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PropertyReflectionUtil {

  private PropertyReflectionUtil() {}

  public static List<PropertyMetadata> resolveProperties(Class<?> clazz) {
    if (clazz.isRecord()) {
      return resolveRecordProperties(clazz);
    }

    var fields = ReflectionUtil.getInstanceFields(clazz);

    var methods =
        ReflectionUtil.getInstanceMethods(clazz).stream()
            .collect(Collectors.toMap(Method::getName, Function.identity(), (a, b) -> a));

    return fields.stream()
        .map(
            field -> {
              var accessor = resolveAccessor(field, methods);
              var mutator = resolveMutator(field, methods);

              return new PropertyMetadata(
                  field, accessor, mutator, accessor != null, mutator != null);
            })
        .toList();
  }

  private static Method resolveAccessor(Field field, Map<String, Method> methods) {
    var fieldName = field.getName();
    var fieldType = field.getType();

    Set<String> candidates = new LinkedHashSet<>();

    if (fieldType == boolean.class) {
      if (fieldName.startsWith("is")
          && fieldName.length() > 2
          && Character.isUpperCase(fieldName.charAt(2))) {
        candidates.add(fieldName); // isActive()
      } else {
        candidates.add("is" + capitalize(fieldName)); // active -> isActive()
      }
    } else {
      candidates.add("get" + capitalize(fieldName));
    }

    for (var candidate : candidates) {
      var method = methods.get(candidate);

      if (method != null
          && method.getParameterCount() == 0
          && fieldType.isAssignableFrom(method.getReturnType())) {
        return method;
      }
    }

    return null;
  }

  private static Method resolveMutator(Field field, Map<String, Method> methods) {
    var fieldName = field.getName();
    var fieldType = field.getType();
    String setterName;

    if (fieldType == boolean.class
        && fieldName.startsWith("is")
        && fieldName.length() > 2
        && Character.isUpperCase(fieldName.charAt(2))) {
      setterName = "set" + fieldName.substring(2); // isActive -> setActive()
    } else {
      setterName = "set" + capitalize(fieldName); // active -> setActive(), name -> setName()
    }

    var method = methods.get(setterName);

    if (method == null) {
      return null;
    }

    if (method.getParameterCount() != 1) {
      return null;
    }

    if (!field.getType().equals(method.getParameterTypes()[0])) {
      return null;
    }

    return method;
  }

  private static String capitalize(String value) {
    if (value.isEmpty()) {
      return value;
    }

    return Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  private static List<PropertyMetadata> resolveRecordProperties(Class<?> clazz) {
    return Stream.of(clazz.getRecordComponents())
        .map(PropertyReflectionUtil::toPropertyMetadata)
        .toList();
  }

  private static PropertyMetadata toPropertyMetadata(RecordComponent component) {
    try {
      var field = component.getDeclaringRecord().getDeclaredField(component.getName());
      var accessor = component.getAccessor();

      return new PropertyMetadata(field, accessor, null, true, false);
    } catch (NoSuchFieldException e) {
      throw new IllegalStateException("Cannot resolve record field: " + component.getName(), e);
    }
  }

  public record PropertyMetadata(
      Field field, Method accessor, Method mutator, boolean readable, boolean writable) {}
}
