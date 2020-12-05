package pl.put.smartgarden.domain.user.dto.response

data class UserGeneralSettingsResponse(
    val username : String,
    val email : String,
    val deviceGuid : String?,
    val latitude : Double?,
    val longitude : Double?
)