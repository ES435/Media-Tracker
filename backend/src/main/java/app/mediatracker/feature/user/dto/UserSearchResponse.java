package app.mediatracker.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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