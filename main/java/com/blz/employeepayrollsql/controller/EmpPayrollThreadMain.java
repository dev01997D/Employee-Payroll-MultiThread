/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.employeepayrollsql.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import com.blz.employeepayrollsql.model.CustomThreadException;
import com.blz.employeepayrollsql.model.EmpPayrollDBServiceThread;
import com.blz.employeepayrollsql.model.Employee;

public class EmpPayrollThreadMain {
	private static Logger log = Logger.getLogger(EmpPayrollThreadMain.class.getName());

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	private List<Employee> empPayrollList;
	private EmpPayrollDBServiceThread empPayrollDBServicebj;

	// Creating Singleton object of EmpPayrollDBService
	public EmpPayrollThreadMain() {
		empPayrollDBServicebj = EmpPayrollDBServiceThread.getInstance();
	}

	public EmpPayrollThreadMain(List<Employee> empPayrollList) {
		this();
		this.empPayrollList = empPayrollList;
	}

	public List<Employee> readEmployeePayrollData(IOService ioService) throws CustomThreadException {
		if (ioService.equals(IOService.DB_IO)) {
			this.empPayrollList = empPayrollDBServicebj.readData();
		}
		return this.empPayrollList;
	}

	public void addEmployeeToPayrollDB(List<Employee> employeeList) {
		employeeList.forEach(employeeData -> {
			log.info("Employee being added : " + employeeData.name);
			try {
				this.addEmployeeToPayrollDatabase(employeeData.emp_id, employeeData.name, employeeData.gender,
						employeeData.salary, employeeData.startDate);
			} catch (CustomThreadException e) {
				e.printStackTrace();
			}
			log.info("Employee added : " + employeeData.name);
		});
		log.info("" + this.empPayrollList);
	}

	public void addEmployeeToPayrollWithThreads(List<Employee> employeePayrollDataList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		employeePayrollDataList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				log.info("Employee being added : " + Thread.currentThread().getName());
				try {
					this.addEmployeeToPayrollDatabase(employeePayrollData.emp_id, employeePayrollData.name, employeePayrollData.gender,
							employeePayrollData.salary, employeePayrollData.startDate);
				} catch (CustomThreadException e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(employeePayrollData.hashCode(), true); //Replacing false with true
				log.info("Employee added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		
		//Making to wait main thread till all thread executes
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		log.info("" + this.empPayrollList);
	}

	private void addEmployeeToPayrollDatabase(int emp_id, String name, String gender, double salary,
			LocalDate startDate) throws CustomThreadException {
		empPayrollList.add(empPayrollDBServicebj.addEmployeeToPayrollDB(emp_id, name, gender, salary, startDate));
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			return empPayrollList.size();
		return 0;
	}

	public void updateSalaryOfMultipleEmployees(Map<String, Double> employeeSalaryMap) {
		Map<Integer, Boolean> salaryUpdateStatus = new HashMap<>();
		employeeSalaryMap.forEach((employee, salary) -> {
			Runnable salaryUpdate = () -> {
				salaryUpdateStatus.put(employee.hashCode(), false);
				log.info("Salary being updated : " + Thread.currentThread().getName());
				this.updateEmployeeSalary(employee, salary);
				salaryUpdateStatus.put(employee.hashCode(), true);
				log.info("Salary updated : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(salaryUpdate, employee);
			thread.start();
		});
		while (salaryUpdateStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("" + this.empPayrollList);
	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = empPayrollDBServicebj.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		Employee employeeData = this.getEmployeeData(name);
		if (employeeData != null)
			employeeData.salary = salary;
	}

	private Employee getEmployeeData(String name) {
		return this.empPayrollList.stream().filter(employeeData->employeeData.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<Employee> employeeDataList = empPayrollDBServicebj.getEmployeeData(name);
		return employeeDataList.get(0).equals(this.getEmployeeData(name));
	}
}
