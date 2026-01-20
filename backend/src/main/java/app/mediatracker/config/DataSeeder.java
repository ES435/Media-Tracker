package app.mediatracker.config;

import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import app.mediatracker.seed.demo_media_data;
import app.mediatracker.seed.demo_user_data;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository, UserLibraryEntryRepository userLibraryEntryRepository, ObjectMapper objectMapper, demo_user_data userTestDataLoader, demo_media_data mediaTestDataLoader) {
        return args -> {
            //only if User Database is empty!
            if (userRepository.count() == 0) {

                List<User> userTestData = userTestDataLoader.createDemoUsers();
                
                for(User user : userTestData) {
                    userRepository.save(user); //save users
                    ObjectId userId = user.getId();
                    List<UserLibraryEntry> mediaTestData = mediaTestDataLoader.createDemoMediaData();
                    for(UserLibraryEntry mediaEntry : mediaTestData) {
                        mediaEntry.setUserId(userId);
                        userLibraryEntryRepository.save(mediaEntry);
                    }
                }
            }
        };
    }
}
