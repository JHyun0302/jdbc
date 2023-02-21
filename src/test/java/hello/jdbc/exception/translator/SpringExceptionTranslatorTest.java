package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스프링이 제공하는 예외 변환기: 오류 코드를 스프링이 정의한 예외로 자동으로 변환해주는 변환기
 */
@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    /**
     * SQL 에러: 직접 번호를 매칭시켜줘야함!(현실성 없음)
     */
    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122); //단점: 직접 번호를 매칭시켜줘야함!(현실성 없음)
            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            log.info("error", e);
        }
    }

    /**
     * 스프링이 제공하는 SQL 예외 변환기 (일관된 예외 추상화 가능) - SQLErrorCodeSQLExceptionTranslator
     * 적절한 스프링 데이터 접근 계층의 예외로 변환해서 반환해줌
     */
    @Test
    void exceptionTranslator() {
        String sql = "select bad grammar"; //문법 오류!!
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);

            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("select", sql, e); //(작업명, sql문, error)
            //각각의 db에 맞게 SQL ErrorCode 만들어줌
            log.info("resultEx", resultEx);

            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }

    }
}
