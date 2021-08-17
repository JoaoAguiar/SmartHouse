#include <NTPClient.h>
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <WiFiUdp.h>

#define BAUD 115200

#define TRIG D5

#define ECHO1 D1
#define ECHO2 D2

#define LED1 D6
#define LED2 D7

#define TEMP A0

#define SHOW_DISTS false  // Muda para true para ver o que os sensores de movimento detetam 

#define AUTO_HOUR_CHECK (auto_hours_on > auto_hours_off && (hour > auto_hours_on || hour < auto_hours_off || (hour == auto_hours_on && minute >= auto_minutes_on) || (hour == auto_hours_off && minute <= auto_minutes_off)) || auto_hours_on < auto_hours_off && (hour > auto_hours_on && hour < auto_hours_off || (hour == auto_hours_on && minute >= auto_minutes_on) || (hour == auto_hours_off && minute <= auto_minutes_off)) || auto_hours_on == auto_hours_off && (auto_minutes_on < auto_minutes_off && (hour == auto_hours_on && minute >= auto_minutes_on && minute <= auto_minutes_off) || auto_minutes_on >= auto_minutes_off && (hour != auto_hours_on || minute <= auto_minutes_on || minute >= auto_minutes_off)))


// Booleanos para guardar estados
bool moved1, moved2, led1, led2, ligado_db, auto_status, auto_was, first_auto;

// Ultimas distancias e atual, contadores para nao aceder a firebase a cada loop
short dist, dists1[3], dists2[3], counter1, counter2, counter_temperature, counter_auto;

// Horas e minutos para tratar das luzes automaticas
int auto_hours_on, auto_hours_off, auto_minutes_on, auto_minutes_off, hour, minute;

// Estruturas para receber hora atual
WiFiUDP ntp_udp;
NTPClient time_client(ntp_udp, "europe.pool.ntp.org", 3600);

// Estruturas para aceder a firebase
FirebaseData fireData;
FirebaseAuth fireAuth;
FirebaseConfig fireConfig;

// Firebase
const char* fire_host = "smart-house-acf58-default-rtdb.europe-west1.firebasedatabase.app";
const char* api_key = "AIzaSyCP1uKNDWdREH7D-LL8MDX39FrNV6o-sbA";
const char* email = "anapaula2011.99@gmail.com";
const char* pass = "asdfghjkl";

// Wifi
const char* wifi_ssid = "";
const char* wifi_pass = "";


void setup() {
  Serial.begin(BAUD);
  Serial.println();

  pinMode(TRIG, OUTPUT);
  pinMode(ECHO1, INPUT);
  pinMode(ECHO2, INPUT);
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);
  pinMode(TEMP, INPUT);

  digitalWrite(LED1, LOW);
  digitalWrite(LED2, LOW);

  initialRead();

  moved1 = false;
  moved2 = false;
  led1 = false;
  led2 = false;
  auto_status = false;
  auto_was = false;
  counter1 = 0;
  counter2 = 5;
  counter_temperature = 5;
  counter_auto = 10;

  WiFi.mode(WIFI_STA);
  WiFi.begin(wifi_ssid, wifi_pass);
  
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(100);
  }

  Serial.println("connected");

  time_client.begin();

  fireConfig.api_key = api_key;
  fireAuth.user.email = email;
  fireAuth.user.password = pass;
  fireConfig.database_url = fire_host;

  Firebase.reconnectWiFi(true);
  Firebase.begin(&fireConfig, &fireAuth);
}

// Leitura inicial para ter um array de distancias no inicio
void initialRead() {
  digitalWrite(TRIG, LOW);
  delay(50);

  dists1[0] = readSensor1();
  delay(50);
  dists2[0] = readSensor2();
  delay(50);
  dists1[1] = readSensor1();
  delay(50);
  dists2[1] = readSensor2();
  delay(50);
  dists1[2] = readSensor1();
  delay(50);
  dists2[2] = readSensor2();
  delay(50);
}

void loop() {
  checkAutoStatus();
  checkSensor1();
  checkSensor2();
  checkTemp();
}

