package item1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 생성자 대신 정적 팩터리 메서드를 고려하라
// 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
public class Code5 {

    public static void connection() {
        String driverName = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "root";

        try {
            Class.forName(driverName);

            // connection 은 interface 이지만 상황에 맞게 정적 팩터리 메서드로 정의할 수 있다.
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }
}
