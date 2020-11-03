/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EmployeePayrollMultiThread;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.blz.employeepayrollsql.controller.EmpPayrollThreadMain;
import com.blz.employeepayrollsql.controller.EmpPayrollThreadMain.IOService;
import com.blz.employeepayrollsql.model.CustomThreadException;
import com.blz.employeepayrollsql.model.Employee;

public class EmpPayrollThreadTest {
	private static Logger log = Logger.getLogger(EmpPayrollThreadTest.class.getName());
	EmpPayrollThreadMain empPayrollThreadObj = null;

	// Using table employee_payroll and payroll_details from payroll_service DB
	@Before
	public void setUp() {
		empPayrollThreadObj = new EmpPayrollThreadMain();
	}

	@Test
	public void given5Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() throws CustomThreadException {
		Employee[] arrayOfEmps = { new Employee(105, "DevS", "M", 2000000.00, LocalDate.of(2018, 12, 6)),
				new Employee(106, "Bill", "M", 3000000.00, LocalDate.now()),
				new Employee(107, "Terisa", "F", 2500000.00, LocalDate.of(2019, 8, 16)),
				new Employee(108, "Natasha", "F", 3000000.00, LocalDate.of(2018, 1, 15)),
				new Employee(109, "Deeksha", "F", 2000000.00, LocalDate.of(2018, 12, 6))
				};
		empPayrollThreadObj.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		empPayrollThreadObj.addEmployeeToPayrollDB(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		log.info("Duration without thread : " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		empPayrollThreadObj.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		log.info("Duartion with Thread : "+Duration.between(threadStart, threadEnd));
		Assert.assertEquals(11, empPayrollThreadObj.countEntries(IOService.DB_IO));
	}
}
