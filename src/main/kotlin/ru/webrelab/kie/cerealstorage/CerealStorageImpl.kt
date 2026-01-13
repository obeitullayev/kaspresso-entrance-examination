package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    init {
        require(containerCapacity >= 0) {
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) {
            "Количество крупы не может быть отрицательным"
        }

        if (cereal !in storage) {
            val usedCapacity = storage.size * containerCapacity
            check(usedCapacity + containerCapacity <= storageCapacity) {
                "Хранилище не может вместить ещё один контейнер для новой крупы"
            }
        }

        return putToContainer(cereal, amount)
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) {
            "Количество крупы не может быть отрицательным"
        }

        val stored = storage[cereal] ?: return 0f
        return if (stored > amount) {
            storage[cereal] = stored - amount
            amount
        } else {
            storage[cereal] = 0f
            stored
        }
    }

    override fun removeContainer(cereal: Cereal): Boolean {
        val stored = storage[cereal] ?: return false
        return if (stored == 0f) {
            storage.remove(cereal)
            true
        } else {
            false
        }
    }

    override fun getAmount(cereal: Cereal): Float =
        storage[cereal] ?: 0f

    override fun getSpace(cereal: Cereal): Float =
        containerCapacity - (storage[cereal]
            ?: throw IllegalStateException(
                "Контейнер для указанной крупы отсутствует"
            ))

    override fun toString(): String {
        val content = if (storage.isEmpty()) {
            "empty"
        } else {
            storage.entries.joinToString { (cereal, amount) ->
                "${cereal.local}=$amount"
            }
        }

        return "CerealStorage(" +
                "containerCapacity=$containerCapacity, " +
                "storageCapacity=$storageCapacity, " +
                "storage=$content" +
                ")"
    }

    private fun putToContainer(
        cereal: Cereal,
        amount: Float
    ): Float {
        val stored = storage[cereal] ?: 0f
        val newAmount = stored + amount

        return if (newAmount <= containerCapacity) {
            storage[cereal] = newAmount
            0f
        } else {
            storage[cereal] = containerCapacity
            newAmount - containerCapacity
        }
    }
}

