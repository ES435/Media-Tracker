package app.mediatracker.config;

import app.mediatracker.feature.library.model.UserLibraryEntry;
import app.mediatracker.feature.library.repo.UserLibraryEntryRepository;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
                User user = new User();
                user.setUsername("alice");
                user.setPasswordHash("hash1");
                user.setPublicList(true);
                user.setProfilePictureUrl("https://randomuser.me/api/portraits/women/1.jpg");
                userRepository.save(user);
                
                UserLibraryEntry entry = new UserLibraryEntry();
                entry.setUserId(user.getId());
                entry.setMediaType("anime");
                entry.setExternalId("an1");
                entry.setTitle("Attack on Titan");
                entry.setImageUrl("https://anilist.co/anime/16498");
                
                userLibraryEntryRepository.save(entry);

                UserLibraryEntry entry2 = new UserLibraryEntry();
                entry2.setUserId(user.getId());
                entry2.setMediaType("movie");
                entry2.setExternalId("mv1");
                entry2.setTitle("Inception");
                entry2.setImageUrl("https://cdn.example.com/movies/inception.jpg");
                
                userLibraryEntryRepository.save(entry2);
            }

            if (userLibraryEntryRepository.count() == 0) {
                InputStream inputStream = new ClassPathResource("testdata/demo-media-data.json").getInputStream();
                List<UserLibraryEntry> entries = objectMapper.readValue(inputStream, new TypeReference<List<UserLibraryEntry>>() {});
            }
        };
    }
}
