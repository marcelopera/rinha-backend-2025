package br.com.truta.resources;

import br.com.truta.models.PaymentRequest;
import br.com.truta.service.PaymentService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @POST
    @Path("/payments")
    @RunOnVirtualThread
    public Response payments(PaymentRequest req) {
        paymentService.accept(req);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
