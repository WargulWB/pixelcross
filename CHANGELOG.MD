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