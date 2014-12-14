package es.jorge.alarmcontroler;

/**
 * Created by Jorge on 16/11/2014.
 */
public class TX_Messages {
    /* this messages goes from tlf to central alarm */
    /* general fields */
    private static int Message_ID = 0;

    /* message 1 fields */
    private int Deactivate_Alarm = -1;

    /* message 2 fields */
    private static int Sensor_Number = 0;
    private static int Sensor_Status = -1;
    private static int Sensor_Reset  = -1;

    private String Msg_1;
    private static String Msg_2;

    private static String[] Msg_2_Array ;

    private static String Default_Msg_2 = "2/0/-1/-1///";

    /* constructor */
    public TX_Messages(){

        int i;

        Message_ID = 0;
        Deactivate_Alarm = -1;
        Sensor_Number = 0;
        Sensor_Status = -1;
        Sensor_Reset  = -1;

        Msg_2_Array = new String[10];

        for (i=0; i < 10; i++){
            Msg_2_Array[i] = Default_Msg_2;
        }
    }

    /*************************************************************************/
    /*  Fill_Msg_1                                                           */
    /*************************************************************************/
    private String Fill_Msg_1 (){
        /* collect information */
        Message_ID = 1;
        if (MainControlFragment.Get_Main_Switch_State())
           Deactivate_Alarm = 1;
        else
           Deactivate_Alarm = 0;

        /* transform to string data */
        Msg_1 = Integer.toString(Message_ID) + '/' + Integer.toString(Deactivate_Alarm) + "///";

        return Msg_1;
    }

    /*************************************************************************/
    /*  Get_Msg_1                                                            */
    /*************************************************************************/
    public String Get_Msg_1 (){

        Fill_Msg_1();

        return Msg_1;
    }

    /*************************************************************************/
    /*  Fill_Msg_2                                                           */
    /*************************************************************************/
    private static String Fill_Msg_2 (int S_N, boolean Power, boolean Reset){

        /* collect information */
        Message_ID    = 2;
        Sensor_Number = S_N;
        Sensor_Status = Power ? 1 : 0;
        Sensor_Reset  = Reset ? 1 : 0;

        /* transform to string data */
        Msg_2 = Integer.toString(Message_ID) + '/' + Integer.toString(Sensor_Number) + '/' +
                Integer.toString(Sensor_Status) + '/' + Integer.toString(Sensor_Reset) + "///";

        return Msg_2;
    }

    /*************************************************************************/
    /*  Get_Msg_2                                                            */
    /*************************************************************************/
    public String Get_Msg_2 (int Msg_Num){

        return Msg_2_Array[Msg_Num];
}

     /*************************************************************************/
    /*  Set_Msg_2                                                            */
    /*************************************************************************/
    public static void Set_Msg_2 (int Sensor_Number, boolean Power_Switch, boolean Sensor_Reset){

        Msg_2_Array[Sensor_Number - 1 ] = Fill_Msg_2(Sensor_Number, Power_Switch, Sensor_Reset);

    }


}
