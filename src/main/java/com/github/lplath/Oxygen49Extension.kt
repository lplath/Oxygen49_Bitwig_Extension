package com.github.lplath

import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.ControllerHost
import com.bitwig.extension.controller.api.CursorTrack
import com.bitwig.extension.controller.api.CursorDevice
import com.bitwig.extension.controller.api.CursorRemoteControlsPage
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback
import com.github.lplath.Mapping
import com.github.lplath.Midi

class Oxygen49Extension(definition: Oxygen49ExtensionDefinition, host: ControllerHost) :
    ControllerExtension(definition, host), ShortMidiDataReceivedCallback  {

    private lateinit var cursorTrack: CursorTrack
    private lateinit var cursorDevice: CursorDevice
    private lateinit var cursorRemoteControlsPage: CursorRemoteControlsPage
    
    override fun init() {
        host.println("[INFO] Running Oxygen49")

        val midiInPort = host.getMidiInPort(0)
        midiInPort.setMidiCallback(this)

        cursorTrack = host.createCursorTrack(0, 0)
        cursorDevice = cursorTrack.createCursorDevice()
        cursorRemoteControlsPage = cursorDevice.createCursorRemoteControlsPage(8)

        // Highlight the remote controls in the GUI
        for (i in 0..7) {
			cursorRemoteControlsPage.getParameter(i).apply {
				setIndication(true)
				setLabel("P ${i + 1}")
			}
		}
    }

    override fun midiReceived(status: Int, data1: Int, data2: Int) {
        if (Midi.isCC(status)) {
            if (data1 in Mapping.KNOBS_REMOTE) {
                cursorRemoteControlsPage.getParameter(Mapping.KNOBS_REMOTE.indexOf(data1)).value().set(data2, 128)
            }
        }
    }

    override fun exit() {}

    override fun flush() {}

}