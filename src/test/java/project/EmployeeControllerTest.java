package project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        Employee employee = new Employee("Davi", "IT", 5000.0);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Davi"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.salary").value(5000.0));
    }

    @Test
    void shouldReturn404WhenEmployeeNotFound() throws Exception {
        mockMvc.perform(get("/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListEmployeesByDepartment() throws Exception {
        repository.save(new Employee("Davi", "IT", 5000.0));
        repository.save(new Employee("Ana", "HR", 4000.0));
        repository.save(new Employee("Bruno", "IT", 4500.0));

        mockMvc.perform(get("/employees").param("department", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateSalary() throws Exception {
        Employee saved = repository.save(new Employee("Davi", "IT", 5000.0));

        String body = "{\"newSalary\": 7500.0}";

        mockMvc.perform(patch("/employees/" + saved.getId() + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(7500.0));
    }

    @Test
    void shouldRejectNonPositiveSalary() throws Exception {
        Employee saved = repository.save(new Employee("Davi", "IT", 5000.0));

        String body = "{\"newSalary\": -100.0}";

        mockMvc.perform(patch("/employees/" + saved.getId() + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
