# SimpleTextEditor - an Android text editor with cloud save

The project consists of two modules:

-   **Client**: An Android app written in Kotlin using Material Design 3
-   **Server**: An ASP.NET Core server that manages users and files

## Features:

-   Store files locally
-   Control the editor text size
-   Export files to external storage or share them using native Android features
-   Undo/redo changes
-   Store files on server (cloud)
-   Auto synchronization between the client and the server (each time a significant modification is made, the text is automatically saved on cloud, no need to explicitly save)

There is also a "_/docs_" folder, which contains valuable information about the implementation of both the client and the server, the general service architecture and other features of the application. This was created as per project requirements.
