package cl.tequecoso.worker.controller;

import cl.tequecoso.worker.model.CuentaPorCobrar;
import cl.tequecoso.worker.service.CuentaPorCobrarService;
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
@RequestMapping("/api/v1/cuentas-por-cobrar")
public class CuentaPorCobrarController {

    private final CuentaPorCobrarService cuentaPorCobrarService;

    public CuentaPorCobrarController(CuentaPorCobrarService cuentaPorCobrarService) {
        this.cuentaPorCobrarService = cuentaPorCobrarService;
    }

    @GetMapping
    public List<CuentaPorCobrar> getAll() {
        return cuentaPorCobrarService.getAll();
    }

    @PostMapping
    public CuentaPorCobrar create(@RequestBody CuentaPorCobrar cuenta) {
        return cuentaPorCobrarService.create(cuenta);
    }

    @GetMapping("/{id}")
    public CuentaPorCobrar getById(@PathVariable Long id) {
        return cuentaPorCobrarService.getById(id);
    }

    @PutMapping("/{id}")
    public CuentaPorCobrar update(@PathVariable Long id, @RequestBody CuentaPorCobrar cuenta) {
        return cuentaPorCobrarService.update(id, cuenta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cuentaPorCobrarService.deleteById(id);
    }

    @GetMapping("/cliente/{clientId}")
    public List<CuentaPorCobrar> getByClientId(@PathVariable Long clientId) {
        return cuentaPorCobrarService.getByClientId(clientId);
    }

    @GetMapping("/estado/{status}")
    public List<CuentaPorCobrar> getByStatus(@PathVariable String status) {
        return cuentaPorCobrarService.getByStatus(status);
    }

    @GetMapping("/documento/{documentNumber}")
    public CuentaPorCobrar getByDocumentNumber(@PathVariable String documentNumber) {
        return cuentaPorCobrarService.getByDocumentNumber(documentNumber);
    }
}
