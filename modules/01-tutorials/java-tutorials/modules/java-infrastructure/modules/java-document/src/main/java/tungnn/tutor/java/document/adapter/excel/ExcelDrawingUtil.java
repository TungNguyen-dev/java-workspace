package tungnn.tutor.java.document.adapter.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;

/**
 * Utility class cung cấp các phương thức thao tác với hình ảnh, hình khối (Shapes) và ghi chú
 * (Comments) trên Excel bằng Apache POI.
 */
public final class ExcelDrawingUtil {

  private ExcelDrawingUtil() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  // ==========================================
  // 1. XỬ LÝ HÌNH ẢNH (IMAGE UTILS)
  // ==========================================

  public static void insertImage(Workbook workbook, Sheet sheet, Cell cell, Path imagePath) {
    Objects.requireNonNull(cell, "Cell must not be null");
    insertImage(workbook, sheet, cell.getAddress(), imagePath);
  }

  public static void insertImage(
      Workbook workbook, Sheet sheet, CellAddress cellAddress, Path imagePath) {
    Objects.requireNonNull(workbook, "Workbook must not be null");
    Objects.requireNonNull(sheet, "Sheet must not be null");
    Objects.requireNonNull(cellAddress, "CellAddress must not be null");
    Objects.requireNonNull(imagePath, "ImagePath must not be null");

    if (!Files.exists(imagePath)) {
      throw new IllegalArgumentException("Image file not found: " + imagePath);
    }

    byte[] imageBytes;
    try (InputStream is = Files.newInputStream(imagePath)) {
      imageBytes = is.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read image file: " + imagePath, e);
    }

    int pictureType = detectPictureType(imagePath);
    int pictureIdx = workbook.addPicture(imageBytes, pictureType);

    Drawing<?> drawing = getOrCreateDrawingPatriarch(sheet);

    ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(cellAddress.getColumn());
    anchor.setRow1(cellAddress.getRow());

    Picture picture = drawing.createPicture(anchor, pictureIdx);
    picture.resize();
  }

  // ==========================================
  // 2. XỬ LÝ HÌNH KHỐI (SHAPE UTILS - TƯƠNG THÍCH XSSF / XLSX)
  // ==========================================

  /**
   * Tạo hình chữ nhật (Rectangle) nối từ góc trên bên trái ô (fromCol, fromRow) đến ô (toCol,
   * toRow).
   */
  public static XSSFSimpleShape createRectangle(
      Workbook workbook, Sheet sheet, int fromCol, int fromRow, int toCol, int toRow) {
    if (!(sheet instanceof org.apache.poi.xssf.usermodel.XSSFSheet)) {
      throw new IllegalArgumentException(
          "Chức năng vẽ Shape hiện tại chỉ hỗ trợ định dạng .xlsx (XSSF)");
    }

    XSSFDrawing drawing = (XSSFDrawing) getOrCreateDrawingPatriarch(sheet);
    XSSFClientAnchor anchor = (XSSFClientAnchor) workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(fromCol);
    anchor.setRow1(fromRow);
    anchor.setCol2(toCol);
    anchor.setRow2(toRow);

    XSSFSimpleShape shape = drawing.createSimpleShape(anchor);
    shape.setShapeType(ShapeTypes.RECT);

    return shape;
  }

