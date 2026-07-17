package cl.tequecoso.worker.controller;

import cl.tequecoso.worker.model.CuentaPorPagar;
import cl.tequecoso.worker.service.CuentaPorPagarService;
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
@RequestMapping("/api/v1/cuentas-por-pagar")
public class CuentaPorPagarController {

    private final CuentaPorPagarService cuentaPorPagarService;

    public CuentaPorPagarController(CuentaPorPagarService cuentaPorPagarService) {
        this.cuentaPorPagarService = cuentaPorPagarService;
    }

    @GetMapping
    public List<CuentaPorPagar> getAll() {
        return cuentaPorPagarService.getAll();
    }

    @PostMapping
    public CuentaPorPagar create(@RequestBody CuentaPorPagar cuenta) {
        return cuentaPorPagarService.create(cuenta);
    }

    @GetMapping("/{id}")
    public CuentaPorPagar getById(@PathVariable Long id) {
        return cuentaPorPagarService.getById(id);
    }

    @PutMapping("/{id}")
    public CuentaPorPagar update(@PathVariable Long id, @RequestBody CuentaPorPagar cuenta) {
        return cuentaPorPagarService.update(id, cuenta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cuentaPorPagarService.deleteById(id);
    }

    @GetMapping("/proveedor/{proveedorId}")
    public List<CuentaPorPagar> getByProveedorId(@PathVariable Long proveedorId) {
        return cuentaPorPagarService.getByProveedorId(proveedorId);
    }

    @GetMapping("/estado/{status}")
    public List<CuentaPorPagar> getByStatus(@PathVariable String status) {
        return cuentaPorPagarService.getByStatus(status);
    }

    @GetMapping("/documento/{documentNumber}")
    public CuentaPorPagar getByDocumentNumber(@PathVariable String documentNumber) {
        return cuentaPorPagarService.getByDocumentNumber(documentNumber);
    }
}
