package pro.udax.app.network

/**
 * Created by Cat-x on 2018/12/24.
 * For KChart
 * Cat-x All Rights Reserved
 */
object RequestHandle {
    fun getHeads(): Map<String, String> {
        val heads = HashMap<String, String>()
        if (false) {
            heads["UUID"] =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzYWx0IjoiTlBVa1pTaHVUY0JlRU9Ka3JCZ1MiLCJpZCI6IjEwMjE3IiwiaWF0IjoxNTQ1NjMxODE5fQ.uqypaqsM6JJr1YaU2UY0YtsEj8xZ_-FcYwRHthLb5VY"
        }
        heads["locale"] = "zh"
        return heads
    }
}