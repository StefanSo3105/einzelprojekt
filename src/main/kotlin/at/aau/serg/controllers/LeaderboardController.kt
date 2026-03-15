package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int? = null): List<GameResult> {

        // Leaderboard nach Score absteigend sortieren und bei gleichem Score nach Zeit aufsteigend sortieren
        val sortedResults = gameResultService.getGameResults()
            .sortedWith(
                compareByDescending<GameResult> { it.score }
                    .thenBy { it.timeInSeconds }
            )

        // ohne rank angabe wird gesamtes board ausgegeben
        if (rank == null) {
            return sortedResults
        }

        // ungültige ranks führen zu HTTP 400 Fehler
        if (rank < 1 || rank > sortedResults.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        // rank ist 1-basiert, Listenindex in Kotlin ist 0-basiert
        val rankIndex = rank - 1

        // spiele auf gewählten rank ausgeben mit den 3 über ihm und 3 unter ihm
        val fromIndex = maxOf(0, rankIndex - 3)
        val toIndex = minOf(sortedResults.size, rankIndex + 4)

        return sortedResults.subList(fromIndex, toIndex)
    }
}
