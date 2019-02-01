package model;

import http.Const;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FinalWorkReport {

    public static final int WORK_TIME = 0;
    public static final int OVER_WORK = 1;
    public static final int NUM_DAY = 2;

    private List<EmployeeWithWorkType> employeeList;

    private static final String WORK_TYPE = "work_type";
    private static final String WORK_DATA = "work_data";

    private static final String SUM_EMPLOYEE_WORK = "employee_work_hours";
    private static final String SUM_EMPLOYEE_OVER_WORK = "employee_over_work_hours";

    private static final String SUM_WORKTYPE_WORK = "work_type_hours";
    private static final String SUM_WORKTYPE_OVER_WORK = "work_type_over_hours";


    //----------------------------------------------------------------------
    // CONSTRUCTOR

    public FinalWorkReport(JSONArray jsonArray){
        this.employeeList = new ArrayList<>();

        // iterate by Employee in JSON
        for(int i = 0; i < jsonArray.length(); ++i){
            JSONObject jEmployee = jsonArray.getJSONObject(i);

            // get employee from json
            int employeeID = jEmployee.getInt(Const.EMPLOYEE_ID);
            String lastname = jEmployee.getString(Const.EMPLOYEE_LASTNAME);
            String firstname = jEmployee.getString(Const.EMPLOYEE_FIRSTNAME);
            String middlename = jEmployee.getString(Const.EMPLOYEE_MIDDLENAME);

            Employee employee = new Employee(lastname, firstname, middlename, employeeID);

            // get sum hours from json
            int sumAllWork = jEmployee.getInt(SUM_EMPLOYEE_WORK);
            int sumAllOverWork = jEmployee.getInt(SUM_EMPLOYEE_OVER_WORK);
            int[] sumArrayEmployee = {sumAllWork, sumAllOverWork};

            List<EmployeeWithWorkType.WorkTypeData> newWorkTypeList = new ArrayList<>();

            EmployeeWithWorkType newEmployeeWithWorkType = new EmployeeWithWorkType(employee);
            newEmployeeWithWorkType.setSumWorkEmployee(sumArrayEmployee);

            //iterate by Work Type
            JSONArray workTypeArray = jEmployee.getJSONArray(WORK_TYPE);
            for(int j = 0; j < workTypeArray.length(); ++j){
                JSONObject jWorkType = workTypeArray.getJSONObject(j);

                // get work type from json
                int workTypeID = jWorkType.getInt(Const.WORK_TYPE_ID);
                String workTypeName = jWorkType.getString(Const.WORK_TYPE_NAME);

                WorkType workType = new WorkType(workTypeID, workTypeName);

                // get sum work type hours
                int sumTypeWork = jWorkType.getInt(SUM_WORKTYPE_WORK);
                int sumTypeOverWork = jWorkType.getInt(SUM_WORKTYPE_OVER_WORK);
                int[] sumArrayWorkType = {sumTypeWork, sumTypeOverWork};

                EmployeeWithWorkType.WorkTypeData newWorkTypeData =
                        newEmployeeWithWorkType.new WorkTypeData(workType);

                newWorkTypeData.setSumWorkType(sumArrayWorkType);

                List<int[]> workDataList = new ArrayList<>();

                //iterate by work data
                JSONArray workArray = jWorkType.getJSONArray(WORK_DATA);
                for(int k = 0; k < workArray.length(); ++k){
                    JSONObject jData = workArray.getJSONObject(k);
                    int[] workData = new int[4];

                    int numDay = jData.getInt(Const.WORK_TIME_NUM_DAY);
                    int work_hours = jData.getInt(Const.WORK_TIME_WORK_TIME);
                    int over_work_hours = jData.getInt(Const.WORK_TIME_OVER_TIME);

                    workData[NUM_DAY] = numDay;
                    workData[WORK_TIME] = work_hours;
                    workData[OVER_WORK] = over_work_hours;
                    workDataList.add(workData);

                }
                newWorkTypeData.setData(workDataList);

                newWorkTypeList.add(newWorkTypeData);
            }
            newEmployeeWithWorkType.setWorkTypeDataList(newWorkTypeList);

            this.employeeList.add(newEmployeeWithWorkType);
        }

    }

    //----------------------------------------------------------------------
    // GETTER

    public List<EmployeeWithWorkType> getEmployeeList() {
        return employeeList;
    }


    //----------------------------------------------------------------------
    // SETTER

    public void setEmployeeList(ArrayList<EmployeeWithWorkType> employeeList) {
        this.employeeList = employeeList;
    }

    //----------------------------------------------------------------------
    // new class for work type with employees
    public class EmployeeWithWorkType{
        private Employee employee;
        private List<WorkTypeData> workTypeDataList;

        private List<int[]> sumWorkOnDayList;

        private int[] sumWorkEmployee;


        //----------------------------------------------------------------------
        // CONSTRUCTOR
        public EmployeeWithWorkType(Employee employee){
            this.employee = employee;
        }

        //----------------------------------------------------------------------
        // GETTER
        public Employee getEmployee() {
            return employee;
        }

        public List<WorkTypeData> getWorkTypeDataList() {
            return workTypeDataList;
        }

        public List<int[]> getSumWorkOnDayList() {
            return sumWorkOnDayList;
        }

        public int[] getSumWorkEmployee() {
            return sumWorkEmployee;
        }

        //----------------------------------------------------------------------
        // SETTER
        public void setEmployee(Employee employee) {
            this.employee = employee;
        }

        public void setWorkTypeDataList(List<WorkTypeData> workTypeDataList) {
            this.workTypeDataList = workTypeDataList;


            ArrayList<int[]> sumList = new ArrayList<>();

            // iterate through month to calculate sum hours on day for work type
            for(int i = 1; i <= 31; i++){
                int sumWork = 0;
                int sumOverWork = 0;

                for(WorkTypeData e: workTypeDataList){
                    List<int[]> data = e.getData();
                    for(int[] w: data){
                        if(i == w[NUM_DAY]){
                            sumWork += w[WORK_TIME];
                            sumOverWork += w[OVER_WORK];
                            break;
                        }
                    }
                }

                if(sumWork > 0 || sumOverWork > 0){
                    int[] sumHoursOnDay = new int[3];
                    sumHoursOnDay[NUM_DAY] = i;
                    sumHoursOnDay[WORK_TIME] = sumWork;
                    sumHoursOnDay[OVER_WORK] = sumOverWork;
                    sumList.add(sumHoursOnDay);
                }

            }

            this.sumWorkOnDayList = sumList;

        }

        public void setSumWorkEmployee(int[] sumWorkEmployee) {
            this.sumWorkEmployee = sumWorkEmployee;
        }

        public void setSumWorkOnDayList(List<int[]> sumWorkOnDayList) {
            this.sumWorkOnDayList = sumWorkOnDayList;
        }

        //----------------------------------------------------------------------
        // new class for money type with info
        public class WorkTypeData{
            private WorkType workType;
            private List<int[]> data;
            private int[] sumWorkType;

            //----------------------------------------------------------------------
            // CONSTRUCTOR
            public WorkTypeData(WorkType moneyType){
                this.workType = moneyType;
            }

            //----------------------------------------------------------------------
            // GETTER
            public WorkType getWorkType() {
                return workType;
            }

            public List<int[]> getData() {
                return data;
            }

            public int[] getSumWorkType() { return sumWorkType; }

            //----------------------------------------------------------------------
            // SETTER
            public void setMoneyType(WorkType moneyType) {
                this.workType = moneyType;
            }

            public void setSumWorkType(int[] sumWorkType) {
                this.sumWorkType = sumWorkType;
            }

            public void setData(List<int[]> data) {
                this.data = data;
            }
        }

    }

}
