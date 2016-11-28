#include <DHT11.h>
#include <SoftwareSerial.h>

//SoftwareSerial BTSerial(10, 11); // BLE(TXD) - RX(Arduino - 10) | BLE(RXD) - TX(Arduino - 11)
                                   
                                   // Serial1 (Arduino - Mega)
                                   // BLE(TXD) - RX(Arduino - 19) | BLE(RXD) - TX(Arduino - 18)
uint8_t buffer[256];
uint8_t index = 0;

// 3.3 v
int Magnetic_Value = 0;
int Magnetic_PIN = 2;   // Digital
int Flame_Value = 0;
int Flame_PIN = 5;      // Digital

// no Voltage
int LEDGREEN_PIN = 9;        // Digital
int LEDRED_PIN = 10;        // Digital

// 5.0v
int CO_Value = 100;
int CO_PIN = 3;         // Analog
int Buzzer_PIN = 12;    // Digital
int DHT_PIN=3;          // Digital
DHT11 dht11(DHT_PIN); 

void setup(){
  pinMode(Magnetic_PIN, INPUT);
  pinMode(Flame_PIN, INPUT); 
  pinMode(Buzzer_PIN,OUTPUT);
  pinMode(LEDGREEN_PIN, OUTPUT);
  pinMode(LEDRED_PIN, OUTPUT);
  
  pinMode(13, OUTPUT);
  Buzzer_OFF();
  
  Serial.begin(9600);        //Arduino Bluetooth - PC Serial
  Serial1.begin(9600);
  // PC   ==  Serial1.begin(115200);    //Arduino Bluetooth - Bluetooth connect Device
  // using Putty set baudrate
  Serial.println("Ready..");
  
}

void loop(){
  Temp_Check();
  
  // 불꽃 발생시  0, 평상 시 1 반환
  Flame_Check();
  
  // 문 닫혀있을 경우(자석 붙어있음) 0, 열려있을 경우(자석 떨어짐) 1 반환
  Magnetic_Check();
  
  // 라이터 가스 주입 시 500 이상 유지 평사시 300 ~ 500 미만 사이 유지
  CO_Check();
  
  delay(150);
  
  buffer[index++] = '\0';
   
   for(uint8_t i = 0; i< index; i++){
     Serial1.write(buffer[i]);
     delay(50);
   }

  index = 0;
}


// 불꽃 발생시  0, 평상 시 1 반환
void Flame_Check(){
  Flame_Value = digitalRead(Flame_PIN);
  
  if(Flame_Value == HIGH){
    Buzzer_OFF();
    ColorLed_OFF();
    buffer[index++] = 's';
  }else{
    ColorLed_ON();
    //Buzzer_ON();
    buffer[index++] = 'f';
  }
  Serial.print("FlameValue : \r ");
  Serial.print(Flame_Value, DEC);
  Serial.println("\n");
  delay(50);
  
  Flame_Value = 0;
}

// 문 닫혀있을 경우(자석 붙어있음) 0, 열려있을 경우(자석 떨어짐) 1 반환
void Magnetic_Check(){
  Magnetic_Value = digitalRead(Magnetic_PIN);
  
  if(Magnetic_Value == HIGH){
    ColorLed_ON();
   buffer[index++] = 'o';
  }else{
    ColorLed_OFF();
   buffer[index++] = 'c';
  }
  Serial.print("MagneticValue : \r ");
  Serial.print(Magnetic_Value, DEC);
  Serial.println("\n");
  delay(50);
  
  Magnetic_Value = 0;
}

// 라이터 가스 주입 시 500 이상 유지 평사시 300 ~ 500 미만 사이 유지
void CO_Check(){
  CO_Value = analogRead(CO_PIN);
  
  if(CO_Value > 420){
    ColorLed_ON();
    buffer[index++] = 'g';
  }else{
    ColorLed_OFF();
    buffer[index++] = 'n';
  }
  Serial.print("CO_Value : \r ");
  Serial.print(CO_Value, DEC);
  Serial.println("\n");
  delay(50);
  
  CO_Value = 0;
}

