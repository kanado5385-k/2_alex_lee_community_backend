package org.community.backend.repository;

import org.community.backend.member.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class JdbcMemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //RowMapper는 쿼리 결과(ResultSet)를 Java 객체(Member)로 변환하는 인터페이스
    //rowNum은 현재 처리 중인 행(Row)의 번호(여기서는 사용되지 않는다)
    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> new Member(
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("nickname")
    );

    // 쿼리 결과가 없을 수도 있기 때문 Optional<T>
    public Optional<Integer> findIdByEmail(String email) {
        String sql = "SELECT id FROM member WHERE email = ?";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, email); // Integer.class -> JDBC가 조회된 값을 Integer 타입으로 변환
            return Optional.ofNullable(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
