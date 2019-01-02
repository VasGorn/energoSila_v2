package http;

import controllers.Massage;

import java.io.*;
import java.net.*;

public final class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();


    public static String postLogin(String username, String password){
        String response = null;
        String reqUrl = Const.URL_LOGIN;
        try {
            String param = "username=" + URLEncoder.encode(username,"UTF-8") +
                    "&password=" + URLEncoder.encode(password,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getAllEmployees(){
        String response = null;
        String reqUrl = Const.URL_ALL_EMPLOYEES;
        try {
            String param = "admin=" + URLEncoder.encode("1","UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String insertEmployee(String lastname, String firstname, String midlename){
        String response = null;
        String reqUrl = Const.URL_INSERT_EMPLOYEE;
        try {
            String param = "lastname=" + URLEncoder.encode(lastname,"UTF-8") +
                    "&firstname=" + URLEncoder.encode(firstname,"UTF-8") +
                    "&midlename=" + URLEncoder.encode(midlename,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String deleteEmployee(String employeeID){
        String response = null;
        String reqUrl = Const.URL_DELETE_EMPLOYEE;
        try {
            String param = "employeeID=" + URLEncoder.encode(employeeID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String updateEmployee(String lastname, String firstname, String midlename, String employeeID){
        String response = null;
        String reqUrl = Const.URL_UPDATE_EMPLOYEE;
        try {
            String param = "lastname=" + URLEncoder.encode(lastname,"UTF-8") +
                    "&firstname=" + URLEncoder.encode(firstname,"UTF-8") +
                    "&midlename=" + URLEncoder.encode(midlename,"UTF-8")+
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getEmployeesWithPosition(String positionID){
        String response = null;
        String reqUrl = Const.URL_EMPLOYEES_WITH_POSITION;
        try {
            String param = "positionID=" + URLEncoder.encode(positionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getWorkersOnMaster(String masterID){
        String response = null;
        String reqUrl = Const.URL_WORKERS_ON_MASTERS;
        try {
            String param = "masterID=" + URLEncoder.encode(masterID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }


    public static String insertWorkerToMaster(String masterID, String workerID){
        String response = null;
        String reqUrl = Const.URL_INSERT_WORKER_TO_MASTER;
        try {
            String param = "masterID=" + URLEncoder.encode(masterID,"UTF-8") +
                    "&workerID=" + URLEncoder.encode(workerID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String deleteWorkerFromMaster(String masterID, String workerID){
        String response = null;
        String reqUrl = Const.URL_DELETE_WORKER_FROM_MASTER;
        try {
            String param = "masterID=" + URLEncoder.encode(masterID,"UTF-8") +
                    "&workerID=" + URLEncoder.encode(workerID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getUsers(){
        String response = null;
        String reqUrl = Const.URL_GET_USERS;
        try {
            String param = "admin=" + URLEncoder.encode("1","UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String insertUser(String username, String employeeID, String password){
        String response = null;
        String reqUrl = Const.URL_INSERT_USER;
        try {
            String param = "username=" + URLEncoder.encode(username,"UTF-8") +
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8") +
                    "&password=" + URLEncoder.encode(password,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String insertPosition(String employeeID, String positionID){
        String response = null;
        String reqUrl = Const.URL_INSERT_POSITION;
        try {
            String param = "employeeID=" + URLEncoder.encode(employeeID,"UTF-8") +
                    "&positionID=" + URLEncoder.encode(positionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String deleteUser(String userID){
        String response = null;
        String reqUrl = Const.URL_DELETE_USER;
        try {
            String param = "userID=" + URLEncoder.encode(userID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String deletePositionFromEmployee(String employeeID, String positionID){
        String response = null;
        String reqUrl = Const.URL_DELETE_POSITION_FROM_EMPLOYEE;
        try {
            String param = "employeeID=" + URLEncoder.encode(employeeID,"UTF-8") +
                    "&positionID=" + URLEncoder.encode(positionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String updateUser(String userID, String userName, String password){
        String response = null;
        String reqUrl = Const.URL_UPDATE_USER;
        try {
            String param = "userID=" + URLEncoder.encode(userID,"UTF-8") +
                    "&userName=" + URLEncoder.encode(userName,"UTF-8")+
                    "&userPassword=" + URLEncoder.encode(password,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String updateEmployeeWithPosition(String employeeID, String oldPositionID, String newPositionID){
        String response = null;
        String reqUrl = Const.URL_UPDATE_EMPLOYEE_WITH_POSITION;
        try {
            String param = "employeeID=" + URLEncoder.encode(employeeID,"UTF-8") +
                    "&oldPositionID=" + URLEncoder.encode(oldPositionID,"UTF-8")+
                    "&newPositionID=" + URLEncoder.encode(newPositionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getManagerOrders(String managerID){
        String response = null;
        String reqUrl = Const.URL_GET_ORDERS_FOR_MANAGER;
        try {
            String param = "managerID=" + URLEncoder.encode(managerID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String insertOrder(String nameOrder, String managerID, String adress, String description,
                                     String maxHours){
        String response = null;
        String reqUrl = Const.URL_INSERT_ORDER;
        try {
            String param = "nameOrder=" + URLEncoder.encode(nameOrder,"UTF-8") +
                    "&managerID=" + URLEncoder.encode(managerID,"UTF-8")+
                    "&adress=" + URLEncoder.encode(adress,"UTF-8")+
                    "&description=" + URLEncoder.encode(description,"UTF-8")+
                    "&maxHours=" + URLEncoder.encode(maxHours,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String deleteOrder(String orderID){
        String response = null;
        String reqUrl = Const.URL_DELETE_ORDER;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String updateOrder(String orderID, String nameOrder, String adress, String description,
                                     String maxHours){
        String response = null;
        String reqUrl = Const.URL_UPDATE_ORDER;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&nameOrder=" + URLEncoder.encode(nameOrder,"UTF-8")+
                    "&adress=" + URLEncoder.encode(adress,"UTF-8")+
                    "&description=" + URLEncoder.encode(description,"UTF-8")+
                    "&maxHours=" + URLEncoder.encode(maxHours,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getEmployee(String id){
        String response = null;
        String reqUrl = Const.URL_GET_EMPLOYEE;
        try {
            String param = "id=" + URLEncoder.encode(id,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getWorkTypeByPosition(String positionID){
        String response = null;
        String reqUrl = Const.URL_GET_WORK_TYPE_BY_POSITION;
        try {
            String param = "positionID=" + URLEncoder.encode(positionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getWorkTypeWithHours(String orderID, String employeeID, String numMonth){
        String response = null;
        String reqUrl = Const.URL_GET_WORK_TYPE_WITH_HOURS;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8")+
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String insertWorkTypeWithHours(String orderID, String employeeID,
                                                 String numMonth, String workTypeID,
                                                 String sumHours, String hours){
        String response = null;
        String reqUrl = Const.URL_INSERT_WORK_TYPE_WITH_HOURS;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8")+
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8")+
                    "&workTypeID=" + URLEncoder.encode(workTypeID,"UTF-8")+
                    "&sumHours=" + URLEncoder.encode(sumHours,"UTF-8")+
                    "&hours=" + URLEncoder.encode(hours,"UTF-8");

            response = postRequest(reqUrl, param);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String updateWorkTypeWithHours(String orderID, String employeeID,
                                                 String numMonth, String workTypeID,
                                                 String sumHours, String hours){
        String response = null;
        String reqUrl = Const.URL_UPDATE_WORK_TYPE_WITH_HOURS;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8")+
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8")+
                    "&workTypeID=" + URLEncoder.encode(workTypeID,"UTF-8")+
                    "&sumHours=" + URLEncoder.encode(sumHours,"UTF-8")+
                    "&hours=" + URLEncoder.encode(hours,"UTF-8");

            response = postRequest(reqUrl, param);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String deleteWorkTypeWithHours(String orderID, String employeeID,
                                                 String numMonth, String workTypeID,
                                                 String sumHours){
        String response = null;
        String reqUrl = Const.URL_DELETE_WORK_TYPE_WITH_HOURS;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&employeeID=" + URLEncoder.encode(employeeID,"UTF-8")+
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8")+
                    "&workTypeID=" + URLEncoder.encode(workTypeID,"UTF-8")+
                    "&sumHours=" + URLEncoder.encode(sumHours,"UTF-8");

            response = postRequest(reqUrl, param);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String insertWorkType(String workTypeName, String positionID){
        String response = null;
        String reqUrl = Const.URL_INSERT_WORKTYPE;
        try {
            String param = "workTypeName=" + URLEncoder.encode(workTypeName,"UTF-8") +
                    "&positionID=" + URLEncoder.encode(positionID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String updateWorkType(String workTypeID, String nameWorkType){
        String response = null;
        String reqUrl = Const.URL_UPDATE_WORKTYPE;
        try {
            String param = "workTypeID=" + URLEncoder.encode(workTypeID,"UTF-8") +
                    "&nameWorkType=" + URLEncoder.encode(nameWorkType,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String deleteWorkType(String workTypeID){
        String response = null;
        String reqUrl = Const.URL_DELETE_WORKTYPE;
        try {
            String param = "workTypeID=" + URLEncoder.encode(workTypeID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getActiveOrdersForManager(String managerID, String numMonth){
        String response = null;
        String reqUrl = Const.URL_GET_ACTIVE_ORDERS_FOR_MANAGER;
        try {
            String param = "managerID=" + URLEncoder.encode(managerID,"UTF-8") +
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getDataToReportWork(String orderID, String numMonth){
        String response = null;
        String reqUrl = Const.URL_GET_DATA_TO_REPORT_WORK;
        try {
            String param = "orderID=" + URLEncoder.encode(orderID,"UTF-8") +
                    "&numMonth=" + URLEncoder.encode(numMonth,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getAllMoneyType(){
        String response = null;
        String reqUrl = Const.URL_ALL_MONEY_TYPE;
        try {
            String param = "admin=" + URLEncoder.encode("1","UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String insertMoneyType(String nameMoneyType){
        String response = null;
        String reqUrl = Const.URL_INSERT_MONEY_TYPE;
        try {
            String param = "nameMoneyType=" + URLEncoder.encode(nameMoneyType,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String deleteMoneyType(String moneyTypeID){
        String response = null;
        String reqUrl = Const.URL_DELETE_MONEY_TYPE;
        try {
            String param = "moneyTypeID=" + URLEncoder.encode(moneyTypeID,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String updateMoneyType(String moneyTypeID, String nameMoneyType){
        String response = null;
        String reqUrl = Const.URL_UPDATE_MONEY_TYPE;
        try {
            String param = "moneyTypeID=" + URLEncoder.encode(moneyTypeID,"UTF-8") +
                    "&nameMoneyType=" + URLEncoder.encode(nameMoneyType,"UTF-8");

            response = postRequest(reqUrl, param);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    //---------------------------------------------------------------------------------------
    private static String postRequest(String reqUrl, String param){
        String response = null;
        try {
            URL url = new URL(reqUrl);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Massage.show("Ошибка сети", e.toString());
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
