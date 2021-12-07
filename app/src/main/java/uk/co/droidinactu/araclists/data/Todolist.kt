package uk.co.droidinactu.araclists.data

import android.media.Image

class Todolist private constructor(
    val name: String?,
    val description: String?,
    val icon: Image? = null,
    val items: MutableList<TodoItem> = mutableListOf<TodoItem>()
) {
    data class Builder(
        var name: String="New List",
        var description: String = "",
        var icon: Image? = null,
        var items: MutableList<TodoItem> = mutableListOf<TodoItem>()
    ) {
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun icon(icon: Image) = apply { this.icon = icon }
        fun item(value: TodoItem) = apply { this.items.add(value) }
        fun items(items: MutableList<TodoItem>) = apply { this.items = items }
        fun build() = Todolist(name = name, description = description, items = items, icon = icon)
    }
}
