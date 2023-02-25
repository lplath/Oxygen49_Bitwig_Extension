package com.github.lplath

import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.ControllerHost

class Oxygen49Extension(definition: Oxygen49ExtensionDefinition, host: ControllerHost) :
    ControllerExtension(definition, host) {

    override fun init() {
        host.println("Running Oxygen49")
    }

    override fun exit() {}

    override fun flush() {}

}