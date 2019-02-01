package http;

public class Const {
    private static final String URL_SERVER = "https://energosila52.000webhostapp.com";

    static final String URL_LOGIN = URL_SERVER + "/login1.php";
    static final String URL_GET_EMPLOYEE = URL_SERVER + "/get_employee_by_id.php";

    static final String URL_ALL_EMPLOYEES = URL_SERVER + "/admin/get_all_employee.php";
    static final String URL_INSERT_EMPLOYEE = URL_SERVER + "/admin/insert_employee.php";
    static final String URL_DELETE_EMPLOYEE = URL_SERVER + "/admin/delete_employee.php";
    static final String URL_UPDATE_EMPLOYEE = URL_SERVER + "/admin/update_employee.php";

    static final String URL_EMPLOYEES_WITH_POSITION = URL_SERVER + "/admin/get_employees_by_position.php";
    static final String URL_WORKERS_ON_MASTERS = URL_SERVER + "/master/get_workers_for_master.php";
    static final String URL_INSERT_WORKER_TO_MASTER = URL_SERVER + "/admin/insert_worker_to_master.php";
    static final String URL_DELETE_WORKER_FROM_MASTER = URL_SERVER + "/admin/delete_worker_from_master.php";

    static final String URL_GET_USERS = URL_SERVER + "/admin/get_users.php";
    static final String URL_INSERT_USER = URL_SERVER + "/admin/insert_user.php";
    static final String URL_INSERT_POSITION = URL_SERVER + "/admin/insert_position.php";
    static final String URL_DELETE_USER = URL_SERVER + "/admin/delete_user.php";
    static final String URL_DELETE_POSITION_FROM_EMPLOYEE = URL_SERVER + "/admin/delete_position_from_employee.php";
    static final String URL_UPDATE_USER = URL_SERVER + "/admin/update_user.php";
    static final String URL_UPDATE_EMPLOYEE_WITH_POSITION = URL_SERVER + "/admin/update_employee_with_position.php";

    static final String URL_GET_ORDERS_FOR_MANAGER = Const.URL_SERVER + "/admin/get_manager_orders.php";
    static final String URL_INSERT_ORDER = Const.URL_SERVER + "/admin/insert_order.php";
    static final String URL_DELETE_ORDER = Const.URL_SERVER + "/admin/delete_order.php";
    static final String URL_UPDATE_ORDER = Const.URL_SERVER + "/admin/update_order.php";
    static final String URL_GET_WORK_TYPE_BY_POSITION = Const.URL_SERVER + "/admin/get_work_type_by_position.php";
    static final String URL_GET_WORK_TYPE_WITH_HOURS = Const.URL_SERVER + "/admin/get_work_types_with_hours.php";
    static final String URL_INSERT_WORK_TYPE_WITH_HOURS = Const.URL_SERVER + "/admin/insert_work_type_with_hours.php";
    static final String URL_UPDATE_WORK_TYPE_WITH_HOURS = Const.URL_SERVER + "/admin/update_work_type_with_hours.php";
    static final String URL_DELETE_WORK_TYPE_WITH_HOURS = Const.URL_SERVER + "/admin/delete_work_type_with_hours.php";

    static final String URL_INSERT_WORKTYPE = Const.URL_SERVER + "/admin/insert_work_type.php";
    static final String URL_UPDATE_WORKTYPE = Const.URL_SERVER + "/admin/update_work_type.php";
    static final String URL_DELETE_WORKTYPE = Const.URL_SERVER + "/admin/delete_work_type.php";

    static final String URL_GET_ACTIVE_ORDERS_FOR_MANAGER = Const.URL_SERVER + "/admin/get_active_orders_for_manager.php";

    static final String URL_GET_DATA_TO_REPORT_WORK = Const.URL_SERVER + "/admin/get_work_to_report.php";
    static final String URL_GET_DATA_TO_FINALL_REPORT_WORK = Const.URL_SERVER + "/admin/get_final_report_work_time.php";
    static final String URL_GET_DATA_TO_REPORT_MONEY = Const.URL_SERVER + "/admin/get_money_to_report.php";


    static final String URL_ALL_MONEY_TYPE = URL_SERVER + "/admin/get_all_money_type.php";
    static final String URL_INSERT_MONEY_TYPE = URL_SERVER + "/admin/insert_money_type.php";
    static final String URL_DELETE_MONEY_TYPE = Const.URL_SERVER + "/admin/delete_money_type.php";
    static final String URL_UPDATE_MONEY_TYPE = Const.URL_SERVER + "/admin/update_money_type.php";

