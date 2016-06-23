package com.yimeng.hyzc.utils;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 依萌 on 2016/6/23.
 */
public class WebServiceUtils {

    /**
     * 调用WebService
     *
     * @param serviceUrl webservice服务器地址
     * @param nameSpace  WebService的命名空间
     * @param methodName WebService方法名
     * @param Params     参数列表（哈希表形式）
     * @return WebService的返回值字符串形式
     */
    public static String callWebService(String serviceUrl, String nameSpace, String methodName, Map<String, Object> Params) {
        // 1、指定webservice的命名空间和调用的方法名
        SoapObject request = new SoapObject(nameSpace, methodName);

        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                request.addProperty((String) entry.getKey(), entry.getValue());
            }
        }

        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(serviceUrl);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                MyLog.i("----收到的回复----", result.toString());
                return result.toString();
            }

        } catch (SoapFault e) {
            MyLog.i("----发生错误---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
