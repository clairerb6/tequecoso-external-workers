package cl.tequecoso.worker.controller;

import cl.tequecoso.worker.model.Cliente;
import cl.tequecoso.worker.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Cliente> getAll() {
        return clientService.getAll();
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return clientService.create(cliente);
    }

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clientService.update(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.deleteById(id);
    }

    @GetMapping("/rut/{rut}")
    public Cliente getByRut(@PathVariable Integer rut) {
        return clientService.getByRut(rut);
    }

    @GetMapping("/email/{email}")
    public Cliente getByEmail(@PathVariable String email) {
        return clientService.getByEmail(email);
    }
}
