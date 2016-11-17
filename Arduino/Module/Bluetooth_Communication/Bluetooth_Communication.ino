#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11); // BLE(TXD) - RX(Arduino - 10) | BLE(RXD) - TX(Arduino - 11)
                                   
                                   // Serial1 (Arduino - Mega)
                                   // BLE(TXD) - RX(Arduino - 19) | BLE(RXD) - TX(Arduino - 18)
uint8_t buffer[256];
uint8_t index = 0;
uint8_t data;

void setup(){
  Serial.begin(9600);        //Arduino Bluetooth - PC Serial
  Serial1.begin(9600);
  // PC   ==  Serial1.begin(115200);    //Arduino Bluetooth - Bluetooth connect Device
  // using Putty set baudrate
  Serial.println("Ready..");
  
}

void loop(){
   while (Serial1.available()) {
     data = Serial1.read();
     buffer[index++] = data;
     if(index == 256 || data =='\0')break;
     
     // 시리얼 통신에서는 9600bps 기준으로
     // read 를 사용할 때 1ms 의 딜레이를 줘야 한다.
     delay(1);
   }
   
   for(uint8_t i = 0; i< index; i++){
     Serial.write(buffer[i]);
   }
   
   for(uint8_t i = 0; i< index; i++){
     Serial1.write(buffer[i]);
   }
   
/*
  if (Serial.available()) {
      Serial1.write(Serial.read());
      delay(1);
      Serial1.flush();
  }
*/
  index = 0;
}



// Receive DataStream ~ one by one byte
void receiveData(){
  // BT Connect Device –> Data –> Serial (Arduino)
   
}

void sendData(){
  // Serial (Arduino) –> Data –> BT Connect Device
  if (Serial.available()) {
    //if(Serial.read() == 'a'){
      Serial1.write(Serial.read());
    //} else{
    //  buff += Serial.read();
     // digitalWrite(13, HIGH);
      // 시리얼 통신에서는 9600bps 기준으로
      // read 를 사용할 때 1ms 의 딜레이를 줘야 한다.
      delay(1);
   // }
  }
  digitalWrite(13, LOW); 
}
