# 0.5.0
- fixed the issue of the config not being loaded from the default path 'config/pixelcross.config.xml'
- (hopefully) fixed the issue of the packaged config not being loaded as fallback if no config is set via option and no config is available at the default path 'config/pixelcross.config.xml'

# 0.4.0
- now highlights horizontal and vertical center in pattern via a triangle indicator, painting the center lines blue and drawing a circle around the center position

# 0.3.0
- now supports an option '-t, --treat-color-trasparent=<color>' to treat a color as transparent (thus not interpreting it as an additional yarn)
- now supports an option '-p, --treat-pixel-trasparent=<pixelPosition>' to treat the color of the pixel as transparent (thus not interpreting it as an additional yarn)
- now supports an option '-c, --config=<configFilePath>' to set the config file path
- now support a '-h, --help' option

# 0.2.0
- fixed index out of bounds issue due to height being given as width in ImageModelLoader
- added integration tests IT01 to IT03, to test general functionality (currently running them as normal unit tests)

# 0.1.0
- initial implementation