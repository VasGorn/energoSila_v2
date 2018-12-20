package model;

import http.Const;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class WorkList {
    public static final int NUM_DAY = 0;
    public static final int WORK_TIME = 1;
    public static final int OVER_WORK = 2;
    public static final int APPROV = 3;

    private Order order;
    private ArrayList<WorkTypeWithEmployees> workTypeList;

    private static final String EMPLOYEES = "employees";
    private static final String WORK = "work";


    //----------------------------------------------------------------------
    // CONSTRUCTOR
    public WorkList(Order order){
        this.order = order;
        this.workTypeList = new ArrayList<>();
    }

    public WorkList(Order order, ArrayList<WorkTypeWithEmployees> workTypeList){
        this.order = order;
        this.workTypeList = workTypeList;
    }

    public WorkList(Order order, JSONArray jsonArray){
        this.order = order;
        this.workTypeList = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); ++i){
            JSONObject jWorkType = jsonArray.getJSONObject(i);

            //WorkTypeWithEmployees newWorkTypeWithEmployee = new

            int workTypeID = jWorkType.getInt(Const.WORK_TYPE_ID);
            String workTypeName = jWorkType.getString(Const.WORK_TYPE_NAME);

            WorkType workType = new WorkType(workTypeID, workTypeName);
            ArrayList<WorkTypeWithEmployees.EmployeeWork> newEmployeeList =
                    new ArrayList<>();

            WorkTypeWithEmployees newWorkTypeWithEmployees =
                    new WorkTypeWithEmployees(workType);


            JSONArray employeeArray = jWorkType.getJSONArray(EMPLOYEES);
            for(int j = 0; j < employeeArray.length(); ++j){
                JSONObject jEmployee = employeeArray.getJSONObject(j);

                int employeeID = jEmployee.getInt(Const.EMPLOYEE_ID);
                String lastname = jEmployee.getString(Const.EMPLOYEE_LASTNAME);
                String firstname = jEmployee.getString(Const.EMPLOYEE_FIRSTNAME);
                String middlename = jEmployee.getString(Const.EMPLOYEE_MIDDLENAME);

                Employee employee = new Employee(lastname,firstname,middlename,employeeID);
                ArrayList<double[]> workDataList = new ArrayList<>();

                WorkTypeWithEmployees.EmployeeWork newEmployeeWithWork =
                        newWorkTypeWithEmployees. new EmployeeWork(employee);

                JSONArray workArray = jEmployee.getJSONArray(WORK);
                for(int k = 0; k < workArray.length(); ++k){
                    JSONObject jWork = workArray.getJSONObject(k);
                    double[] workData = new double[4];

                    int numDay = jWork.getInt(Const.WORK_TIME_NUM_DAY);
                    int work_time = jWork.getInt(Const.WORK_TIME_WORK_TIME);
                    int over_time = jWork.getInt(Const.WORK_TIME_OVER_TIME);
                    int approve = jWork.getInt(Const.WORK_TIME_APPROVAL);

                    workData[NUM_DAY] = numDay;
                    workData[WORK_TIME] = work_time;
                    workData[OVER_WORK] = over_time;
                    workData[APPROV] = approve;

                    workDataList.add(workData);
                }
                newEmployeeWithWork.setWork(workDataList);

                newEmployeeList.add(newEmployeeWithWork);
            }
            newWorkTypeWithEmployees.setEmployeeList(newEmployeeList);

            this.workTypeList.add(newWorkTypeWithEmployees);
        }

    }

    //----------------------------------------------------------------------
    // GETTER
    public Order getOrder() {
        return order;
    }

    public ArrayList<WorkTypeWithEmployees> getWorkTypeList() {
        return workTypeList;
    }

    //----------------------------------------------------------------------
    // SETTER
    public void setOrder(Order order) {
        this.order = order;
    }

    public void setWorkTypeList(ArrayList<WorkTypeWithEmployees> workTypeList) {
        this.workTypeList = workTypeList;
    }

    //----------------------------------------------------------------------
    // new class for work type with employees
    public class WorkTypeWithEmployees{
        private WorkType workType;
        private ArrayList<EmployeeWork> employeeList;

        //----------------------------------------------------------------------
        // CONSTRUCTOR
        public WorkTypeWithEmployees(WorkType workType){
            this.workType = workType;
            this.employeeList = new ArrayList<>();
        }

        public WorkTypeWithEmployees(WorkType workType, ArrayList<EmployeeWork> employeeList){
            this.workType = workType;
            this.employeeList = employeeList;
        }

        //----------------------------------------------------------------------
        // GETTER
        public WorkType getWorkType() {
            return workType;
        }

        public ArrayList<EmployeeWork> getEmployeeList() {
            return employeeList;
        }

        //----------------------------------------------------------------------
        // SETTER
        public void setWorkType(WorkType workType) {
            this.workType = workType;
        }

        public void setEmployeeList(ArrayList<EmployeeWork> employeeList) {
            this.employeeList = employeeList;
        }

        //----------------------------------------------------------------------
        // new class for employee with work info
        public class EmployeeWork{
            private Employee employee;
            private ArrayList<double[]> work;

            //----------------------------------------------------------------------
            // CONSTRUCTOR
            public EmployeeWork(Employee employee){
                this.employee = employee;
                this.work = new ArrayList<>();
            }

            public EmployeeWork(Employee employee, ArrayList<double[]> work){
                this.employee = employee;
                this.work = work;
            }

            //----------------------------------------------------------------------
            // GETTER
            public Employee getEmployee() {
                return employee;
            }

            public ArrayList<double[]> getWork() {
                return work;
            }

            //----------------------------------------------------------------------
            // SETTER
            public void setEmployee(Employee employee) {
                this.employee = employee;
            }


            public void setWork(ArrayList<double[]> work) {
                this.work = work;
            }
        }

    }

}
