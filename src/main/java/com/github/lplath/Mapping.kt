package com.github.lplath

object Mapping {
    const val PREV_TRACK = 110
	const val NEXT_TRACK = 111
	const val LOOP = 113
	const val REWIND = 114
	const val FORWARD = 115
	const val STOP = 116
	const val PLAY = 117
	const val RECORD = 118
	const val SLIDER = 74
    val KNOBS_REMOTE = listOf(75, 76, 77, 78, 79, 92, 95, 10)
	val FADERS = listOf(74, 71, 91, 93, 73, 72, 5, 84)
	val BUTTONS = listOf(49, 50, 51, 52, 53, 54, 55, 56)
	const val MASTER_FADER = 7
	const val MASTER_BUTTON = 57
}