package es.jorge.alarmcontroler;

/**
 * Created by Jorge on 16/11/2014.
 */
public class RX_Messages {
    /* this messages goes from central alarm to tlf */
    /* general fields */
    private int Message_ID;

    /* message 1 fields */
    private int Total_Sensors;
    private int Activated_Alarm;
    private int Battery;
    private double Central_Voltaje;
    private double Central_Temp;

    /* message 2 fields */
    private int Sensor_Number;
    private int Sensor_Status;
    private int Sensor_Activated;
    private double Sensor_Temperature;
    private double Sensor_Voltaje;

    private String Message_1;

    /* constructor */
    public RX_Messages(){
         Message_ID         = 0;
         Total_Sensors      = 0;
         Activated_Alarm    = -1;
         Battery            = -1;
         Central_Voltaje    = 0.0;
         Central_Temp       = 0.0;
         Sensor_Number      = 0;
         Sensor_Status      = -1;
         Sensor_Activated   = -1;
         Sensor_Temperature = 0.0;
         Sensor_Voltaje     = 0.0;
    }


    public void Decode_Msg_1(){
        Message_ID = 1;







    }


    public void Decode_Msg_2(){

    }

}
