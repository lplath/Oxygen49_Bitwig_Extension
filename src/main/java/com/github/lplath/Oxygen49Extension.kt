package com.github.lplath

import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.ControllerHost
import com.bitwig.extension.controller.api.MidiOut
import com.bitwig.extension.controller.api.MidiIn
import com.bitwig.extension.controller.api.Transport
import com.bitwig.extension.controller.api.TrackBank
import com.bitwig.extension.controller.api.CursorTrack
import com.bitwig.extension.controller.api.MasterTrack
import com.bitwig.extension.controller.api.CursorDevice
import com.bitwig.extension.controller.api.CursorRemoteControlsPage
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback

import com.github.lplath.Mapping
import com.github.lplath.Midi
import com.github.lplath.Memory

class Oxygen49Extension(definition: Oxygen49ExtensionDefinition, host: ControllerHost) :
    ControllerExtension(definition, host), ShortMidiDataReceivedCallback {

    private lateinit var midiOutPort: MidiOut
    private lateinit var midiInPort: MidiIn

    private lateinit var transport: Transport
    private lateinit var trackBank: TrackBank
    private lateinit var masterTrack: MasterTrack
    private lateinit var cursorTrack: CursorTrack
    private lateinit var cursorDevice: CursorDevice
    private lateinit var cursorRemoteControlsPage: CursorRemoteControlsPage

    private var isShiftPressed = false
    
    override fun init() {
        host.println("[INFO] Running Oxygen49")

        midiInPort = host.getMidiInPort(0)
        midiOutPort = host.getMidiOutPort(0)

        midiInPort.setMidiCallback(this)
        midiInPort.createNoteInput("Keyboard")

        transport = host.createTransport()
        trackBank = host.createTrackBank(8, 0, 0)
        masterTrack = host.createMasterTrack(0)
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

        host.preferences.getSignalSetting("Memory", "Device (Slot 10)", "Reset").addSignalObserver() {
            this.writeDeviceMemory()
        }
    }

    override fun midiReceived(status: Int, data1: Int, data2: Int) {
        //host.println("[MIDI] $status $data1 $data2")

        if (Midi.isCC(status)) {
            when (data1) {
                Mapping.SHIFT -> isShiftPressed = if (data2 == 0) false else true

                Mapping.PREV_TRACK -> if (data2 == 0) {
                    if (isShiftPressed) cursorRemoteControlsPage.selectPreviousPage(false) 
                    else cursorTrack.selectPrevious()
                }

                Mapping.NEXT_TRACK -> if (data2 == 0) {
                    if (isShiftPressed) cursorRemoteControlsPage.selectNextPage(false) 
                    else cursorTrack.selectNext()
                }

                Mapping.LOOP -> if (data2 == 0) transport.isArrangerLoopEnabled().toggle()
                
                Mapping.REWIND -> if (data2 == 0) {
                    if (isShiftPressed) transport.incPosition(-1.0, true) 
                    else transport.rewind()
                }
                Mapping.FORWARD -> if (data2 == 0) {
                    if (isShiftPressed) transport.incPosition(1.0, true) 
                    else transport.fastForward()
                }
                Mapping.STOP -> if (data2 == 0) transport.stop()

                Mapping.PLAY -> if (data2 == 0) {
                    if (isShiftPressed) transport.isMetronomeEnabled().toggle() 
                    else transport.play()
                }
                Mapping.RECORD -> if (data2 == 0) {
                    if (isShiftPressed) transport.isArrangerOverdubEnabled().toggle() 
                    else transport.record()
                }

                Mapping.MASTER_FADER -> masterTrack.volume().value().set(data2, 128)

                in Mapping.BUTTONS ->  if (data2 == 0) {
                    if (isShiftPressed) trackBank.getItemAt(Mapping.BUTTONS.indexOf(data1)).mute().toggle()
                    else trackBank.getItemAt(Mapping.BUTTONS.indexOf(data1)).selectInMixer()          
                }

                in Mapping.FADERS -> trackBank.getItemAt(Mapping.FADERS.indexOf(data1)).volume().value().set(data2, 128)

                in Mapping.KNOBS_REMOTE -> cursorRemoteControlsPage.getParameter(Mapping.KNOBS_REMOTE.indexOf(data1)).value().set(data2, 128)

                else -> host.println("[WARN] Unknown CC $data1 $data2")
            }
        } else if (Midi.isProgramChange(status)) {
            //TODO: Handle +/- Buttons
        }
    }

    /** reset slot 10 of the device presets to the factory settings. Usefull if the user has already saved presets and doesnt want to do a full
    /   factory reset */
    fun writeDeviceMemory() {
        host.println("[SYS] Beginning to write to device memory")
        for (message in Memory) {
            midiOutPort.sendSysex(message)
        }
        host.println("[SYS] Done!")
    }

    override fun exit() = host.println("[INFO] Exiting Oxygen49")

    override fun flush() {}

}