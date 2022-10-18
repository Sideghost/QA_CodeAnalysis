package battleship.model

import kotlin.test.Test
import kotlin.test.assertSame

class PlayerTest {
    @Test
    fun `Stress Test Player other`() {
        val playerA = Player.A
        val playerB = Player.B

        assertSame(playerA.other(), playerB)
        assertSame(playerB.other(), playerA)
        assertSame(playerA.other().other(), playerA)
        assertSame(playerB.other().other(), playerB)
    }
}
