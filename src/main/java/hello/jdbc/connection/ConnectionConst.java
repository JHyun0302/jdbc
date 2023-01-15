package hello.jdbc.connection;

public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
/**
 * DB에 접속하는데 필요한 기본 정보를 상수로 만듬!
 * 따로 객체 생성하면 안되므로 abstract class 선언
 */
