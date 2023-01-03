package storage

import mongoDB.*
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class DeleteTest {

    private val collectionName = "tests"
    private val testName: String = "test_nr"
    private val docName: String = "test"
    private val id: String = "_id"
    private val numberOfConnections = 10

    /**
     * Deletes a document form the DB and updates the test counter.
     */
    @Test
    fun `delete Document`() {
        MongoDriver().use { drv ->
            val collection = drv.getCollection<MongoTest.Doc>(collectionName)
            val currentNr = getLastTestNr() - 1
            val old = collection.getDocument(testName + currentNr + id)
            requireNotNull(old) { "Not possible to retrieve document" }
            collection.deleteDocument(testName + getLastTestNr() + id)
            val testAcc = collection.getDocument(docName + id)
            collection.replaceDocument(MongoTest.Doc(docName + id, currentNr))
            requireNotNull(testAcc) { "Test counter should already been initialized" }
            assertNotEquals(testAcc.field, collection.getDocument(docName + id)?.field)
        }
    }

    /**
     * Inserts before deletion of [numberOfConnections] test files.
     * Gets the average time of deletion.
     */
    @Test
    fun `delete connection time`() {
        val nrOfTests = getLastTestNr() + 1
        if (nrOfTests < numberOfConnections) {
            val counter = numberOfConnections - nrOfTests
            insertDocuments(nrOfTests, counter)
        }
        var acc = 0L
        for (i in 0 until numberOfConnections) {
            acc += `get time from delete`()
        }
        val avg = acc / numberOfConnections
        assertNotNull(avg)
        println("avg connection time of $numberOfConnections connections \n$avg")
    }

    /**
     * Gets the time of deletion of a single test file.
     */
    private fun `get time from delete`(): Long {
        val oldTime = System.currentTimeMillis()
        `delete Document`()
        val newTime = System.currentTimeMillis()
        return newTime - oldTime
    }

    /**
     *  Gets the latest test number.
     */
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

    /**
     * Inserts [counter] number of documents.
     */
    private fun insertDocuments(start: Int, counter: Int) {
        var acc = start
        MongoDriver().use {
            val collection = it.getCollection<MongoTest.Doc>(collectionName)
            while (acc < start + counter) {
                val name = testName + acc + id
                collection.insertDocument(MongoTest.Doc(name, 10))
                acc++
            }
            val sut = start + counter
            collection.replaceDocument(MongoTest.Doc(docName + id, sut))
        }
    }
}