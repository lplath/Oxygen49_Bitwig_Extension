package com.github.lplath

/**
 * https://www.midi.org/specifications-old/item/table-2-expanded-messages-list-status-bytes
 */
object Midi {
	/** Checks if a  message is a 'Control/Mode Change' on any channel */
	fun isCC(status: Int): Boolean = status and 0xB0 == 0xB0
	/** Checks if a  message is a 'Program Change' on any channel */
	fun isProgramChange(status: Int): Boolean = status and 0xC0 == 0xC0
	fun isNoteUp(status: Int): Boolean = status and 0x80 == 0x80
	fun isNoteDown(status: Int): Boolean = status and 0x90 == 0x90
}