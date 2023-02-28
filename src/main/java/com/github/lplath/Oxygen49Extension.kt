package com.github.lplath

import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.ControllerHost
import com.bitwig.extension.controller.api.Transport
import com.bitwig.extension.controller.api.TrackBank
import com.bitwig.extension.controller.api.CursorTrack
import com.bitwig.extension.controller.api.CursorDevice
import com.bitwig.extension.controller.api.CursorRemoteControlsPage
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback
import com.github.lplath.Mapping
import com.github.lplath.Midi

class Oxygen49Extension(definition: Oxygen49ExtensionDefinition, host: ControllerHost) :
    ControllerExtension(definition, host), ShortMidiDataReceivedCallback  {

    private lateinit var transport: Transport
    private lateinit var trackBank: TrackBank
    private lateinit var cursorTrack: CursorTrack
    private lateinit var cursorDevice: CursorDevice
    private lateinit var cursorRemoteControlsPage: CursorRemoteControlsPage
    
    override fun init() {
        host.println("[INFO] Running Oxygen49")

        val midiInPort = host.getMidiInPort(0)
        midiInPort.setMidiCallback(this)

        transport = host.createTransport()
        trackBank = host.createTrackBank(8, 0, 0)
        cursorTrack = host.createCursorTrack(0, 0)
        trackBank.followCursorTrack(cursorTrack)
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
            when (data1) {
                in Mapping.KNOBS_REMOTE -> cursorRemoteControlsPage.getParameter(Mapping.KNOBS_REMOTE.indexOf(data1)).value().set(data2, 128)
                Mapping.PREV_TRACK -> if (data2 != 0) cursorTrack.selectPrevious()
                Mapping.NEXT_TRACK -> if (data2 != 0) cursorTrack.selectNext()
                Mapping.LOOP -> if (data2 != 0) transport.isArrangerLoopEnabled().toggle()
                Mapping.REWIND -> if (data2 != 0) transport.rewind()
                Mapping.FORWARD -> if (data2 != 0) transport.fastForward()
                Mapping.STOP -> if (data2 != 0) transport.stop()
                Mapping.PLAY -> if (data2 != 0) transport.play()
                Mapping.RECORD -> if (data2 != 0) transport.record()
                // TODO: Why does the keyboard only sometimes send the correct midi notes?
                //in Mapping.BUTTONS -> trackBank.getItemAt(Mapping.BUTTONS.indexOf(data1)).select()
                in Mapping.FADERS -> trackBank.getItemAt(Mapping.FADERS.indexOf(data1)).volume().value().set(data2, 128)
            }
        }
    }

    override fun exit() {
        host.println("[INFO] Exiting Oxygen49")
    }

    override fun flush() {}

}