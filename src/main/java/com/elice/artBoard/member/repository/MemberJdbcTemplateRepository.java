package com.elice.artBoard.member.repository;

import com.elice.artBoard.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJdbcTemplateRepository implements MemberRepository{

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> {
        Member member = new Member();

        member.setMemberId(rs.getInt("member_id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPassword(rs.getString("password"));
        member.setNickname(rs.getString("nickname"));

        return member;
    };

    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM member";
        return jdbcTemplate.query(sql, memberRowMapper);
    }

    @Override
    public Optional<Member> findById(int memberId) {
        String sql = "SELECT * FROM member WHERE member_id=?";
        Member findMember = jdbcTemplate.queryForObject(sql, memberRowMapper, memberId);

        return Optional.ofNullable(findMember);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email=?";

        try {
            Member findMember = jdbcTemplate.queryForObject(sql, memberRowMapper, email);

            return Optional.ofNullable(findMember);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> check(Member member) {
        String sql = "SELECT * FROM member WHERE email=? AND password=?";

        try {
            Member findMember = jdbcTemplate.queryForObject(sql, memberRowMapper, member.getEmail(), member.getPassword());

            return Optional.ofNullable(findMember);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Member save(Member member) {
        if (member.getMemberId() == null) {
            String sql = "INSERT INTO member (name, email, password, nickname) VALUES(?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"member_id"});

                pstmt.setString(1, member.getName());
                pstmt.setString(2, member.getEmail());
                pstmt.setString(3, member.getPassword());
                pstmt.setString(4, member.getNickname());

                return pstmt;
            }, keyHolder);

            Number key = keyHolder.getKey();

            if (key != null) {
                member.setMemberId(key.intValue());
            }
        } else {
            String sql = "UPDATE member SET name=?, email=?, password=?, nickname=? WHERE member_id=?";

            jdbcTemplate.update(sql, member.getName(), member.getEmail(), member.getPassword(), member.getNickname(), member.getMemberId());
        }

        return member;
    }

    @Override
    public void delete(Member member) {
        String sql = "DELETE FROM member WHERE member_id=?";

        jdbcTemplate.update(sql, member.getMemberId());
    }
}
