package registration.helpers

import io.qameta.allure.Step
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import registration.requests.RegisterRequest
import registration.requests.RegistrationClient

object RegistrationHelper {

    @Step("Создать пользователя и вернуть verification_token")
    suspend fun createUser(request: RegisterRequest): HttpResponse =
        RegistrationClient.register(request)

    @Step("Проверить отсутствие записи в БД для email={email}")
    fun verifyUserNotInDb(email: String) {
        println("[DB-CHECK] Verify no user with email=$email in DB (stub: implement via DB client)")
    }

    @Step("Проверить наличие записи в БД для email={email} со статусом PENDING")
    fun verifyUserInDb(email: String) {
        println("[DB-CHECK] Verify user with email=$email has status=PENDING in DB (stub: implement via DB client)")
    }

    @Step("Проверить отсутствие дублирующей записи в БД для email={email}")
    fun verifyNoDuplicateInDb(email: String) {
        println("[DB-CHECK] Verify only one record for email=$email in DB (stub: implement via DB client)")
    }

    @Step("Удалить пользователя из БД для email={email}")
    fun deleteUserFromDb(email: String) {
        println("[CLEANUP] Delete user with email=$email from DB (stub: implement via DB client)")
    }

    @Step("Проверить маскировку пароля в логах приложения")
    fun verifyPasswordMaskedInLogs(password: String) {
        println("[LOG-CHECK] Verify password=$password is masked in application logs (stub: implement via log reader)")
    }
}
