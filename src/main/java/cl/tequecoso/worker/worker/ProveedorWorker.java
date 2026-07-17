package cl.tequecoso.worker.worker;

import cl.tequecoso.worker.model.Proveedor;
import cl.tequecoso.worker.service.ProveedorService;
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
public class ProveedorWorker extends AbstractRestWorker {

    private final ProveedorService proveedorService;

    public ProveedorWorker(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @FlowableWorker(topic = "proveedor_get_all")
    public WorkerResult getAll(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        List<Proveedor> proveedores = proveedorService.getAll();
        return resultBuilder.success()
                .convertAndAddJsonVariable("proveedores", proveedores)
                .variable("totalProveedores", proveedores.size());
    }

    @FlowableWorker(topic = "registrar_proveedor")
    @FlowableWorker(topic = "proveedor_create")
    public WorkerResult create(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Proveedor proveedor = readEntity(
                variables,
                "proveedor",
                this::proveedorDesdeVariables
        );
        Proveedor created = proveedorService.create(proveedor);
        return resultBuilder.success()
                .convertAndAddJsonVariable("proveedor", created)
                .variable("proveedorId", created.getId())
                .variable("proveedorRegistrado", true);
    }

    @FlowableWorker(topic = "proveedor_get_by_id")
    public WorkerResult getById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            Long id = requiredLong(job.getVariables(), "id");
            Proveedor proveedor = proveedorService.getById(id);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", proveedor)
                    .variable("proveedorId", proveedor.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", null)
                    .variable("proveedorId", (Long) null)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "proveedor_update")
    public WorkerResult update(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Long id = requiredLong(variables, "id");
        Proveedor proveedor = readEntity(
                variables,
                "proveedor",
                this::proveedorDesdeVariables
        );
        Proveedor updated = proveedorService.update(id, proveedor);
        return resultBuilder.success()
                .convertAndAddJsonVariable("proveedor", updated)
                .variable("proveedorId", updated.getId())
                .variable("proveedorActualizado", true);
    }

    @FlowableWorker(topic = "proveedor_delete")
    public WorkerResult deleteById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long id = requiredLong(job.getVariables(), "id");
        proveedorService.deleteById(id);
        return resultBuilder.success()
                .variable("proveedorId", id)
                .variable("proveedorEliminado", true);
    }

    @FlowableWorker(topic = "search_proveedor")
    @FlowableWorker(topic = "proveedor_get_by_rut")
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
            Proveedor proveedor = proveedorService.getByRut(rut);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", proveedor)
                    .variable("proveedorId", proveedor.getId())
                    .variable("exists", true)
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", null)
                    .variable("proveedorId", (Long) null)
                    .variable("exists", false)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "proveedor_get_by_email")
    public WorkerResult getByEmail(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            String email = requiredText(job.getVariables(), "email");
            Proveedor proveedor = proveedorService.getByEmail(email);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", proveedor)
                    .variable("proveedorId", proveedor.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("proveedor", null)
                    .variable("proveedorId", (Long) null)
                    .variable("found", false);
        }
    }

    private Proveedor proveedorDesdeVariables(Map<String, Object> variables) {
        Proveedor proveedor = new Proveedor();
        proveedor.setProveedor(optionalText(variables, "proveedor"));
        if (proveedor.getProveedor() == null) {
            proveedor.setProveedor(optionalText(variables, "idProveedor"));
        }
        Integer rut = optionalInteger(variables, "rut");
        proveedor.setRut(rut == null ? 0 : rut);
        proveedor.setDv(optionalChar(variables, "dv"));
        proveedor.setContact(optionalText(variables, "contact"));
        if (proveedor.getContact() == null) {
            proveedor.setContact(optionalText(variables, "nombreDeContacto"));
        }
        proveedor.setContactName(optionalText(variables, "contactName"));
        if (proveedor.getContactName() == null) {
            proveedor.setContactName(optionalText(variables, "nombreDeContacto"));
        }
        proveedor.setEmail(optionalText(variables, "email"));
        proveedor.setPhone(optionalText(variables, "phone"));
        if (proveedor.getPhone() == null) {
            proveedor.setPhone(optionalText(variables, "telefono"));
        }
        proveedor.setAddress(optionalText(variables, "address"));
        if (proveedor.getAddress() == null) {
            proveedor.setAddress(optionalText(variables, "direccion"));
        }
        proveedor.setCity(optionalText(variables, "city"));
        if (proveedor.getCity() == null) {
            proveedor.setCity(optionalText(variables, "ciudad"));
        }
        proveedor.setRegion(optionalText(variables, "region"));
        proveedor.setCategory(optionalText(variables, "category"));
        if (proveedor.getCategory() == null) {
            proveedor.setCategory(optionalText(variables, "rubro"));
        }
        proveedor.setNotes(optionalText(variables, "notes"));
        if (proveedor.getNotes() == null) {
            proveedor.setNotes(optionalText(variables, "observaciones"));
        }
        return proveedor;
    }
}
