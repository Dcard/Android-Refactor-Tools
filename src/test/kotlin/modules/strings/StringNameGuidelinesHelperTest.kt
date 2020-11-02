package modules.strings

import com.hiking.art.modules.strings.StringNameGuidelinesHelper
import org.junit.Assert
import org.junit.Test

class StringNameGuidelinesHelperTest {

    @Test
    fun fixSeparator_fix() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar_bar_title")
        Assert.assertEquals("foo.bar_bar.title", result)
    }

    @Test
    fun fixSeparator_fixFormat() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar_bar_message_format")
        Assert.assertEquals("foo.bar_bar.message_format", result)
    }

    @Test
    fun fixSeparator_fixNumber() {
        val result = StringNameGuidelinesHelper.autoFix("ec.add_711_action")
        Assert.assertEquals("ec.add_711.action", result)
    }

    @Test
    fun fixSeparator_fixMainUnderline() {
        val result = StringNameGuidelinesHelper.autoFix("foo_title")
        Assert.assertEquals("foo.main.title", result)
    }

    @Test
    fun fixSeparator_fixMainDot() {
        val result = StringNameGuidelinesHelper.autoFix("foo.title")
        Assert.assertEquals("foo.main.title", result)
    }

    @Test
    fun fixSeparator_alreadyLegal() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar_bar.title")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixUnderline() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar_bar_baz")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixDot() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar_bar.baz")
        Assert.assertNull(result)
    }

    @Test
    fun fixSeparator_nonFixableIllegalSuffixMainDot() {
        val result = StringNameGuidelinesHelper.autoFix("foo.bar")
        Assert.assertNull(result)
    }
}