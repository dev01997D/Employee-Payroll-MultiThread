package com.blz.employeepayrollsql.model;

import java.time.LocalDate;
import java.util.Objects;

public class Employee {
	public int emp_id;
	public double salary;
	public String name;
	public String address;
	public LocalDate startDate;
	public String gender;
	public String companyName;
	public int companyId;
	public int deptId;
	public String deptName;
	public String is_active;


	public Employee(int emp_id, String name,  String gender, double salary,  LocalDate startDate) {
		this.emp_id = emp_id;
		this.name = name;
		this.gender = gender;
		this.startDate = startDate;
		this.salary = salary;
	}

	@Override
	public int hashCode() {
		return Objects.hash(emp_id, gender, name, salary, startDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Employee))
			return false;
		Employee other = (Employee) obj;
		return emp_id == other.emp_id && Objects.equals(gender, other.gender) && Objects.equals(name, other.name)
				&& Double.doubleToLongBits(salary) == Double.doubleToLongBits(other.salary)
				&& Objects.equals(startDate, other.startDate);
	}


	@Override
	public String toString() {
		return "Employee [emp_id=" + emp_id + ", salary=" + salary + ", name=" + name + ", startDate=" + startDate
				+ ", gender=" + gender + "]";
	}
}
