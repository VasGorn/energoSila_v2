package model;

import http.Const;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportMoney {
    public static final int NUM_DAY = 0;
    public static final int SUM_MONEY = 1;
    public static final int APPROV = 2;

    private Order order;
    private List<EmployeeWithMoneyType> employeeList;
    private int sumMoneyForAllEmployee;
    private int sumMoneyAllSpent;

    private static final String MONEY_TYPE = "money_type";
    private static final String MONEY_DATA = "money_data";


    //----------------------------------------------------------------------
    // CONSTRUCTOR
    public ReportMoney(Order order){
        this.order = order;
        this.employeeList = new ArrayList<>();
    }

    public ReportMoney(Order order, ArrayList<EmployeeWithMoneyType> employeeList){
        this.order = order;
        this.employeeList = employeeList;
    }

    public ReportMoney(Order order, JSONArray jsonArray){
        this.order = order;
        this.employeeList = new ArrayList<>();
        this.sumMoneyForAllEmployee = 0;
        this.sumMoneyAllSpent = 0;
        // iterate by Employee in JSON
        for(int i = 0; i < jsonArray.length(); ++i){
            JSONObject jEmployee = jsonArray.getJSONObject(i);
            int sumEmployee = jEmployee.getInt(Const.MONEY_QOUT_SUM);

            sumMoneyForAllEmployee += sumEmployee;

            int employeeID = jEmployee.getInt(Const.EMPLOYEE_ID);
            String lastname = jEmployee.getString(Const.EMPLOYEE_LASTNAME);
            String firstname = jEmployee.getString(Const.EMPLOYEE_FIRSTNAME);
            String middlename = jEmployee.getString(Const.EMPLOYEE_MIDDLENAME);

            Employee employee = new Employee(lastname, firstname, middlename, employeeID);

            List<EmployeeWithMoneyType.MoneyTypeData> newMoneyTypeList = new ArrayList<>();

            EmployeeWithMoneyType newEmployeeWithMoneyType = new EmployeeWithMoneyType(employee);
            newEmployeeWithMoneyType.setSumOnEmployeeByOrder(sumEmployee);

            //iterate by Money Type
            JSONArray moneyTypeArray = jEmployee.getJSONArray(MONEY_TYPE);
            for(int j = 0; j < moneyTypeArray.length(); ++j){
                JSONObject jMoneyType = moneyTypeArray.getJSONObject(j);

                int moneyTypeID = jMoneyType.getInt(Const.MONEY_TYPE_ID);
                String moneyTypeName = jMoneyType.getString(Const.MONEY_TYPE_NAME);

                WorkType moneyType = new WorkType(moneyTypeID, moneyTypeName);

                List<int[]> moneyDataList = new ArrayList<>();

                EmployeeWithMoneyType.MoneyTypeData newMoneyTypeData =
                        newEmployeeWithMoneyType.new MoneyTypeData(moneyType);

                //iterate by money data
                JSONArray moneyArray = jMoneyType.getJSONArray(MONEY_DATA);
                for(int k = 0; k < moneyArray.length(); ++k){
                    JSONObject jData = moneyArray.getJSONObject(k);
                    int[] moneyData = new int[3];

                    int numDay = jData.getInt(Const.MONEY_SPENT_NUM_DAY);
                    int sum_money = jData.getInt(Const.MONEY_SPENT_NUM);
                    int approve = jData.getInt(Const.MONEY_SPENT_APPROVE);

                    boolean flag = true;
                    //iterate records
                    for(int a = 0; a < moneyDataList.size(); ++a){
                        int[] array = moneyDataList.get(a);

                        //if record already exist in the same day
                        if(array[NUM_DAY] == numDay){
                            array[SUM_MONEY] += sum_money;
                            if(approve != 1 || array[APPROV] != 1) array[APPROV] = 0;
                            flag = false;
                        }
                    }

                    //if record new
                    if(flag) {
                        moneyData[NUM_DAY] = numDay;
                        moneyData[SUM_MONEY] = sum_money;
                        moneyData[APPROV] = approve;
                        moneyDataList.add(moneyData);
                    }

                }
                newMoneyTypeData.setData(moneyDataList);

                newMoneyTypeList.add(newMoneyTypeData);
            }
            newEmployeeWithMoneyType.setMoneyTypeDataList(newMoneyTypeList);

            sumMoneyAllSpent += newEmployeeWithMoneyType.getSumRecordEmployee();

            this.employeeList.add(newEmployeeWithMoneyType);
        }

    }

    //----------------------------------------------------------------------
    // GETTER
    public Order getOrder() {
        return order;
    }

    public List<EmployeeWithMoneyType> getEmployeeList() {
        return employeeList;
    }

    public int getSumMoneyForAllEmployee() {
        return sumMoneyForAllEmployee;
    }

    public int getSumMoneyAllSpent(){
        return sumMoneyAllSpent;
    }

    //----------------------------------------------------------------------
    // SETTER
    public void setOrder(Order order) {
        this.order = order;
    }

    public void setEmployeeList(ArrayList<EmployeeWithMoneyType> employeeList) {
        this.employeeList = employeeList;
    }

    //----------------------------------------------------------------------
    // new class for work type with employees
    public class EmployeeWithMoneyType{
        private Employee employee;
        private List<MoneyTypeData> moneyTypeDataList;
        private int sumOnEmployeeByOrder;

        private List<int[]> sumMoneyOnDayList;

        private int sumRecordEmployee;
        private boolean isSumApprove;


        //----------------------------------------------------------------------
        // CONSTRUCTOR
        public EmployeeWithMoneyType(Employee employee){
            this.employee = employee;
        }

        //----------------------------------------------------------------------
        // GETTER
        public Employee getEmployee() {
            return employee;
        }

        public List<MoneyTypeData> getMoneyTypeDataList() {
            return moneyTypeDataList;
        }

        public List<int[]> getSumMoneyOnDayList() {
            return sumMoneyOnDayList;
        }

        public int getSumRecordEmployee() {
            return sumRecordEmployee;
        }

        public int getSumOnEmployeeByOrder() {
            return sumOnEmployeeByOrder;
        }

        public boolean getIsSumApprove() { return isSumApprove; }

        //----------------------------------------------------------------------
        // SETTER
        public void setEmployee(Employee employee) {
            this.employee = employee;
        }

        public void setSumOnEmployeeByOrder(int sumEmployee){
            this.sumOnEmployeeByOrder = sumEmployee;
        }

        public void setMoneyTypeDataList(List<MoneyTypeData> moneyTypeDataList) {
            this.moneyTypeDataList = moneyTypeDataList;

            List<int[]> sumList = new ArrayList<>();

            sumRecordEmployee = 0;
            isSumApprove = true;

            // sum money over month
            for(MoneyTypeData m: moneyTypeDataList){
                sumRecordEmployee += m.getSumMoneyType();
                if(isSumApprove && !m.isSumAppove()) isSumApprove = false;
            }


            // iterate through month to calculate sum money on day for employee
            for(int i = 1; i <= 31; i++){
                int sumMoney = 0;
                int approve = 1;
                for(MoneyTypeData e: moneyTypeDataList){
                    List<int[]> moneyData = e.getData();
                    for(int[] d: moneyData){
                        if(i == d[NUM_DAY]){
                            sumMoney += d[SUM_MONEY];
                            if(approve == 1 && d[APPROV] == 0) approve = 0;
                            break;
                        }
                    }
                }

                if(sumMoney > 0){
                    int[] sumMoneyOnDay = new int[3];
                    sumMoneyOnDay[NUM_DAY] = i;
                    sumMoneyOnDay[SUM_MONEY] = sumMoney;
                    sumMoneyOnDay[APPROV] = approve;
                    sumList.add(sumMoneyOnDay);
                }

            }

            this.sumMoneyOnDayList = sumList;

        }

        //----------------------------------------------------------------------
        // new class for money type with info
        public class MoneyTypeData{
            private WorkType moneyType;
            private List<int[]> data;
            private int sumMoneyType;
            private boolean isSumApprove;

            //----------------------------------------------------------------------
            // CONSTRUCTOR
            public MoneyTypeData(WorkType moneyType){
                this.moneyType = moneyType;
            }

            //----------------------------------------------------------------------
            // GETTER
            public WorkType getMoneyType() {
                return moneyType;
            }

            public List<int[]> getData() {
                return data;
            }

            public int getSumMoneyType() { return sumMoneyType; }

            public boolean isSumAppove() { return isSumApprove; }

            //----------------------------------------------------------------------
            // SETTER
            public void setMoneyType(WorkType moneyType) {
                this.moneyType = moneyType;
            }


            public void setData(List<int[]> data) {
                this.data = data;

                // sum all money on type in month
                sumMoneyType = 0;
                isSumApprove = true;

                for(int[] h_array: data){
                    sumMoneyType += h_array[SUM_MONEY];
                    if(isSumApprove && h_array[APPROV] == 0) isSumApprove = false;
                }

            }
        }

    }

}
