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
package net.hasor.context.setting;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import net.hasor.Hasor;
import org.more.util.StringUtils;
import org.more.util.map.DecSequenceMap;
import org.more.xml.XmlParserKitManager;
import org.more.xml.stream.XmlReader;
/***
 * ����InputStream�ķ�ʽ��ȡSettings�ӿڵ�֧�֡�
 * @version : 2013-9-8
 * @author ������ (zyc@byshell.org)
 */
public class InputStreamSettings extends AbstractIOSettings {
    private String                  settingEncoding = "utf-8";
    private LinkedList<InputStream> pendingStream   = new LinkedList<InputStream>();
    //
    /**����{@link InputStreamSettings}����*/
    public InputStreamSettings() throws IOException, XMLStreamException {
        this(new InputStream[0], null);
    }
    /**����{@link InputStreamSettings}����*/
    public InputStreamSettings(InputStream inStream) throws IOException, XMLStreamException {
        this(inStream, null);
    }
    /**����{@link InputStreamSettings}����*/
    public InputStreamSettings(InputStream[] inStreams) throws IOException, XMLStreamException {
        this(inStreams, null);
    }
    /**����{@link InputStreamSettings}����*/
    public InputStreamSettings(InputStream inStream, String encoding) throws IOException, XMLStreamException {
        this(new InputStream[] { inStream }, encoding);
    }
    /**����{@link InputStreamSettings}����*/
    public InputStreamSettings(InputStream[] inStreams, String encoding) throws IOException, XMLStreamException {
        super();
        Hasor.assertIsNotNull(inStreams);
        for (InputStream ins : inStreams) {
            Hasor.assertIsNotNull(ins);
            this.addStream(ins);
        }
        if (StringUtils.isBlank(encoding) == false)
            this.setSettingEncoding(encoding);
        this.loadSettings();
    }
    //
    //
    /**��ȡ���������ļ�ʱʹ�õ��ַ����롣*/
    public String getSettingEncoding() {
        return this.settingEncoding;
    }
    /**���ý��������ļ�ʱʹ�õ��ַ����롣*/
    public void setSettingEncoding(String encoding) {
        this.settingEncoding = encoding;
    }
    /**��һ�����������ӵ������ش����б���ʹ��load�������ش������б��е�����
     * ע�⣺�������б��е���һ��װ����Ͻ���Ӵ������б��������ȥ��*/
    public void addStream(InputStream stream) {
        if (stream != null)
            if (this.pendingStream.contains(stream) == false)
                this.pendingStream.add(stream);
    }
    /**loadװ�����д��������������û�д���������ֱ��return��*/
    public synchronized void loadSettings() throws IOException, XMLStreamException {
        this.readyLoad();//׼��װ��
        {
            if (this.pendingStream.isEmpty() == true)
                return;
            //����װ�ػ���
            Map<String, Map<String, Object>> loadTo = this.getNamespaceSettingMap();
            XmlParserKitManager xmlParserKit = this.getXmlParserKitManager(loadTo);
            xmlParserKit.setContext(this);
            String encoding = this.getSettingEncoding();
            InputStream inStream = null;
            //
            while ((inStream = this.pendingStream.pollFirst()) != null) {
                new XmlReader(inStream, encoding).reader(xmlParserKit, null);
                inStream.close();
            }
        }
        this.loadFinish();//���װ��
    }
    /**׼��װ��*/
    protected void readyLoad() throws IOException {}
    /**���װ��*/
    protected void loadFinish() throws IOException {}
    /**{@link InputStreamSettings}���Ͳ�֧�ָ÷�����������ø÷�����õ�һ��{@link UnsupportedOperationException}�����쳣��*/
    public void refresh() throws IOException {
        throw new UnsupportedOperationException();
    }
    //
    private DecSequenceMap<String, Object>   mergeSettingsMap     = new DecSequenceMap<String, Object>();
    private Map<String, Map<String, Object>> namespaceSettingsMap = new HashMap<String, Map<String, Object>>();
    //
    protected Map<String, Map<String, Object>> getNamespaceSettingMap() {
        return namespaceSettingsMap;
    }
    protected DecSequenceMap<String, Object> getSettingsMap() {
        return mergeSettingsMap;
    }
    protected synchronized XmlParserKitManager getXmlParserKitManager(Map<String, Map<String, Object>> loadTo) throws IOException {
        XmlParserKitManager kitManager = super.getXmlParserKitManager(loadTo);
        this.mergeSettingsMap.removeAllMap();
        for (Map<String, Object> ent : loadTo.values())
            this.mergeSettingsMap.addMap(ent);
        return kitManager;
    }
}