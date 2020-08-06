package jdbcpackage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcUtils {
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/student_achievement_system";
    private static String user = "root";
    private static String password = "admin";

    /**
     * 链接数据库
     */
    static {
        try {
            Class.forName(JdbcUtils.driverName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取链接对象connection
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(JdbcUtils.url, JdbcUtils.user, JdbcUtils.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭资源
     *
     * @param conn
     * @param st
     * @param rs
     */
    public static void close(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

