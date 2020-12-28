/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.communication

import kotlinx.serialization.Serializable

/**
 * Settings for a server-client connection.
 *
 * This should be sent to the server by a client to initialize the connection.
 *
 *
 * @property sendDefinedAnimationInfoOnConnection Should the server send information about all supported animations
 * to the client when this is received?
 * @property sendRunningAnimationInfoOnConnection Should the server send information about all currently running
 * animations to the client when this is received?
 * @property sendSectionInfoOnConnection Should the server send information about all currently defined strip sections
 * to the client when this is received?
 * @property sendStripInfoOnConnection Should the server send information about the strip to the client when this is
 * received?
 * @property sendAnimationStart Should the server send a message whenever a new animation is started?
 * (See [MessageFrequency])
 * @property sendAnimationEnd Should the server send a message whenever an animation is stopped?
 * (See [MessageFrequency])
 * @property sendSectionCreation Should the server send a message whenever a new strip section is created?
 * (See [MessageFrequency])
 * @property sendLogs Should the server send log outputs to the client?
 */
@Serializable
data class ClientParams(
    var sendDefinedAnimationInfoOnConnection: Boolean = true,
    var sendRunningAnimationInfoOnConnection: Boolean = true,
    var sendSectionInfoOnConnection: Boolean = true,
    var sendStripInfoOnConnection: Boolean = true,
    var sendAnimationStart: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendAnimationEnd: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendSectionCreation: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendLogs: Boolean = false,
) : SendableData {

    override fun toHumanReadableString(): String  = toString()
}
