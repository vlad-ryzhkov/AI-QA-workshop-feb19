# API Tests: Code Examples

## Models
```kotlin
@JsonNaming(SnakeCaseStrategy::class)
data class CreateReq(val name: String, val price: Double)

@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemResp(val id: String, val name: String, val status: String)
```

## Config
```kotlin
object Endpoints { const val ITEMS = "/api/v1/items" }
object Msgs { const val SUCCESS = "Should succeed" }
```

## Requests
```kotlin
class CreateItemReq(body: CreateReq, token: String) : ApiRequestBaseJson<ItemResp>(ItemResp::class.java) {
    init {
        url = Config.baseUrl + Endpoints.ITEMS
        this.body = body
        headers["Authorization"] = "Bearer $token"
    }
}

class GetItemReq(id: String) : ApiRequestBaseJson<ItemResp>(ItemResp::class.java) {
    init {
        url = Config.baseUrl + "${Endpoints.ITEMS}/$id"
    }
}
```

## Helpers
```kotlin
object ItemHelper {
    @Step("Create Item")
    fun create(token: String): String {
        val r = apiClient.execute { CreateItemReq(TestData.valid(), token) }
        assertEquals(201, r.code)
        return r.body.id
    }
}
```

## Tests
```kotlin
@Epic("Items")
class ItemTests : TestBase() {
    @Test @Severity(BLOCKER) @DisplayName("201: Create & Verify")
    fun createItem() {
        val req = TestData.valid()
        val resp = apiClient.execute { CreateItemReq(req, token) }

        assertEquals(201, resp.code, Msgs.SUCCESS)
        assertEquals(req.name, resp.body.name)
        assertNotNull(resp.body.id)
    }

    @Test @Severity(NORMAL) @DisplayName("400: Invalid Data")
    fun validationError() {
        val resp = apiClient.execute { CreateItemReq(TestData.invalid(), token) }
        assertEquals(400, resp.code)
    }
}
```
