package core

class Inventory(val capacity: Int) {
    val items: MutableList<Item> = mutableListOf()
    private var selected: Int = -1

    fun addItem(item: Item): Boolean {
        return if (items.size >= capacity) {
            false
        } else {
            items.add(item)
        }
    }

    fun removeItem(itemNum: Int): Item? {
        return if (itemNum >= 0 && itemNum < items.size) {
            items.removeAt(itemNum)
        } else {
            null
        }
    }

    fun selectItem(num: Int) {
        if (num >= 0 && num < items.size) {
            selected = num
        }
    }

    fun unselectItem() {
        selected = -1
    }

    fun getSelectedItem(): Item? {
        synchronized(selected, {
            return if (selected == -1 || selected >= items.size) {
                null
            } else {
                items[selected]
            }
        })

    }


}
