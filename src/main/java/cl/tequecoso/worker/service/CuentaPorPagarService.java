package cl.tequecoso.worker.service;

import cl.tequecoso.worker.model.CuentaPorPagar;
import cl.tequecoso.worker.worker.support.AbstractRestWorker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaPorPagarService extends AbstractRestWorker {

    private static final String PATH = "/cuentas-por-pagar";

    public List<CuentaPorPagar> getAll() {
        return getList(PATH, CuentaPorPagar.class);
    }

    public CuentaPorPagar create(CuentaPorPagar cuenta) {
        validarCuenta(cuenta);
        return post(PATH, cuenta, CuentaPorPagar.class);
    }

    public CuentaPorPagar getById(Long id) {
        return get(PATH + "/" + id, CuentaPorPagar.class);
    }

    public CuentaPorPagar update(Long id, CuentaPorPagar cuenta) {
        cuenta.setId(id);
        validarCuenta(cuenta);
        return put(PATH + "/" + id, cuenta, CuentaPorPagar.class);
    }

    public void deleteById(Long id) {
        delete(PATH + "/" + id);
    }

    public List<CuentaPorPagar> getByProveedorId(Long proveedorId) {
        if (proveedorId == null) {
            throw new IllegalArgumentException("La variable 'proveedorId' es obligatoria");
        }
        return getList(PATH + "/proveedor/" + proveedorId, CuentaPorPagar.class);
    }

    public List<CuentaPorPagar> getByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("La variable 'status' es obligatoria");
        }
        return getList(PATH + "/estado/" + status.trim(), CuentaPorPagar.class);
    }

    public CuentaPorPagar getByDocumentNumber(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("La variable 'documentNumber' es obligatoria");
        }
        return get(PATH + "/documento/" + documentNumber.trim(), CuentaPorPagar.class);
    }

    public void validarCuenta(CuentaPorPagar cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta por pagar es obligatoria");
        }
        if (cuenta.getProveedor() == null || cuenta.getProveedor().getId() == null) {
            throw new IllegalArgumentException("La variable 'proveedorId' es obligatoria");
        }
        if (cuenta.getDocumentNumber() == null || cuenta.getDocumentNumber().isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        if (cuenta.getStatus() == null || cuenta.getStatus().isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
    }
}
