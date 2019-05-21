
@Entity(primaryKeys = ["id"])
open class Post(
    @field:SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @field:SerializedName("userId")
    var userId: String = UUID.randomUUID().toString(),

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("location")
    var location: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("createdAt")
    var createdAt: Long = System.currentTimeMillis(),

    @field:SerializedName("updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
): Serializable