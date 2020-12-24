package animatedledstrip.test.colors

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.offsetBy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlin.test.assertTrue

class PreparedColorContainerTest : StringSpec(
    {
        "equality" {
            checkAll(100, Arb.list(Arb.int(0, 0xFFFFFF), 1..500)) { c ->
                checkAll(50, Arb.int(1..500)) { len ->
                    val testPCC = ColorContainer(c.toMutableList()).prepare(len)

                    checkAll(10, Arb.int(1..500).filter { it != len }) { len2 ->
                        testPCC shouldBe ColorContainer(c.toMutableList()).prepare(len2)
                        testPCC.hashCode() shouldNotBe ColorContainer(c.toMutableList()).prepare(len2).hashCode()
                    }
                }
            }
        }

        "get" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = PreparedColorContainer(listOf(c1, c2, c3, c4), listOf(c1, c2, c3, c4))

                testCC[0] shouldBe c1
                testCC[1] shouldBe c2
                testCC[2] shouldBe c3
                testCC[3] shouldBe c4
                testCC[5] shouldBe 0
                testCC.color shouldBe 0
            }
        }

        "string" {
            ColorContainer(0xFF3B82).prepare(1).toString() shouldBe "[ff3b82]"

            ColorContainer(0xFF7B50, 0xFFFFFF).prepare(2).toString() shouldBe "[ff7b50, ffffff]"
        }

        "offset by".config(enabled = false) {
            val c = ColorContainer(
                0x0000FF, 0x00FFFF, 0xFF00FF,
                0x00FF00, 0xFF0000, 0xFFFFFF,
            ).prepare(6)

            assertTrue {
                c.colors ==
                        listOf<Long>(
                            0x0000FF, 0x00FFFF, 0xFF00FF,
                            0x00FF00, 0xFF0000, 0xFFFFFF,
                        )
            }
            assertTrue {
                c.offsetBy(0).colors ==
                        listOf<Long>(
                            0x0000FF, 0x00FFFF, 0xFF00FF,
                            0x00FF00, 0xFF0000, 0xFFFFFF,
                        )
            }
            assertTrue {
                c.offsetBy(4).colors ==
                        listOf<Long>(
                            0xFF00FF, 0x00FF00, 0xFF0000,
                            0xFFFFFF, 0x0000FF, 0x00FFFF,
                        )
            }
            assertTrue {
                c.offsetBy(-4).colors ==
                        listOf<Long>(
                            0xFF0000, 0xFFFFFF, 0x0000FF,
                            0x00FFFF, 0xFF00FF, 0x00FF00,
                        )
            }
            assertTrue {
                c.offsetBy(10).colors ==
                        c.offsetBy(4).colors
            }
            assertTrue {
                c.offsetBy(-10).colors ==
                        c.offsetBy(-4).colors
            }
        }
    }
)
