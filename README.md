# SmartHouse

> SmartHouse where you can control the lighting system, automatically or manually

## Objetivo
O objetivo era criar uma SmartHouse, no sentido de automatizar o sistema de iluminação das 5 divisões de uma casa (maquete neste caso). Sempre que alguém entrar numa certa divisão, as luzes iriam acender automaticamente e caso abandonem essa mesma divisão as luzes apagar-se-ão. Da mesma forma, ao abandonar a casa todas as luzes se apagarão.

Alem deste sistema de automatização das luzes, vamos também utilizar uma aplicação Android para podermos controlar manualmente a iluminaçãoda casa, assim como obter a temperaturada dentro de casa.

A partir de uma hora predefinida, na aplicação, ou num certo nível de luminosidade, pretende-se que as luzes da casa se liguem/desliguem automaticamente.

## Funcionamento das SmarthHouse
![diagrama](https://user-images.githubusercontent.com/47954209/129787985-d28ee3b2-032f-4e1b-bc60-cf08a1cd3e80.png)

## Materiais usados
* Arduino
* Módulo WiFi ESP8266
* LEDs
* Sensores de movimento (acabamos por usar sensores de distancia)
* Sensores de luminosidade
* DHT11

## Arduino

IMAGEM

O arduino vai ter ligação direta ao sensor de luminosidade, aos LEDs, aos sensores de movimento(distancia) e ao sensor de temperatura. Os LEDs irão ser os outputs e os sensores serão os nossos inputs.

No Arduinovão estar oscomponentes de hardware responsáveis pela leitura de dados (sensor de luz e movimento).

## Android

IMAGEM

Vamos usar um sistema Android como user interface para todos os nossos sistemas. 

## FireBase
É através do FireBase que os nossos sistemas, Arduino e Aplicação Android, estão conectados, sendo que é aqui que está presente a base de dado
