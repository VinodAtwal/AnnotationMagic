package org.va.runner;

import org.va.model.Employee;
import org.va.model.EmployeeDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RepositoryScenariosRunner implements Runnable {
    @Override
    public void run() {
        /*
        * change as your ENV
        */
        String url = "jdbc:mysql://localhost:3306/test";
        String username = "username";
        String password = "password";

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

    }
}
