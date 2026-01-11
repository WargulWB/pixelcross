# What does it do?
'pixelcross' is a open source project which implements a Command Line Interface (CLI) program which you can use to generate a crossstitch pattern image from your pixel art image.
The program will determine for each pixel the yarn with the approximately closest color and render a crossstitch pattern using those yarns. The yarn sortiments can be configured including their RGB pixel color representation.

In short - go from this:

![Capybara babies pixel art.](images/capybara_babies_crossstitch_transparent_background.png)

To this:

![Capybara babies as cross stitch render.](images/@crosstitch.png)

And more importantly this:

![Capybara babies as cross stitch pattern.](images/@crosstitch_pattern.png)

# What does it not do?
'pixelcross' does provide no graphical user interface (GUI). It also does not reduce the amount of colors in your image nor does it resize the image. Reason being there is enough free software to edit and adjust images which you could use for that purpose and than process the result with 'pixelcross'.

# Why does this exist?
Pixel art is cool.

Cross stitching is cool.

I do not want to pay around 50$ for proprietary software, which does the same thing but is awkward to use.

Why not?

# How to install?
* Install Java
	* Java Programs require a runtime environment called JRE to be executed. For example you could download the Adoptium OpenJDK Java from this [site](https://adoptium.net/de/temurin/releases/), simply pick the JRE which matches your system. For example 'Windows | x64 | JRE 21 - LTS' and install either via msi or download the zip container, unpack it where you want and add the bin directory to your Path environment variable.
Since this is about the 'PixelCross' application, I do not really want to got into this in further detail. 
* Download pixelcross release package
* Unpack pixelcross release package (zip or tar.gz)
* Go into the unpacked directory
* Open console
	* On windows simply click into the field in the explorer which shows the directory path, type CMD and hit enter, that opens the command prompt in the directory.
* Run command explained under 'How to run?'


# How to run?
```
java -jar pixelcross-0.2.0-jar-with-dependencies.jar <pixelArtImagePath>
```

_'pixelArtImagePath'_ needs to be replaced with the path to the pixel art image you want to create a pattern for.

This means you need an installed Java Runtime Environment (JRE) or Java Development Kit (JDK) for Java 21 or later versions. Which you have to put on your Path environment variable.

Alternatively you can call the 'java' program directly (version number in the JAR name might differ):

* Windows example:
```
C:\Program Files\Java\jdk-21.0.3+9\bin\java.exe -jar pixelcross-0.2.0-jar-with-dependencies.jar <pixelArtImagePath>
```

* Linux (Ubuntu) example:
```
/usr/lib/jvm/jdk-21.0.3+9/bin/java -jar pixelcross-0.2.0-jar-with-dependencies.jar <pixelArtImagePath>
```
The application will generate 2 new image files in the same directory as the pixel art image you referenced:
* @crosstitch.png - a preview image which shows how the image would look cross stitched
* @crosstitch_pattern.png - a pattern image you can use as a reference for cross stitching, the image also contains a legend which shows how many of each color there are and what yarns to use, additionally the legend shows the dimensions of the image (without transparent pixels) and how large this is on different CT cloth in inches and in cm

# What can be configured?
Currently you can only specify on the CLI the path to the pixelart file you want to create a pattern for.
However the program reads in the config file 'config/pixelcross.config.xml'. The yarn sortiments used by the application can be configured via this file. That means if you want to add yarns, sortiments or if you want to change the RGB value of a yarn you can do this by editing this config file.
The current yarn sortiment is taken from [Lord Libidan - DMC Thread Inventory Spreadsheet](https://lordlibidan.com/dmc-thread-inventory-spreadsheet/). I only wrote a script to convert the spreadsheet into an XML format. This means I can not guarantee that all RGB colors are a good fit for the actual yarn color. That being said, the spreadsheet looks accurate. In any case you shoul double check the color of the yarn when buying it and not blindy follow the suggestions of this application.

## CLI Options
Versions >= 0.3.0 allow to provide options as arguments to the CLI which change the behaviour.
Since the options might change in the future, they are not listed here. Instead you can run the program without any arguments or with the option '-h' (or '--help') which tells the program to print the help information to the console. The help information explains the different options.

```
$ java -jar pixelcross-0.4.0-jar-with-dependencies.jar --help
java -jar pixelcross-*-jar-with-dependencies.jar [-h] [-c=<configFilePath>] [-t=<color>] [-p=<pixelPosition>] <pixelArtImagePath>
Creates a cross stitching example image and a pattern image with paths relative to the path of the input pixel art image.
-h, --help
  Shows the CLI usage information. If specified all other arguments are ignored.
-c, --config=<configFilePath>
  Per default the config file is loaded from 'config/pixelcross.config.xml' (realtive to the directory from which you started the application). If that file is not available a default config, packaged with the application is loaded. This option allows to specify a different path from which the config is loaded instead.
-t, --treat-color-trasparent=<color>
  The color has to be given as rgb value '#RRGGBB'. Each pixel matching the color is treated as if it were transparent.
-p, --treat-pixel-trasparent=<pixelPosition>
  The pixel has to be given as coordinate '<horinzontal>,<vertical>' e.g. '0,0' or as 'top_left, top_right, bottom_left, bottom_right'. Each pixel matching the color of the specified pixel is treated as if it were transparent.

```

One example is option 't' with which you can tell the programm to treat a specific color as transparent which might be useful if your pixel art image does no use transparency but an alternative color (e.g. magenta) which represents the transparent pixels.

# Plans for the future?
Currently I am improving my cross stitching and thus do not plan immediate improvements to this project.
If you have any feature requests let me know, I might implement them.

Ideas for possible future features:
* add a graphical user interface (GUI)
* make the result file paths configurable
	* add a timestamp or UUID to them to prevent overwriting of existing files
* add tests ... probably ... maybe ...
* ...


# How to build?
'pixelcross' is a Java program using maven as a build tool, hence you can build the project via the following command (assuming you installed maven (aka 'mvn')):
```
mvn clean install
```
