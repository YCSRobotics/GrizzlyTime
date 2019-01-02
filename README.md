# GrizzlyTime-JavaFX
JavaFX version of GrizzlyTime logger
GrizzlyTime is a google sheets based logging system. It logs students/mentors hours and days based on a 6 digit identification code. It can read barcodes and is configurable to use different google sheets. 

## Downloads
Download the latest release here
https://github.com/Daltz333/GrizzlyTime-JavaFX/releases

## Video Tutorial
https://www.youtube.com/watch?v=Cnrck_dascw

## Building Source Code
Get API Credentials from https://developers.google.com/sheets/api/quickstart/java  
Place google sheet api credentials under `src/main/resources/credentials/credentials.json'  
Clone with `https://github.com/Daltz333/GrizzlyTime-JavaFX.git` 
Build with `.\gradlew shadowJar`

Releases are curated towards Grizzly Robotics personal google sheet. If you download a compiled jar, you **must change** 
the sheet variable in the config.json.
### WARNING: THIS PROJECT IS CURRENTLY INCUBATING
**What works:**
 - Logging in/out
 - Basic UI
 - Grabbing frames
 - Reading barcode images
 - Logs individual days/hours
 - Configurable JSON to disable camera or change sheet ID
 
 **What doesn't work/to fix:**
  - Launch nuke command
  - Optimize gsheet wrapper
 
![example image](https://i.ibb.co/TK4Q1WT/90.png)
### Usage
1. Import the template spreadsheet into a google doc.
2. Copy the spreadsheet ID (google it)
3. Paste spreadsheet id into the config.json, it should look something like
```
"sheet" = "fkljATsgsdfsdfaslkjtaSDsfs"
```
4. Launch the application
5. It should open a web browser and request for permission to access your account, grant it.
6. Profit???
