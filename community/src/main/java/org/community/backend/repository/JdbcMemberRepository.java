package org.community.backend.repository;

import org.community.backend.member.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

    public int save(Member member) {
        String sql = "INSERT INTO member (email, password, nickname) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 자동 생성된 기본 키(id)를 저장할 객체

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); //Statement.RETURN_GENERATED_KEYS -> INSERT 후 생성된 ID 값을 반환받기 위한 옵션
            ps.setString(1, member.getEmail());
            ps.setString(2, member.getPassword());
            ps.setString(3, member.getNickname());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();// 생성된 ID 반환
    }

    public void saveProfileImage(int memberId, String imageUrl) {
        String sql = "INSERT INTO member_profile_image (member_id, image_url) VALUES (?, ?)";
        jdbcTemplate.update(sql, memberId, imageUrl);
    }
}
