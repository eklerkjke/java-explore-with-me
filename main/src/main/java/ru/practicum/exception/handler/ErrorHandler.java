package ru.practicum.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.JSON_DATE_FORMAT);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        String nowTime = LocalDateTime.now().format(FORMATTER);
        HttpStatus status = HttpStatus.NOT_FOUND;
        return getErrorResponse(status, "Объект не найден", exception.getMessage());
    }

    // Перехватывает исключение из базы данных, при неверных параметрах в запросах
    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        return getErrorResponse(status, "Нарушение целостности данных", exception.getMessage());
    }

    // Перехватывает исключения при валидации с помощью jakarta.validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return getErrorResponse(status, "Запрос составлен некорректно", exception.getMessage());
    }


    @ExceptionHandler(DataAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataAlreadyInUseException(DataAlreadyInUseException exception) {
        HttpStatus status = HttpStatus.CONFLICT;

        return getErrorResponse(status, "Исключение, связанное с нарушением целостности данных", exception.getMessage());
    }

    @ExceptionHandler(ConflictStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictEventStateException(ConflictStateException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        return getErrorResponse(status, "Исключение, связанное с конфликтом статуса события при изменении", exception.getMessage());
    }

    @ExceptionHandler(ConflictTimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictEventTimeException(ConflictTimeException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        return getErrorResponse(status, "Исключение, связанное  с конфликтом времени события при изменении", exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return getErrorResponse(status, "Исключение, связанное  валидацией данных", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        return getErrorResponse(status, "Исключение, связанное с неизвестным значением аргумента", exception.getMessage());
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalArgumentException(ConditionsNotMetException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        return getErrorResponse(status, "Исключение, связанное с неизвестным значением аргумента", exception.getMessage());
    }

    private ErrorResponse getErrorResponse(HttpStatus status, String reason, String message) {
        return ErrorResponse.builder()
                .status(status.name())
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

}