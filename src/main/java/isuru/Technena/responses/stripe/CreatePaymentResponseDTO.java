package isuru.Technena.responses.stripe;

public record CreatePaymentResponseDTO(
        String clientSecret,
        String dpmCheckerLink
) {
}
