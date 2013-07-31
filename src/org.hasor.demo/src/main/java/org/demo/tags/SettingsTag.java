/*
 * Copyright 2008-2009 the original author or authors.
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
package org.demo.tags;
import org.hasor.annotation.Bean;
import org.hasor.context.AppContext;
import org.hasor.freemarker.FmMethod;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-7-31
 * @author ������ (zyc@byshell.org)
 */
@Bean("cfg")
public class SettingsTag {
    @Inject
    private AppContext appContext = null;
    @FmMethod("Settings")
    public Object callMethod(String args) {
        return appContext.getSettings().getString(args);
    }
}