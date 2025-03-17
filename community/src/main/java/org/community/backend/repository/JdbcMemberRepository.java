package org.community.backend.repository;

import jakarta.transaction.Transactional;
import org.community.backend.member.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public Optional<String> findPasswordById(int id) {
        String sql = "SELECT password FROM member WHERE id = ?";
        try {
            String password = jdbcTemplate.queryForObject(sql, String.class, id);
            return Optional.ofNullable(password);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<Member> findUserById(int id) {
        String sql = "SELECT m.nickname, p.image_url FROM member m " +
                "LEFT JOIN member_profile_image p ON m.id = p.member_id " +
                "WHERE m.id = ?";

        List<Member> members = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String nickname = rs.getString("nickname");
            String imageUrl = rs.getString("image_url");

            return new Member(nickname, imageUrl);
        }, id);

        return members.stream().findFirst(); // 첫 번째 값만 가져오기 (없으면 Optional.empty())
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

    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new Member(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickname")
                ));
            }
            return Optional.empty(); // 반환된 값이 없으면 null 반환
        }, email);
    }

    @Transactional
    public void updateMemberInfo(int memberId, String newNickname, String newImageUrl) {
        // 닉네임 업데이트
        String updateNicknameSql = "UPDATE member SET nickname = ? WHERE id = ?";
        int nicknameUpdated = jdbcTemplate.update(updateNicknameSql, newNickname, memberId);

        // 프로필 이미지 업데이트 (member_profile_image 테이블)
        String updateImageSql = "UPDATE member_profile_image SET image_url = ? WHERE member_id = ?";
        int imageUpdated = jdbcTemplate.update(updateImageSql, newImageUrl, memberId);

        // 프로필 이미지가 없는 경우 새로 삽입
        if (imageUpdated == 0) {
            String insertImageSql = "INSERT INTO member_profile_image (member_id, image_url) VALUES (?, ?)";
            jdbcTemplate.update(insertImageSql, memberId, newImageUrl);
        }
    }
}