// Verificar se luzes automaticas
void checkAutoStatus() {
  if(counter_auto == 0) {
    counter_auto = 20;

    // Verificar se luzes automaticas estao ativas
    Firebase.getBool(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/AutomaticLights/SwitchAutomaticLights", auto_status);
    
    if(auto_status) {
      // Verificar as hora para ligar e desligar as luzes automaticas
      Firebase.getInt(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/AutomaticLights/HoursOn", auto_hours_on);
      Firebase.getInt(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/AutomaticLights/HoursOff", auto_hours_off);
      Firebase.getInt(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/AutomaticLights/MinutesOn", auto_minutes_on);
      Firebase.getInt(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/AutomaticLights/MinutesOff", auto_minutes_off);

      // Hora atual
      time_client.update();
      hour = time_client.getHours();
      minute = time_client.getMinutes();

      // Se estivermos na hora das luzes automaticas
      if(AUTO_HOUR_CHECK) {
        // Se antes nao estavamos na hora das luzes automaticas
        if(!auto_was) {
          // Ligar as luzes
          first_auto = true;
          auto_was = true;
        }
      }
      else {
        auto_was = false;
      }
    }
  }
  else {
    counter_auto--;
  }
}

// Verificar sensor 1
void checkSensor1() {
  // Ver o valor que tem na firebase e fazer update se for diferente do valor local
  if(counter1 == 0) {
    Firebase.getBool(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/LightSwitches/Room1", ligado_db);
    
    if(ligado_db != led1) {
      updateLed1();

      return;
    }

    counter1 = 10;
  }
  else {
    counter1--;
  }

  // Verificar se estamos na hora das luzes automaticas
  if(auto_status) {
    if(AUTO_HOUR_CHECK) {
      if(first_auto) {
        if(!led1) {
          updateLed1();
        }
        if(!led2) {
          updateLed2();
        }

        first_auto = false;
      }

      return;
    }
  }

  // Ler distancia
  dist = readSensor1();

  if(SHOW_DISTS) {
    Serial.printf("Sensor1: %hd\n", dist);
  }

  // Se no ultimo loop houve movimento, faz reset ao array de distancias
  if (moved1) {
    dists1[0] = dist;
    dists1[1] = dist;
    dists1[2] = dist;
    moved1 = false;
  }
  // Verificar se mudança de distancia foi substancial
  else if(abs(dist - dists1[0]) + abs(dist - dists1[1]) + abs(dist - dists1[2]) > 12.5) {
    updateLed1();
    delay(2000);

    return;
  }
  // Meter distancia no array se foi parecida
  else {
    dists1[0] = dists1[1];
    dists1[1] = dists1[2];
    dists1[2] = dist;
  }

  delay(50);
}
// Verificar sensor 2
void checkSensor2() {
  // Ver o valor que tem na firebase e fazer update se for diferente do valor local
  if (counter2 == 0) {
    Firebase.getBool(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/LightSwitches/Room2", ligado_db);

    if(ligado_db != led2) {
      updateLed2();

      return;
    }

    counter2 = 10;
  }
  else {
    counter2--;
  }

  // Verificar se estamos na hora das luzes automaticas (afunçao do sensor 1 trata do resto)
  if(auto_status) {
    if(AUTO_HOUR_CHECK) {
      return;
    }
  }

  // Ler distancia
  dist = readSensor2();
  
  if(SHOW_DISTS) {
    Serial.printf("Sensor2: %hd\n", dist);
  }
  
  // Se no ultimo loop houve movimento, faz reset ao array de distancias
  if (moved2) {
    dists2[0] = dist;
    dists2[1] = dist;
    dists2[2] = dist;
    moved2 = false;
  }
  // Verificar se mudança de distancia foi substancial
  else if (abs(dist - dists2[0]) + abs(dist - dists2[1]) + abs(dist - dists2[2]) > 12.5) {
    updateLed2();
    delay(2000);
  
    return;
  }
  // Meter distancia no array se foi parecida
  else {
    dists2[0] = dists2[1];
    dists2[1] = dists2[2];
    dists2[2] = dist;
  }

  delay(50);
}
// Verificar temperatura e mandar para a firebase
void checkTemp() {
  if (counter_temperature == 0) {
    double mv = analogRead(TEMP) * 3.3 / 1024;
    double temperature = (mv - 0.5) * 100;

    Firebase.setDouble(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/Values/Temperature", temperature);
    
    counter_temperature = 10;
    
    delay(50);
  }
  else {
    counter_temperature--;
  }
}

// Mudar led 1
void updateLed1() {
  moved1 = true;

  if(led1) {
    digitalWrite(LED1, LOW);
    led1 = false;
  }
  else {
    digitalWrite(LED1, HIGH);
    led1 = true;
  }

  Firebase.setBool(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/LightSwitches/Room1", led1);
}
// Mudar led 2
void updateLed2() {
  moved2 = true;

  if(led2) {
    digitalWrite(LED2, LOW);
    led2 = false;
  }
  else {
    digitalWrite(LED2, HIGH);
    led2 = true;
  }

  Firebase.setBool(fireData, "/lIqz2tyZktU96qrxh769CImo2Ez1/LightSwitches/Room2", led2);
}

// Ler a distancia do sensor 1
short readSensor1() {
  pulseTrig();
  return pulseIn(ECHO1, HIGH, 50000) / 29 / 2;
}
// Ler a distancia do sensor 1
short readSensor2() {
  pulseTrig();
  return pulseIn(ECHO2, HIGH, 50000) / 29 / 2;
}

// Mandar trigger para os sensores de movimento
void pulseTrig() {
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(8);
  digitalWrite(TRIG, LOW);
}