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
                ArrayList<int[]> workDataList = new ArrayList<>();

                WorkTypeWithEmployees.EmployeeWork newEmployeeWithWork =
                        newWorkTypeWithEmployees. new EmployeeWork(employee);

                JSONArray workArray = jEmployee.getJSONArray(WORK);
                for(int k = 0; k < workArray.length(); ++k){
                    JSONObject jWork = workArray.getJSONObject(k);
                    int[] workData = new int[4];

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
        private ArrayList<int[]> hoursTypeList;
        private int sumWorkType;
        private int sumOverWorkType;
        private boolean isSumApprove;


        //----------------------------------------------------------------------
        // CONSTRUCTOR
        public WorkTypeWithEmployees(WorkType workType){
            this.workType = workType;
        }

        //----------------------------------------------------------------------
        // GETTER
        public WorkType getWorkType() {
            return workType;
        }

        public ArrayList<EmployeeWork> getEmployeeList() {
            return employeeList;
        }

        public ArrayList<int[]> getHoursTypeList() {
            return hoursTypeList;
        }

        public int getSumWorkType() { return sumWorkType; }

        public int getSumOverWork() { return sumOverWorkType; }

        public boolean getIsSumApprove() { return isSumApprove; }

        //----------------------------------------------------------------------
        // SETTER
        public void setWorkType(WorkType workType) {
            this.workType = workType;
        }

        public void setEmployeeList(ArrayList<EmployeeWork> employeeList) {
            this.employeeList = employeeList;

            ArrayList<int[]> sumList = new ArrayList<>();

            sumWorkType = 0;
            sumOverWorkType = 0;
            isSumApprove = true;

            // sum hours over month
            for(EmployeeWork e: employeeList){
                sumWorkType += e.getSumWorkEmployee();
                sumOverWorkType += e.getSumOverWorkEmployee();
                if(isSumApprove && !e.isSumAppove()) isSumApprove = false;
            }


            // iterate through month to calculate sum hours on day for work type
            for(int i = 1; i <= 31; i++){
                int sumWork = 0;
                int sumOverWork = 0;
                int approve = 1;
                for(EmployeeWork e: employeeList){
                    ArrayList<int[]> employeeWork = e.getWork();
                    for(int[] w: employeeWork){
                        if(i == w[NUM_DAY]){
                            sumWork += w[WORK_TIME];
                            sumOverWork += w[OVER_WORK];
                            if(approve == 1 && w[APPROV] == 0) approve = 0;
                            break;
                        }
                    }
                }

                if(sumWork > 0 || sumOverWork > 0){
                    int[] sumHoursOnDay = new int[4];
                    sumHoursOnDay[NUM_DAY] = i;
                    sumHoursOnDay[WORK_TIME] = sumWork;
                    sumHoursOnDay[OVER_WORK] = sumOverWork;
                    sumHoursOnDay[APPROV] = approve;
                    sumList.add(sumHoursOnDay);
                }

            }

            this.hoursTypeList = sumList;

        }

        //----------------------------------------------------------------------
        // new class for employee with work info
        public class EmployeeWork{
            private Employee employee;
            private ArrayList<int[]> work;
            private int sumWorkEmployee;
            private int sumOverWorkEmployee;
            private boolean isSumApprove;

            //----------------------------------------------------------------------
            // CONSTRUCTOR
            public EmployeeWork(Employee employee){
                this.employee = employee;
            }

            //----------------------------------------------------------------------
            // GETTER
            public Employee getEmployee() {
                return employee;
            }

            public ArrayList<int[]> getWork() {
                return work;
            }

            public int getSumWorkEmployee() { return sumWorkEmployee; }

            public int getSumOverWorkEmployee() { return sumOverWorkEmployee; }

            public boolean isSumAppove() { return isSumApprove; }

            //----------------------------------------------------------------------
            // SETTER
            public void setEmployee(Employee employee) {
                this.employee = employee;
            }


            public void setWork(ArrayList<int[]> work) {
                this.work = work;

                sumWorkEmployee = 0;
                sumOverWorkEmployee = 0;
                isSumApprove = true;

                for(int[] h_array: work){
                    sumWorkEmployee += h_array[WORK_TIME];
                    sumOverWorkEmployee += h_array[OVER_WORK];
                    if(isSumApprove && h_array[APPROV] == 0) isSumApprove = false;
                }

            }
        }

    }

}
