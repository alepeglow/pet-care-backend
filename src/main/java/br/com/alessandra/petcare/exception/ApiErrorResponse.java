package br.com.alessandra.petcare.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiErrorResponse", description = "Modelo padrão de erro retornado pela API")
public class ApiErrorResponse {

    @Schema(description = "Data/hora do erro", example = "2025-12-15T16:54:19.5221681")
    private LocalDateTime timestamp;

    @Schema(description = "Status HTTP", example = "400")
    private int status;

    @Schema(description = "Nome do erro HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem do erro", example = "status: O status é obrigatório")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/pets/5")
    private String path;

    @Schema(description = "Erros de validação por campo (campo -> mensagem)")
    private Map<String, String> fieldErrors;

    public ApiErrorResponse() {}

    public ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }
}
