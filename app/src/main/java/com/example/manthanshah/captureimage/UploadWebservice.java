package com.example.manthanshah.captureimage;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by Manthan.Shah on 03-10-2017.
 */

public class UploadWebservice {

    String namespace = "http://tempuri.org/";
    private String url = "http://10.10.10.3:921/SimpleWebService.asmx";
    String res;

    String SOAP_ACTION;
    Object resultRequestSOAP = null;
    SoapObject request = null, objMessages = null;
    SoapSerializationEnvelope envelope;
    HttpTransportSE androidHttpTransport;
    String error;
    String error1;
    String result;

    UploadWebservice() {
    }


    /**
     * Set Envelope
     */
    protected void SetEnvelope() {

        try {

            // Creating SOAP envelope
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            //You can comment that line if your web service is not .NET one.
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            androidHttpTransport = new HttpTransportSE(url);
            androidHttpTransport.debug = true;
            new MarshalBase64().register(envelope);
            envelope.encodingStyle = SoapEnvelope.ENC;

        } catch (Exception e) {
            System.out.println("Soap Exception---->>>" + e.toString());
        }
    }

    // MethodName variable is define for which webservice function  will call
    public String UploadWebservice(String MethodName, byte[] imageData, String imageName, String type)
    {

        try {
            SOAP_ACTION = namespace + MethodName;

            //Adding values to request object
            request = new SoapObject(namespace, MethodName);

            //Adding Double value to request object
            PropertyInfo usenameproperty =new PropertyInfo();
            usenameproperty.setName("imageData");
            usenameproperty.setValue(imageData);
            usenameproperty.setType(byte[].class);
            request.addProperty(usenameproperty);

            PropertyInfo imagenameproperty =new PropertyInfo();
            imagenameproperty.setName("imageName");
            imagenameproperty.setValue(imageName);
            imagenameproperty.setType(String.class);
            request.addProperty(imagenameproperty);

            PropertyInfo typeproperty =new PropertyInfo();
            typeproperty.setName("Type");
            typeproperty.setValue(type);
            typeproperty.setType(String.class);
            request.addProperty(typeproperty);

            SetEnvelope();


            try {

                //SOAP calling webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);
                resultRequestSOAP = envelope.getResponse();
                res = resultRequestSOAP.toString();
                return res ;


            } catch (Exception e) {
                // TODO: handle exception
                error = e.toString();
                return error;

            }
        } catch (Exception e) {
            // TODO: handle exception
            error1 = e.toString();
            return error1;
        }

    }
}
