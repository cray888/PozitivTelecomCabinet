package ru.pozitivtelecom.cabinet.soap;

import android.os.AsyncTask;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoapClass extends AsyncTask<Void, Void, String> {

    // private references.
    private String mMethodName;
    private Map<String, String> mProperty = new HashMap<String, String>();

    // constant private references.
    private static final String USERNAME = "Abonent";
    private static final String PASSWORD = "sA4pa4sy";
    private static final String NAMESPACE = "http://lk.pozitivtelecom.ru";
    private static final String URL = "http://95.47.243.206:81/Cabinet/ws/cabinet.1cws";
    private static final String SOAP_ACTION = "http://95.47.243.206:81/Cabinet/ws/cabinet.1cws?wsdl";
    public static final String SOAP_ERROR_NODATA = "NoReciveData";

    protected OnSoapEventListener mOnSoapEventListener;

    public void setSoapEventListener(OnSoapEventListener eventListener) {
        mOnSoapEventListener = eventListener;
    }

    public SoapClass(String MethodName, Map<String, String> Property) {
        this.mMethodName = MethodName;
        this.mProperty = Property;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            SoapObjectCustom request = new SoapObjectCustom(NAMESPACE, mMethodName);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            for (Map.Entry entry : mProperty.entrySet()) {
                request.addProperty(entry.getKey().toString(), entry.getValue().toString());
            }
            envelope.setOutputSoapObject(request);
            HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(URL, USERNAME, PASSWORD);
            androidHttpTransport.debug = true;
            ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
            headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
            boolean error = false;
            String errorStr = "";
            for (int i = 0; i < 10; i++) {
                try {
                    androidHttpTransport.call(SOAP_ACTION, envelope, headerPropertyArrayList);
                    SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                    if (resultsRequestSOAP.getPropertyCount() > 0)
                        mOnSoapEventListener.onComplete(resultsRequestSOAP.getProperty(0).toString());
                    else
                        mOnSoapEventListener.onError(SOAP_ERROR_NODATA);
                    break;
                } catch (HttpResponseException e) {
                    error = true;
                    errorStr = e.getMessage();
                    break;
                } catch (IOException e) {
                    error = true;
                    errorStr = e.getMessage();
                    Thread.sleep(100);
                } catch (XmlPullParserException e) {
                    error = true;
                    errorStr = e.getMessage();
                    break;
                } catch (Exception e) {
                    error = true;
                    errorStr = e.getMessage();
                    break;
                }
            }
            if (error) mOnSoapEventListener.onError(errorStr);
        } catch (Exception e) {
            mOnSoapEventListener.onError(e.getMessage());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

}

class SoapObjectCustom extends SoapObject {

    public SoapObjectCustom(String namespace, String name) {
        super(namespace, name);
    }

    @Override
    public SoapObject addProperty(String name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
        propertyInfo.setValue(value);

        propertyInfo.setNamespace(this.namespace);

        return addProperty(propertyInfo);
    }

}

class HttpTransportBasicAuthSE extends HttpTransportSE {
    private String username;
    private String password;

    public HttpTransportBasicAuthSE(String url, String username, String password) {
        super(url);
        this.username = username;
        this.password = password;
        this.timeout = 30;
    }

    public ServiceConnection getServiceConnection() throws IOException {
        ServiceConnectionSE midpConnection = new ServiceConnectionSE(url);
        addBasicAuthentication(midpConnection);
        return midpConnection;
    }

    protected void addBasicAuthentication(ServiceConnection midpConnection) throws IOException {
        if (username != null && password != null) {
            StringBuffer buf = new StringBuffer(username);
            buf.append(':').append(password);
            byte[] raw = buf.toString().getBytes();
            buf.setLength(0);
            buf.append("Basic ");
            org.kobjects.base64.Base64.encode(raw, 0, raw.length, buf);
            midpConnection.setRequestProperty("Authorization", buf.toString());
        }
    }

}

