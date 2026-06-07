package project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SalaryUpdateRequest {

    @NotNull
    @Positive
    private Double newSalary;

    public SalaryUpdateRequest() {}

    public Double getNewSalary() { return newSalary; }
    public void setNewSalary(Double newSalary) { this.newSalary = newSalary; }
}