  /** Tạo đường mũi tên (Arrow) bắt đầu từ ô (fromCol, fromRow) đến ô (toCol, toRow). */
  public static XSSFSimpleShape createArrow(
      Workbook workbook, Sheet sheet, int fromCol, int fromRow, int toCol, int toRow) {
    if (!(sheet instanceof org.apache.poi.xssf.usermodel.XSSFSheet)) {
      throw new IllegalArgumentException(
          "Chức năng vẽ Shape hiện tại chỉ hỗ trợ định dạng .xlsx (XSSF)");
    }

    XSSFDrawing drawing = (XSSFDrawing) getOrCreateDrawingPatriarch(sheet);
    XSSFClientAnchor anchor = (XSSFClientAnchor) workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(fromCol);
    anchor.setRow1(fromRow);
    anchor.setCol2(toCol);
    anchor.setRow2(toRow);

    XSSFSimpleShape shape = drawing.createSimpleShape(anchor);
    shape.setShapeType(ShapeTypes.LINE);

    shape.setLineWidth(1.5);
    shape
        .getCTShape()
        .getSpPr()
        .getLn()
        .addNewHeadEnd()
        .setType(org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType.NONE);
    shape
        .getCTShape()
        .getSpPr()
        .getLn()
        .addNewTailEnd()
        .setType(org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType.TRIANGLE);

    return shape;
  }

  // ==========================================
  // 3. XỬ LÝ GHI CHÚ (CELL COMMENT UTILS)
  // ==========================================

  /**
   * Thêm Cell Comment (Ghi chú) vào một Cell cụ thể.
   *
   * @param cell Cell cần gắn comment
   * @param author Tên tác giả comment
   * @param commentText Nội dung ghi chú
   */
  public static Comment addCellComment(Cell cell, String author, String commentText) {
    Objects.requireNonNull(cell, "Cell must not be null");
    Sheet sheet = cell.getSheet();
    Workbook workbook = sheet.getWorkbook();
    CreationHelper factory = workbook.getCreationHelper();

    Drawing<?> drawing = getOrCreateDrawingPatriarch(sheet);

    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 2);
    anchor.setRow1(cell.getRowIndex());
    anchor.setRow2(cell.getRowIndex() + 2);

    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(commentText);
    comment.setString(str);

    if (author != null && !author.isBlank()) {
      comment.setAuthor(author);
    }

    cell.setCellComment(comment);
    return comment;
  }

  // ==========================================
  // 4. HELPER METHODS (PHƯƠNG THỨC PHỤ TRỢ)
  // ==========================================

  public static Drawing<?> getOrCreateDrawingPatriarch(Sheet sheet) {
    Objects.requireNonNull(sheet, "Sheet must not be null");
    Drawing<?> drawing = sheet.getDrawingPatriarch();
    if (drawing == null) {
      drawing = sheet.createDrawingPatriarch();
    }
    return drawing;
  }

  private static ClientAnchor createAnchor(
      Workbook workbook, int fromCol, int fromRow, int toCol, int toRow) {
    ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(fromCol);
    anchor.setRow1(fromRow);
    anchor.setCol2(toCol);
    anchor.setRow2(toRow);
    return anchor;
  }

  private static int detectPictureType(Path imagePath) {
    String mimeType = null;
    try {
      mimeType = Files.probeContentType(imagePath);
    } catch (IOException e) {
      // Nếu probeContentType lỗi thì fallback về kiểm tra đuôi mở rộng phía dưới
    }

    if (mimeType != null) {
      switch (mimeType.toLowerCase()) {
        case "image/png":
          return Workbook.PICTURE_TYPE_PNG;
        case "image/jpeg":
          return Workbook.PICTURE_TYPE_JPEG;
        case "image/bmp":
        case "image/x-ms-bmp":
          return Workbook.PICTURE_TYPE_DIB;
        case "image/x-emf":
          return Workbook.PICTURE_TYPE_EMF;
        case "image/x-wmf":
          return Workbook.PICTURE_TYPE_WMF;
      }
    }

    String filename = imagePath.getFileName().toString().toLowerCase();
    if (filename.endsWith(".png")) return Workbook.PICTURE_TYPE_PNG;
    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return Workbook.PICTURE_TYPE_JPEG;
    if (filename.endsWith(".bmp") || filename.endsWith(".dib")) return Workbook.PICTURE_TYPE_DIB;

    throw new IllegalArgumentException("Unsupported image type: " + imagePath);
  }
}
