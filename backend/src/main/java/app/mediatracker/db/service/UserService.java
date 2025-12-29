import org.springframework.stereotype.Service;

import app.mediatracker.db.repo.UserLibraryEntryRepository;
import app.mediatracker.db.repo.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Anwendungslogik für die Medienbibliothek.
 * <p>
 * Diese Service-Klasse koordiniert die Speicherung und Abfrage von Bibliothekseinträgen eines Users.
 * In dieser Variante werden Medien-Basisdaten direkt im Eintrag als Snapshot gespeichert (kein separates MediaItem).
 * </p>
 * <p>
 * Persistenz: MongoDB über Spring Data Repositories. Zeitstempel werden durch Mongo Auditing gesetzt.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
}