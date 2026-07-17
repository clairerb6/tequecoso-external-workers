package cl.tequecoso.worker.service;

import cl.tequecoso.worker.model.Proveedor;
import cl.tequecoso.worker.worker.support.AbstractRestWorker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService extends AbstractRestWorker {

    private static final String PATH = "/proveedores";

    public List<Proveedor> getAll() {
        return getList(PATH, Proveedor.class);
    }

    public Proveedor create(Proveedor proveedor) {
        validarProveedor(proveedor);
        return post(PATH, proveedor, Proveedor.class);
    }

    public Proveedor getById(Long id) {
        return get(PATH + "/" + id, Proveedor.class);
    }

    public Proveedor update(Long id, Proveedor proveedor) {
        proveedor.setId(id);
        validarProveedor(proveedor);
        return put(PATH + "/" + id, proveedor, Proveedor.class);
    }

    public void deleteById(Long id) {
        delete(PATH + "/" + id);
    }

    public Proveedor getByRut(Integer rut) {
        if (rut == null || rut <= 0) {
            throw new IllegalArgumentException("La variable 'rut' es obligatoria");
        }
        return get(PATH + "/rut/" + rut, Proveedor.class);
    }

    public Proveedor getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("La variable 'email' es obligatoria");
        }
        return get(PATH + "/email/" + email.trim(), Proveedor.class);
    }

    public void validarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor es obligatorio");
        }
        if (proveedor.getProveedor() == null || proveedor.getProveedor().isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        if (proveedor.getRut() <= 0) {
            throw new IllegalArgumentException("El RUT es obligatorio");
        }
        if (proveedor.getDv() == '\0') {
            throw new IllegalArgumentException("El dígito verificador es obligatorio");
        }
        if (proveedor.getEmail() == null || proveedor.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
    }
}
