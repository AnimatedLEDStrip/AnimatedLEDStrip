/*
 *  Copyright (c) 2020 AnimatedLEDStrip
 *  Copyright (c) 2013 FastLED
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

package animatedledstrip.colors.ccpresets

import animatedledstrip.colors.ColorContainer

/*
 * Presets taken from FastLED color palettes
 */


object CCRainbowColors : ColorContainer(
    0xFF0000,
    0xD52A00,
    0xAB5500,
    0xAB7F00,
    0xABAB00,
    0x56D500,
    0x00FF00,
    0x00D52A,
    0x00AB55,
    0x0056AA,
    0x0000FF,
    0x2A00D5,
    0x5500AB,
    0x7F0081,
    0xAB0055,
    0xD5002B,
)

object CCCloudColors : ColorContainer(
    CCBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCDarkBlue.color,
    CCBlue.color,
    CCDarkBlue.color,
    CCSkyBlue.color,
    CCSkyBlue.color,
    CCLightBlue.color,
    CCWhite.color,
    CCLightBlue.color,
    CCSkyBlue.color,
)

object CCLavaColors : ColorContainer(
    CCBlack.color,
    CCMaroon.color,
    CCBlack.color,
    CCMaroon.color,
    CCDarkRed.color,
    CCMaroon.color,
    CCDarkRed.color,
    CCDarkRed.color,
    CCDarkRed.color,
    CCRed.color,
    CCOrange.color,
    CCWhite.color,
    CCOrange.color,
    CCRed.color,
    CCDarkRed.color,
)

object CCOceanColors : ColorContainer(
    CCMidnightBlue.color,
    CCDarkBlue.color,
    CCMidnightBlue.color,
    CCNavy.color,
    CCDarkBlue.color,
    CCMediumBlue.color,
    CCSeaGreen.color,
    CCTeal.color,
    CCCadetBlue.color,
    CCBlue.color,
    CCDarkCyan.color,
    CCCornflowerBlue.color,
    CCAquamarine.color,
    CCSeaGreen.color,
    CCAqua.color,
    CCLightSkyBlue.color,
)

object CCForestColors : ColorContainer(
    CCDarkGreen.color,
    CCDarkGreen.color,
    CCDarkOliveGreen.color,
    CCDarkGreen.color,
    CCGreen.color,
    CCForestGreen.color,
    CCOliveDrab.color,
    CCGreen.color,
    CCSeaGreen.color,
    CCMediumAquamarine.color,
    CCLimeGreen.color,
    CCLawnGreen.color,
    CCMediumAquamarine.color,
    CCForestGreen.color,
)

object CCRainbowStripesColors : ColorContainer(
    0xFF0000,
    0x000000,
    0xAB5500,
    0x000000,
    0xABAB00,
    0x000000,
    0x00FF00,
    0x000000,
    0x00AB55,
    0x000000,
    0x0000FF,
    0x000000,
    0x5500AB,
    0x000000,
    0xAB0055,
    0x000000,
)

object CCPartyColors : ColorContainer(
    0x5500AB,
    0x84007C,
    0xB5004B,
    0xE5001B,
    0xE81700,
    0xB84700,
    0xAB7700,
    0xABAB00,
    0xAB5500,
    0xDD2200,
    0xF2000E,
    0xC2003E,
    0x8F0071,
    0x5F00A1,
    0x2F00D0,
    0x0007F9,
)

val CCGroupPresets = listOf(
    CCRainbowColors,
    CCCloudColors,
    CCLavaColors,
    CCOceanColors,
    CCForestColors,
    CCRainbowStripesColors,
    CCPartyColors,
)

