package app.mediatracker.feature.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * User document.
 * <p>
 * Contains minimal master data: display name and hashed password.
 * A unique ID (MongoDB) identifies each user. Optionally, more fields like email
 * can be added later.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;

    /**
     * Public display name of the user.
     * <p>
     * This value must be unique. Uniqueness is enforced by a database index
     * (see {@code @Indexed(unique = true)}). Use this value for login/registration.
     * </p>
     */
    @Indexed(unique = true)
    private String username;


    private String passwordHash;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Flag indicating whether the user's library is publicly visible.
     */
    private Boolean publicList;


    private String profilePictureUrl;

}