    static final String URL_GET_ORDERS_WITH_QOUTS = Const.URL_SERVER + "/manager/get_orders_with_qouts.php";
    static final String URL_GET_EMPLOYEE_ON_QOUT = Const.URL_SERVER + "/manager/get_employees_on_qout.php";
    static final String URL_GET_MONEY_QOUT = Const.URL_SERVER + "/manager/get_money_qout.php";
    static final String URL_INSERT_MONEY_QOUT = Const.URL_SERVER + "/manager/insert_money_sum.php";
    static final String URL_UPDATE_MONEY_QOUT = Const.URL_SERVER + "/manager/update_money_sum.php";
    static final String URL_DELETE_MONEY_QOUT = Const.URL_SERVER + "/manager/delete_money_sum.php";

    static final String URL_GET_MONEY_ORDERS_FOR_MANAGER = Const.URL_SERVER + "/manager/get_money_orders_for_manager.php";


    //------------------------------------------------------------------------------------
    // EMPLOYEE_TABLE
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String EMPLOYEE_LASTNAME = "lastname";
    public static final String EMPLOYEE_FIRSTNAME = "firstname";
    public static final String EMPLOYEE_MIDDLENAME = "middlename";

    //------------------------------------------------------------------------------------
    // USER_TABLE
    public static final String USER_TABLE = "users";
    public static final String USER_ID = "users_id";
    public static final String USER_EMPLOYEE_ID = "employee_employee_id";
    public static final String USER_NAME = "user_name";

    //------------------------------------------------------------------------------------
    // EMPLOYEE_HAS_POSITION_TABLE
    public static final String EMPLOYEE_HAS_POSITION_TABLE = "employee_has_position";
    public static final String EMPLOYEE_HAS_POSITION_EMPLOYEE_ID = "employee_employee_id";
    public static final String EMPLOYEE_HAS_POSITION_POSITION_ID = "position_position_id";

    //------------------------------------------------------------------------------------
    // POSITION_TABLE
    public static final String POSITION_TABLE = "position_table";
    public static final String POSITION_ID = "position_id";
    public static final String POSITION_NAME = "position_name";

    //------------------------------------------------------------------------------------
    // ORDER_TABLE
    public static final String ORDER_TABLE = "orders";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_NAME = "name_order";
    public static final String ORDER_MANAGER_ID = "employee_manager_id";
    public static final String ORDER_ADRESS = "adress";
    public static final String ORDER_DERSCRIPTION = "description";
    public static final String ORDER_MAX_HOURS = "max_hour";

    //------------------------------------------------------------------------------------
    // WORK_TYPE_TABLE
    public static final String WORK_TYPE_TABLE = "work_type";
    public static final String WORK_TYPE_ID = "type_id";
    public static final String WORK_TYPE_NAME = "type_name";
    public static final String WORK_TYPE_POSITION = "position_position_id";

    //------------------------------------------------------------------------------------
    // ORDER_HAS_WORKTYPE_TABLE
    public static final String ORDER_HAS_WORKTYPE_HOURS = "hours_on_type";

    //------------------------------------------------------------------------------------
    // WORK_TIME TABLE
    public static final String WORK_TIME_TABLE = "work_time";
    public static final String WORK_TIME_ID = "id";
    public static final String WORK_TIME_ORDER_ID = "order_order_id";
    public static final String WORK_TIME_EMPLOYEE_ID = "employee_employee_id";
    public static final String WORK_TIME_WORK_TYPE_ID = "work_type_type_id";
    public static final String WORK_TIME_NUM_MONTH = "num_month";
    public static final String WORK_TIME_NUM_DAY = "num_day";
    public static final String WORK_TIME_WORK_TIME = "work_time";
    public static final String WORK_TIME_OVER_TIME = "over_time";
    public static final String WORK_TIME_APPROVAL = "work_approval";

    //------------------------------------------------------------------------------------
    // MONEY_TYPE_TABLE
    public static final String MONEY_TYPE_TABLE = "money_type";
    public static final String MONEY_TYPE_ID = "id";
    public static final String MONEY_TYPE_NAME = "name_money";

    //------------------------------------------------------------------------------------
    // MONEY_QOUT_TABLE
    public static final String MONEY_QOUT_TABLE = "money_on_order";
    public static final String MONEY_QOUT_ID = "id";
    public static final String MONEY_QOUT_HOUR_PER_MONTH_ID = "hours_per_month_id";
    public static final String MONEY_QOUT_SUM = "sum_money";

    //------------------------------------------------------------------------------------
    // MONEY_SPENT_TABLE TABLE
    public static final String MONEY_SPENT_TABLE = "money_spent";
    public static final String MONEY_SPENT_ID = "id";
    public static final String MONEY_SPENT_ON_ORDER_ID = "money_on_order_id";
    public static final String MONEY_SPENT_TYPE_ID = "money_type_id";
    public static final String MONEY_SPENT_NUM_DAY = "num_day";
    public static final String MONEY_SPENT_NUM = "num_money";
    public static final String MONEY_SPENT_APPROVE = "approve";

    //------------------------------------------------------------------------------------

}
