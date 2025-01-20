package com.teerth.budget_app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Autowired
        private DataSource dataSource;

        private final CustomLoginSuccessHandler successHandler;

        public WebSecurityConfig(CustomLoginSuccessHandler successHandler){
            this.successHandler = successHandler;
        }

        @Autowired
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication().dataSource(dataSource)
                    // mapped by position to (username, password, active)
                    .usersByUsernameQuery("SELECT email, password, enabled FROM account WHERE email = ?")
            .authoritiesByUsernameQuery("SELECT a.email, au.role FROM authority au JOIN account a ON a.account_id = au.account_id WHERE a.email = ? ");
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            System.out.println("Entered in config");
            http.authorizeHttpRequests(authz -> authz
//                    .requestMatchers("/","/register").permitAll()
                    .requestMatchers("/", "/register","/userHome/**").hasAnyRole("USER")// Ensure /register is allowed
                            .requestMatchers("/static/**").permitAll()
                            .anyRequest().authenticated()
            );

            // when access denied, show "/403" page
            http.exceptionHandling((exceptionHandling) -> exceptionHandling
                    .accessDeniedPage("/403"));
            // "/login" allow custom login page
            // "/login?error" displays login.html with error message
            // "/userHome" default page when /login was the path.
            http.formLogin(formLogin -> formLogin
//                    .loginPage("/login")
                            .successHandler(successHandler)
//                    .failureUrl("/login?error")
//                    .defaultSuccessUrl("/userHome")
//                    .permitAll()

            );

            http.logout(logout -> logout.permitAll());


            // needed for auto-login
            http.securityContext((securityContext) -> securityContext.requireExplicitSave(true));


            return http.build();
        }

        // needed for auto login
        @Bean
        public SecurityContextRepository securityContextRepository() {
            return new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(),
                    new HttpSessionSecurityContextRepository());
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
}
