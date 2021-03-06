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
package com.android.tools.idea.lint;

import com.android.tools.lint.checks.ObjectAnimatorDetector;
import com.android.tools.lint.detector.api.TextFormat;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.android.inspections.lint.AndroidLintInspectionBase;
import org.jetbrains.android.inspections.lint.AndroidLintQuickFix;
import org.jetbrains.android.inspections.lint.AndroidQuickfixContexts;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;

import static com.android.tools.lint.checks.ObjectAnimatorDetector.KEEP_ANNOTATION;

public class AndroidLintAnimatorKeepInspection extends AndroidLintInspectionBase {
  public AndroidLintAnimatorKeepInspection() {
    super(AndroidBundle.message("android.lint.inspections.animator.keep"), ObjectAnimatorDetector.MISSING_KEEP);
  }

  @NotNull
  @Override
  public AndroidLintQuickFix[] getQuickFixes(@NotNull PsiElement startElement, @NotNull PsiElement endElement, @NotNull String message) {
    return new AndroidLintQuickFix[]{
      new AndroidLintQuickFix() {
        @Override
        public void apply(@NotNull PsiElement startElement,
                          @NotNull PsiElement endElement,
                          @NotNull AndroidQuickfixContexts.Context context) {
          if (!ObjectAnimatorDetector.isAddKeepErrorMessage(message, TextFormat.RAW)) {
            return;
          }
          PsiModifierListOwner container = PsiTreeUtil.getParentOfType(startElement, PsiModifierListOwner.class);
          if (container == null) {
            return;
          }

          if (!FileModificationService.getInstance().preparePsiElementForWrite(container)) {
            return;
          }
          final PsiModifierList modifierList = container.getModifierList();
          if (modifierList != null) {
            PsiAnnotation annotation = AnnotationUtil.findAnnotation(container, KEEP_ANNOTATION);
            if (annotation == null) {
              Project project = startElement.getProject();
              new AddAnnotationFix(KEEP_ANNOTATION, container).invoke(project, null, container.getContainingFile());
            }
          }
        }

        @Override
        public boolean isApplicable(@NotNull PsiElement startElement,
                                    @NotNull PsiElement endElement,
                                    @NotNull AndroidQuickfixContexts.ContextType contextType) {
          return true;
        }

        @NotNull
        @Override
        public String getName() {
          return "Annotate with @Keep";
        }
      }
    };
  }
}
