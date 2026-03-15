package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_correctTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    //Test Spieler mit gewählten rang zusätzlich 3 darunter und 3 darüber
    fun test_getLeaderboard_withRank_returnsPlayerAndThreeAboveAndBelow() {
        val results = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 10.0),
            GameResult(3, "p3", 80, 10.0),
            GameResult(4, "p4", 70, 10.0),
            GameResult(5, "p5", 60, 10.0),
            GameResult(6, "p6", 50, 10.0),
            GameResult(7, "p7", 40, 10.0),
            GameResult(8, "p8", 30, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val res: List<GameResult> = controller.getLeaderboard(4)

        verify(mockedService).getGameResults()
        assertEquals(7, res.size)
        assertEquals("p1", res[0].playerName)
        assertEquals("p7", res[6].playerName)
    }

    @Test
    fun test_getLeaderboard_withRankAtStart_returnsOnlyAvailableEntries() {
        val results = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 10.0),
            GameResult(3, "p3", 80, 10.0),
            GameResult(4, "p4", 70, 10.0),
            GameResult(5, "p5", 60, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val res: List<GameResult> = controller.getLeaderboard(1)

        verify(mockedService).getGameResults()
        assertEquals(4, res.size)
        assertEquals("p1", res[0].playerName)
        assertEquals("p4", res[3].playerName)
    }

    @Test
    // Ungültiger Rank soll zu HTTP 400 Fehler führen
    fun test_getLeaderboard_withNegativeRank_throwsBadRequest() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_withTooLargeRank_throwsBadRequest() {
        val results = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(3)
        }

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }
}
