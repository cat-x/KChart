package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage


interface IKMLine : IKLine, IMinuteLine {

    object ITimeStepConfig {

        var useMainOrMin: Boolean
            get() {
                return LStorage(CONFIG_TAG).getBoolean("useMainOrMin", true)
            }
            set(value) {
                LStorage(CONFIG_TAG).putBoolean("useMainOrMin", value)
            }

        var timeStep: Long
            get() {
                return LStorage(CONFIG_TAG).getLong("timeStep", 15)
            }
            set(value) {
                LStorage(CONFIG_TAG).putLong("timeStep", value)
            }
    }
}
