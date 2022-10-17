package battleship.ui

import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class InputTest {
    @Test
    fun `parseCommand- normal usage`() {
        val sut = "abc 123 xpto".parseCommand()
        assertNotNull(sut)
        assertEquals("ABC", sut.first)
        assertEquals(listOf("123", "xpto"), sut.second)
    }

    @Test
    fun `parseCommand- line with spaces`() {
        val sut = " abc 123   xpto  ".parseCommand()
        assertNotNull(sut)
        assertEquals("ABC", sut.first)
        assertEquals(listOf("123", "xpto"), sut.second)
    }

    @Test
    fun `parseCommand- empty line results null`() {
        val sut = "   ".parseCommand()
        assertTrue(sut == null)
    }

    @Test
    fun `readCommand- normal usage`() {
        val output = redirectInOut("put h5") {
            val (name, args) = readCommand()
            assertEquals("PUT", name)
            assertEquals(listOf("h5"), args)
        }
        assertEquals(listOf("> "), output)
    }

    @Test
    fun `readCommand- empty line reads again`() {
        val output = redirectInOut("", "exit") {
            val (name, args) = readCommand()
            assertEquals("EXIT", name)
            assertTrue(args.isEmpty())
        }
        assertEquals(listOf("> > "), output)
    }

    /**
     * Executes the [test] function with the input and output redirected.
     * @param lines Each line that will be read from the input.
     * @param test Code to test that reads from stdin and writes to stdout.
     * @return Lines written in the output.
     */
    private fun redirectInOut(vararg lines: String, test: () -> Unit): List<String> {
        val oldInput = System.`in`
        System.setIn(ByteArrayInputStream(lines.joinToString(System.lineSeparator()).toByteArray()))
        val oldOutput = System.out
        val result = ByteArrayOutputStream()
        System.setOut(PrintStream(result))
        test()
        System.setIn(oldInput)
        System.setOut(oldOutput)
        val out = result.toString().split(System.lineSeparator())
        return if (out.size > 1 && out.last().isEmpty()) out.dropLast(1) else out
    }
}
