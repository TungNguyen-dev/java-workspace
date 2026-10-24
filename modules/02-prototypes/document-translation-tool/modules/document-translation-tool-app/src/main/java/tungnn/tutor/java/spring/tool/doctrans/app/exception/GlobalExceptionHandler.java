package tungnn.tutor.java.spring.tool.doctrans.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import tungnn.tutor.java.spring.tool.doctrans.app.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApi(ApiException e) {
    return ResponseEntity.status(Integer.parseInt(e.code()))
        .body(new ErrorResponse(e.code(), e.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException e) {
    return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
        .body(new ErrorResponse("FILE_TOO_LARGE", "Uploaded file exceeds the size limit"));
  }
}
