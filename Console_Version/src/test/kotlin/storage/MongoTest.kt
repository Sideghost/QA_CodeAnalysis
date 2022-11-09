package storage

import mongoDB.*
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.test.assertNotNull
import kotlin.test.assertEquals


class MongoTest {

    data class Doc(val _id: String, val field: Int)

    private val collectionName = "tests"

    private val testNumber: Int by lazy { getLastTestNr() }

    private val testName = "test_nr"

    private val docName = "test"

    private val id = "_id"


    /**
     * Adds a blank document with the test number and an arbitrary field and updates the test counter.
     */
    @Test
    fun `test insert Document v2`() {
        MongoDriver().use { drv ->
            val currentTestNr = testNumber + 1
            val collection = drv.getCollection<Doc>(collectionName)
            collection.replaceDocument(Doc(docName+id, currentTestNr))
            println(collection.getDocument(docName+id))
            collection.insertDocument(Doc(testName+currentTestNr+id, 10))
            val doc = collection.getDocument(testName+currentTestNr+id)
            assertNotNull(doc)
            assertEquals(10,doc.field)
        }
    }

    /**
     * Gets the latest test number.
     */
    private fun getLastTestNr():Int {
        return MongoDriver().use { drv ->
            val collection = drv.getCollection<Doc>(collectionName)
            val doc = collection.getDocument(docName+id)
            if (doc == null) {
                collection.insertDocument(Doc(docName+id,-1))
            }
            val newDoc = collection.getDocument(docName+id)
            newDoc?.field ?: throw IllegalStateException("BA BUM")
        }
    }
}
