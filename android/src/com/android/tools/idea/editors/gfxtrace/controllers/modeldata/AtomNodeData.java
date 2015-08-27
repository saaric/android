/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.tools.idea.editors.gfxtrace.controllers.modeldata;

import com.android.tools.idea.editors.gfxtrace.service.atom.Atom;
import com.android.tools.rpclib.schema.Render;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

public class AtomNodeData implements Render.ToComponent {
  public final long index;
  public final Atom atom;

  public AtomNodeData(long index, Atom atom) {
    this.index = index;
    this.atom = atom;
  }

  @Override
  public void render(@NotNull SimpleColoredComponent component, SimpleTextAttributes defaultAttributes) {
    component.append(Long.toString(index) + "   ", SimpleTextAttributes.REGULAR_ATTRIBUTES);
    atom.render(component, SimpleTextAttributes.SYNTHETIC_ATTRIBUTES);
  }
}