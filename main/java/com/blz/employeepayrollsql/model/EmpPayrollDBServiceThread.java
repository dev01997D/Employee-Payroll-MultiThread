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
import java.util.logging.Logger;

public class EmpPayrollDBServiceThread {
	private static Logger log = Logger.getLogger(EmpPayrollDBServiceThread.class.getName());
	private int connectionCounter = 0;
	private PreparedStatement preparedStmt = null;
	private static EmpPayrollDBServiceThread employeePayrollDBServiceObj;

	public static EmpPayrollDBServiceThread getInstance() {
		if (employeePayrollDBServiceObj == null)
			employeePayrollDBServiceObj = new EmpPayrollDBServiceThread();
		return employeePayrollDBServiceObj;
	}

	// Loading Driver and getting connection object
	private synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Kumar@12345";
		Connection connection;
		log.info("Processing Thread : " + Thread.currentThread().getName() + " Connecting to database : " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		log.info("Processing Thread : " + Thread.currentThread().getName() + " ID : " + connectionCounter
				+ " Connection is successful! " + connection);
		return connection;
	}

	public List<Employee> readData() throws CustomThreadException {
		String sql = "SELECT * FROM employee_payroll;";
		List<Employee> employeePayrollList = new ArrayList<Employee>();
		try (Connection connection = this.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeeData(resultSet);
		} catch (SQLException e) {
			throw new CustomThreadException("Unable to read available employee data from database");
		}
		return employeePayrollList;
	}

	public synchronized Employee addEmployeeToPayrollDB(int emp_id, String name, String gender, double salary,
			LocalDate startDate) throws CustomThreadException {
		int employeeId = -1;
		Employee employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			int rowAffected = preparedstatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedstatement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new Employee(employeeId, name, gender, salary, startDate);
		} catch (SQLException e) {
			throw new CustomThreadException("Unable to insert employee data into DB");
		}
		return employeePayrollData;
	}

	public synchronized int updateEmployeeData(String name, Double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, Double salary) {
		String sql = String.format("update employee_payroll set salary=%.2f where name='%s'", salary, name);
		try (Connection connection = this.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			return prepareStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<Employee> getEmployeeData(String name) {
		List<Employee> employeePayrollList = null;
		if (this.preparedStmt == null)
			this.preparedStatementForEmployeeData();
		try {
			preparedStmt.setString(1, name);
			ResultSet resultSet = preparedStmt.executeQuery();
			employeePayrollList = this.getEmployeeData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		preparedStmt = null;
		return employeePayrollList;
	}

	private List<Employee> getEmployeeData(ResultSet resultSet) throws SQLException {
		List<Employee> employeePayrollList = new ArrayList<Employee>();
		while (resultSet.next()) {
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			String gender = resultSet.getString("Gender");
			double salary = resultSet.getDouble("salary");
			LocalDate startDate = resultSet.getDate("start").toLocalDate();
			employeePayrollList.add(new Employee(id, name, gender, salary, startDate));
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			preparedStmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
