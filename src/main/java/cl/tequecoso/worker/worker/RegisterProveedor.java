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
public class RegisterProveedor implements ExternalWorkerHandler {

    @Value("${servicios.endpoint-url}")
    private String endpoint;

    private static final String PATH = "/proveedores";

    @FlowableWorker(topic = "registrar_proveedor")
    @Override
    public WorkerResult processJob(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder) {

        try {
            Map<String, Object> variables = job.getVariables();

            System.out.println("Variables recibidas: " + variables);

            Proveedor proveedor = crearProveedorDesdeVariables(variables);

            validarDatosObligatorios(proveedor);

            Proveedor proveedorRegistrado =
                    (Proveedor) callToRestApi(proveedor);

            if (proveedorRegistrado == null) {
                return resultBuilder.failure()
                        .message("No fue posible registrar el proveedor")
                        .details("El Web Service no devolvió información");
            }

            return resultBuilder.success()
                    .variable("proveedorId", proveedorRegistrado.getId())
                    .variable("proveedorRegistrado", true)
                    .variable(
                            "mensajeRegistro",
                            "Proveedor registrado correctamente"
                    );

        } catch (WebClientResponseException.Conflict e) {

            System.out.println("El proveedor ya existe");
            System.out.println(e.getResponseBodyAsString());

            return resultBuilder.failure()
                    .message("El proveedor ya existe")
                    .details(e.getResponseBodyAsString());

        } catch (WebClientResponseException.BadRequest e) {

            System.out.println("Datos del proveedor no válidos");
            System.out.println(e.getResponseBodyAsString());

            return resultBuilder.failure()
                    .message("Los datos del proveedor no son válidos")
                    .details(e.getResponseBodyAsString());

        } catch (Exception e) {

            System.out.println("Error al registrar proveedor");
            e.printStackTrace();

            return resultBuilder.failure()
                    .message("Error al registrar proveedor")
                    .details(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : e.toString()
                    );
        }
    }

    @Override
    public Object callToRestApi(Object obj) {

        if (!(obj instanceof Proveedor proveedor)) {
            throw new IllegalArgumentException(
                    "El objeto recibido no corresponde a un proveedor"
            );
        }

        String url = endpoint + PATH;

        System.out.println("POST -> " + url);
        System.out.println("Proveedor: " + proveedor.getProveedor());
        System.out.println("RUT: " + proveedor.getRut());
        System.out.println("DV: " + proveedor.getDv());
        System.out.println("Contacto: " + proveedor.getContact());
        System.out.println("Email: " + proveedor.getEmail());

        WebClient client = WebClient.create();

        try {
            Proveedor respuesta = client.post()
                    .uri(url)
                    .bodyValue(proveedor)
                    .retrieve()
                    .bodyToMono(Proveedor.class)
                    .block();

            System.out.println(
                    "Respuesta del Web Service: " + respuesta
            );

            if (respuesta != null) {
                System.out.println(
                        "Proveedor registrado correctamente. ID: "
                                + respuesta.getId()
                );
            }

            return respuesta;

        } catch (WebClientResponseException e) {

            System.out.println("ERROR HTTP EN REGISTRO");
            System.out.println("Estado: " + e.getStatusCode());
            System.out.println(
                    "Respuesta: " + e.getResponseBodyAsString()
            );

            throw e;

        } catch (Exception e) {

            System.out.println("ERROR GENERAL EN REGISTRO");
            e.printStackTrace();

            throw e;
        }
    }

    private Proveedor crearProveedorDesdeVariables(
            Map<String, Object> variables) {

        Proveedor proveedor = new Proveedor();

        proveedor.setProveedor(
                obtenerTexto(variables, "idProveedor")
        );

        proveedor.setRut(
                obtenerEntero(variables, "rut")
        );

        proveedor.setDv(
                obtenerCaracter(variables, "dv")
        );

        proveedor.setContact(
                obtenerTexto(variables, "nombreDeContacto")
        );

        proveedor.setContactName(
                obtenerTexto(variables, "nombreDeContacto")
        );

        proveedor.setEmail(
                obtenerTexto(variables, "email")
        );

        proveedor.setPhone(
                obtenerTexto(variables, "telefono")
        );

        proveedor.setAddress(
                obtenerTexto(variables, "direccion")
        );

        proveedor.setCity(
                obtenerTexto(variables, "ciudad")
        );

        proveedor.setRegion(
                obtenerTexto(variables, "region")
        );

        proveedor.setCategory(
                obtenerTexto(variables, "rubro")
        );

        proveedor.setNotes(
                obtenerTexto(variables, "observaciones")
        );

        return proveedor;
    }

    private void validarDatosObligatorios(Proveedor proveedor) {

        if (proveedor.getProveedor() == null
                || proveedor.getProveedor().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio"
            );
        }

        if (proveedor.getRut() <= 0) {
            throw new IllegalArgumentException(
                    "El RUT es obligatorio"
            );
        }

        if (proveedor.getDv() == '\0') {
            throw new IllegalArgumentException(
                    "El dígito verificador es obligatorio"
            );
        }

        if (proveedor.getContact() == null
                || proveedor.getContact().isBlank()) {
            throw new IllegalArgumentException(
                    "El contacto es obligatorio"
            );
        }

        if (proveedor.getEmail() == null
                || proveedor.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio"
            );
        }
    }

    private String obtenerTexto(
            Map<String, Object> variables,
            String nombreVariable) {

        Object valor = variables.get(nombreVariable);

        if (valor == null) {
            return null;
        }

        String texto = valor.toString().trim();

        return texto.isEmpty() ? null : texto;
    }

    private int obtenerEntero(
            Map<String, Object> variables,
            String nombreVariable) {

        Object valor = variables.get(nombreVariable);

        if (valor == null) {
            return 0;
        }

        if (valor instanceof Number numero) {
            return numero.intValue();
        }

        return Integer.parseInt(
                valor.toString().trim()
        );
    }

    private char obtenerCaracter(
            Map<String, Object> variables,
            String nombreVariable) {

        String valor =
                obtenerTexto(variables, nombreVariable);

        if (valor == null) {
            return '\0';
        }

        return valor.charAt(0);
    }
}