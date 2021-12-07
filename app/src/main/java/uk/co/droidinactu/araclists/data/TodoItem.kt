package uk.co.droidinactu.araclists.data

import android.media.Image
import java.time.LocalDateTime

class TodoItem private constructor(
    val name: String?,
    val description: String?,
    val icon: Image?,
    val priority: Int?,
    val dueDate: LocalDateTime?,
    val createdDate: LocalDateTime?,
    val modifiedDate: LocalDateTime?,
    val items: MutableList<TodoItem> = mutableListOf<TodoItem>()
) {
    data class Builder(
        var name: String="New Item",
        var description: String = "",
        var priority: Int = 5,
        var dueDate: LocalDateTime = LocalDateTime.now(),
        var createdDate: LocalDateTime = LocalDateTime.now(),
        var modifiedDate: LocalDateTime = LocalDateTime.now(),
        var icon: Image? = null,
        var items: MutableList<TodoItem> = mutableListOf<TodoItem>()
    ) {
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun icon(icon: Image) = apply { this.icon = icon }
        fun item(value: TodoItem) = apply { this.items.add(value) }
        fun items(items: MutableList<TodoItem>) = apply { this.items = items }
        fun build() = TodoItem(
            name = name,
            description = description,
            items = items,
            icon = icon,
            priority = priority,
            dueDate = dueDate,
            createdDate = createdDate,
            modifiedDate = modifiedDate
        )
    }
}
