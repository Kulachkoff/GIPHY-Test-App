/**
 * Simple semantic versioning implementation
 *
 * It is designed to support release annotations such as -SNAPSHOT or -RC1..98 to differentiate
 * between final version (i.e. stable) release from the release candidates
 *
 * Suffix explanation:
 * SNAPSHOT = 0
 * RC1..98 = 1..98 (maximum of 98 release candidates per version)
 * No suffix (final release) = 99
 *
 * Version code result examples:
 * v1.0.0-SHAPSHOT = 1000000,
 * v1.0.0-RC1 = 1000001,
 * v1.0.0 = 1000099,
 * v1.0.1-RC5 = 1000105,
 * etc.
 * @return
 */

@file:Suppress("MagicNumber")

object Versioning {
    fun buildVersionCode(versionStr: String): Int {
        val normalized = versionStr.lowercase().replace("-", "")
        val parts = normalized.split(".")
        require(parts.size >= 3) { "Version must be MAJOR.MINOR.PATCH, got '$versionStr'" }

        val major = parts[0].toInt()
        val minor = parts[1].toInt()

        var patchToken = parts[2]
        var candidate = 99

        if (patchToken.endsWith("snapshot")) {
            candidate = 0
            patchToken = patchToken.filter { it.isDigit() }
        } else {
            val rcIdx = patchToken.indexOf("rc")
            if (rcIdx != -1) {
                val rcNum = patchToken.substring(rcIdx + 2)
                if (rcNum.isNotEmpty()) candidate = rcNum.toInt()
                patchToken = patchToken.substring(0, rcIdx)
            }
        }

        val patch = patchToken.toInt()
        return major * 1_000_000 + minor * 10_000 + patch * 100 + candidate
    }
}