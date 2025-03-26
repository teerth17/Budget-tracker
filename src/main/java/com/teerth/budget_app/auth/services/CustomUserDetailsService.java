package com.teerth.budget_app.auth.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.util.List;

public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String sql = "SELECT email, password, enabled FROM account WHERE email = ?";
        List<UserDetails> users = jdbcTemplate.query(sql, new Object[]{email}, userRowMapper());

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return users.get(0);
    }

    private RowMapper<UserDetails> userRowMapper() {
        return (rs, rowNum) -> User
                .withUsername(rs.getString("email"))
                .password(rs.getString("password"))
                .roles("USER") // Modify roles if needed
                .accountLocked(!rs.getBoolean("enabled"))
                .build();
    }
}
