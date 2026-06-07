package project;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // GET /employees  and  GET /employees?department=xxx
    @GetMapping
    public ResponseEntity<List<Employee>> list(@RequestParam(required = false) String department) {
        List<Employee> result = (department == null)
                ? repository.findAll()
                : repository.findByDepartment(department);
        return ResponseEntity.ok(result);
    }

    // GET /employees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return ResponseEntity.ok(employee);
    }

    // POST /employees
    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        Employee saved = repository.save(employee);
        return ResponseEntity
                .created(URI.create("/employees/" + saved.getId()))
                .body(saved);
    }

    // PUT /employees/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Employee> replace(@PathVariable Long id,
                                            @Valid @RequestBody Employee newEmployee) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        existing.setName(newEmployee.getName());
        existing.setDepartment(newEmployee.getDepartment());
        existing.setSalary(newEmployee.getSalary());
        return ResponseEntity.ok(repository.save(existing));
    }

    // PATCH /employees/{id}/salary
    @PatchMapping("/{id}/salary")
    public ResponseEntity<Employee> updateSalary(@PathVariable Long id,
                                                 @Valid @RequestBody SalaryUpdateRequest request) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        existing.setSalary(request.getNewSalary());
        return ResponseEntity.ok(repository.save(existing));
    }

    // DELETE /employees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
