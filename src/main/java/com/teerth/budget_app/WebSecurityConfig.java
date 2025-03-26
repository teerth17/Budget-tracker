//package com.teerth.budget_app;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.context.DelegatingSecurityContextRepository;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import javax.sql.DataSource;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//
//        @Autowired
//        private DataSource dataSource;
//
//        private final CustomLoginSuccessHandler successHandler;
//
//        public WebSecurityConfig(CustomLoginSuccessHandler successHandler){
//            this.successHandler = successHandler;
//        }
//
//        @Autowired
//        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.jdbcAuthentication().dataSource(dataSource)
//                    // mapped by position to (username, password, active)
//                    .usersByUsernameQuery("SELECT email, password, enabled FROM account WHERE email = ?")
//            .authoritiesByUsernameQuery("SELECT a.email, au.role FROM authority au JOIN account a ON a.account_id = au.account_id WHERE a.email = ? ");
//        }
//
//        @Bean
//        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//            System.out.println("Entered in config");
//            http.authorizeHttpRequests(authz -> authz
//                    .requestMatchers("/register", "/index").permitAll()
//                    .requestMatchers("/","/userHome/**").hasAnyRole("USER")// Ensure /register is allowed
//                            .requestMatchers("/static/**").permitAll()
//                            .anyRequest().authenticated()
//            );
//
//            // when access denied, show "/403" page
//            http.exceptionHandling((exceptionHandling) -> exceptionHandling
//                    .accessDeniedPage("/403"));
//            // "/login" allow custom login page
//            // "/login?error" displays login.html with error message
//            // "/userHome" default page when /login was the path.
//            http.formLogin(form -> form
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/userHome", true)
//                    .permitAll()
//            );
//
//            http.logout(logout -> logout
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/index")
//                    .permitAll()
//            );
//
//
//            // needed for auto-login
//            http.securityContext((securityContext) -> securityContext.requireExplicitSave(true));
//
//
//            return http.build();
//        }
//
//        // needed for auto login
//        @Bean
//        public SecurityContextRepository securityContextRepository() {
//            return new DelegatingSecurityContextRepository(
//                    new RequestAttributeSecurityContextRepository(),
//                    new HttpSessionSecurityContextRepository());
//        }
//
//        @Bean
//        PasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//}

package com.teerth.budget_app;

import com.teerth.budget_app.auth.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.sql.DataSource;
import java.util.List;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final DataSource dataSource;
    private final CustomLoginSuccessHandler successHandler;

    public WebSecurityConfig(DataSource dataSource, CustomLoginSuccessHandler successHandler) {
        this.dataSource = dataSource;
        this.successHandler = successHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(dataSource);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("Entered in config");

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/register", "/index").permitAll()
                .requestMatchers("/", "/userHome/**").hasAnyRole("USER")
                .requestMatchers("/static/**").permitAll()
                .anyRequest().authenticated()
        );

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
        );

        http.securityContext(securityContext -> securityContext.requireExplicitSave(true));

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

