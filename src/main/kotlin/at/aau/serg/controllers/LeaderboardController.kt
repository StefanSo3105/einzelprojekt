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
        val sortedResults = gameResultService.getGameResults()
            .sortedWith(
                compareByDescending<GameResult> { it.score }
                    .thenBy { it.timeInSeconds }
            )

        if (rank == null) {
            return sortedResults
        }

        if (rank < 1 || rank > sortedResults.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        val rankIndex = rank - 1
        val fromIndex = maxOf(0, rankIndex - 3)
        val toIndex = minOf(sortedResults.size, rankIndex + 4)

        return sortedResults.subList(fromIndex, toIndex)
    }
}