// Temp Check()
void Temp_Check(){
  int err;
  float temp, humi;
  
  if((err=dht11.read(humi, temp))==0)
  {
    if(temp > 30){
      buffer[index++] = '3';
      if(temp >39){
        buffer[index++] = '9';
      }else if(temp > 38){
        buffer[index++] = '8';
      }else if(temp > 37){
        buffer[index++] = '7';
      }else if(temp > 36){
        buffer[index++] = '6';
      }else if(temp > 35){
        buffer[index++] = '5';
      }else if(temp > 34){
        buffer[index++] = '4';
      }else if(temp > 33){
        buffer[index++] = '3';
      }else if(temp > 32){
        buffer[index++] = '2';
      }else if(temp > 31){
        buffer[index++] = '1';
      }else if(temp > 30){
        buffer[index++] = '0';
      }
    }else if(temp > 20){
      buffer[index++] = '2';
      if(temp >=29){
        buffer[index++] = '9';
      }else if(temp >= 28){
        buffer[index++] = '8';
      }else if(temp >= 27){
        buffer[index++] = '7';
      }else if(temp >= 26){
        buffer[index++] = '6';
      }else if(temp >= 25){
        buffer[index++] = '5';
      }else if(temp >= 24){
        buffer[index++] = '4';
      }else if(temp >= 23){
        buffer[index++] = '3';
      }else if(temp >= 22){
        buffer[index++] = '2';
      }else if(temp >= 21){
        buffer[index++] = '1';
      }else if(temp >= 20){
        buffer[index++] = '0';
      }
    }else if(temp > 10){
      buffer[index++] = '1';
      if(temp >=19){
        buffer[index++] = '9';
      }else if(temp >= 18){
        buffer[index++] = '8';
      }else if(temp >= 17){
        buffer[index++] = '7';
      }else if(temp >= 16){
        buffer[index++] = '6';
      }else if(temp >= 15){
        buffer[index++] = '5';
      }else if(temp >= 14){
        buffer[index++] = '4';
      }else if(temp >= 13){
        buffer[index++] = '3';
      }else if(temp >= 12){
        buffer[index++] = '2';
      }else if(temp >= 11){
        buffer[index++] = '1';
      }else if(temp >= 10){
        buffer[index++] = '0';
      }
    }else if(temp > 0){
      if(temp >=9){
        buffer[index++] = '9';
      }else if(temp >= 8){
        buffer[index++] = '8';
      }else if(temp >= 7){
        buffer[index++] = '7';
      }else if(temp >= 6){
        buffer[index++] = '6';
      }else if(temp >= 5){
        buffer[index++] = '5';
      }else if(temp >= 4){
        buffer[index++] = '4';
      }else if(temp >= 3){
        buffer[index++] = '3';
      }else if(temp >= 2){
        buffer[index++] = '2';
      }else if(temp >= 1){
        buffer[index++] = '1';
      }else if(temp >= 0){
        buffer[index++] = '0';
      }
    }
      
    Serial.print("temperature:");
    Serial.print(temp);
    Serial.print(" humidity:");
    Serial.print(humi);
    Serial.println();
  }
  else
  {
    Serial.println();
    Serial.print("Error No :");
    Serial.print(err);
    Serial.println();    
  }
  delay(DHT11_RETRY_DELAY); //delay for reread
}

void Buzzer_ON(){
  int i;
    for(i=0;i<80;i++){ //output sound of one frequency
      digitalWrite(Buzzer_PIN,HIGH);//make a sound
      delay(1);//delay 1ms
      digitalWrite(Buzzer_PIN,LOW);//silient
      delay(1);//delay 1ms
    }
    for(i=0;i<300;i++) {//output sound of another frequency 
      digitalWrite(Buzzer_PIN,HIGH);//make a sound
      delay(2);//delay 2ms
      digitalWrite(Buzzer_PIN,LOW);//silient 
      delay(2);//delay 2ms
    }
}
void Buzzer_OFF(){
   digitalWrite(Buzzer_PIN,LOW);
}

// RED 점멸
void ColorLed_ON(){
  digitalWrite(LEDGREEN_PIN,HIGH);
}

// GREEN 점멸
void ColorLed_OFF(){
  digitalWrite(LEDGREEN_PIN, LOW);
}
