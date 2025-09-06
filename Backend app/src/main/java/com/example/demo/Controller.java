package com.example.demo;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Controller {

    @PostMapping("/create-payment-intent")
    public Map<String, String> createPayment(@RequestBody Map<String, Object> data) throws StripeException {

        //setting api key
        String KEY = "sk_test_*************************************************************************************************";
        Stripe.apiKey = KEY;


        Long amount = ((Number)data.get("amount")).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true).build()
                ).build();

        PaymentIntent intent = PaymentIntent.create(params);

        return Map.of("clientSecret", intent.getClientSecret());

    }

    @GetMapping("/hello")
    public String hello() {
        return "hello from the server";
    }

}
