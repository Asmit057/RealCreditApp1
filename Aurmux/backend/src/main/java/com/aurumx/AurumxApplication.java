package com.aurumx;

import com.aurumx.entity.CesUser;
import com.aurumx.enums.UserRole;
import com.aurumx.repository.CesUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan
public class
AurumxApplication {

    public static void main(String[] args) {
        SpringApplication.run(AurumxApplication.class, args);
    }

    @Bean
    CommandLineRunner initUsers(CesUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if(repo.count() == 0) {
                CesUser admin = new CesUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("password123"));
                admin.setRole(UserRole.ROLE_ADMIN_CES);
                admin.setActive(true);

                CesUser user = new CesUser();
                user.setUsername("cesuser");
                user.setPassword(encoder.encode("password124"));
                user.setRole(UserRole.ROLE_CES_USER);
                user.setActive(true);

                repo.save(admin);
                repo.save(user);

                System.out.println("Default users created!");
            }
        };
    }
}
