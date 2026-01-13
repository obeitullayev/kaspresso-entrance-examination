package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CerealStorageImplTest {

    private companion object {
        const val DELTA = 0.01f
    }
    /* ---------- Инициализация ---------- */

    @Test
    fun `should create storage with valid capacities`() {
        assertDoesNotThrow {
            CerealStorageImpl(10f, 50f)
        }
    }

    @Test
    fun `should throw if containerCapacity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `should throw if storageCapacity is less than containerCapacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(10f, 5f)
        }
    }

    /* ---------- addCereal ---------- */

    @Test
    fun `should add cereal to empty storage and create container`() {
        val storage = CerealStorageImpl(10f, 50f)

        val leftover = storage.addCereal(Cereal.RICE, 5f)

        assertEquals(0f, leftover, DELTA)
        assertEquals(5f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should add cereal to existing container`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 5f)

        val leftover = storage.addCereal(Cereal.RICE, 3f)

        assertEquals(0f, leftover, DELTA)
        assertEquals(8f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should return leftover when container overflows`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 8f)

        val leftover = storage.addCereal(Cereal.RICE, 5f)

        assertEquals(3f, leftover, DELTA)
        assertEquals(10f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should throw if adding negative cereal amount`() {
        val storage = CerealStorageImpl(10f, 50f)

        assertThrows(IllegalArgumentException::class.java) {
            storage.addCereal(Cereal.RICE, -1f)
        }
    }

    @Test
    fun `should add containers until storage capacity is reached`() {
        val storage = CerealStorageImpl(10f, 30f)

        storage.addCereal(Cereal.RICE, 5f)
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        storage.addCereal(Cereal.MILLET, 5f)

        assertEquals(5f, storage.getAmount(Cereal.RICE), DELTA)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT), DELTA)
        assertEquals(5f, storage.getAmount(Cereal.MILLET), DELTA)
    }

    @Test
    fun `should throw when storage capacity does not allow new container`() {
        val storage = CerealStorageImpl(10f, 20f)
        storage.addCereal(Cereal.RICE, 5f)
        storage.addCereal(Cereal.BUCKWHEAT, 5f)

        assertThrows(IllegalStateException::class.java) {
            storage.addCereal(Cereal.MILLET, 1f)
        }
    }

    /* ---------- getCereal ---------- */

    @Test
    fun `should get part of cereal when enough exists`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 8f)

        val received = storage.getCereal(Cereal.RICE, 3f)

        assertEquals(3f, received, DELTA)
        assertEquals(5f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should get all cereal when requested more than exists`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 4f)

        val received = storage.getCereal(Cereal.RICE, 10f)

        assertEquals(4f, received, DELTA)
        assertEquals(0f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should throw if getting negative cereal amount`() {
        val storage = CerealStorageImpl(10f, 50f)

        assertThrows(IllegalArgumentException::class.java) {
            storage.getCereal(Cereal.RICE, -2f)
        }
    }

    /* ---------- getAmount ---------- */

    @Test
    fun `should return amount for existing container`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 6f)

        assertEquals(6f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should return zero for non existing container`() {
        val storage = CerealStorageImpl(10f, 50f)

        assertEquals(0f, storage.getAmount(Cereal.RICE), DELTA)
    }

    /* ---------- getSpace ---------- */

    @Test
    fun `should return correct free space for existing container`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 7f)

        assertEquals(3f, storage.getSpace(Cereal.RICE), DELTA)
    }

    @Test
    fun `should throw if getSpace called for non existing container`() {
        val storage = CerealStorageImpl(10f, 50f)

        assertThrows(IllegalStateException::class.java) {
            storage.getSpace(Cereal.RICE)
        }
    }

    /* ---------- removeContainer ---------- */

    @Test
    fun `should remove empty container`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 5f)
        storage.getCereal(Cereal.RICE, 5f)

        val removed = storage.removeContainer(Cereal.RICE)

        assertTrue(removed)
        assertEquals(0f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should not remove non empty container`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 3f)

        val removed = storage.removeContainer(Cereal.RICE)

        assertFalse(removed)
        assertEquals(3f, storage.getAmount(Cereal.RICE), DELTA)
    }

    @Test
    fun `should return false when removing absent container`() {
        val storage = CerealStorageImpl(10f, 50f)

        assertFalse(storage.removeContainer(Cereal.RICE))
    }

    /* ---------- toString ---------- */

    @Test
    fun `toString should return non empty string`() {
        val storage = CerealStorageImpl(10f, 50f)
        storage.addCereal(Cereal.RICE, 5f)
        storage.addCereal(Cereal.BUCKWHEAT, 3f)

        val result = storage.toString()

        assertTrue(result.isNotBlank())
    }
}