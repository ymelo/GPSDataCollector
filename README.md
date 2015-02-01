# GPSDataCollector
A simple GPS spy - Collect your location whenever you want, and display them on a map (running, biking, walking... record your trips)

This application is composed of two main parts, the collection, and the visualisation of your gps data.

#Data collection
Collection occurs when you hit the recording button, until you stop hit by hitting the stop button.
Fairly straight forward.
At the moment the app just append all the data to a file. This file is located in the app private's folder, and therefore
cannot be accessed by the user.

This behaviour should be modified in the near future. The file should be accessible and readable.
Also the rate at which the file is written is one of the next focuses for this app.

#Data visualisation
All the trips taken can be visualized on a simple google map.
