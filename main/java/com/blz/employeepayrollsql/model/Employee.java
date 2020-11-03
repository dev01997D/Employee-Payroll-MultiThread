package com.blz.employeepayrollsql.model;

import java.time.LocalDate;

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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (companyId != other.companyId)
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (deptId != other.deptId)
			return false;
		if (deptName == null) {
			if (other.deptName != null)
				return false;
		} else if (!deptName.equals(other.deptName))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (emp_id != other.emp_id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Employee [id=" + emp_id + ", salary=" + salary + ", name=" + name + ", address=" + address + ", startDate="
				+ startDate + ", gender=" + gender + ", companyName=" + companyName + ", companyId=" + companyId
				+ ", deptId=" + deptId + ", deptName=" + deptName + ", is_active=" + is_active + "]";
	}
	

}
