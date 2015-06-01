# [rapidRunner](https://github.com/sn0cr/rapidRunner/releases/latest)
The main interface:
![A screenshot of the main interface](https://raw.githubusercontent.com/sn0cr/rapidRunner/develop/screenshot1.png)
--------------------------------------------
## Setup:
  - [ ] The directory with all your test files (one for the input and one for the expected output(-> with the same name but a   different extension))
  - [ ] The directory your command should be run (aka "workingdirectory")
  - [ ] The commandline to run your command
  - [ ] The name and extensions of your testsamples 
All set? - Then your ready to go! :clap:
![A screenshot of the test interface](https://raw.githubusercontent.com/sn0cr/rapidRunner/develop/screenshot0.png)


# Build instructions
This project uses gradle as the build system:
## Build jar
```bash
  gradle shadowJar
```
## Create files for import into eclipse:
```shell
  gradle eclipse
```
