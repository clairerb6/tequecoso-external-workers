package cl.tequecoso.worker.worker;

import cl.tequecoso.worker.model.CuentaPorPagar;
import cl.tequecoso.worker.model.Proveedor;
import cl.tequecoso.worker.service.CuentaPorPagarService;
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
public class CuentaPorPagarWorker extends AbstractRestWorker {

    private final CuentaPorPagarService cuentaPorPagarService;

    public CuentaPorPagarWorker(CuentaPorPagarService cuentaPorPagarService) {
        this.cuentaPorPagarService = cuentaPorPagarService;
    }

    @FlowableWorker(topic = "cuenta_por_pagar_get_all")
    public WorkerResult getAll(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        List<CuentaPorPagar> cuentas = cuentaPorPagarService.getAll();
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorPagar", cuentas)
                .variable("totalCuentasPorPagar", cuentas.size());
    }

    @FlowableWorker(topic = "cuenta_por_pagar_create")
    public WorkerResult create(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        CuentaPorPagar cuenta = readEntity(
                variables,
                "cuentaPorPagar",
                this::cuentaDesdeVariables
        );
        CuentaPorPagar created = cuentaPorPagarService.create(cuenta);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentaPorPagar", created)
                .variable("cuentaPorPagarId", created.getId())
                .variable("cuentaPorPagarCreada", true);
    }

    @FlowableWorker(topic = "cuenta_por_pagar_get_by_id")
    public WorkerResult getById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            Long id = requiredLong(job.getVariables(), "id");
            CuentaPorPagar cuenta = cuentaPorPagarService.getById(id);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorPagar", cuenta)
                    .variable("cuentaPorPagarId", cuenta.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorPagar", null)
                    .variable("cuentaPorPagarId", (Long) null)
                    .variable("found", false);
        }
    }

    @FlowableWorker(topic = "cuenta_por_pagar_update")
    public WorkerResult update(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Map<String, Object> variables = job.getVariables();
        Long id = requiredLong(variables, "id");
        CuentaPorPagar cuenta = readEntity(
                variables,
                "cuentaPorPagar",
                this::cuentaDesdeVariables
        );
        CuentaPorPagar updated = cuentaPorPagarService.update(id, cuenta);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentaPorPagar", updated)
                .variable("cuentaPorPagarId", updated.getId())
                .variable("cuentaPorPagarActualizada", true);
    }

    @FlowableWorker(topic = "cuenta_por_pagar_delete")
    public WorkerResult deleteById(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long id = requiredLong(job.getVariables(), "id");
        cuentaPorPagarService.deleteById(id);
        return resultBuilder.success()
                .variable("cuentaPorPagarId", id)
                .variable("cuentaPorPagarEliminada", true);
    }

    @FlowableWorker(topic = "cuenta_por_pagar_get_by_proveedor_id")
    public WorkerResult getByProveedorId(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        Long proveedorId = requiredLong(job.getVariables(), "proveedorId");
        List<CuentaPorPagar> cuentas = cuentaPorPagarService.getByProveedorId(proveedorId);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorPagar", cuentas)
                .variable("totalCuentasPorPagar", cuentas.size())
                .variable("proveedorId", proveedorId);
    }

    @FlowableWorker(topic = "cuenta_por_pagar_get_by_status")
    public WorkerResult getByStatus(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        String status = requiredText(job.getVariables(), "status");
        List<CuentaPorPagar> cuentas = cuentaPorPagarService.getByStatus(status);
        return resultBuilder.success()
                .convertAndAddJsonVariable("cuentasPorPagar", cuentas)
                .variable("totalCuentasPorPagar", cuentas.size())
                .variable("status", status);
    }

    @FlowableWorker(topic = "cuenta_por_pagar_get_by_document_number")
    public WorkerResult getByDocumentNumber(
            AcquiredExternalWorkerJob job,
            WorkerResultBuilder resultBuilder
    ) {
        try {
            String documentNumber = requiredText(
                    job.getVariables(),
                    "documentNumber"
            );
            CuentaPorPagar cuenta = cuentaPorPagarService.getByDocumentNumber(documentNumber);
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorPagar", cuenta)
                    .variable("cuentaPorPagarId", cuenta.getId())
                    .variable("found", true);
        } catch (WebClientResponseException.NotFound e) {
            return resultBuilder.success()
                    .convertAndAddJsonVariable("cuentaPorPagar", null)
                    .variable("cuentaPorPagarId", (Long) null)
                    .variable("found", false);
        }
    }

    private CuentaPorPagar cuentaDesdeVariables(Map<String, Object> variables) {
        CuentaPorPagar cuenta = new CuentaPorPagar();
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

        Long proveedorId = optionalLong(variables, "proveedorId");
        if (proveedorId != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(proveedorId);
            cuenta.setProveedor(proveedor);
        }

        return cuenta;
    }
}
