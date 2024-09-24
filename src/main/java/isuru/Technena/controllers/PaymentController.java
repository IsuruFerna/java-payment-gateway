package isuru.Technena.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import java.util.Arrays;
import com.stripe.model.tax.Calculation;
import com.stripe.model.tax.Transaction;
import com.stripe.param.tax.CalculationCreateParams;
import com.stripe.param.tax.CalculationCreateParams.CustomerDetails;
import com.stripe.param.tax.CalculationCreateParams.CustomerDetails.Address;
import com.stripe.param.tax.CalculationCreateParams.CustomerDetails.AddressSource;
import com.stripe.param.tax.CalculationCreateParams.LineItem;
import com.stripe.param.tax.TransactionCreateFromCalculationParams;
import com.stripe.exception.StripeException;
import isuru.Technena.responses.TokenResponse;
import isuru.Technena.responses.stripe.CreatePaymentResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/stripe")
public class PaymentController {

    private static Gson gson = new Gson();

    static class CreatePaymentItem {
        @SerializedName("id")
        String id;

        public String getId() {
            return id;
        }
        @SerializedName("amount")
        Long amount;

        public CreatePaymentItem(String id, Long amount) {
            this.id = id;
            this.amount = amount;
        }

        public Long getAmount() {
            return amount;
        }
    }

    static class CreatePayment {
        @SerializedName("items")
        CreatePaymentItem[] items;

        public CreatePaymentItem[] getItems() {
            return items;
        }
    }

    static class CreatePaymentResponse {
        private String clientSecret;
        private String dpmCheckerLink;
        public CreatePaymentResponse(String clientSecret, String transactionId) {
            this.clientSecret = clientSecret;
            // [DEV]: For demo purposes only, you should avoid exposing the PaymentIntent ID in the client-side code.
            this.dpmCheckerLink = "https://dashboard.stripe.com/settings/payment_methods/review?transaction_id="+transactionId;
        }
    }

    static Calculation calculateTax(List<CreatePaymentItem> items, String currency) throws StripeException {
        List<LineItem> lineItems = items.stream()
                .map(PaymentController::buildLineItem)
                .collect(Collectors.toList());

        CalculationCreateParams.Builder createParamsBuilder = CalculationCreateParams.builder()
                .setCurrency(currency)
                .setCustomerDetails(CustomerDetails.builder()
                        .setAddress(Address.builder()
                                .setLine1("920 5th Ave")
                                .setCity("Seattle")
                                .setState("WA")
                                .setPostalCode("98104")
                                .setCountry("US")
                                .build())
                        .setAddressSource(AddressSource.SHIPPING)
                        .build())
                .addAllLineItem(lineItems);

        return Calculation.create(createParamsBuilder.build());
    }

    static LineItem buildLineItem(CreatePaymentItem item) {
        return LineItem.builder ()
                .setAmount(item.getAmount()) // Amount in cents
                .setReference(item.getId()) // Unique reference for the item in the scope of the calculation
                .build();
    }

    // Securely calculate the order amount, including tax
    static long calculateOrderAmount(Calculation taxCalculation) {
        // Calculate the order total with any exclusive taxes on the server to prevent
        // people from directly manipulating the amount on the client
        return taxCalculation.getAmountTotal();
    }

    // Invoke this method in your webhook handler when `payment_intent.succeeded` webhook is received
    static Transaction handlePaymentIntentSucceeded(PaymentIntent paymentIntent) throws StripeException {
        // Create a Tax Transaction for the successful payment
        TransactionCreateFromCalculationParams createParams = TransactionCreateFromCalculationParams.builder()
                .setCalculation(paymentIntent.getMetadata().get("tax_calculation"))
                .setReference("myOrder_123") // Replace with a unique reference from your checkout/order system
                .build();

        return Transaction.createFromCalculation(createParams);
    }

    // This is stripe secret API key.


    @GetMapping("/check")
    public TokenResponse res() {
        System.out.println("testing!");
        return new TokenResponse("this is checking!");
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponseDTO createPaymentIntent(@RequestBody CreatePayment createPayment) throws StripeException {
//        @RequestBody CreatePayment createPayment
//        String json = gson.toJson(createPayment);


//        Logger loggerCreatePayment = LoggerFactory.getLogger(CreatePayment.class);
//        loggerCreatePayment.info("this is payment info: " + createPayment.toString());


//        ArrayList<CreatePaymentItem> paymentItemArrayList = new ArrayList<>();
//        paymentItemArrayList.add(new CreatePaymentItem("xl-tshirt", 1000L));

        // Create a Tax Calculation for the items being sold
        Calculation taxCalculation = calculateTax(Arrays.asList(createPayment.getItems()), "eur");
//        Calculation taxCalculation = calculateTax(paymentItemArrayList, "eur");

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
//                        .setAmount(calculateOrderAmount(taxCalculation))
                        .setAmount(15 * 100L)
                        .setCurrency("eur")
                        // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .putMetadata("tax_calculation", taxCalculation.getId())
                        .build();

        // Create a PaymentIntent with the order amount and currency
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        String link = "https://dashboard.stripe.com/settings/payment_methods/review?transaction_id=" + paymentIntent.getId();

//        CreatePaymentResponse paymentResponse = new CreatePaymentResponse(paymentIntent.getClientSecret(), paymentIntent.getId());
//        return paymentResponse;

        return new CreatePaymentResponseDTO(paymentIntent.getClientSecret(), link);



    }
}
