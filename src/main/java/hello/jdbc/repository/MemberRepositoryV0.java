package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 * SQL mapper 사용 X
 */
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null; //커넥션 얻기
        PreparedStatement pstmt = null; //DB로 전달 할 SQL문 & 파라미터로 전달할 데이터 준비

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); //values(?, )
            pstmt.setInt(2, member.getMoney()); //values( ,?)
            pstmt.executeUpdate(); //데이터 변경, 반환 값: int - 영향받은 DB row 수
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null; // DB 내부에 커서를 이동해 데이터 조회 가능!

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); //데이터 조회

            if (rs.next()) { //최초 1번은 실행해야 데이터가 있는 곳 가리킴!
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money); //value s(?, )
            pstmt.setString(2, memberId); //values( ,?)
            int resultSize = pstmt.executeUpdate(); //쿼리를 실행하고 영향받은 row 수
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 리소스 정리: 항상 역순
     * connection 얻고 PreparedStatement 만들었기 때문에 역순으로 정리! (Statement - 부모, PreparedStatement - 자식)
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        /**
         * 핵심은 stmt와 con이 독립적인 관계여야함!
         * stmt의 문제 때문에 con이 close 안되면 안됨!
         */
        if (rs != null) {
            try {
                rs.close(); //Exception 터지면? catch에서 잡아주므로 stmt, con에 영향 X
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close(); //Exception 터져도 catch로 잡기 때문에 con 닫을 수 있음
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
