/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.ktSignature;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.analyzer.AnalyzeExhaust;
import org.jetbrains.jet.lang.descriptors.ClassDescriptor;
import org.jetbrains.jet.lang.psi.JetElement;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.DescriptorUtils;
import org.jetbrains.jet.lang.resolve.java.JavaDescriptorResolver;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaClassImpl;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.plugin.JetFileType;
import org.jetbrains.jet.plugin.JetLightProjectDescriptor;
import org.jetbrains.jet.plugin.caches.resolve.JavaResolveExtension;
import org.jetbrains.jet.plugin.caches.resolve.ResolvePackage;

import static org.jetbrains.jet.lang.resolve.ResolvePackage.resolveTopLevelClass;

public class SignatureMarkerProviderTest extends LightCodeInsightFixtureTestCase {

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JetLightProjectDescriptor.INSTANCE;
    }

    public void testReResolveJavaClass() {
        Project project = myFixture.getProject();

        PsiFile file = myFixture.configureByText(JetFileType.INSTANCE, "val t: Thread? = null");
        ResolvePackage.getAnalysisResults((JetFile) file);

        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass("java.lang.Thread", GlobalSearchScope.allScope(project));
        assert psiClass != null;
        JavaDescriptorResolver resolver = JavaResolveExtension.INSTANCE$.get(project).invoke(psiClass);
        ClassDescriptor preResolvedClass = resolver.resolveClass(new JavaClassImpl(psiClass));

        assert preResolvedClass != null;
        ClassDescriptor reResolvedClass = resolveTopLevelClass(DescriptorUtils.getContainingModule(preResolvedClass), new FqName("java.lang.Thread"));

        assertSame(preResolvedClass, reResolvedClass);
    }
}
