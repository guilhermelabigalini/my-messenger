/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import mymessenger.backend.api.SessionHeaderFilter;
import mymessenger.backend.model.users.UserProfile;
import mymessenger.backend.services.UserRepository;
import mymessenger.backend.services.UserService;
import mymessenger.backend.services.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author guilherme
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@Configuration
public class Application implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**"); //.allowedOrigins("*"); //.allowedOrigins("http://localhost:9000");
            }
        };
    }
    
    @Bean
    public FilterRegistrationBean headerFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        SessionHeaderFilter filter = new SessionHeaderFilter();
        beanFactory.autowireBean(filter);
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(SessionHeaderFilter.PROTECTED_URLS);
        return registrationBean;
    }

    private void tryToAddUser(UserProfile up) throws ValidationException {

        Optional<UserProfile> dbProfile = userService.getProfile(up.getUsername());

        if (!dbProfile.isPresent()) {
            userService.register(up);
            dbProfile = userService.getProfile(up.getUsername());

            LOGGER.info("Added profile:" + dbProfile.get());
        } else {
            LOGGER.info("Profile already present:" + dbProfile.get());
        }
    }

    private void showStats() {
        LOGGER.info("Total users: " + userRepository.count());
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Preparing database");
        
        // save a couple of customers
        tryToAddUser(new UserProfile("user1", "654321", LocalDate.of(1984, Month.FEBRUARY, 4)));
        tryToAddUser(new UserProfile("user2", "123456", LocalDate.of(1995, Month.DECEMBER, 3)));

        showStats();
    }

}
