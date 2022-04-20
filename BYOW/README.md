# Flower Power (BYOW)

A simple 2D tile-based world exploration engine made with Java. The world consists of a 2D grid of tiles. The user is able to explore the map by walking around with the keyboard and interacting with the objects in this world. The world consists of rooms (of differing lengths and sizes), connected by hallways(the paths used to connect rooms and enable us to travel between them). The user is able to move around with the keyboard (WASD keys) and interact with objects. The main objective of this game is to collect 10 flowers. 

<a href="/gif/-U4w4w8" title=""><img src="https://i.makeagif.com/media/3-22-2022/U4w4w8.gif" alt="">

# To Play
  
```
# Open up a text editor(Intellij is preferred) and locate Main.java from byow/Core
# Run Main.java
```
<a href="https://imgbb.com/"><img src="https://i.ibb.co/2qs3Y8w/Menu.png" alt="Menu" border="0"></a><br/>
You should be able to see the main menu and the different options:
  - New game(N) starts a new game with seed input
  - Load game(L) allows you to load a previously saved world
  - Language (V) switches languages, current available (English,Espanol)
  - Quit (:Q) exits game
  - Lights (J) switches lights on and off in-game
  
After selecting a new game, you will be prompted to enter a seed (unique value for determing how the world is rendered)
<a href="https://imgbb.com/"><img src="https://i.ibb.co/zHfwQ5L/seed.jpg" alt="seed" border="0"></a><br /><br />
  
Once our world is generated, the user is able to move around using the WASD keys. The objective of the game is collect 10 flowers to win. The user can place their mouse over tiles to see the type of tile. 
  
# Packages

* RandomUtils.java - Collection of random utility methods to generate the randomness of my program
* Engine.java - Allows us to interact with system
* Tileset.java - library of different tiles to form world
* TETile.java - used to identify the type of tile
* TERenderer.java - used to render tiles

# Get Started

```
# Clone this repository
git clone Portfolio

# Go into the repository
cd Portfolio
cd BYOW

# Remove current origin repository
git remove remove origin
```






