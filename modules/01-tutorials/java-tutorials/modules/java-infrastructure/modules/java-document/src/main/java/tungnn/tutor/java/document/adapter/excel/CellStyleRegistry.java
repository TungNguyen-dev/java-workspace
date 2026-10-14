package tungnn.tutor.java.document.adapter.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class CellStyleRegistry {

  private final Map<String, CellStyle> cellStyleMap = new HashMap<>();
  private final Workbook workbook;

  public CellStyleRegistry(Workbook workbook) {
    if (workbook == null) {
      throw new IllegalArgumentException("Workbook cannot be null");
    }
    this.workbook = workbook;
  }

  // ==========================================
  // ENUM METHODS (Sử dụng chính)
  // ==========================================

  public CellStyle getOrCreate(CellStyleEnum styleEnum, Supplier<CellStyle> styleSupplier) {
    return getOrCreate(styleEnum.name(), styleSupplier);
  }

  public boolean contains(CellStyleEnum styleEnum) {
    return contains(styleEnum.name());
  }

  public CellStyle get(CellStyleEnum styleEnum) {
    return get(styleEnum.name());
  }

  // ==========================================
  // STRING OVERLOADS (Dự phòng cho key động)
  // ==========================================

  public CellStyle getOrCreate(String key, Supplier<CellStyle> styleSupplier) {
    return cellStyleMap.computeIfAbsent(
        key,
        k -> {
          CellStyle newStyle = styleSupplier.get();
          if (newStyle == null) {
            throw new IllegalStateException("Supplier returned a null CellStyle for key: " + key);
          }
          return newStyle;
        });
  }

  public boolean contains(String key) {
    return cellStyleMap.containsKey(key);
  }

  public CellStyle get(String key) {
    return cellStyleMap.get(key);
  }

  public Workbook getWorkbook() {
    return this.workbook;
  }

  public void clear() {
    cellStyleMap.clear();
  }
}
