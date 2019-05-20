package com.example.systemious.ui.system_details.entities

data class SystemParameter(var parameterName: String = "",
                           var parameterValue: String = "",
                           var parameterType: ParameterTypes = ParameterTypes.DEVICE)

enum class ParameterTypes{
    DEVICE, OS, CPU, MEMORY, SENSORS
}