package br.com.truta.service;

import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import br.com.truta.clients.DefaultProcessPayment;
import br.com.truta.clients.FallbackProcessPayment;
import br.com.truta.models.HealthDetails;
import br.com.truta.models.PaymentProcessRequest;
import br.com.truta.models.PaymentRequest;
import io.quarkus.bootstrap.runner.VirtualThreadSupport;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {

    @Inject
    CacheService cacheService;

    @Inject
    Logger logger;

    @RestClient
    @Inject
    DefaultProcessPayment defaultProcessPayment;

    @RestClient
    @Inject
    FallbackProcessPayment fallbackProcessPayment;

    private AtomicBoolean up = new AtomicBoolean(true);

    public void accept(PaymentRequest req) {
        cacheService.add(req);
    }

    private Instant downSince;

    @Scheduled(every = "1s")
    public void process() {
        if (!up.get()) {
            if (downSince != null && Instant.now().isAfter(downSince.plusSeconds(10))) {
                var keys = cacheService.getAllKeys();
                try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                    for (String key : keys) {
                        scope.fork(() -> {
                            sendToProcessorFallback(key, cacheService.get(key));
                            return null;
                        });
                    }
                    scope.join();
                    scope.throwIfFailed();
                } catch (Exception e) {
                    logger.error("Erro no fallback", e);
                }
            }
            return;
        }
        var keys = cacheService.getAllKeys();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (String key : keys) {
                scope.fork(() -> {
                    sendToProcessorDefault(key, cacheService.get(key));
                    return null;
                });
            }
            scope.join();
            scope.throwIfFailed();
        } catch (Exception e) {
            logger.error(e);
            up.set(false);
            downSince = Instant.now();
        }
    }

    @Scheduled(every = "5s")
    public void checkHealth() {
        if (!up.get()) {
            logger.info(Instant.now() + " verificando saude do default");
            try {
                up.set(!defaultProcessPayment.getHealth().readEntity(HealthDetails.class).failing());
                logger.info("resultado do up: " + up.get());
                if (up.get()) {
                    downSince = null;
                }
            } catch (Exception e) {
                logger.error("Erro ao verificar saude", e);
            }
        }
    }

    private void sendToProcessorDefault(String correlationId, String amount) {
        logger.info("tentando executar no default para: " + correlationId);
        throw new RuntimeException();
        // defaultProcessPayment.processPayment(new PaymentProcessRequest(correlationId, amount, Instant.now().toString()));
        // cacheService.del(correlationId);
    }

    private void sendToProcessorFallback(String correlationId, String amount) {
        logger.info("tentando executar no fallback para: " + correlationId);
        fallbackProcessPayment.processPayment(new PaymentProcessRequest(correlationId, amount, Instant.now().toString()));
        cacheService.del(correlationId);
    }
}
