package modules.strings

import com.hiking.art.modules.strings.StringNameGuidelines
import org.junit.Assert
import org.junit.Test

class StringNameGuidelinesTest {

    @Test
    fun fixSeparator_fix() {
        val result = StringNameGuidelines.autoFix("foo.bar_bar_title")
        Assert.assertEquals("foo.bar_bar.title", result)
    }

    @Test
    fun fixSeparator_fixFormat() {
        val result = StringNameGuidelines.autoFix("foo.bar_bar_message_format")
        Assert.assertEquals("foo.bar_bar.message_format", result)
    }

    @Test
    fun fixSeparator_fixNumber() {
        val result = StringNameGuidelines.autoFix("ec.add_711_action")
        Assert.assertEquals("ec.add_711.action", result)
    }

    @Test
    fun fixSeparator_fixMainUnderline() {
        val result = StringNameGuidelines.autoFix("foo_title")
        Assert.assertEquals("foo.main.title", result)
    }

    @Test
    fun fixSeparator_fixMainDot() {
        val result = StringNameGuidelines.autoFix("foo.title")
        Assert.assertEquals("foo.main.title", result)
    }

    @Test
    fun fixSeparator_alreadyLegal() {
        val result = StringNameGuidelines.autoFix("foo.bar_bar.title")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixUnderline() {
        val result = StringNameGuidelines.autoFix("foo.bar_bar_baz")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixDot() {
        val result = StringNameGuidelines.autoFix("foo.bar_bar.baz")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixMainDot() {
        val result = StringNameGuidelines.autoFix("foo.bar")
        Assert.assertNull(result)
    }
}