package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MonitorRepositoryImplTest {

    private lateinit var repository: MonitorRepositoryImpl
    private val dataSource = mockk<MonitorDataSource>()

    @Before
    fun setUp() {
        repository = MonitorRepositoryImpl(dataSource)
    }

    @Test
    fun `addProvider should delegate to dataSource`() {
        val provider = mockk<MonitorProvider>()
        every { dataSource.addProvider(any()) } returns Unit
        repository.addProvider(provider)
        verify(exactly = 1) { dataSource.addProvider(provider) }
    }

    @Test
    fun `removeProvider should delegate to dataSource`() {
        val key = "key"
        every { dataSource.removeProvider(any()) } returns Unit
        repository.removeProvider(key)
        verify(exactly = 1) { dataSource.removeProvider(key) }
    }

    @Test
    fun `trackEvent with provider key should delegate to dataSource`() = runTest {
        val event = MonitorEvent("test")
        val key = "key"
        coEvery { dataSource.trackEvent(any(), any()) } returns Unit
        repository.trackEvent(event, key)
        coVerify(exactly = 1) { dataSource.trackEvent(event, key) }
    }

    @Test
    fun `trackEvent without provider key should delegate to dataSource with null`() = runTest {
        val event = MonitorEvent("test")
        coEvery { dataSource.trackEvent(any(), any()) } returns Unit
        repository.trackEvent(event)
        coVerify(exactly = 1) { dataSource.trackEvent(event, null) }
    }

    @Test
    fun `trackMetric with provider key should delegate to dataSource`() = runTest {
        val metric = PerformanceMetric.ScreenLoad("login", 100L)
        val key = "key"
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit
        repository.trackMetric(metric, key)
        coVerify(exactly = 1) { dataSource.trackMetric(metric, key) }
    }

    @Test
    fun `trackMetric without provider key should delegate to dataSource with null`() = runTest {
        val metric = PerformanceMetric.ScreenLoad("login", 100L)
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit
        repository.trackMetric(metric)
        coVerify(exactly = 1) { dataSource.trackMetric(metric, null) }
    }

    @Test
    fun `setAttribute with provider key should delegate to dataSource`() {
        every { dataSource.setAttribute(any(), any(), any()) } returns Unit
        repository.setAttribute("k", "v", "key")
        verify(exactly = 1) { dataSource.setAttribute("k", "v", "key") }
    }

    @Test
    fun `setAttribute without provider key should delegate to dataSource with null`() {
        every { dataSource.setAttribute(any(), any(), any()) } returns Unit
        repository.setAttribute("k", "v")
        verify(exactly = 1) { dataSource.setAttribute("k", "v", null) }
    }

    @Test
    fun `setAttributes with provider key should delegate to dataSource`() {
        val attrs = mapOf("a" to "1")
        every { dataSource.setAttributes(any(), any()) } returns Unit
        repository.setAttributes(attrs, "key")
        verify(exactly = 1) { dataSource.setAttributes(attrs, "key") }
    }

    @Test
    fun `setAttributes without provider key should delegate to dataSource with null`() {
        val attrs = mapOf("a" to "1")
        every { dataSource.setAttributes(any(), any()) } returns Unit
        repository.setAttributes(attrs)
        verify(exactly = 1) { dataSource.setAttributes(attrs, null) }
    }

    @Test
    fun `removeAttribute with provider key should delegate to dataSource`() {
        every { dataSource.removeAttribute(any(), any()) } returns Unit
        repository.removeAttribute("k", "key")
        verify(exactly = 1) { dataSource.removeAttribute("k", "key") }
    }

    @Test
    fun `removeAttribute without provider key should delegate to dataSource with null`() {
        every { dataSource.removeAttribute(any(), any()) } returns Unit
        repository.removeAttribute("k")
        verify(exactly = 1) { dataSource.removeAttribute("k", null) }
    }

    @Test
    fun `removeAttributes with provider key should delegate to dataSource`() {
        val keys = listOf("a")
        every { dataSource.removeAttributes(any(), any()) } returns Unit
        repository.removeAttributes(keys, "key")
        verify(exactly = 1) { dataSource.removeAttributes(keys, "key") }
    }

    @Test
    fun `removeAttributes without provider key should delegate to dataSource with null`() {
        val keys = listOf("a")
        every { dataSource.removeAttributes(any(), any()) } returns Unit
        repository.removeAttributes(keys)
        verify(exactly = 1) { dataSource.removeAttributes(keys, null) }
    }

    @Test
    fun `startTrace with provider key should delegate to dataSource`() = runTest {
        coEvery { dataSource.startTrace(any(), any(), any()) } returns Unit
        repository.startTrace("t", null, "key")
        coVerify(exactly = 1) { dataSource.startTrace("t", null, "key") }
    }

    @Test
    fun `startTrace without provider key should delegate to dataSource with null`() = runTest {
        coEvery { dataSource.startTrace(any(), any(), any()) } returns Unit
        repository.startTrace("t")
        coVerify(exactly = 1) { dataSource.startTrace("t", null, null) }
    }

    @Test
    fun `stopTrace with provider key should delegate to dataSource`() = runTest {
        coEvery { dataSource.stopTrace(any(), any(), any()) } returns Unit
        repository.stopTrace("t", null, "key")
        coVerify(exactly = 1) { dataSource.stopTrace("t", null, "key") }
    }

    @Test
    fun `stopTrace without provider key should delegate to dataSource with null`() = runTest {
        coEvery { dataSource.stopTrace(any(), any(), any()) } returns Unit
        repository.stopTrace("t")
        coVerify(exactly = 1) { dataSource.stopTrace("t", null, null) }
    }

    @Test
    fun `cancelTrace with provider key should delegate to dataSource`() = runTest {
        coEvery { dataSource.cancelTrace(any(), any()) } returns Unit
        repository.cancelTrace("t", "key")
        coVerify(exactly = 1) { dataSource.cancelTrace("t", "key") }
    }

    @Test
    fun `cancelTrace without provider key should delegate to dataSource with null`() = runTest {
        coEvery { dataSource.cancelTrace(any(), any()) } returns Unit
        repository.cancelTrace("t")
        coVerify(exactly = 1) { dataSource.cancelTrace("t", null) }
    }
}
