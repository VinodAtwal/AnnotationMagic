package org.va.model;


import org.va.annotations.Id;
import org.va.annotations.Repository;

@Repository
public class Department {
    @Id
    private String id;
    private String departmentName;
    private String departmentGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentGroup() {
        return departmentGroup;
    }

    public void setDepartmentGroup(String departmentGroup) {
        this.departmentGroup = departmentGroup;
    }
}
