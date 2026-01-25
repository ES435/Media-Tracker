package app.mediatracker.feature.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refreshTokens")
@Data
@Builder
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;
    @Indexed(unique = true)
    private ObjectId userId;
    private String token;
}
