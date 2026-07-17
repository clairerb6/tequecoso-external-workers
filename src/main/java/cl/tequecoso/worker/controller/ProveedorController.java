package cl.tequecoso.worker.controller;

import cl.tequecoso.worker.model.Proveedor;
import cl.tequecoso.worker.service.ProveedorService;
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
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> getAll() {
        return proveedorService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proveedor create(@RequestBody Proveedor proveedor) {
        return proveedorService.create(proveedor);
    }

    @GetMapping("/{id}")
    public Proveedor getById(@PathVariable Long id) {
        return proveedorService.getById(id);
    }

    @PutMapping("/{id}")
    public Proveedor update(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        return proveedorService.update(id, proveedor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        proveedorService.deleteById(id);
    }

    @GetMapping("/rut/{rut}")
    public Proveedor getByRut(@PathVariable Integer rut) {
        return proveedorService.getByRut(rut);
    }

    @GetMapping("/email/{email}")
    public Proveedor getByEmail(@PathVariable String email) {
        return proveedorService.getByEmail(email);
    }
}
