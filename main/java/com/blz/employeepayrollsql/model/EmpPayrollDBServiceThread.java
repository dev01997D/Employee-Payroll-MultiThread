package com.blz.employeepayrollsql.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpPayrollDBServiceThread {
	private static EmpPayrollDBServiceThread employeePayrollDBServiceObj;
	private PreparedStatement preparedStmt;

	public static EmpPayrollDBServiceThread getInstance() {
		if (employeePayrollDBServiceObj == null)
			employeePayrollDBServiceObj = new EmpPayrollDBServiceThread();
		return employeePayrollDBServiceObj;
	}

	// Loading Driver and getting connection object
	private Connection getConnection() throws CustomThreadException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "Kumar@12345";
		Connection con;

		try {
			con = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new CustomThreadException("Error!!! Unable to establish the Connection with JDBC");
		}
		return con;
	}

	public List<Employee> readData() throws CustomThreadException {
		String sql = "SELECT * FROM employee_payroll;";
		List<Employee> employeePayrollList = new ArrayList<Employee>();
		try (Connection connection = this.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new Employee(id, name, gender, salary, startDate));
			}
		} catch (SQLException e) {
			throw new CustomThreadException("Unable to read available employee data from database");
		}
		return employeePayrollList;
	}

	public Employee addEmployeeToPayrollDB(int emp_id, String name, String gender, double salary, LocalDate startDate) throws CustomThreadException {
		int employeeId = -1;
		Employee employeeData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			preparedStmt = connection.prepareStatement(sql);
			int rowAffected = preparedStmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStmt.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeeData = new Employee(employeeId, name, gender, salary, startDate);
		} catch (SQLException e) {
			throw new CustomThreadException("Unable to insert employee data to DB");
		}
		return employeeData;
	}

}
