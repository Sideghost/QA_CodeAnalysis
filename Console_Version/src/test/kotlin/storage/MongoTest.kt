package storage

import mongoDB.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull


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
    fun `insert Document`() {
        MongoDriver().use { drv ->
            val currentTestNr = testNumber + 1
            val collection = drv.getCollection<Doc>(collectionName)
            collection.replaceDocument(Doc(docName + id, currentTestNr))
            val testNr = collection.getDocument(docName + id)
            assertNotNull(testNr)
            assertEquals(testNr.field, currentTestNr)
            collection.insertDocument(Doc(testName + currentTestNr + id, 10))
            val doc = collection.getDocument(testName + currentTestNr + id)
            assertNotNull(doc)
            assertEquals(10, doc.field)
        }
    }

    /**
     * Deletes a document form the DB and updates the test counter.
     */
    @Test
    fun `delete Document`() {
        MongoDriver().use { drv ->
            val collection = drv.getCollection<Doc>(collectionName)
            val old = collection.getDocument(testName + testNumber + id)
            requireNotNull(old) { "Not possible to retrieve document" }
            collection.deleteDocument(testName + testNumber + id)
            val testAcc = collection.getDocument(docName + id)
            collection.replaceDocument(Doc(docName + id, testNumber - 1))
            requireNotNull(testAcc) { "Test counter should already been initialized" }
            assertNotEquals(testAcc.field, collection.getDocument(docName + id)?.field)
        }
    }

    /**
     * Gets the latest test number.
     */
    private fun getLastTestNr(): Int {
        return MongoDriver().use { drv ->
            val collection = drv.getCollection<Doc>(collectionName)
            val doc = collection.getDocument(docName + id)
            if (doc == null) {
                collection.insertDocument(Doc(docName + id, -1))
            }
            val newDoc = collection.getDocument(docName + id)
            newDoc?.field ?: throw IllegalStateException("BA BUM")
        }
    }
}
