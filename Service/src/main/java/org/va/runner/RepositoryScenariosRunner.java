package org.va.runner;

import org.va.model.Employee;
import org.va.model.EmployeeDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RepositoryScenariosRunner implements Runnable {
    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "root";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            EmployeeDao employeeDao = new EmployeeDao(connection);
            Employee employee = new Employee("ID-1", "Vinod Atwal", "ENG");
            employeeDao.createEmployee(employee);

            Employee getEmployee = employeeDao.getEmployee(employee.getId());
            System.out.println(getEmployee);
            System.out.println(employee);
            assert(getEmployee.getId()==employee.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

// Use the connection to interact with the database
    }
}
