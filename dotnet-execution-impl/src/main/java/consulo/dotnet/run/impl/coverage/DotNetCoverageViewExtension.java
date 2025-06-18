/*
 * Copyright 2013-2015 must-be.org
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
package consulo.dotnet.run.impl.coverage;

import com.intellij.rt.coverage.data.ClassData;
import com.intellij.rt.coverage.data.LineData;
import consulo.application.ApplicationManager;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.execution.coverage.CoverageSuitesBundle;
import consulo.execution.coverage.CoverageViewManager;
import consulo.execution.coverage.view.CoverageListRootNode;
import consulo.execution.coverage.view.CoverageViewExtension;
import consulo.execution.coverage.view.ElementColumnInfo;
import consulo.execution.coverage.view.PercentageCoverageColumnInfo;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.ui.ex.awt.ColumnInfo;

import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageViewExtension extends CoverageViewExtension {
    private static final String UNKNOWN = "?? %";

    private final GlobalSearchScope mySearchScope;

    public DotNetCoverageViewExtension(Project project, CoverageSuitesBundle suitesBundle, CoverageViewManager.StateBean stateBean) {
        super(project, suitesBundle, stateBean);
        mySearchScope = getSuitesBundle().getSearchScope(getProject());
    }

    @Nullable
    @Override
    public String getSummaryForNode(AbstractTreeNode node) {
        return null;
    }

    @Nullable
    @Override
    public String getSummaryForRootNode(AbstractTreeNode childNode) {
        return null;
    }

    @Nullable
    @Override
    public String getPercentage(int columnIdx, AbstractTreeNode node) {
        Object value = node.getValue();
        if (!(value instanceof PsiElement)) {
            return UNKNOWN;
        }
        double percentValue = getPercentValue((PsiElement) value, -1);
        if (percentValue == -1) {
            return UNKNOWN;
        }
        else {
            return percentValue + " %";
        }
    }

    private double getPercentValue(PsiElement value, double unknownValue) {
        if (value instanceof DotNetTypeDeclaration) {
            String vmQName = ((DotNetTypeDeclaration) value).getVmQName();
            if (vmQName == null) {
                return unknownValue;
            }

            ClassData classData = getSuitesBundle().getCoverageData().getOrCreateClassData(vmQName);

            LineData[] lines = (LineData[]) classData.getLines();
            if (lines == null) {
                return unknownValue;
            }

            int len = 0;
            double i = 0;
            for (LineData line : lines) {
                if (line != null) {
                    if (line.getHits() > 0) {
                        i++;
                    }
                    len++;
                }
            }

            return (i / len) * 100;
        }
        else if (value instanceof DotNetNamespaceAsElement) {
            Collection<PsiElement> children =
                ((DotNetNamespaceAsElement) value).getChildren(mySearchScope, DotNetNamespaceAsElement.ChildrenFilter.NONE);
            double all = 0;
            for (PsiElement temp : children) {
                all += getPercentValue(temp, 0);
            }

            return all / children.size();
        }
        return unknownValue;
    }

    @Override
    public List<AbstractTreeNode> getChildrenNodes(final AbstractTreeNode node) {
        return ApplicationManager.getApplication().runReadAction(new Supplier<List<AbstractTreeNode>>() {
            @Override
            public List<AbstractTreeNode> get() {
                List<AbstractTreeNode> nodes = new ArrayList<AbstractTreeNode>();
                Object element = node.getValue();
                if (element instanceof DotNetNamespaceAsElement) {
                    Collection<PsiElement> children =
                        ((DotNetNamespaceAsElement) element).getChildren(mySearchScope, DotNetNamespaceAsElement.ChildrenFilter.NONE);
                    for (PsiElement element1 : children) {
                        if (element1 instanceof PsiNamedElement) {
                            CoverageListRootNode e = new CoverageListRootNode(getProject(), (PsiNamedElement) element1, getSuitesBundle(),
                                getStateBean()
                            );
                            e.setParent(node);
                            nodes.add(e);
                        }
                    }
                }
                else if (element instanceof DotNetTypeDeclaration) {
                    for (DotNetNamedElement element1 : ((DotNetTypeDeclaration) element).getMembers()) {
                        if (element1 instanceof DotNetTypeDeclaration) {
                            CoverageListRootNode e = new CoverageListRootNode(getProject(), element1, getSuitesBundle(), getStateBean());
                            e.setParent(node);
                            nodes.add(e);
                        }
                    }
                }
                return nodes;
            }
        });
    }

    @Override
    public ColumnInfo[] createColumnInfos() {
        return new ColumnInfo[]{new ElementColumnInfo(), new PercentageCoverageColumnInfo(
            1,
            "Statistics, %",
            getSuitesBundle(),
            getStateBean()
        )};
    }

    @Nullable
    @Override
    public PsiElement getParentElement(PsiElement element) {
        return element.getParent();
    }

    @Override
    public AbstractTreeNode createRootNode() {
        DotNetNamespaceAsElement namespace = DotNetPsiSearcher.getInstance(getProject()).findNamespace("", mySearchScope);
        return new CoverageListRootNode(getProject(), namespace, getSuitesBundle(), getStateBean());
    }
}
