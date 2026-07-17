package cl.tequecoso.worker.service;

import cl.tequecoso.worker.model.CuentaPorCobrar;
import cl.tequecoso.worker.worker.support.AbstractRestWorker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaPorCobrarService extends AbstractRestWorker {

    private static final String PATH = "/cuentas-por-cobrar";

    public List<CuentaPorCobrar> getAll() {
        return getList(PATH, CuentaPorCobrar.class);
    }

    public CuentaPorCobrar create(CuentaPorCobrar cuenta) {
        validarCuenta(cuenta);
        return post(PATH, cuenta, CuentaPorCobrar.class);
    }

    public CuentaPorCobrar getById(Long id) {
        return get(PATH + "/" + id, CuentaPorCobrar.class);
    }

    public CuentaPorCobrar update(Long id, CuentaPorCobrar cuenta) {
        cuenta.setId(id);
        validarCuenta(cuenta);
        return put(PATH + "/" + id, cuenta, CuentaPorCobrar.class);
    }

    public void deleteById(Long id) {
        delete(PATH + "/" + id);
    }

    public List<CuentaPorCobrar> getByClientId(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("La variable 'clientId' es obligatoria");
        }
        return getList(PATH + "/cliente/" + clientId, CuentaPorCobrar.class);
    }

    public List<CuentaPorCobrar> getByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("La variable 'status' es obligatoria");
        }
        return getList(PATH + "/estado/" + status.trim(), CuentaPorCobrar.class);
    }

    public CuentaPorCobrar getByDocumentNumber(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("La variable 'documentNumber' es obligatoria");
        }
        return get(PATH + "/documento/" + documentNumber.trim(), CuentaPorCobrar.class);
    }

    public void validarCuenta(CuentaPorCobrar cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta por cobrar es obligatoria");
        }
        if (cuenta.getClient() == null || cuenta.getClient().getId() == null) {
            throw new IllegalArgumentException("La variable 'clientId' es obligatoria");
        }
        if (cuenta.getDocumentNumber() == null || cuenta.getDocumentNumber().isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        if (cuenta.getStatus() == null || cuenta.getStatus().isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
    }
}
