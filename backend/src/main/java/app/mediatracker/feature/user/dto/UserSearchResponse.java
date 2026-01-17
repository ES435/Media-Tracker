package app.mediatracker.feature.user.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * API-Response für die User-Search. 
 * Gibt eine Liste mit zur Suche passenden Usern zurück.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {

    private List<UserSummary> users;

}