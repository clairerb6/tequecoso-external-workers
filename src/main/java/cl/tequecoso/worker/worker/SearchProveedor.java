package cl.tequecoso.worker.worker;

import cl.tequecoso.worker.commonInterface.ExternalWorkerHandler;
import cl.tequecoso.worker.model.Proveedor;
import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.WorkerResult;
import org.flowable.external.worker.WorkerResultBuilder;
import org.flowable.external.worker.annotation.FlowableWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
public class SearchProveedor implements ExternalWorkerHandler {

    @Value("${servicios.endpoint-url}")
    private String endpoint;

    private static final String PATH = "/proveedores/rut/";

    @FlowableWorker(topic = "search_proveedor")
    @Override
    public WorkerResult processJob(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder) {

        try {
            Map<String, Object> variables = job.getVariables();

            Object rutVariable = variables.get("rut");

            if (rutVariable == null) {
                return resultBuilder.failure()
                        .message("El RUT es obligatorio")
                        .details("El job no contiene la variable 'rut'");
            }

            Proveedor proveedor =
                    (Proveedor) callToRestApi(rutVariable);

            if (proveedor == null) {
                return resultBuilder.success()
                        .variable("exists", false)
                        .variable("proveedorId", (Long) null);
            }

            return resultBuilder.success()
                    .variable("exists", true)
                    .variable("proveedorId", proveedor.getId());

        } catch (WebClientResponseException.NotFound e) {

            return resultBuilder.success()
                    .variable("exists", false)
                    .variable("proveedorId", (Long) null);

        } catch (Exception e) {

            e.printStackTrace();

            return resultBuilder.failure()
                    .message("Error al buscar proveedor")
                    .details(e.toString());
        }
    }

    @Override
    public Object callToRestApi(Object obj) {

        System.out.println("Buscando proveedor vía API REST");

        int rut = parseRut(obj);

        WebClient client = WebClient.builder()
                .baseUrl(endpoint)
                .build();

        return client.get()
                .uri(PATH + rut)
                .retrieve()
                .bodyToMono(Proveedor.class)
                .block();
    }

    private int parseRut(Object obj) {

        if (obj instanceof Number number) {
            return number.intValue();
        }

        if (obj instanceof String value) {
            return Integer.parseInt(value.trim());
        }

        throw new IllegalArgumentException(
                "Tipo de variable 'rut' no soportado: "
                        + obj.getClass().getName()
        );
    }
}