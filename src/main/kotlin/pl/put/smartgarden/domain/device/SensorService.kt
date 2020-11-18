package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.device.exception.SensorInAnotherDeviceException
import pl.put.smartgarden.domain.device.repository.SensorRepository
import kotlin.collections.ArrayList

@Service
class SensorService(val sensorRepository: SensorRepository) {
    fun createSensors(deviceId: Int, sensorsRequest: List<SensorRequest>): List<Sensor> {
        val sensorsInDb = sensorRepository.findAllByDeviceId(deviceId)
        val requestGuids = sensorsRequest.map { it.guid }
        checkIfSensorsAreUsedInAnotherDevice(requestGuids, deviceId)

        val inactiveSensors = inactivateNotUsedSensors(sensorsInDb, requestGuids)
        val activeSensors = selectActiveSensors(sensorsInDb, requestGuids)
        val newSensors = createNewSensors(sensorsInDb, sensorsRequest, deviceId)

       return concatenateLists(inactiveSensors, activeSensors, newSensors)
    }

    private fun selectActiveSensors(sensorsInDb: List<Sensor>, requestGuids: List<String>) =
        sensorsInDb.filter { requestGuids.contains(it.guid) && it.isActive }

    private fun createNewSensors(sensorsInDb: List<Sensor>, sensors: List<SensorRequest>, deviceId: Int): List<Sensor> {
        val dbGuids: List<String> = sensorsInDb.map { it.guid }
        val newSensors = sensors.filter { !dbGuids.contains(it.guid) }.map { Sensor(it.type, it.guid, deviceId) }
        newSensors.forEach { sensorRepository.save(it) }
        return newSensors
    }

    private fun inactivateNotUsedSensors(sensorsInDb: List<Sensor>, requestGuids: List<String>): List<Sensor> {
        val inactiveSensors = sensorsInDb.filter { !requestGuids.contains(it.guid) && it.isActive }
        inactiveSensors.forEach { it.isActive = false; sensorRepository.save(it) }
        return inactiveSensors
    }

    private fun checkIfSensorsAreUsedInAnotherDevice(requestGuids: List<String>, deviceId: Int) {
        if (sensorRepository.findAllByGuidIn(requestGuids).filter { it.deviceId != deviceId }.count() > 0) {
            throw SensorInAnotherDeviceException()
        }
    }

    private fun concatenateLists(vararg lists: List<Sensor>): List<Sensor> = listOf(*lists).flatten()
}
