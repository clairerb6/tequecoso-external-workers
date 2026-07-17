package cl.tequecoso.worker.worker;

import cl.tequecoso.worker.model.Cliente;
import cl.tequecoso.worker.service.ClientService;
import cl.tequecoso.worker.worker.support.AbstractRestWorker;
import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.WorkerResult;
import org.flowable.external.worker.WorkerResultBuilder;
import org.flowable.external.worker.annotation.FlowableWorker;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class ClientWorker extends AbstractRestWorker {

    private final ClientService clientService;

    public ClientWorker(ClientService clientService) {
        this.clientService = clientService;
    }

    @FlowableWorker(topic = "client_get_all")
    public WorkerResult getAll(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        List<Cliente> clientes = clientService.getAll();
        return resultBuilder.success()
                .convertAndAddJsonVariable("clientes", clientes)
                .variable("totalClientes", clientes.size());
    }

    @FlowableWorker(topic = "client_create")
    public WorkerResult create(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Cliente cliente = readEntity(
                variables,
                "client",
                this::clienteDesdeVariables
        );
        Cliente created = clientService.create(cliente);
        return resultBuilder.success()
                .convertAndAddJsonVariable("clientObject", created)
                .variable("client", created.getId())
                .variable("clientCreated", true);
    }

    @FlowableWorker(topic = "client_get_by_id")
    public WorkerResult getById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            Long id = requiredLong(job.getVariables(), "id");
            Cliente cliente = clientService.getById(id);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", cliente)
                    .variable("client", cliente.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", null)
                    .variable("client", (Long) null)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "client_update")
    public WorkerResult update(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Long id = requiredLong(variables, "id");
        String variableName = variables.containsKey("clientObject")
                ? "clientObject"
                : "client";
        Cliente cliente = readEntity(variables, variableName, this::clienteDesdeVariables);
        Cliente updated = clientService.update(id, cliente);
        return resultBuilder.success()
                .convertAndAddJsonVariable("clientObject", updated)
                .variable("client", updated.getId())
                .variable("clientUpdated", true);
    }

    @FlowableWorker(topic = "client_delete")
    public WorkerResult deleteById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long id = requiredLong(job.getVariables(), "id");
        clientService.deleteById(id);
        return resultBuilder.success()
                .variable("client", id)
                .variable("clientDeleted", true);
    }

    @FlowableWorker(topic = "search_client")
    @FlowableWorker(topic = "client_get_by_rut")
    public WorkerResult getByRut(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            Integer rut = optionalInteger(job.getVariables(), "rut");
            if (rut == null) {
                throw new IllegalArgumentException(
                        "La variable 'rut' es obligatoria"
                );
            }
            Cliente cliente = clientService.getByRut(rut);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", cliente)
                    .variable("client", cliente.getId())
                    .variable("exists", true)
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", null)
                    .variable("client", (Long) null)
                    .variable("exists", false)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "client_get_by_email")
    public WorkerResult getByEmail(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            String email = requiredText(job.getVariables(), "email");
            Cliente cliente = clientService.getByEmail(email);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", cliente)
                    .variable("client", cliente.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("clientObject", null)
                    .variable("client", (Long) null)
                    .variable("found", false);
        }
    }

    private Cliente clienteDesdeVariables(Map<String, Object> variables) {
        Cliente cliente = new Cliente();
        cliente.setBusinessName(optionalText(variables, "businessName"));
        Integer rut = optionalInteger(variables, "rut");
        cliente.setRut(rut == null ? 0 : rut);
        cliente.setDv(optionalChar(variables, "dv"));
        cliente.setContact(optionalText(variables, "contact"));
        if (cliente.getContact() == null) {
            cliente.setContact(optionalText(variables, "nombreDeContacto"));
        }
        cliente.setContactName(optionalText(variables, "contactName"));
        if (cliente.getContactName() == null) {
            cliente.setContactName(optionalText(variables, "nombreDeContacto"));
        }
        cliente.setEmail(optionalText(variables, "email"));
        cliente.setPhone(optionalText(variables, "phone"));
        if (cliente.getPhone() == null) {
            cliente.setPhone(optionalText(variables, "telefono"));
        }
        cliente.setPhoneSecondary(optionalText(variables, "phoneSecondary"));
        cliente.setAddress(optionalText(variables, "address"));
        if (cliente.getAddress() == null) {
            cliente.setAddress(optionalText(variables, "direccion"));
        }
        cliente.setCity(optionalText(variables, "city"));
        if (cliente.getCity() == null) {
            cliente.setCity(optionalText(variables, "ciudad"));
        }
        cliente.setRegion(optionalText(variables, "region"));
        cliente.setType(optionalText(variables, "type"));
        Boolean active = optionalBoolean(variables, "active");
        cliente.setActive(active == null || active);
        cliente.setNotes(optionalText(variables, "notes"));
        if (cliente.getNotes() == null) {
            cliente.setNotes(optionalText(variables, "observaciones"));
        }
        return cliente;
    }
}
