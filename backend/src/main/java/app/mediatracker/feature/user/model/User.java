package app.mediatracker.feature.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Benutzer-Dokument.
 * <p>
 * Enthält minimale Stammdaten: Anzeigename und gehashtes Passwort.
 * Eine eindeutige ID (MongoDB) identifiziert jeden Benutzer. Optional können später
 * weitere Felder wie E-Mail ergänzt werden.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    /**
     * Anzeigename des Benutzers.
     * <p>
     * Dieser Name muss eindeutig sein. Die Eindeutigkeit wird durch einen Datenbankindex erzwungen
     * (siehe {@code @Indexed(unique = true)}). Verwende diesen Wert später für Login/Registrierung.
     * </p>
     */
    @Indexed(unique = true)
    private String username;

    /**
     * Gehashter Passwort-String
     */
    private String passwordHash;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Boolean publicList;

    /** Optional: Profilbild */
    private String profilePictureUrl;

}
