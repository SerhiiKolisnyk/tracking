
package com.kolisnyk.serhii.kn313.option1.tracking.data
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import java.util.UUID

@Entity(tableName = "tasks", primaryKeys = arrayOf("id"))
data class Task
(var title: String,
 var description: String,
 var id: String,
 var isCompleted: Boolean) {
    @Ignore constructor(title: String, description: String ) : this(title, description, UUID.randomUUID().toString(), false)
    @Ignore constructor(title: String, description: String, isCompleted: Boolean ) : this(title, description, UUID.randomUUID().toString(), isCompleted)
    @Ignore constructor(title: String, description: String, id: String? ) : this(title, description, id?: UUID.randomUUID().toString(), false)


    val titleForList: String?
        @Ignore
        get() {
            if (!title.isNullOrEmpty()) {
                return title
            } else {
                return description
            }
        }

    val isActive: Boolean
        @Ignore
        get() = !isCompleted

    val isEmpty: Boolean
        @Ignore
        get() = title.isNullOrEmpty() && description.isNullOrEmpty()

}