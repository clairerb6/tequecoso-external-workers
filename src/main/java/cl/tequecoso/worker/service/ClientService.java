package cl.tequecoso.worker.service;

import cl.tequecoso.worker.model.Cliente;
import cl.tequecoso.worker.worker.support.AbstractRestWorker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService extends AbstractRestWorker {

    private static final String PATH = "/client";

    public List<Cliente> getAll() {
        return getList(PATH, Cliente.class);
    }

    public Cliente create(Cliente cliente) {
        validarCliente(cliente);
        return post(PATH, cliente, Cliente.class);
    }

    public Cliente getById(Long id) {
        return get(PATH + "/" + id, Cliente.class);
    }

    public Cliente update(Long id, Cliente cliente) {
        cliente.setId(id);
        validarCliente(cliente);
        return put(PATH + "/" + id, cliente, Cliente.class);
    }

    public void deleteById(Long id) {
        delete(PATH + "/" + id);
    }

    public Cliente getByRut(Integer rut) {
        if (rut == null || rut <= 0) {
            throw new IllegalArgumentException("La variable 'rut' es obligatoria");
        }
        return get(PATH + "/rut/" + rut, Cliente.class);
    }

    public Cliente getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("La variable 'email' es obligatoria");
        }
        return get(PATH + "/email/" + email.trim(), Cliente.class);
    }

    public void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (cliente.getBusinessName() == null || cliente.getBusinessName().isBlank()) {
            throw new IllegalArgumentException("La razón social del cliente es obligatoria");
        }
        if (cliente.getRut() <= 0) {
            throw new IllegalArgumentException("El RUT es obligatorio");
        }
        if (cliente.getDv() == '\0') {
            throw new IllegalArgumentException("El dígito verificador es obligatorio");
        }
        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
    }
}
