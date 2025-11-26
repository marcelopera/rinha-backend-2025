package br.com.truta.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.truta.models.PaymentProcessRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "fallback-payment")
public interface FallbackProcessPayment {

    @POST
    @Path("/payments")
    Response processPayment(PaymentProcessRequest req);
}
