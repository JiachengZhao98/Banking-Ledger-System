package dev.codescreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;

/**
 * Handles all transaction-related API requests for the banking application.
 */

@RestController
@RequestMapping("/api")
class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private static final FormatNumber formatNumber = new FormatNumber();
    private final TransactionProcessor processor = new TransactionProcessor();

    /**
     * Respond to a ping request. Useful for health check purposes.
     * @return PingResponse with status OK
     */
    @GetMapping("/ping")
    public ResponseEntity<PingResponse> ping() {
        PingResponse pingResponse = new PingResponse();
        return ResponseEntity.ok(pingResponse);
    }

    /**
     * Retrieve the current balance for a user based on the provided request details.
     * @param request Contains user and message IDs
     * @return the current balance or an error message if an exception occurs
     */
    @GetMapping("/checkBalance")
    public ResponseEntity<CheckBalanceResponse> checkBalance(@RequestBody CheckBalanceRequest request) {
        try {
            BigDecimal currentBalance = processor.checkBalance(request.getMessageId(), request.getUserId());
            CheckBalanceResponse checkBalanceResponse = new CheckBalanceResponse(
                    request.getMessageId(),
                    request.getUserId(),
                    formatNumber.formatNumber(currentBalance.toString())
            );
            return ResponseEntity.ok(checkBalanceResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CheckBalanceResponse(
                    request.getMessageId(),
                    request.getUserId(),
                    "ERROR" + e.getMessage()
            ));
        }
    }

    /**
     * Process a load transaction to add funds to a user's account.
     * @param request Transaction details including amount, userId and messageId
     * @return LoadResponse with the updated balance or an error message if an exception occurs
     */
    @PutMapping("/load")
    public ResponseEntity<LoadResponse> LOAD(@RequestBody TransactionRequest request) {
        try {
            BigDecimal newBalance = processor.load(request.getMessageId(), request.getUserId(), new BigDecimal(request.getAmount()));
            LoadResponse loadResponse = new LoadResponse(
                    request.getMessageId(),
                    request.getUserId(),
                    formatNumber.formatNumber(newBalance.toString()),
                    request.getCurrency(),
                    request.getDebitOrCredit()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(loadResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadResponse(request.getMessageId(), request.getUserId(),
                            "ERROR: " + e.getMessage(), request.getCurrency(), request.getDebitOrCredit()));
        }
    }

    /**
     * Processes an authorization request to withdraw funds if the user's balance permits.
     * @param request Transaction details including amount, userId and messageID
     * @return AuthorizationResponse with the result of the transaction or an error message if an exception occurs
     */
    @PutMapping("/authorization")
    public ResponseEntity<AuthorizationResponse> AUTHORIZATION(@RequestBody TransactionRequest request) {
        try {
            BigDecimal currentBalance = processor.checkBalance(request.getMessageId(), request.getUserId());
            BigDecimal newBalance = processor.authorization(request.getMessageId(), request.getUserId(), new BigDecimal(request.getAmount()));
            String responseCode = currentBalance.compareTo(newBalance) > 0 ? "APPROVED" : "DENIED";
            AuthorizationResponse authorizationResponse = new AuthorizationResponse(
                    request.getMessageId(),
                    request.getUserId(),
                    responseCode,
                    formatNumber.formatNumber(newBalance.toString()),
                    request.getCurrency(),
                    request.getDebitOrCredit()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(authorizationResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthorizationResponse(request.getMessageId(), request.getUserId(),
                            "ERROR: " + e.getMessage(), "ERROR", request.getCurrency(), request.getDebitOrCredit()));
        }
    }

    /**
     * Generic exception handler for non-specific exceptions.
     * @param ex The exception that was caught
     * @param request Web request during which the exception occurred
     * @return ErrorResponse detailing the issue
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "ERROR_CODE_UNDEFINED");  // Default error code
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Exception handler specifically for handling bad requests such as invalid arguments.
     * @param ex The exception that was caught
     * @param request Web request during which the exception occurred
     * @return ErrorResponse with a custom message indicating an invalid request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> handleBadRequestExceptions(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "INVALID_REQUEST");
        return ResponseEntity.badRequest().body(error);
    }
}
