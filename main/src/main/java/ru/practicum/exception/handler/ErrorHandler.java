package ru.practicum.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.JSON_DATE_FORMAT);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.NOT_FOUND;
        return getResponseEntity(status, "Объект не найден",
                exception.getMessage(), nowTime);
    }

    // Перехватывает исключение из базы данных, при неверных параметрах в запросах
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;

        return getResponseEntity(status, "Нарушение целостности данных",
                exception.getMessage(), nowTime);
    }

    // Перехватывает исключения при валидации с помощью jakarta.validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return getResponseEntity(status, "Запрос составлен некорректно",
                exception.getMessage(), nowTime);
    }


    @ExceptionHandler(DataAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleDataAlreadyInUseException(DataAlreadyInUseException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT; // возможно статус должен быть другим

        return getResponseEntity(status,
                "Исключение, связанное с нарушением целостности данных", exception.getMessage(), nowTime);
    }

    @ExceptionHandler(ConflictStateException.class)
    public ResponseEntity<ErrorResponse> handleConflictEventStateException(ConflictStateException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;
        return getResponseEntity(status,
                "Исключение, связанное с конфликтом статуса события при изменении", exception.getMessage(), nowTime);
    }

    @ExceptionHandler(ConflictTimeException.class)
    public ResponseEntity<ErrorResponse> handleConflictEventTimeException(ConflictTimeException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;
        return getResponseEntity(status,
                "Исключение, связанное  с конфликтом времени события при изменении", exception.getMessage(), nowTime);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return getResponseEntity(status,
                "Исключение, связанное  валидацией данных", exception.getMessage(), nowTime);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;
        return getResponseEntity(status,
                "Исключение, связанное с неизвестным значением аргумента", exception.getMessage(), nowTime);
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(ConditionsNotMetException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.CONFLICT;
        return getResponseEntity(status,
                "Исключение, связанное с неизвестным значением аргумента", exception.getMessage(), nowTime);
    }

    private ResponseEntity<ErrorResponse> getResponseEntity(HttpStatus status, String reason, String message,
                                                            String timestamp) {
        return new ResponseEntity<>(new ErrorResponse(status.name(), reason, message, timestamp), status);
    }

}