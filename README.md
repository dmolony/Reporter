# Reporter
Display mainframe files in various formats. This is part of the dm3270 terminal emulator, but it can also be used as a standalone application. When a file is opened it is analysed and then displayed in the best format. When more than one format is possible, the various options are user-selectable.

Record types
* Prefixed
  * RDW
  * VB
* Terminated
  * CR/LF
  * CR
  * LF
* Fixed length
  * FB80
  * FB132
  * FB252
* Other
  * Ravel

Encodings
* ASCII
* EBCDIC

Display formats
* Hex
* Text
* ASA
* Natload

### Documentation
* [Project layout](resources/structure.md)

### Screens
![Hex](resources/output1.png?raw=true "hex")
![Report](resources/output2.png?raw=true "report")
