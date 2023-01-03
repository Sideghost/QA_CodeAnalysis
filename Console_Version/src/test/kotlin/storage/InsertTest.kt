package storage

import mongoDB.MongoDriver
import mongoDB.getDocument
import mongoDB.insertDocument
import mongoDB.replaceDocument
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class InsertTest {

    private val collectionName = "tests"

    private val testName: String = "test_nr"

    private val docName: String = "test"

    private val id: String = "_id"

    private val numberOfConnections = 100

    @Test
    fun `insert connection time`() {
        var acc = 0L
        for (i in 0..numberOfConnections) {
            acc += `get time from insert`()
        }
        val avg = acc / numberOfConnections
        assertNotNull(avg)
        println("avg connection time of $numberOfConnections connections \n$avg")
    }

    private fun `get time from insert`(): Long {
        val oldTime = System.currentTimeMillis()
        `insert Document`()
        val newTime = System.currentTimeMillis()
        return newTime - oldTime
    }


    /*** Adds a blank document with the test number and an arbitrary field and updates the test counter.***/
    @Test
    fun `insert Document`() {
        MongoDriver().use { drv ->
            val currentTestNr = getLastTestNr() + 1
            val collection = drv.getCollection<MongoTest.Doc>(collectionName)
            collection.replaceDocument(MongoTest.Doc(docName + id, currentTestNr))
            val testNr = collection.getDocument(docName + id)
            assertNotNull(testNr)
            assertEquals(testNr.field, currentTestNr)
            collection.insertDocument(MongoTest.Doc(testName + currentTestNr + id, 10))
            val doc = collection.getDocument(testName + currentTestNr + id)
            assertNotNull(doc)
            assertEquals(10, doc.field)
        }
    }

    /*** Gets the latest test number. ***/
    private fun getLastTestNr(): Int {
        return MongoDriver().use { drv ->
            val collection = drv.getCollection<MongoTest.Doc>(collectionName)
            val doc = collection.getDocument(docName + id)
            if (doc == null) {
                collection.insertDocument(MongoTest.Doc(docName + id, -1))
            }
            val newDoc = collection.getDocument(docName + id)
            newDoc?.field ?: throw IllegalStateException("BA BUM")
        }
    }
}