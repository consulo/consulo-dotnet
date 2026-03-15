/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.debugger.impl.nodes;

import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.execution.debug.frame.XCompositeNode;
import consulo.execution.debug.frame.XValueChildrenList;
import consulo.execution.debug.frame.XValueModifier;
import consulo.execution.debug.icon.ExecutionDebugIconGroup;
import consulo.ui.image.Image;
import consulo.util.lang.ref.SimpleReference;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * @author VISTALL
 * @since 05.01.16
 */
public class DotNetThisAsStructValueNode extends DotNetAbstractVariableValueNode {
    private final DotNetTypeProxy myTypeMirror;
    private final DotNetStructValueProxy myValue;

    public DotNetThisAsStructValueNode(DotNetDebugContext debuggerContext,
                                       DotNetStackFrameProxy frameProxy,
                                       DotNetTypeProxy typeMirror,
                                       DotNetStructValueProxy value) {
        super(debuggerContext, "this", frameProxy);
        myTypeMirror = typeMirror;
        myValue = value;
    }

    @Nullable
    @Override
    public XValueModifier getModifier() {
        return null;
    }

    @Override
    public Image getIconForVariable(@Nullable SimpleReference<DotNetValueProxy> alreadyCalledValue) {
        return ExecutionDebugIconGroup.nodeValue();
    }

    @Nullable
    @Override
    public DotNetValueProxy getValueOfVariableImpl() {
        return myValue;
    }

    @Override
    public void setValueForVariableImpl(DotNetValueProxy value) {
    }

    @Override
    public void computeChildren(XCompositeNode node) {
        final XValueChildrenList childrenList = new XValueChildrenList();

        Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map = myValue.getValues();
        for (Map.Entry<DotNetFieldOrPropertyProxy, DotNetValueProxy> entry : map.entrySet()) {
            DotNetFieldOrPropertyProxy key = entry.getKey();
            DotNetValueProxy value = entry.getValue();

            DotNetStructValueInfo valueInfo = new DotNetStructValueInfo(myValue, this, key, value);

            childrenList.add(new DotNetFieldOrPropertyValueNode(myDebugContext, key, myFrameProxy, null, valueInfo));
        }

        node.addChildren(childrenList, true);
    }

    @Override
    public DotNetTypeProxy getTypeOfVariableImpl() {
        return myTypeMirror;
    }
}
