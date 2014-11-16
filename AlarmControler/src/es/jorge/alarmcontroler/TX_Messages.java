package es.jorge.alarmcontroler;

/**
 * Created by Jorge on 16/11/2014.
 */
public class TX_Messages {
    /* this messages goes from tlf to central alarm */
    /* general fields */
    private int Message_ID = 0;

    /* message 1 fields */
    private int Deactivate_Alarm = -1;

    /* message 2 fields */
    private int Sensor_Number = 0;
    private int Sensor_Status = -1;
    private int Sensor_Reset  = -1;

    private String Msg_1;

    /* constructor */
    public TX_Messages(){

       Message_ID = 0;
       Deactivate_Alarm = -1;
       Sensor_Number = 0;
       Sensor_Status = -1;
       Sensor_Reset  = -1;

    }

    /* create message 1 */
    private String Fill_Msg_1 (int Deact_Alarm){
        /* collect information */
        Message_ID = 1;
        Deactivate_Alarm = Deact_Alarm;

        /* transform to string data */
        Msg_1 = Integer.toString(Message_ID) + '/' + Integer.toString(Deactivate_Alarm);

        return Msg_1;
    }
}
