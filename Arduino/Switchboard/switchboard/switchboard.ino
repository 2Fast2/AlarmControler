/* Esto es una prueba para ver si puedo tener comunicación con el programa de Android */
#include <Ethernet.h>
#include <SPI.h>

// Constants
// mac address.
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED}; 
// assign an IP address for the controller:
IPAddress ip(192,168,1,40);
IPAddress gateway(192,168,1,1);
IPAddress subnet(255,255,255,0);

// Initialize the Ethernet server library
// with the IP address and port you want to use 
EthernetServer server(5000);

/* declare android client */
EthernetClient AndroidClient;
/* declare android client IP */
IPAddress remoteIP(0,0,0,0);

/* null IP */
IPAddress NullIP(0,0,0,0);
/* remote port */
#define REMOTE_PORT 5001

/* maximum sensors*/
#define MAX_SENSORS 10
/* maximum command storage deep */
#define MAX_CMD_DEEP 100
/* store commands form the app */
/* plus 1 to store commnads to the switchbox */
char Input_Sensor_Status_Array [MAX_SENSORS + 1][MAX_CMD_DEEP];
/* store commands to send to the app */
/* plus 1 to store commnads to the switchbox */
char Output_Sensor_Status_Array [MAX_SENSORS + 1][MAX_CMD_DEEP];
/* message 1 maximum size */
#define MAX_MSG_1 3
/* message 2 maximum size */
#define MAX_MSG_2 7


/********************************************************/
/* This function configure the Arduino*/
void setup() {
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip);
  server.begin();

  // configure serial line
  Serial.begin(9600);

  // give the sensor and Ethernet shield time to set up:
  delay(1000);

}

/********************************************************/
/* this function decode received msg 1 */
void DecodeMsg1 (char *Msg){
  int i = 0;
  char Power = '0';
  
  /* decode the msg */
  /* looking for the first field */
  for (i=0; i < MAX_MSG_1; i++){
    if (Msg[i] == '/'){
       Power = Msg[i+1];
       break;    
    }
  }
   /* set the switchbox power */
   Input_Sensor_Status_Array[0][1] = Power;
}

/********************************************************/
/* this function decode received msg 2 */
void DecodeMsg2 (char *Msg){
  char Sensor[2] = {0};
  int Sensor_Num = 0;
  char Status = '0';
  char Reset = '0';
   
  Serial.println(Msg);
  
  Sensor_Num = atoi(&Msg[2]);
  Status = Msg[4];
  Reset = Msg[6];
  Serial.print("Sensor: ");Serial.println(Msg[2]);
  Serial.print("Status: ");Serial.println(Status);
  Serial.print("Reset: ");Serial.println(Reset);
  
  //Sensor_Num = atoi(Sensor);
   /* set sensor command */
   Input_Sensor_Status_Array[Sensor_Num][1] = Status;
   Input_Sensor_Status_Array[Sensor_Num][2] = Reset;
}


/********************************************************/
/* this function decode the received command */
int DecodeCommand (char Cmd[], int Size)
{
  int validity = 0;
  int i = 0;
  char AuxArray [20];
  int AuxArrayIndex = 0;
  int EoM = 0;
  
  memset(AuxArray, 0, sizeof(AuxArray));
  
  Serial.print(Cmd); Serial.print(" Size: "); Serial.println(Size);
    
  for (i=0; i < (Size + 1); i++){
    Serial.print("Cmd[");Serial.print(i);Serial.print("]: ");Serial.println(Cmd[i]);
    if ( Cmd[i] != '/'){
    
      AuxArray[AuxArrayIndex] = Cmd[i];
      
    }else if (Cmd[i+1] == '/' && Cmd[i+2] == '/'){
    Serial.println("HEMOS TERMIANDO EL MSG");
       /* end of msg */
       EoM = 1;
       
    }else{
    
      AuxArray[AuxArrayIndex] = '/';
    }
   
    AuxArrayIndex = AuxArrayIndex + 1;
    
    if ( EoM == 1){
    Serial.println(AuxArray);
      /* end of message decode the message */
      if (AuxArray[0] == '1'){
           
        DecodeMsg1(AuxArray);
        validity = 1;
        EoM = 0;
        memset(AuxArray, 0, sizeof(AuxArray));
        i = i+2;
        AuxArrayIndex = 0;
        
      }else if(AuxArray[0] == '2'){
        
        DecodeMsg2(AuxArray);
        validity = 1;
        EoM = 0;
        memset(AuxArray, 0, sizeof(AuxArray));
        i = i+2;
        AuxArrayIndex = 0;
        
      } 
    }
    
  /*   switch (Cmd[i]){
       case '1':
          DecodeMsg1(Cmd[i+2]);
          validity = 1;
          i = 5; /* move the pointer until next msgs */
    /*      break;
       case '2':
          DecodeMsg2(Cmd[i+2], Cmd[i+4], Cmd[i+6]);
          validity = 1;
          
          break;
       default:
          break;
     }
     */
  }
  
  
//  if (Cmd[0] == '1'){
//  /* msg 1 for switchbox */
//  
//  }else if (Cmd[0] == '2'){
//    /* msg 2 for sensors */
//  
//  }else{
//     validity = 0;
//  }
  
  return validity;
}

/********************************************************/
/* this is the main loop controller */
void loop() {

  int index = 0;
  char command[MAX_CMD_DEEP];
  int value = 0;
  char character;
  int Valid_Cmd = 0;
  int Cmd_Size = 0;
 

  // listen for incoming clients
  EthernetClient client = server.available();

  if (client) {
    Serial.println("Got a client");
    
    remoteIP = client.getRemoteIP();
    Serial.print("IP: ");
    Serial.println(remoteIP);
    
    // when the client connect to the server
    if (client.connected()){
      Serial.println("client connected");
            
      /* clean reception buffer */
     memset(command, 0, sizeof(command));
     
      /* while client connedted, receive and transmit info */
      while (client.available()){
        
        /* read character by character */        
        character = client.read();
        
        // decode the command
        if (character != '\n' && character != '\0'){
          
          // take command
          command[index] = character;
          index++;
          // truncate the command
          if (index >= MAX_CMD_DEEP) index = MAX_CMD_DEEP -1;
          continue;
        }
        command[index] = '\0';
      }
      /* store command size */
      Cmd_Size = index - 1;
          
      /* decode received command */
      Valid_Cmd = DecodeCommand(command, Cmd_Size);
      
      /* if the cmd is valid send the information to the sensors */
    //  if (Valid_Cmd){
        /* send the information */
      
   //   } 
    }else{ Serial.println("ERROR to receive data from the client");}
  }
  
  /* collect information from the sensors */
  
  /* if the switchboard is connected to android app */
  if (remoteIP != NullIP){
    
    /*************************************************************/
    /* ONLY FOR TEST */
          /* to test send a msg to the android app */
      if (!AndroidClient.connected()){
        if (AndroidClient.connect(remoteIP,REMOTE_PORT)){
          Serial.println("CONECTADO A LA APP");
          AndroidClient.write("Prueba");
        }else{
          Serial.println("ERROR: error to connect to the Android App");
        }
      }
    /*************************************************************/
  
    /* create msgs to send to android app */
    
    /* send msgs to the android app */
  }
}






