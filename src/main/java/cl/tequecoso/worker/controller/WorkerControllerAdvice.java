package cl.tequecoso.worker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class WorkerControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Solicitud inválida");
        detail.setDetail(exception.getMessage());
        return detail;
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ProblemDetail handleWebClient(WebClientResponseException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(exception.getStatusCode());
        detail.setTitle("Error al consumir servicio REST");
        detail.setDetail(exception.getResponseBodyAsString());
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Error interno");
        detail.setDetail(exception.getMessage());
        return detail;
    }
}
