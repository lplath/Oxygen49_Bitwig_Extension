package com.github.lplath

import com.bitwig.extension.api.PlatformType
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList
import com.bitwig.extension.controller.ControllerExtensionDefinition
import com.bitwig.extension.controller.api.ControllerHost
import java.util.*

class Oxygen49ExtensionDefinition : ControllerExtensionDefinition() {
	override fun getName() = "Oxygen 49"
	override fun getAuthor() = "lplath"
	override fun getVersion() = "0.1"
	override fun getId(): UUID = UUID.fromString("e71d6d9c-e64f-405a-8ab0-68c18ae4c887")
	override fun getHardwareVendor() = "M-Audio"
	override fun getHardwareModel() = "Oxygen 49"
	override fun getRequiredAPIVersion() = 10
	override fun getNumMidiInPorts() = 1
	override fun getNumMidiOutPorts() = 1
	override fun listAutoDetectionMidiPortNames(list: AutoDetectionMidiPortNamesList, platformType: PlatformType) {}
	override fun createInstance(host: ControllerHost): Oxygen49Extension = Oxygen49Extension(this, host)
}