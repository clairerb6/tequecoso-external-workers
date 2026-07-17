package cl.tequecoso.worker.worker;

import cl.tequecoso.worker.model.Cliente;
import cl.tequecoso.worker.model.CuentaPorCobrar;
import cl.tequecoso.worker.service.CuentaPorCobrarService;
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
public class CuentaPorCobrarWorker extends AbstractRestWorker {

    private final CuentaPorCobrarService cuentaPorCobrarService;

    public CuentaPorCobrarWorker(CuentaPorCobrarService cuentaPorCobrarService) {
        this.cuentaPorCobrarService = cuentaPorCobrarService;
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_get_all")
    public WorkerResult getAll(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarService.getAll();
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorCobrar", cuentas)
                .variable("totalCuentasPorCobrar", cuentas.size());
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_create")
    public WorkerResult create(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        CuentaPorCobrar cuenta = readEntity(
                variables,
                "cuentaPorCobrar",
                this::cuentaDesdeVariables
        );
        CuentaPorCobrar created = cuentaPorCobrarService.create(cuenta);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentaPorCobrar", created)
                .variable("cuentaPorCobrarId", created.getId())
                .variable("cuentaPorCobrarCreada", true);
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_get_by_id")
    public WorkerResult getById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            Long id = requiredLong(job.getVariables(), "id");
            CuentaPorCobrar cuenta = cuentaPorCobrarService.getById(id);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorCobrar", cuenta)
                    .variable("cuentaPorCobrarId", cuenta.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorCobrar", null)
                    .variable("cuentaPorCobrarId", (Long) null)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_update")
    public WorkerResult update(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Long id = requiredLong(variables, "id");
        CuentaPorCobrar cuenta = readEntity(
                variables,
                "cuentaPorCobrar",
                this::cuentaDesdeVariables
        );
        CuentaPorCobrar updated = cuentaPorCobrarService.update(id, cuenta);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentaPorCobrar", updated)
                .variable("cuentaPorCobrarId", updated.getId())
                .variable("cuentaPorCobrarActualizada", true);
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_delete")
    public WorkerResult deleteById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long id = requiredLong(job.getVariables(), "id");
        cuentaPorCobrarService.deleteById(id);
        return resultBuilder.success()
                .variable("cuentaPorCobrarId", id)
                .variable("cuentaPorCobrarEliminada", true);
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_get_by_client_id")
    public WorkerResult getByClientId(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long clientId = requiredLong(job.getVariables(), "clientId");
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarService.getByClientId(clientId);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorCobrar", cuentas)
                .variable("totalCuentasPorCobrar", cuentas.size())
                .variable("clientId", clientId);
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_get_by_status")
    public WorkerResult getByStatus(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        String status = requiredText(job.getVariables(), "status");
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarService.getByStatus(status);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorCobrar", cuentas)
                .variable("totalCuentasPorCobrar", cuentas.size())
                .variable("status", status);
    }

    @FlowableWorker(topic = "cuenta_por_cobrar_get_by_document_number")
    public WorkerResult getByDocumentNumber(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            String documentNumber = requiredText(
                    job.getVariables(),
                    "documentNumber"
            );
            CuentaPorCobrar cuenta = cuentaPorCobrarService.getByDocumentNumber(documentNumber);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorCobrar", cuenta)
                    .variable("cuentaPorCobrarId", cuenta.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorCobrar", null)
                    .variable("cuentaPorCobrarId", (Long) null)
                    .variable("found", false);
        }
    }

    private CuentaPorCobrar cuentaDesdeVariables(Map<String, Object> variables) {
        CuentaPorCobrar cuenta = new CuentaPorCobrar();
        cuenta.setDocumentNumber(optionalText(variables, "documentNumber"));
        cuenta.setDescription(optionalText(variables, "description"));
        cuenta.setTotalAmount(optionalDecimal(variables, "totalAmount"));
        cuenta.setOutstandingBalance(optionalDecimal(variables, "outstandingBalance"));
        cuenta.setIssueDate(optionalDate(variables, "issueDate"));
        cuenta.setDueDate(optionalDate(variables, "dueDate"));
        cuenta.setPaymentDate(optionalDate(variables, "paymentDate"));
        cuenta.setStatus(optionalText(variables, "status"));
        cuenta.setCurrency(optionalText(variables, "currency"));
        cuenta.setNotes(optionalText(variables, "notes"));

        Long clientId = optionalLong(variables, "clientId");
        if (clientId != null) {
            Cliente cliente = new Cliente();
            cliente.setId(clientId);
            cuenta.setClient(cliente);
        }

        return cuenta;
    }
}
