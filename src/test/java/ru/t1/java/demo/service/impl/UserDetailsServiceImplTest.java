package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.t1.java.demo.model.Role;
import ru.t1.java.demo.model.RoleEnum;
import ru.t1.java.demo.model.User;
import ru.t1.java.demo.repository.RoleRepository;
import ru.t1.java.demo.repository.UserRepository;
import ru.t1.java.demo.util.AbstractIntegrationTestInitializer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDetailsServiceImplTest extends AbstractIntegrationTestInitializer {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_roles");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "role");
    }

    @Test
    @Sql(scripts = "classpath:db/create-role.sql")
    public void loadUserByUsername_whenUserExist_returnUserDetailsImpl() {

        Set<Role> roles = new HashSet<>();

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN);
        optionalRole.ifPresent(roles::add);

        User user = new User();
        user.setLogin("JohnDoe");
        user.setEmail("John12@gmaile.com");
        user.setPassword("12345Password");
        user.setRoles(roles);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLogin());

        assertEquals("JohnDoe", userDetails.getUsername());
        assertEquals("12345Password", userDetails.getPassword());

    }

    @Test
    public void loadUserByUsername_whenUserNotExist_returnUsernameNotFoundException() {

        String userName = "Tom";

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(userName));
    }

    @Test
    public void loadUserByUsername_whenUserNameNull_returnUsernameNotFoundException() {

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null));
    }
}
