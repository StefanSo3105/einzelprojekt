package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

// Tests für den GameResultController
// Der Service wird simuliert Mock, damit nur der Controller getestet wird.
class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getAllGameResults_returnsAllResults() {
        // Vorbereitung von zwei Testdaten
        val results = listOf(
            GameResult(1, "player1", 100, 10.0),
            GameResult(2, "player2", 90, 12.0)
        )

        // Verhalten des gemockten Services definieren
        whenever(mockedService.getGameResults()).thenReturn(results)

        val res = controller.getAllGameResults()

        // Prüfen, ob der Controller den Service korrekt aufruft
        verify(mockedService).getGameResults()
        assertEquals(2, res.size)
        assertEquals(results, res)
    }

    @Test
    fun test_addGameResult_callsService() {
        val gameResult = GameResult(1, "player1", 100, 10.0)

        controller.addGameResult(gameResult)

        // Überprüft, ob der Controller den Service zum Speichern aufruft
        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_getGameResult_returnsRequestedResult() {
        val gameResult = GameResult(1, "player1", 100, 10.0)

        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val res = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(gameResult, res)
    }

    @Test
    fun test_deleteGameResult_callsService() {
        controller.deleteGameResult(1)

        // Prüft, ob der Controller die Löschfunktion des Services aufruft
        verify(mockedService).deleteGameResult(1)
    }
}
