package hello.jdbc.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    /**
     * DriverManager는 DataSource 인터페이스 사용 X -> 인터페이스 사용할려면 관련 코드 다 뜯어 고쳐야함
     */
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //설정 & 사용
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection= {}, class={}", con1, con1.getClass());
        log.info("connection= {}, class={}", con2, con2.getClass());
    }

    /**
     * 스프링이 제공하는 DriverManagerDataSource는 DataSource 인터페이스 사용 O, 설정과 사용 분리
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD); //설정, DataSource-부모, DriverManagerDataSource-자식
        useDataSource(dataSource);
    }

    /**
     * DataSource: HiKari 사용
     */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링
        HikariConfig hikariConfig = new HikariConfig();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

//        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        useDataSource(dataSource);
        Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 시간 대기
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //사용
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection= {}, class={}", con1, con1.getClass());
        log.info("connection= {}, class={}", con2, con2.getClass());
    }


}
