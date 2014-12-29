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

#define BUFFER_SIZE 100

// Initialize the Ethernet server library
// with the IP address and port you want to use 
EthernetServer server(5000);

/* declare android client */
EthernetClient AndroidClient;
/* declare android client IP */
IPAddress remoteIP(0,0,0,0);
/* remote port */
#define REMOTE_PORT 5001

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
/* this function clean buffer reception */
void Clean_Buffer_Reception(char * Buffer){
  
  int i=0;
  
  for (i=0; i < BUFFER_SIZE; i++){
    Buffer[i] = '\0';
  }
}

/********************************************************/
/* this function decode the received command */
void DecodeCommand (char Cmd[])
{
}

/********************************************************/
/* this is the main loop controller */
void loop() {

  int index = 0;
  char command[BUFFER_SIZE];
  int value = 0;
  char character;

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
        Clean_Buffer_Reception(&command[0]);
      //while client connedted, receive and transmit info
      while (client.available()){
        /* read character by character */        
        character = client.read();
        
        // decode the command
        if (character != '\n' && character != '\0'){
          //Serial.println(character);
          // take command
          command[index] = character;
          index++;
          // truncate the command
          if (index >= BUFFER_SIZE) index = BUFFER_SIZE -1;
          continue;
        }
        command[index] = '\0';
      }

      Serial.println(command);
      
      /* decode received command */
      DecodeCommand(command);
      
      /* to test send a msg to the android app */
      if (!AndroidClient.connected()){
        if (AndroidClient.connect(remoteIP,REMOTE_PORT)){
          Serial.println("CONECTADO A LA APP");
          AndroidClient.write("Prueba");
        }else{
          Serial.println("ERROR: error to connect to the Android App");
        }
      } 
    }
  }
}






