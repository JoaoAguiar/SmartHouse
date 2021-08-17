# SmartHouse

> SmartHouse where you can control the lighting system, automatically or manually

## Objective
The objective was to create a SmartHouse, in the sense of automating the lighting system of the 5 rooms of a house. Whenever someone enters a certain room, the lights would automatically turn on and if they leave that same room, the lights will go out. Likewise, when you leave the house all the lights will go out.

In addition to this lighting automation system, we will also use an Android App to be able to manually control the lighting of the house, as well as obtain the temperature inside the house.

From a predefined time, in the application, or at a certain level of brightness, it is intended that the house lights turn on/off automatically.

## SmarthHouse Operation
![diagrama](https://user-images.githubusercontent.com/47954209/129787985-d28ee3b2-032f-4e1b-bc60-cf08a1cd3e80.png)

## Materials
* Arduino
* ESP8266 WiFi Module
* LEDs
* Motion sensors (we ended up using distance sensors)
* Light sensors
* DHT11

## Arduino

![arduino](https://user-images.githubusercontent.com/47954209/129788192-3cbb8f07-63f8-40d9-9f40-e19094e49a89.png)

The Arduino will have direct connection to the luminosity sensor, the LEDs, the movement sensors (distance) and the temperature sensor. LEDs will be the outputs and sensors will be our inputs.

## App Android

![app](https://user-images.githubusercontent.com/47954209/129788208-edfaf418-4d3f-47df-b330-1b0a61e9d75b.png)

We are going to use an Android system as the user interface for all our systems.

## FireBase
It is through FireBase that our systems, Arduino and Android Application, are connected, and this is where the database is present.
