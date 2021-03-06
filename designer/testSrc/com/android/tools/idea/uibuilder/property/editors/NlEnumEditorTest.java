/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.uibuilder.property.editors;

import com.android.tools.idea.uibuilder.property.PropertyTestCase;
import com.android.tools.idea.uibuilder.property.fixtures.NlEnumEditorFixture;

import static com.android.SdkConstants.*;
import static java.awt.event.KeyEvent.*;

public class NlEnumEditorTest extends PropertyTestCase {
  private NlEnumEditorFixture myFixture;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture = NlEnumEditorFixture.create();
  }

  @Override
  public void tearDown() throws Exception {
    try {
      myFixture.tearDown();
    }
    finally {
      super.tearDown();
    }
  }

  public void testEscapeRestoresOriginalAfterTyping() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .expectSelectedText(null)
      .gainFocus()
      .expectSelectedText("wrap_content")
      .type("5")
      .key(VK_ESCAPE)
      .verifyCancelEditingCalled()
      .expectValue("wrap_content")
      .expectSelectedText("wrap_content");
  }

  public void testEscapeRestoresOriginalSelecting() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .expectSelectedText(null)
      .gainFocus()
      .expectSelectedText("wrap_content")
      .showPopup()
      .expectChoices("none", null,
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .key(VK_UP)
      .expectText("match_parent")
      .key(VK_ESCAPE)
      .expectPopupVisible(false)
      .verifyCancelEditingCalled()
      .expectValue("wrap_content")
      .expectText("wrap_content")
      .expectSelectedText("wrap_content");
  }

  public void testFocusLoss() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .expectSelectedText(null)
      .gainFocus()
      .expectSelectedText("wrap_content")
      .type("match_parent")
      .expectValue("wrap_content")
      .expectSelectedText(null)
      .loseFocus()
      .expectSelectedText(null)
      .expectText("match_parent")
      .expectValue("match_parent");
  }

  public void testReplaceAddedValue() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .expectSelectedText(null)
      .gainFocus()
      .expectSelectedText("wrap_content")
      .type("80")
      .expectSelectedText(null)
      .key(VK_ENTER)
      .expectSelectedText("80dp")
      .type("60")
      .expectSelectedText(null)
      .key(VK_ENTER)
      .expectSelectedText("60dp")
      .loseFocus()
      .expectSelectedText(null)
      .expectText("60dp")
      .expectValue("60dp");
  }

  public void testSelectItemThroughModel() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .gainFocus()
      .setSelectedModelItem("60")
      .expectText("60dp")
      .expectValue("wrap_content")
      .loseFocus()
      .expectText("60dp")
      .expectValue("60dp");
  }

  public void testEnterSimpleDimension() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .expectSelectedText(null)
      .gainFocus()
      .expectSelectedText("wrap_content")
      .type("55")
      .key(VK_ENTER)
      .verifyStopEditingCalled()
      .expectValue("55dp")
      .expectSelectedText("55dp")
      .loseFocus()
      .expectValue("55dp");
  }

  public void testEnterDimensionFromResourceValue() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .gainFocus()
      .type("@android:dimen/notification_large_icon_width")
      .key(VK_ENTER)
      .expectValue("@android:dimen/notification_large_icon_width")
      .expectSelectedText("@android:dimen/notification_large_icon_width")
      .loseFocus()
      .expectText("64dp")
      .expectSelectedText(null)
      .gainFocus()
      .expectText("@android:dimen/notification_large_icon_width")
      .loseFocus()
      .expectText("64dp")
      .expectValue("@android:dimen/notification_large_icon_width");
  }

  public void testSelectFromDimensionList() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .gainFocus()
      .type("@android:dimen/notification_large_icon_width")
      .key(VK_ENTER)
      .expectValue("@android:dimen/notification_large_icon_width")
      .showPopup()
      .expectChoices("none", null,
                     "64dp", "@android:dimen/notification_large_icon_width",
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .expectText("@android:dimen/notification_large_icon_width")
      .key(VK_UP)
      .expectText("")
      .expectValue("@android:dimen/notification_large_icon_width")
      .key(VK_DOWN)
      .expectText("@android:dimen/notification_large_icon_width")
      .key(VK_DOWN)
      .expectText("match_parent")
      .expectValue("@android:dimen/notification_large_icon_width")
      .key(VK_DOWN)
      .expectText("wrap_content")
      .expectValue("@android:dimen/notification_large_icon_width")
      .key(VK_DOWN)
      .expectText("wrap_content")
      .expectValue("@android:dimen/notification_large_icon_width")
      .key(VK_UP)
      .expectPopupVisible(true)
      .key(VK_ENTER)
      .expectPopupVisible(false)
      .expectText("match_parent")
      .expectValue("match_parent");
  }

  public void testSelectFromRadioGroupChoices() {
    myFixture
      .setProperty(getProperty(myRadioGroup, ATTR_CHECKED_BUTTON))
      .expectSelectedText(null)
      .expectText("none")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("none", null,
                     "radio1", "@+id/radio1",
                     "radio2", "@+id/radio2")
      .key(VK_DOWN)
      .hidePopup()
      .expectText("@+id/radio1")
      .expectValue("@+id/radio1")
      .loseFocus()
      .expectText("radio1")
      .expectValue("@+id/radio1");
  }

  public void testEnterId() {
    myFixture
      .setProperty(getProperty(myRadioGroup, ATTR_CHECKED_BUTTON))
      .expectSelectedText(null)
      .expectText("none")
      .gainFocus()
      .type("radio2")
      .key(VK_ENTER)
      .expectSelectedText("@+id/radio2")
      .expectText("@+id/radio2")
      .expectValue("@+id/radio2")
      .loseFocus()
      .expectSelectedText(null)
      .expectText("radio2")
      .expectValue("@+id/radio2")
      .gainFocus()
      .type("textView")
      .key(VK_ENTER)
      .expectText("@+id/textView")
      .expectValue("@+id/textView")
      .loseFocus()
      .expectText("textView")
      .expectValue("@+id/textView")
      .gainFocus()
      .type("@id/textView")
      .key(VK_ENTER)
      .expectText("@id/textView")
      .expectValue("@id/textView")
      .loseFocus()
      .expectText("textView")
      .expectValue("@id/textView");
  }

  public void testSelectTextAppearance() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_TEXT_APPEARANCE, "?attr/textAppearanceSmall"))
      .expectSelectedText(null)
      .expectText("Small")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectFirstChoices(5,
                          "Small", null,
                          "TextAppearance", "@android:style/TextAppearance",
                          "AutoCorrectionSuggestion", "@android:style/TextAppearance.AutoCorrectionSuggestion",
                          "DeviceDefault", "@android:style/TextAppearance.DeviceDefault",
                          "DeviceDefault.DialogWindowTitle", "@android:style/TextAppearance.DeviceDefault.DialogWindowTitle")
      .key(VK_DOWN)
      .hidePopup()
      .expectText("@android:style/TextAppearance")
      .expectValue("@android:style/TextAppearance")
      .loseFocus()
      .expectText("TextAppearance")
      .expectValue("@android:style/TextAppearance");
  }

  public void testEnterTextAppearanceFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_TEXT_APPEARANCE, "?attr/textAppearanceSmall"))
      .expectSelectedText(null)
      .expectText("Small")
      .gainFocus()
      .expectText("")
      .type("Material.Small")
      .key(VK_ENTER)
      .expectText("@android:style/TextAppearance.Material.Small")
      .expectValue("@android:style/TextAppearance.Material.Small")
      .loseFocus()
      .expectText("Material.Small")
      .expectValue("@android:style/TextAppearance.Material.Small");
  }

  public void testEnterTextAppearanceNotFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_TEXT_APPEARANCE, "?attr/textAppearanceSmall"))
      .expectSelectedText(null)
      .expectText("Small")
      .gainFocus()
      .expectText("")
      .type("Widget.ActionButton")
      .key(VK_ENTER)
      .expectText("@android:style/Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton")
      .loseFocus()
      .expectText("Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton")
      .gainFocus()
      .type("Widget.AppCompat.ActionBar")
      .key(VK_ENTER)
      .loseFocus()
      .expectText("Widget.AppCompat.ActionBar")
      .expectValue("@style/Widget.AppCompat.ActionBar")
      .gainFocus()
      .type("@style/Widget.AppCompat.ActionButton")
      .key(VK_ENTER)
      .loseFocus()
      .expectText("Widget.AppCompat.ActionButton")
      .expectValue("@style/Widget.AppCompat.ActionButton")
      .gainFocus()
      .type("@android:style/Widget.ActionButton")
      .key(VK_ENTER)
      .loseFocus()
      .expectText("Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton");
  }

  public void testSelectTextAppearanceInSwitch() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_SWITCH_TEXT_APPEARANCE, "@style/TextAppearance.Material.Widget.Switch"))
      .expectSelectedText(null)
      .expectText("Material.Widget.Switch")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectFirstChoices(5,
                          "Material.Widget.Switch", null,
                          "TextAppearance", "@android:style/TextAppearance",
                          "AutoCorrectionSuggestion", "@android:style/TextAppearance.AutoCorrectionSuggestion",
                          "DeviceDefault", "@android:style/TextAppearance.DeviceDefault",
                          "DeviceDefault.DialogWindowTitle", "@android:style/TextAppearance.DeviceDefault.DialogWindowTitle")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("@android:style/TextAppearance.AutoCorrectionSuggestion")
      .expectValue("@android:style/TextAppearance.AutoCorrectionSuggestion")
      .loseFocus()
      .expectText("AutoCorrectionSuggestion")
      .expectValue("@android:style/TextAppearance.AutoCorrectionSuggestion");
  }

  public void testEnterTextAppearanceInSwitchFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_SWITCH_TEXT_APPEARANCE, "@style/TextAppearance.Material.Widget.Switch"))
      .expectSelectedText(null)
      .expectText("Material.Widget.Switch")
      .gainFocus()
      .expectText("")
      .type("Material.Small")
      .key(VK_ENTER)
      .expectText("@android:style/TextAppearance.Material.Small")
      .expectValue("@android:style/TextAppearance.Material.Small")
      .loseFocus()
      .expectText("Material.Small")
      .expectValue("@android:style/TextAppearance.Material.Small");
  }

  public void testEnterTextAppearanceInSwitchNotFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_SWITCH_TEXT_APPEARANCE, "@style/TextAppearance.Material.Widget.Switch"))
      .expectSelectedText(null)
      .expectText("Material.Widget.Switch")
      .gainFocus()
      .type("Widget.ActionButton")
      .key(VK_ENTER)
      .expectText("@android:style/Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton")
      .loseFocus()
      .expectText("Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton");
  }

  public void testSelectStyleInSwitch() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_STYLE, "?android:switchStyle"))
      .expectSelectedText(null)
      .expectText("Widget.CompoundButton.Switch")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("Widget.CompoundButton.Switch", null,
                     "Widget.CompoundButton.Switch", "@android:style/Widget.CompoundButton.Switch",
                     "Widget.Holo.Light.CompoundButton.Switch", "@android:style/Widget.Holo.Light.CompoundButton.Switch")
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("@android:style/Widget.CompoundButton.Switch")
      .expectValue("@android:style/Widget.CompoundButton.Switch")
      .loseFocus()
      .expectText("Widget.CompoundButton.Switch")
      .expectValue("@android:style/Widget.CompoundButton.Switch");
  }

  public void testEnterStyleInSwitchFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_STYLE, "?android:switchStyle"))
      .expectSelectedText(null)
      .expectText("Widget.CompoundButton.Switch")
      .gainFocus()
      .type("Widget.Holo.Light.CompoundButton.Switch")
      .key(VK_ENTER)
      .expectText("@android:style/Widget.Holo.Light.CompoundButton.Switch")
      .expectValue("@android:style/Widget.Holo.Light.CompoundButton.Switch")
      .loseFocus()
      .expectText("Widget.Holo.Light.CompoundButton.Switch")
      .expectValue("@android:style/Widget.Holo.Light.CompoundButton.Switch");
  }

  public void testEnterStyleInSwitchNotFoundInModel() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(mySwitch, ATTR_STYLE, "?android:switchStyle"))
      .expectSelectedText(null)
      .expectText("Widget.CompoundButton.Switch")
      .gainFocus()
      .type("Widget.ActionButton")
      .key(VK_ENTER)
      .expectText("@android:style/Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton")
      .loseFocus()
      .expectText("Widget.ActionButton")
      .expectValue("@android:style/Widget.ActionButton");
  }

  public void testSelectFontFamily() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_FONT_FAMILY, "@string/font_family_body_1_material"))
      .expectSelectedText(null)
      .expectText("sans-serif")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("sans-serif", null,
                     "sans-serif", "sans-serif",
                     "sans-serif-condensed", "sans-serif-condensed",
                     "serif", "serif",
                     "monospace", "monospace",
                     "serif-monospace", "serif-monospace",
                     "casual", "casual",
                     "cursive", "cursive",
                     "sans-serif-smallcaps", "sans-serif-smallcaps")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("serif")
      .expectValue("serif")
      .loseFocus()
      .expectText("serif")
      .expectValue("serif");
  }

  public void testSelectTypeface() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_TYPEFACE))
      .expectSelectedText(null)
      .expectText("none")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("none", null,
                     "normal", "normal",
                     "sans", "sans",
                     "serif", "serif",
                     "monospace", "monospace")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("sans")
      .expectValue("sans")
      .loseFocus()
      .expectText("sans")
      .expectValue("sans");
  }

  public void testSelectTextSize() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_TEXT_SIZE, "@dimen/text_size_small_material"))
      .expectSelectedText(null)
      .expectText("14sp")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("14sp", null,
                     "8sp", "8sp",
                     "10sp", "10sp",
                     "12sp", "12sp",
                     "14sp", "14sp",
                     "18sp", "18sp",
                     "24sp", "24sp",
                     "30sp", "30sp",
                     "36sp", "36sp")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("10sp")
      .expectValue("10sp")
      .loseFocus()
      .expectText("10sp")
      .expectValue("10sp");
  }

  public void testEnterTextSize() {
    myFixture
      .setProperty(getPropertyWithDefaultValue(myTextView, ATTR_TEXT_SIZE, "@dimen/text_size_small_material"))
      .expectSelectedText(null)
      .expectText("14sp")
      .gainFocus()
      .expectText("")
      .type("22")
      .key(VK_ENTER)
      .expectText("22sp")
      .expectValue("22sp")
      .loseFocus()
      .expectText("22sp")
      .expectValue("22sp")
      .gainFocus()
      .expectSelectedText("22sp")
      .key(VK_BACK_SPACE)
      .key(VK_ENTER)
      .expectText("")
      .expectValue(null)
      .loseFocus()
      .expectText("14sp")
      .expectValue(null);
  }

  public void testSelectLineSpacing() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LINE_SPACING_EXTRA))
      .expectSelectedText(null)
      .expectText("none")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("none", null,
                     "8sp", "8sp",
                     "10sp", "10sp",
                     "12sp", "12sp",
                     "14sp", "14sp",
                     "18sp", "18sp",
                     "24sp", "24sp",
                     "30sp", "30sp",
                     "36sp", "36sp")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .key(VK_ENTER)
      .expectText("10sp")
      .expectValue("10sp")
      .loseFocus()
      .expectText("10sp")
      .expectValue("10sp");
  }

  public void testEnterLineSpacing() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LINE_SPACING_EXTRA))
      .expectSelectedText(null)
      .expectText("none")
      .gainFocus()
      .expectText("")
      .type("4")
      .key(VK_ENTER)
      .expectText("4sp")
      .expectValue("4sp")
      .loseFocus()
      .expectText("4sp")
      .expectValue("4sp")
      .gainFocus()
      .expectSelectedText("4sp")
      .key(VK_BACK_SPACE)
      .key(VK_ENTER)
      .expectText("")
      .expectValue(null)
      .loseFocus()
      .expectText("none")
      .expectValue(null);
  }

  public void testSelectWidth() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_WIDTH))
      .expectText("wrap_content")
      .gainFocus()
      .showPopup()
      .expectChoices("none", null,
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .expectText("wrap_content")
      .key(VK_UP)
      .expectText("match_parent")
      .expectValue("wrap_content")
      .key(VK_ENTER)
      .expectText("match_parent")
      .expectValue("match_parent");
  }

  public void testSelectHeight() {
    myFixture
      .setProperty(getProperty(myTextView, ATTR_LAYOUT_HEIGHT))
      .expectText("wrap_content")
      .gainFocus()
      .showPopup()
      .expectChoices("none", null,
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .expectText("wrap_content")
      .key(VK_UP)
      .expectText("match_parent")
      .expectValue("wrap_content")
      .key(VK_ENTER)
      .expectText("match_parent")
      .expectValue("match_parent");
  }

  public void testSelectDropDownWidth() {
    myFixture
      .setProperty(getProperty(myAutoCompleteTextView, ATTR_DROPDOWN_WIDTH))
      .expectText("none")
      .gainFocus()
      .showPopup()
      .expectChoices("none", null,
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .expectText("")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .expectText("wrap_content")
      .expectValue(null)
      .key(VK_ENTER)
      .expectText("wrap_content")
      .expectValue("wrap_content");
  }

  public void testSelectDropDownHeight() {
    myFixture
      .setProperty(getProperty(myAutoCompleteTextView, ATTR_DROPDOWN_HEIGHT))
      .expectText("none")
      .gainFocus()
      .showPopup()
      .expectChoices("none", null,
                     "match_parent", "match_parent",
                     "wrap_content", "wrap_content")
      .expectText("")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .expectText("wrap_content")
      .expectValue(null)
      .key(VK_ENTER)
      .expectText("wrap_content")
      .expectValue("wrap_content");
  }

  public void testSelectOnClick() {
    myFixture
      .setProperty(getProperty(myButton, ATTR_ON_CLICK))
      .expectText("none")
      .gainFocus()
      .expectText("")
      .showPopup()
      .expectChoices("none", null)
      .key(VK_ENTER)
      .loseFocus()
      .expectText("none")
      .expectValue(null);
  }

  public void testEnterOnClick() {
    myFixture
      .setProperty(getProperty(myButton, ATTR_ON_CLICK))
      .expectText("none")
      .gainFocus()
      .expectText("")
      .type("sendEmail")
      .key(VK_ENTER)
      .loseFocus()
      .expectText("sendEmail")
      .expectValue("sendEmail");
  }

  public void testSelectVisibility() {
    myFixture
      .setProperty(getProperty(myButton, ATTR_VISIBILITY))
      .expectText("none")
      .gainFocus()
      .showPopup()
      .expectChoices("none", null,
                     "visible", "visible",
                     "invisible", "invisible",
                     "gone", "gone")
      .expectText("")
      .key(VK_DOWN)
      .key(VK_DOWN)
      .expectText("invisible")
      .expectValue(null)
      .key(VK_ENTER)
      .expectText("invisible")
      .expectValue("invisible");
  }
}
