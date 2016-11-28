#include <Servo.h>
uint8_t buffer[256];
uint8_t index = 0;
uint8_t data;
int degree = 18;

// 5V    
Servo myservo;
int pos = 0;

void setup() {
  myservo.attach(9);
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  while(Serial1.available()){
    data = Serial1.read();
    buffer[index++] = data;
    if(index == 256 || data =='\0')break;
    
    delay(1);
  }
  
  if(buffer[0] == 'r'){
    int now = degree * 5;
    int next;
    degree = degree + 1;
    next = degree * 5;
    
    for(pos = now; pos <=next; pos++){
      myservo.write(pos);
      delay(50);
    }
  }
  else if(buffer[0] == 'l'){
    int now = degree * 5;
    int next;
    degree = degree - 1;
    next = degree * 5;
    
    for(pos = now; pos>=next; pos--){
      myservo.write(pos);
      delay(50);
    }
  }
  
  index = 0;
}
