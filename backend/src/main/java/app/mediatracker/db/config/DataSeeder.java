package app.mediatracker.db.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.mediatracker.db.repo.UserLibraryEntryRepository;
import app.mediatracker.db.repo.UserRepository;
import app.mediatracker.db.domain.User;
import app.mediatracker.db.domain.UserLibraryEntry;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository, UserLibraryEntryRepository userLibraryEntryRepository, ObjectMapper objectMapper) {
        return args -> {
            if (userRepository.count() == 0) {
                InputStream inputStream = new ClassPathResource("testdata/demo-user-data.json").getInputStream();
                List<User> users = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {});
                userRepository.saveAll(users);
            }

            if (userLibraryEntryRepository.count() == 0) {
                InputStream inputStream = new ClassPathResource("testdata/demo-media-data.json").getInputStream();
                List<UserLibraryEntry> entries = objectMapper.readValue(inputStream, new TypeReference<List<UserLibraryEntry>>() {});
                userLibraryEntryRepository.saveAll(entries); 
            }
        };
    }
}
