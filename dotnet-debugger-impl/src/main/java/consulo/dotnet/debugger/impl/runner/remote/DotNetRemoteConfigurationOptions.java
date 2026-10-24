/*
 * Copyright 2013-2026 consulo.io
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
package consulo.dotnet.debugger.impl.runner.remote;

import com.dslplatform.json.CompiledJson;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Attribute;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2026-10-24
 */
@CompiledJson
public class DotNetRemoteConfigurationOptions extends RunConfigurationOptions {
    private boolean myServerMode;
    private @Nullable String myHost;
    private int myPort;

    @Attribute("serverMode")
    public boolean isServerMode() {
        return myServerMode;
    }

    public void setServerMode(boolean serverMode) {
        myServerMode = serverMode;
    }

    @Attribute("host")
    public @Nullable String getHost() {
        return myHost;
    }

    public void setHost(@Nullable String host) {
        myHost = StringUtil.nullize(host, true);
    }

    @Attribute("port")
    public int getPort() {
        return myPort;
    }

    public void setPort(int port) {
        myPort = port;
    }
}
