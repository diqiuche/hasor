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
package net.hasor.rsf.serialize.coder;
import net.hasor.core.Environment;
import net.hasor.rsf.SerializeCoder;

import java.io.*;
/**
 *
 * @version : 2014年9月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public class JavaSerializeCoder implements SerializeCoder {
    @Override
    public void initCoder(Environment environment) {
    }
    //
    public byte[] encode(Object object) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(byteArray);
        output.writeObject(object);
        output.flush();
        output.close();
        return byteArray.toByteArray();
    }
    //
    public Object decode(byte[] bytes, Class<?> returnType) throws IOException {
        try {
            ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object resultObject = objectIn.readObject();
            objectIn.close();
            return resultObject;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}