package br.com.truta.clients;

import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.truta.models.PaymentProcessRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "default-payment")
public interface DefaultProcessPayment {
    
    @POST
    @Path("/payments")
    Response processPayment(PaymentProcessRequest req);

    @GET
    @Path("/payments/service-health")
    Response getHealth();
    
}
